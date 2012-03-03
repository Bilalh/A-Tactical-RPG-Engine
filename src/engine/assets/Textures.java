package engine.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.skills.ISkill;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("textures")
public class Textures extends AbstractAssets<ISkill>{

}
