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

	private AbstractAIBehaviour behaviour;

	public AiUnit() {
		super();
		this.behaviour = new LowestHp();
	}

	public AiUnit(IMutableUnit u) {
		super(u);
	}

	public AbstractAIBehaviour getBehaviour() {
		if (behaviour == null) behaviour  = new LowestHp();
		return behaviour;
	}

	public void setBehaviour(AbstractAIBehaviour behaviour) {
		this.behaviour = behaviour;
	}

}
