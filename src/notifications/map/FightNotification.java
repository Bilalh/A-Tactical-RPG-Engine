package notifications.map;

import view.map.GuiMap;
import common.interfaces.IMapNotification;
import common.interfaces.IMapUnit;

/**
 * @author Bilal Hussain
 */
public class FightNotification implements IMapNotification {

	private IMapUnit attacker, target;
	private int damage;
	
	/** @category Generated */
	public FightNotification(IMapUnit attacker, IMapUnit target, int damage) {
		this.attacker = attacker;
		this.target = target;
		this.damage = damage;
	}

	@Override
	public void process(GuiMap map) {
		map.unitsBattle(attacker,target, damage);
	}

}
