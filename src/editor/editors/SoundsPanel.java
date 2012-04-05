package editor.editors;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import editor.Editor;

/**
 * Handles sound effects
 * 
 * @author Bilal Hussain
 */
public class SoundsPanel extends MusicPanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SoundsPanel.class);

	public SoundsPanel(Editor editor) {
		super(editor);
		chooser.setFileFilter(new FileNameExtensionFilter("Ogg and Wav Audio (*.ogg, *.wav)", "ogg", "wav"));
	}

	@Override
	protected String resourceName() {
		return "Sound";
	}

	@Override
	protected boolean play() {
		music.forcePlaySound(current.getLocation());
		return false;
	}

	@Override
	protected String storePath() {
		return "music/sounds/";
	}

	@Override
	protected JComponent createInfoPanel() {
		JComponent p = super.createInfoPanel();
		infoStop.setVisible(false);
		return p;
	}

}
