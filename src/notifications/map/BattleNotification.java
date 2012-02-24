package notifications.map;

import java.util.Collection;
import java.util.Iterator;

import view.map.GuiMap;
import common.interfaces.IBattleInfo;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;
import engine.map.Battle;
import engine.map.BattleResult;

/**
 * @author Bilal Hussain
 */
public class BattleNotification implements IMapNotification {

	private IBattleInfo battle;

	public BattleNotification(IBattleInfo battle) {
		this.battle = battle;
	}

	@Override
	public void process(GuiMap map) {
		map.unitsBattle(battle);
	}

	@Override
	public String readableInfo() {
		Collection<BattleResult> results = battle.getResults();
		StringBuffer sb = new StringBuffer(results.size()*70);
		IMapUnit m = battle.getAttacker(); 
		sb.append(String.format("%s' %s attacked: ", m.isAI() ? "AI" :"Player", m.getName()));
		
		for (Iterator<BattleResult> it = results.iterator(); it.hasNext();) {
			BattleResult b = it.next();
			sb.append(String.format("%s(%s)", b.getTarget().getName(),  b.isTargetDead() ? "killed" : b.getDamage()));
			if (it.hasNext()) sb.append(", ");
		}
		
		return sb.toString();
	}

}
