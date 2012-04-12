package editor.editors;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.UUID;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import config.assets.UnitPlacement;
import config.xml.MapConditions;
import editor.Editor;
import engine.map.win.DefeatAllUnitsCondition;
import engine.map.win.DefeatSpecificUnitCondition;
import engine.map.win.IWinCondition;
import engine.unit.IMutableUnit;

/**
 * Used to specify the music on a Map
 * @author Bilal Hussain
 */
public class MapConditionsPanel extends JPanel implements IRefreshable {
	private static final long serialVersionUID = -9163168786596236956L;

	protected MapsPanel mapPanel;
	protected UnitPlacement enemies;

	protected MapConditions mapConditions;
	protected IWinCondition currentCondition;

	protected JComboBox conditions;
	
	protected JLabel    ltargets;
	protected JComboBox targets;
	
	protected ConditionsTypes currentType;
	
	public MapConditionsPanel(MapsPanel mapPanel){
		this.mapPanel = mapPanel;
		createMainPane();
	}

	@Override
	public void panelSelected(Editor editor) {
		enemies = mapPanel.getEnemies();
		
		ItemListener il =  targets.getItemListeners()[0];
		targets.removeItemListener(il);
		targets.removeAllItems(); 

		
		for (UUID uuid : enemies.getUnitPlacement().keySet()) {
			IMutableUnit m = enemies.getUnit(uuid);
			targets.addItem(m);
		}
		targets.addItemListener(il);
	}

	
	public void setMapConditions(MapConditions data){
		assert data != null;
		mapConditions = data;
		ItemListener lsl  = conditions.getItemListeners()[0];
		conditions.removeItemListener(lsl);
		
		currentCondition =  mapConditions.getWinCondition();
		assert currentCondition != null;
		
		if (currentCondition instanceof DefeatAllUnitsCondition){
			currentType = ConditionsTypes.DEFEAT_ALL;
		}else if (currentCondition instanceof DefeatSpecificUnitCondition){
			currentType = ConditionsTypes.DEFEAT_SINGLE;
		}else{
			assert false : currentType + "  " + data;
		}
		
		conditions.setSelectedItem(currentType);
		currentType.updateEditor(this, currentCondition);
		
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
				changeType(ct);
				assert currentCondition != null;
				mapConditions.setWinCondition(currentCondition);
			}
		});
		this.add(conditions, "span, growx");
		
		this.add(ltargets = new JLabel("Target:"));
		targets = new JComboBox(new IMutableUnit[]{});
		targets.setRenderer(new UnitsPanel.UnitListRenderer());
		targets.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				changeTarget((IMutableUnit) e.getItem());
				assert currentCondition != null;
				mapConditions.setWinCondition(currentCondition);
			}
		});
		this.add(targets, "span, growx");
	}

	private void changeTarget(IMutableUnit u) {
		assert currentType == ConditionsTypes.DEFEAT_SINGLE;
		assert u != null;
		if (currentCondition == null) return;
		((DefeatSpecificUnitCondition)currentCondition).setUnitId(u.getUuid());
	}

	private void changeType(ConditionsTypes ct) {
		currentCondition = ct.newCondition(MapConditionsPanel.this);
		assert currentCondition != null;
		currentType      = ct;
		
		ct.updateEditor(this, currentCondition);
		assert currentCondition != null;
		mapConditions.setWinCondition(currentCondition);
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
				cp.ltargets.setVisible(false);
				cp.targets.setVisible(false);
			}
		},
		DEFEAT_SINGLE("Defeat a Specific Unit") {
			@Override
			IWinCondition newCondition(MapConditionsPanel cp) {
				DefeatSpecificUnitCondition dsuc = new DefeatSpecificUnitCondition(null);
				return dsuc;
			}

			@Override
			String getInfo() {
				return "Defeat a Specific Unit";
			}

			@Override
			void updateEditor(MapConditionsPanel cp, IWinCondition wc) {
				assert wc != null;
				cp.ltargets.setVisible(true);
				cp.targets.setVisible(true);
				cp.targets.setSelectedItem(cp.enemies.getUnit(((DefeatSpecificUnitCondition)wc).getUnitId()));
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
