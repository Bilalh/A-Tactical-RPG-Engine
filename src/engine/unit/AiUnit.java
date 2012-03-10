package engine.unit;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.map.ai.AbstractTargetOrdering;
import engine.map.ai.LowestHp;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("aiUnit")
public class AiUnit extends Unit {

	private AbstractTargetOrdering ordering;

	/** @category Generated */
	public AiUnit() {
		super();
	}

	/** @category Generated */
	public AiUnit(IMutableUnit u) {
		super(u);
	}

	/** @category Generated */
	public AbstractTargetOrdering getOrdering() {
		return ordering;
	}

	/** @category Generated */
	public void setOrdering(AbstractTargetOrdering ordering) {
		this.ordering = ordering;
	}

}
