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

	
	/** @category Generated */
	public UUID getBackground() {
		return backgroundId;
	}

	/** @category Generated */
	public void setBackground(UUID backgroundId) {
		this.backgroundId = backgroundId;
	}

	/** @category Generated */
	public UUID getAttackSound() {
		return attackSound;
	}

	/** @category Generated */
	public void setAttackSound(UUID attackSound) {
		this.attackSound = attackSound;
	}

	/** @category Generated */
	public UUID getDefeatUnitSound() {
		return defeatUnitSound;
	}

	/** @category Generated */
	public void setDefeatUnitSound(UUID defeatUnitSound) {
		this.defeatUnitSound = defeatUnitSound;
	}

	/** @category Generated */
	public UUID getLoseUnitSound() {
		return loseUnitSound;
	}

	/** @category Generated */
	public void setLoseUnitSound(UUID loseUnitSound) {
		this.loseUnitSound = loseUnitSound;
	}

	/** @category Generated */
	public UUID getWinMapSound() {
		return winMapSound;
	}

	/** @category Generated */
	public void setWinMapSound(UUID winMapSound) {
		this.winMapSound = winMapSound;
	}

	/** @category Generated */
	public UUID getLoseMapSound() {
		return loseMapSound;
	}

	/** @category Generated */
	public void setLoseMapSound(UUID loseMapSound) {
		this.loseMapSound = loseMapSound;
	}

	/** @category Generated */
	public UUID getLevelUpSound() {
		return levelUpSound;
	}

	/** @category Generated */
	public void setLevelUpSound(UUID levelUpSound) {
		this.levelUpSound = levelUpSound;
	}

}
