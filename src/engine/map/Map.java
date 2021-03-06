package engine.map;

import java.util.*;
import java.util.Map.Entry;

import javax.management.RuntimeErrorException;

import notifications.map.*;

import org.apache.log4j.Logger;

import util.Args;
import util.ArrayUtil;
import util.Logf;

import common.Location;
import common.LocationInfo;
import common.enums.Direction;
import common.interfaces.ILocation;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import common.interfaces.IWeapon;
import config.Config;
import config.xml.MapConditions;
import config.xml.MapEvents;

import engine.Player;
import engine.items.MeleeWeapon;
import engine.items.RangedWeapon;
import engine.map.*;
import engine.map.interfaces.IMap;
import engine.map.interfaces.IMutableMapUnit;
import engine.map.win.IWinCondition;
import engine.pathfinding.PathFinder;
import engine.skills.ISkill;
import engine.unit.AiUnit;
import engine.unit.IMutableUnit;
import engine.unit.Unit;
import engine.unit.SpriteSheetData;

/**
 * The model
 * @author Bilal Hussain
 */
public class Map extends BasicMap implements IMap {
	private static final Logger log = Logger.getLogger(Map.class);
	private Player playerinfo;

	private AIPlayer ai;
	private MapPlayer player;

	// Cached movement data
	private HashMap<IMutableMapUnit, PathFinder> paths;
	// Turn ordering
	private PriorityQueue<IMutableMapUnit> order;

	private IMutableMapUnit current;

	private enum MapState{
		START, INBATTLE, PRE_WIN, WON
	}
	private MapState mapState;
	
	
	public Map(String name, Player player) {
		this.playerinfo = player;
		loadSettings(name);
		paths = new HashMap<IMutableMapUnit, PathFinder>();
		order = new PriorityQueue<IMutableMapUnit>(16, conditions.getTurnComparator());
		
		this.player = new MapPlayer();
		setUpAI();
	}

	private void loadSettings(String name) {
		loadMap(name);
		assert (field != null);
		assert (width > 0);
		assert (height > 0);
	}

	private void setUpAI() {
		ai = new AIPlayer(this, player);
		ArrayList<IMutableMapUnit> aiUnits = new ArrayList<IMutableMapUnit>();

		for (Entry<UUID,Location> e : enemies.getUnitPlacement().entrySet()) {
			IMutableUnit u = enemies.getUnit(e.getKey());
			AIMapUnit au = new AIMapUnit((AiUnit) u, e.getValue(), ai);
			getTile(au.getLocation()).setCurrentUnit(au);
			aiUnits.add(au);
		}
		
		assert aiUnits.size() == enemies.getUnitPlacement().size(); 
		if (aiUnits.size() ==0) throw new RuntimeException("No Ai units");
		
		ai.setUnits(aiUnits);
		
		assert order != null;
		for (IMutableMapUnit aiu : aiUnits) {
			assert aiu != null;
			order.add(aiu);
		}
		
	}

	private void sendNotification(IMapNotification n){
		setChanged();
		notifyObservers(n);
	}
	
	@Override
	public void start() {
		sendNotification(new ChooseUnitsNotifications(playerinfo.getUnits(), ai.getUnits()));
	}

	@Override
	public void setUsersUnits(HashMap<IMutableUnit, Location> selected) {
		ArrayList<IMutableMapUnit> units = new ArrayList<IMutableMapUnit>();

		for (Entry<IMutableUnit, Location> e : selected.entrySet()) {
			IMutableMapUnit u = new MapUnit(e.getKey(), e.getValue(), player);
			field[u.getGridX()][u.getGridY()].setCurrentUnit(u);
			units.add(u);
			order.add(u);
		}
		player.setUnits(units);
		
		sendNotification(new UnitsChosenNotification(new ArrayList<IMapUnit>(units)));
		
		Logf.debug(log, "ordering %s", order);
		setChanged();
		
		mapState =  MapState.START;
		sendNotification(new DialogNotification(events.getStartDialog()));
	}

	@Override
	public void dialogFinished(){
		switch(mapState){
			case START:
				mapState = MapState.INBATTLE;
				sendNextUnit();
				break;
			case PRE_WIN:
				mapState = MapState.WON;
				sendNotification(new PlayerWonNotification());
				break;
		}
	}
	
