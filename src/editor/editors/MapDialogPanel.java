package editor.editors;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import common.assets.DialogPart;
import common.assets.DialogParts;
import editor.Editor;

/**
 * @author Bilal Hussain
 */
public class MapDialogPanel extends AbstractResourcesPanel<DialogPart, DialogParts> {
	private static final long serialVersionUID = 4626052435769578123L;
	private static final Logger log = Logger.getLogger(MapDialogPanel.class);
	
	public MapDialogPanel(Editor editor) {
		super(editor,false);
	}


	@Override
	public void panelSelected(Editor editor) {
		
	}

	@Override
	protected void setCurrentResource(DialogPart resource) {
		// FIXME setCurrentResource method
		
	}

	@Override
	protected void addToList() {
		// FIXME addToList method
		
	}

	@Override
	protected JComponent createInfoPanel() {
		return new JPanel();
	}

	@Override
	protected String resourceDisplayName(DialogPart resource) {
		return resource.hashCode() + "";
	}

	@Override
	protected String resourceName() {
		return "Dialog";
	}

	@Override
	protected DialogParts createAssetInstance() {
		return new DialogParts();
	}

	@Override
	protected DialogPart defaultResource() {
		return new DialogPart("",null);
	}

}
