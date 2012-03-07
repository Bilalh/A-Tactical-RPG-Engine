package common.assets;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * A subclass is used since the generic type is lost due to type erasure.
 * @author Bilal Hussain
 */
@XStreamAlias("musics")
public class Musics extends AbstractAssets<MusicData> {

	
}
