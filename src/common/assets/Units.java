package common.assets;

import java.util.Iterator;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import engine.unit.IMutableUnit;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("units")
public class Units extends AbstractAssets<IMutableUnit> {
}
