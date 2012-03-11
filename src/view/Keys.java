package view;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * Shows what each key does
 * @author Bilal Hussain
 */
public class Keys extends JFrame {
	private static final long serialVersionUID = 1853159466655061L;

	KeyMapping[] keys = new KeyMapping[] {
			new KeyMapping("Arrow Keys", "Movement"),
			new KeyMapping("X", "Comfirm"),
			new KeyMapping("Z", "Cancel"),
			new KeyMapping("S", "Skip Dialog"),
			new KeyMapping("R", "Rotate Map"),
			new KeyMapping("M", "Toggle Music"),

			new KeyMapping("-", "Zoom Out"),
			new KeyMapping("+", "Zoom In"),

			new KeyMapping(",", "Decrease map pitch"),
			new KeyMapping(".", "Increase map pitch"),
			new KeyMapping("?", "Show this Key Mapping"),

	};

	KeyMapping[] waiting = new KeyMapping[] {
			new KeyMapping("A", "Shows a Unit's Attack Range"),
			new KeyMapping("C", "Shows a Unit's Movement Range"),
	};

	public Keys() {
		setContentPane(createMainPane());
		setSize(320, 400);
		setTitle("Key Mapping");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	private Font headings = new Font("Serif", Font.BOLD, 17);
	private Font extra = new Font("Serif", Font.ITALIC, 16);


	JPanel createMainPane() {
		JPanel p = new JPanel(new MigLayout("wrap 2"));
		p.add(labelf("Key", headings), new CC().gapAfter("17px"));
		p.add(labelf("Usage", headings), new CC().wrap("10px"));
		for (KeyMapping m : keys) {
			p.add(new JLabel(m.key), new CC().gapAfter("17px"));
			p.add(new JLabel(m.info), new CC().wrap("5px"));
		}

		p.add(labelf("While no unit is selected", extra), new CC().span(2).newline("20px"));
		for (KeyMapping m : waiting) {
			p.add(new JLabel(m.key), new CC().gapAfter("20px"));
			p.add(new JLabel(m.info), new CC().wrap("5px"));
		}

		return p;
	}

	public JLabel labelf(String text, Font f) {
		JLabel l = new JLabel(text);
		l.setFont(f);
		return l;
	}

	public static void main(String[] args) {
		new Keys().setVisible(true);
	}

	class KeyMapping {
		String key;
		String info;

		public KeyMapping(String key, String info) {
			this.key = key;
			this.info = info;
		}
	}

}
