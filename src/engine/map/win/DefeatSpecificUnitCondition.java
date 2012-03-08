package engine.map.win;

import java.util.UUID;

import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public class DefeatSpecificUnitCondition implements IWinCondition {

	private UUID unitId;
	
	/** @category Generated */
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

	/** @category Generated */
	public UUID getUnitId() {
		return unitId;
	}

	/** @category Generated */
	public void setUnitId(UUID unitId) {
		this.unitId = unitId;
	}

}
