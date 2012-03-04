package engine.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.xml.SavedMap;

import engine.skills.ISkill;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("maps")
public class Maps extends AbstractAssets<SavedMap> {

}
