package editor.editors;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;

import common.assets.MusicData;
import common.assets.Musics;
import editor.Editor;

/**
 * @author Bilal Hussain
 */
public class MusicPanel extends AbstractResourcesPanel<MusicData, Musics> {
	private static final long serialVersionUID = -8134784428673033659L;

	MusicData current;
	
	public MusicPanel(Editor editor) {
		super(editor);
	}

	@Override
	public void panelSelected(Editor editor) {
		// FIXME panelSelected method
	}

	@Override
	protected void setCurrentResource(MusicData resource) {
		current = resource;
	}

	@Override
	protected void addToList() {
		// FIXME addToList method
		
	}

	@Override
	protected JComponent createInfoPanel() {
		JPanel p = new JPanel();
		return p;
	}

	@Override
	protected MusicData defaultResource() {
		return new MusicData();
	}

	@Override
	protected Musics createAssetInstance() {
		return new Musics();
	}

	@Override
	protected String resourceDisplayName(MusicData resource, int index) {
		if (resource.getLocation() == null) return "none";
		return new File(resource.getLocation()).getName();
	}

	@Override
	protected String resourceName() {
		return "Music";
	}

}
