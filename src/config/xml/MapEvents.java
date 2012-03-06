package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import common.assets.DialogParts;
import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("mapEvents")
public class MapEvents implements IPreference {

	private DialogParts startDialog;
	private DialogParts endDialog;
	
	/** @category Generated */
	public MapEvents(DialogParts startDialog, DialogParts endDialog) {
		this.startDialog = startDialog;
		this.endDialog = endDialog;
	}

	/** @category Generated */
	public DialogParts getStartDialog() {
		return startDialog;
	}

	/** @category Generated */
	public DialogParts getEndDialog() {
		return endDialog;
	}

	/** @category Generated */
	public void setStartDialog(DialogParts startDialog) {
		this.startDialog = startDialog;
	}

	/** @category Generated */
	public void setEndDialog(DialogParts endDialog) {
		this.endDialog = endDialog;
	}
	
}
