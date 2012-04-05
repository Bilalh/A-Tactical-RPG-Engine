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

	public AbstractAIBehaviour getBehaviour() {
		if (ordering == null) ordering  = new LowestHp();
		return ordering;
	}

	public void setBehaviour(AbstractAIBehaviour behaviour) {
		this.ordering = behaviour;
	}

}
