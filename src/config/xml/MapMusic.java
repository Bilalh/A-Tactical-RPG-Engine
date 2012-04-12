package config.xml;

import java.util.UUID;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.Config;
import config.IPreference;
import config.assets.MusicData;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("mapMusic")
public class MapMusic implements IPreference {
	private UUID backgroundId;

	private UUID attackSound;
	private UUID defeatUnitSound;
	private UUID loseUnitSound;
	private UUID winMapSound;
	private UUID loseMapSound;
	private UUID levelUpSound;

	

	public UUID getBackground() {
		return backgroundId;
	}


	public void setBackground(UUID backgroundId) {
		this.backgroundId = backgroundId;
	}


	public UUID getAttackSound() {
		return attackSound;
	}


	public void setAttackSound(UUID attackSound) {
		this.attackSound = attackSound;
	}


	public UUID getDefeatUnitSound() {
		return defeatUnitSound;
	}


	public void setDefeatUnitSound(UUID defeatUnitSound) {
		this.defeatUnitSound = defeatUnitSound;
	}


	public UUID getLoseUnitSound() {
		return loseUnitSound;
	}


	public void setLoseUnitSound(UUID loseUnitSound) {
		this.loseUnitSound = loseUnitSound;
	}


	public UUID getWinMapSound() {
		return winMapSound;
	}


	public void setWinMapSound(UUID winMapSound) {
		this.winMapSound = winMapSound;
	}


	public UUID getLoseMapSound() {
		return loseMapSound;
	}


	public void setLoseMapSound(UUID loseMapSound) {
		this.loseMapSound = loseMapSound;
	}


	public UUID getLevelUpSound() {
		return levelUpSound;
	}


	public void setLevelUpSound(UUID levelUpSound) {
		this.levelUpSound = levelUpSound;
	}

}
