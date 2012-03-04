package common.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.skills.ISkill;
import engine.unit.SpriteSheetData;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("unitsImages")
public class SpriteSheetsData extends AbstractAssets<SpriteSheetData>{

}
