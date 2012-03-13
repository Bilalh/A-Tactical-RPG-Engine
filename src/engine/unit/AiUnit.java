package engine.unit;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.map.ai.AbstractAIBehaviour;
import engine.map.ai.LowestHp;

/**
 * Ai Unit has  
 * @author Bilal Hussain
 */
@XStreamAlias("aiUnit")
public class AiUnit extends Unit {

	private AbstractAIBehaviour ordering;

	public AiUnit() {
		super();
		this.ordering = new LowestHp();
	}

	public AiUnit(IMutableUnit u) {
		super(u);
	}

	public AbstractAIBehaviour getOrdering() {
		return ordering;
	}

	public void setOrdering(AbstractAIBehaviour ordering) {
		this.ordering = ordering;
	}

}
