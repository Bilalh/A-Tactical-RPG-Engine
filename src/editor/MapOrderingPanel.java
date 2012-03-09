package editor;

import java.awt.Component;
import java.awt.dnd.DragSourceDropEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;

import config.assets.*;
import config.xml.SavedMap;
import editor.editors.AbstractResourcesPanel;
import editor.spritesheet.IDragFinishedListener;
import editor.spritesheet.ReorderableJList;

/**
 * @author Bilal Hussain
 */
public class MapOrderingPanel extends AbstractResourcesPanel<OrderedMap, MapOrdering> implements IDragFinishedListener {
	private static final long serialVersionUID = 8603891838764329257L;

	MapOrdering ordering;

	public MapOrderingPanel(Editor editor) {
		super(editor);
		listAddButton.setVisible(false);
		listDeleteButton.setVisible(false);
		ordering = new MapOrdering();
	}

	@Override
	public void panelSelected(Editor editor) {
		Maps maps = editor.getMaps();

		System.out.println(maps);
		System.out.println(ordering);
		for (Iterator<OrderedMap> it = ordering.iterator(); it.hasNext();) {
			OrderedMap om = it.next();
			if (!maps.containsKey(om.getUuid())) {
				it.remove();
			}
		}

		for (DeferredMap df : maps) {
			if (!ordering.containsKey(df.getUuid())) {
				ordering.put(new OrderedMap(df, ordering.size()));
			}
		}

		ArrayList<OrderedMap> al = new ArrayList(ordering.values());
		Collections.sort(al);

		resourceListModel.clear();
		for (OrderedMap om : al) {
			resourceListModel.addElement(om);
		}

	}

	@Override
	protected JComponent createInfoPanel() {
		return null;
	}

	@Override
	protected JList createJList(ListModel model) {
		ReorderableJList l = new ReorderableJList(model, this);
		l.setCellRenderer(new ReorderableJList.ReorderableListCellRenderer() {
			private static final long serialVersionUID = 5631531917215157509L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				OrderedMap r = (OrderedMap) value;
				label.setText(resourceDisplayName(r, index));
				return label;
			}
		});
		return l;
	}

	@Override
	public MapOrdering getResouces() {
		panelSelected(editor);
		return super.getResouces();
	}

	@Override
	protected void setCurrentResource(OrderedMap resource) {
	}

	@Override
	protected OrderedMap defaultResource() {
		return null;
	}

	@Override
	protected String resourceName() {
		return "map";
	}

	@Override
	protected String resourceDisplayName(OrderedMap map, int index) {
		assert map != null;
		SavedMap m = map.getAsset();
		assert m != null;
		return map.getIndex() + " - " + map.getAsset().getMapData().getName();
	}

	@Override
	protected MapOrdering createAssetInstance() {
		return new MapOrdering();
	}

	@Override
	protected void addToList() {

	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde, int oldIndex, int newIndex) {
		for (int i = 0; i < resourceListModel.size(); i++) {
			OrderedMap om = (OrderedMap) resourceListModel.get(i);
			om.setIndex(i);
		}

	}

}
