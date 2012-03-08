package editor.editors;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import config.assets.MusicData;
import config.assets.Musics;
import config.xml.MapConditions;
import config.xml.MapMusic;
import editor.Editor;
import engine.map.win.DefeatAllUnitsCondition;
import engine.map.win.DefeatSpecificUnitCondition;
import engine.map.win.IWinCondition;
import engine.skills.ISkill;
import engine.skills.RangedSkill;

/**
 * Used to specify the music on a Map
 * @author Bilal Hussain
 */
public class MapConditionsPanel extends JPanel {
	private static final long serialVersionUID = -9163168786596236956L;

	protected JComboBox conditions;
	
	protected MapConditions  mapConditions;
	protected IWinCondition currentCondition;
	
	public MapConditionsPanel(){
		createMainPane();
	}

	public void setMapConditions(MapConditions data){
		mapConditions = data;
		ItemListener lsl  = conditions.getItemListeners()[0];
		conditions.removeItemListener(lsl);
		
		IWinCondition wc =  data.getWinCondition();
		System.out.println(wc);
		if (wc instanceof DefeatAllUnitsCondition){
			conditions.setSelectedItem(ConditionsTypes.DEFEAT_ALL);
		}else if (wc instanceof DefeatSpecificUnitCondition){
			conditions.setSelectedItem(ConditionsTypes.DEFEAT_SINGLE);
		}
		
		conditions.addItemListener(lsl);

	}

	protected void createMainPane() {
		this.setLayout(AbstractResourcesPanel.defaultInfoPanelLayout());
		
		this.add(new JLabel("Type:"));
		conditions = new JComboBox(ConditionsTypes.values());
		conditions.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				ConditionsTypes ct = (ConditionsTypes) conditions.getSelectedItem();
				ct.updateEditor(MapConditionsPanel.this, currentCondition);
				mapConditions.setWinCondition(currentCondition);
			}
		});
		this.add(conditions, "span, growx");
		
	}

	static enum ConditionsTypes {
		DEFEAT_ALL("Defeat All Units") {
			@Override
			IWinCondition newCondition(MapConditionsPanel cp) {
				return new DefeatAllUnitsCondition();
			}

			@Override
			String getInfo() {
				return "Defeat All Units";
			}

			@Override
			void updateEditor(MapConditionsPanel cp, IWinCondition wc) {
				// FIXME updateEditor method
				
			}
		},
		DEFEAT_SINGLE("Defeat a Specific Unit") {
			@Override
			IWinCondition newCondition(MapConditionsPanel cp) {
				//FIXME finish
				DefeatSpecificUnitCondition dsuc = new DefeatSpecificUnitCondition(null);
				return dsuc;
			}

			@Override
			String getInfo() {
				return "Defeat a Specific Unit";
			}

			@Override
			void updateEditor(MapConditionsPanel cp, IWinCondition wc) {
				// FIXME updateEditor method
				
			}
		};
		
		abstract IWinCondition newCondition(MapConditionsPanel cp);
		abstract String getInfo();
		abstract void updateEditor(MapConditionsPanel cp, IWinCondition wc);

		private String name;
		
		private ConditionsTypes(String name) {
			this.name = name;
		}
		
		@Override
		public String toString(){
			return name;
		}
		
	}
	
}
