package engine.map.win;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import common.interfaces.IUnit;

import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class DefeatSpecificUnitCondition implements IWinCondition {

	private UUID unitId;
	

	public DefeatSpecificUnitCondition(UUID unitId) {
		this.unitId = unitId;
	}

	@Override
	public boolean hasWon(Map map, MapPlayer player, MapPlayer ai) {
		for (IMutableMapUnit unit : ai.getUnits()) {
			if (unit.getUuid().equals(unitId)) return false;
		}
		return true;
	}


	public UUID getUnitId() {
		return unitId;
	}


	public void setUnitId(UUID unitId) {
		this.unitId = unitId;
	}

	@Override
	public String info(Collection<? extends IUnit> units) {
		
		String name = null;
		for (IUnit u : units) {
			if (u.getUuid().equals(unitId)){
				name = u.getName();
				break;
			}
		}
		assert name != null;
		
		return "Defeat Ai's " + name;
	}

}