	// Precondition getMovementRange must have been called first
	@Override
	public void moveUnit(IMutableMapUnit u, ILocation p) {

		Collection<LocationInfo> path = paths.get(u).getMovementPath(p);
		field[u.getGridX()][u.getGridY()].setCurrentUnit(null);
		u.setLocation(p);
		field[p.getX()][p.getY()].setCurrentUnit(u);

//		// FIXME events
//		for (LocationInfo l : path) {
//			// field[l.x][l.y].event(u)
//		}

		u.setReadiness(u.getReadiness() - 60);
		order.add(u);
		paths.remove(u);

		sendNotification(new UnitMovedNotification(u, path));
	}


	public void finishedMoving(IMutableMapUnit u) {
		if (u.isAI()){
			log.info("AI moved a unit");
			IMutableMapUnit target = ai.getTarget(u);
			
			if (target == null){
				unitTurnFinished(u);
			}else{
				attackTargetChosen(u, target);
			}
		}
	}

	public void unitTurnFinished(IMutableMapUnit u) {
		if (!checkForFinished()){
			sendNextUnit();
		}
	}
	
	// returns true if the map is finished.
	public boolean checkForFinished(){
		
		if (processCondition(conditions.getWinCondition())) {
			System.err.println("PLAYERS WINS");	
			mapState = MapState.PRE_WIN;
			sendNotification(new DialogNotification(events.getEndDialog()));
			return true;
		}
		if (player.getUnits().isEmpty()) {
			System.err.println("AI WINS");
			sendNotification(new PlayerLostNotification());
			return true;
		}
		
		return false;
	}
	
	public boolean processCondition(IWinCondition conditions){
		return conditions.hasWon(this, player, ai);
	}
	
	/**
	 * Tell the  Observers the next unit to move
	 */
	void sendNextUnit() {
		current = order.remove();
		if (current.getReadiness() <= 0) {
			order.clear();
			for (IMutableMapUnit u : player.getUnits()) {
				u.setReadiness(100);
				order.add(u);
			}
			for (IMutableMapUnit u : ai.getUnits()) {
				u.setReadiness(100);
				order.add(u);
			}

		}

		sendNotification(new UnitTurnNotification(current));
		
		if (current.isAI()) {
			log.info("Next Unit is ai:" + current);
			ILocation l = ai.getMoveLocation((AIMapUnit) current);
			moveUnit(current, l);
		}
	}

	@Override
	public Collection<LocationInfo> getMovementRange(IMutableMapUnit u) {
		Args.nullCheck(u);
		PathFinder pf;
		if ((pf = paths.get(u)) != null) {
			return pf.getMovementRange();
		}

		pf = new PathFinder(u, this);
		paths.put(u, pf);
		
		return pf.getMovementRange();
	}

	// Returns the units that the specifed units can attack
	public Collection<Location> getVaildTargets(IMutableMapUnit u) {
		assert u != null;
		
		Collection<Location> attackRange = u.getWeapon().getAttackRange(u.getLocation(), width, height); 

		Collection<Location> results = new ArrayList<Location>();
		for (Location l : attackRange) {
			IMutableMapUnit m =getTile(l).getCurrentUnit();
			if ( m != null && m.isAI() != u.isAI() ){
				results.add(l);
			}
		}
		return results;
	}

	private void unitDied(IMutableMapUnit target){
		if (target.isAI()){
			ai.unitDied(target);
		}else{
			player.unitDied(target);
		}
		order.remove(target);
		paths.remove(target);
		getTile(target.getLocation()).setCurrentUnit(null);		
	}
	
	// Peforms the attack and notifies the Observers what the results were 
	public void attackTargetChosen(IMutableMapUnit u, IMutableMapUnit target){
		Battle battle = new Battle(u, target,this);
		peformBattle(battle);
	}

	void peformBattle(Battle battle){
		boolean leveledUp = battle.performBattle();

		for (BattleResult b : battle.getResults()) {
			if (b.isTargetDead()) {
				unitDied(b.getMutableTarget());
			}
		}
		sendNotification(new BattleNotification(battle));
	}
	
	
	// Peforms the attack and notifies the Observers what the results were 
	public void skillTargetChosen(ISkill skill, IMutableMapUnit u, Location target) {
		Battle battle = new SkillBattle(skill, u, target, this);
		peformBattle(battle);
	}

	public boolean hasOutlines() {
		return data.hasOutlines();
	}
	
	
}
