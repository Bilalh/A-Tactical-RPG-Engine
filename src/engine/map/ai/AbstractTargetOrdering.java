package engine.map.ai;

import java.util.ArrayList;
import java.util.Comparator;

import engine.map.AIPlayer;
import engine.map.Map;
import engine.map.MapPlayer;
import engine.map.interfaces.IMutableMapUnit;

/**
 * @author Bilal Hussain
 */
public abstract class AbstractTargetOrdering implements Comparator<IMutableMapUnit> {

	protected transient Map map;
	protected transient AIPlayer ai;
	protected transient MapPlayer player;
	
	// If true negate the result of the Comparator
	protected boolean negated;
	public AbstractTargetOrdering(){}
	
	public AbstractTargetOrdering(Map map, AIPlayer ai, MapPlayer player) {
		this.map = map;
		this.ai = ai;
		this.player = player;
	}

	@Override
	public abstract String toString();

	/** @category Generated */
	public void setMap(Map map) {
		this.map = map;
	}

	/** @category Generated */
	public void setAi(AIPlayer ai) {
		this.ai = ai;
	}

	/** @category Generated */
	public void setPlayer(MapPlayer player) {
		this.player = player;
	}

	/** @category Generated */
	public boolean isNegated() {
		return negated;
	}

	/** @category Generated */
	public void setNegated(boolean negated) {
		this.negated = negated;
	}

}
