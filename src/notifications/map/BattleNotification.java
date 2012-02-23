package notifications.map;

import view.map.GuiMap;
import common.interfaces.IBattleInfo;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;

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

}
