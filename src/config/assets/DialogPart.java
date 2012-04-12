package config.assets;

import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import common.interfaces.IUnit;
import common.interfaces.Identifiable;

/**
 * A DialogPart has the text to be displayed. It  can optionally specify the id of speaker.  
 * @author Bilal Hussain
 */
@XStreamAlias("dialogPart")
public class DialogPart implements Identifiable, Comparable<DialogPart> {

	@XStreamAsAttribute
	private UUID uuid;
	private String text;
	private UUID unitId;

	private int partNo;
	
	public DialogPart(){
		uuid = UUID.randomUUID();
		text = "";
	}
	
	public DialogPart(String text, UUID unitId) {
		this();
		this.text   = text;
		this.unitId = unitId;
	}

	public void setUnitId(IUnit u) {
		if (u == null ) unitId = null;
		else            unitId   = u.getUuid();
	}
	

	public String getText() {
		return text;
	}


	public UUID getUnitId() {
		return unitId;
	}


	public void setText(String text) {
		this.text = text;
	}


	public void setUnitId(UUID unitId) {
		this.unitId = unitId;
	}


	@Override
	public UUID getUuid() {
		return uuid;
	}


	public int getPartNo() {
		return partNo;
	}


	public void setPartNo(int partNo) {
		this.partNo = partNo;
	}

	@Override
	public int compareTo(DialogPart o) {
		return this.partNo - o.partNo;
	}
	
}
