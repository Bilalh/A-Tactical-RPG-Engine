package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;
import config.assets.DialogParts;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("mapEvents")
public class MapEvents implements IPreference {

	private DialogParts startDialog;
	private DialogParts endDialog;
	

	public MapEvents(DialogParts startDialog, DialogParts endDialog) {
		this.startDialog = startDialog;
		this.endDialog = endDialog;
	}


	public DialogParts getStartDialog() {
		return startDialog;
	}


	public DialogParts getEndDialog() {
		return endDialog;
	}


	public void setStartDialog(DialogParts startDialog) {
		this.startDialog = startDialog;
	}


	public void setEndDialog(DialogParts endDialog) {
		this.endDialog = endDialog;
	}
	
}
