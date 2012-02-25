package editor;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import common.enums.Orientation;
import config.Config;

import editor.map.others.AbstactMapEditor;

/**
 * @author Bilal Hussain
 */
public class WeaponsEditor extends AbstactMapEditor {
	private static final long serialVersionUID = -7965140392208111973L;

	// Infopanel controls
	private JLabel infoLocation;
	private JTextField infoType;
	private JComboBox infoOrientation;
	private JSpinner infoHeight;

	public WeaponsEditor() {
		super("Weapons Editor", "Weapons", 10, 10);
	}

	@Override
	protected JPanel createInfoPanel() {
		JPanel p = new JPanel(new MigLayout("", "[right]"));

		infoOrientation = new JComboBox(Orientation.values());
		infoHeight = new JSpinner(new SpinnerNumberModel(1, 0, 20, 1));

		p.add(new JLabel("General"), new CC().split().spanX().gapTop("4"));
		p.add(new JSeparator(), new CC().growX().wrap().gapTop("4"));

		p.add(new JLabel("Location:"), "gap 4");
		p.add((infoLocation = new JLabel("        ")), "span, growx");

		p.add(new JLabel("Type:"), "gap 4");
		p.add((infoType = new JTextField(15)), "span, growx");

		infoOrientation.setEditable(false);
		p.add(new JLabel("Orientation:"), "gap 4");
		infoOrientation.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO
			}
		});
		p.add(infoOrientation, "span, growx");

		p.add(new JLabel("Height:"), "gap 4");
		infoHeight.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO
			}
		});
		p.add(infoHeight, "alignx leading, span, wrap");

		return p;
	}

	public static void main(String[] args) {
		Config.loadLoggingProperties();
		new WeaponsEditor().setVisible(true);
	}

}
