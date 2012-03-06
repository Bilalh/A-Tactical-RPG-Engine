package common.assets;

import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import common.interfaces.Identifiable;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("dialogPart")
public class DialogPart implements Identifiable {

	@XStreamAsAttribute
	private UUID uuid;
	private String text;
	private UUID unitId;
	
	public DialogPart(String text, UUID unitId) {
		uuid = UUID.randomUUID();
		this.text   = text;
		this.unitId = unitId;
	}

	/** @category Generated */
	public String getText() {
		return text;
	}

	/** @category Generated */
	public UUID getUnitId() {
		return unitId;
	}

	/** @category Generated */
	public void setText(String text) {
		this.text = text;
	}

	/** @category Generated */
	public void setUnitId(UUID unitId) {
		this.unitId = unitId;
	}

	/** @category Generated */
	@Override
	public UUID getUuid() {
		return uuid;
	}
	
	
}
