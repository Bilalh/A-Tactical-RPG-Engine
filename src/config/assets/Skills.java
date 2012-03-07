package config.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.skills.ISkill;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("skills")
public class Skills extends AbstractAssets<ISkill>{

}
