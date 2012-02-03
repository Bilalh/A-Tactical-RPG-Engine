import java.util.Arrays;

import javax.swing.UIManager;
import com.sun.xml.internal.ws.org.objectweb.asm.Label;


/**
 * @author Bilal Hussain
 */
public class A {

	public static void main(String[] args) {
		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
		System.out.println(Arrays.toString(lafInfo));
	}
	
}
