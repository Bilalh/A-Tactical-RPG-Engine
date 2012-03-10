package editor.editors;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import common.interfaces.IWeapon;
import config.assets.Units;

import editor.editors.UnitsPanel.WeaponDropDownListRenderer;
import editor.map.EditorSpriteSheet;
import engine.map.ai.AbstractTargetOrdering;
import engine.map.ai.LowestHp;
import engine.map.ai.LowestStrength;
import engine.unit.AiUnit;
import engine.unit.IMutableUnit;

/**
 * @author Bilal Hussain
 */
public class AiUnitPanel extends UnitsPanel<AiUnit> {
	private static final long serialVersionUID = 7490587700244524035L;

	protected JComboBox infoBehaviour;

	public AiUnitPanel(Map<UUID, EditorSpriteSheet> spriteSheets, boolean listOnLeft, String displayName) {
		super(spriteSheets, listOnLeft, displayName);
	}

	@Override
	protected IMutableUnit createUnit(IMutableUnit old) {
		return new AiUnit(old);
	}


	@Override
	public void setCurrentUnit(AiUnit u) {
		super.setCurrentUnit(u);
		for (int i = 0; i < infoBehaviour.getModel().getSize(); i++) {
			AbstractTargetOrdering ordering = (AbstractTargetOrdering) infoBehaviour.getModel().getElementAt(i);
			assert ordering != null;
			assert u.getOrdering() != null;
			if (ordering.getClass().equals(u.getOrdering().getClass()) && ordering.isNegated() == u.getOrdering().isNegated()){
				infoBehaviour.setSelectedIndex(i);
				break;
			}
		}
	}
	
	
	protected void changeOrdering(AbstractTargetOrdering ordering) {
		currentUnit.setOrdering(ordering);
	}

	protected AbstractTargetOrdering[] getTargetOrderings() {
		AbstractTargetOrdering[] ato = new AbstractTargetOrdering[] {
				new LowestHp(),
				new LowestStrength(),
				new LowestHp(),
				new LowestStrength(),
		};
		for (int i = ato.length/2 ; i < ato.length; i++) {
			ato[i].setNegated(true);
		}
		return ato;
	}

	@Override
	protected void extraComponents(JPanel p) {
		addSeparator(p, "Behaviour:");
		p.add(new JLabel("Target:"));
		infoBehaviour = new JComboBox(getTargetOrderings());
		infoBehaviour.setEditable(false);
		infoBehaviour.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (infoBehaviour.getItemCount() <= 0) return;

				AbstractTargetOrdering ordering = (AbstractTargetOrdering) e.getItem();
				changeOrdering(ordering);
			}
		});
		p.add(infoBehaviour, "span, growx");
	}

}
