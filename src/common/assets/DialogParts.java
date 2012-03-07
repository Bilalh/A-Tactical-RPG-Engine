package common.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("dialogParts")
public class DialogParts extends AbstractAssets<DialogPart> {

	public List<DialogPart> sortedValues(){
		List<DialogPart> l = new ArrayList(values());
		Collections.sort(l);
		return l;
	}
	
}
