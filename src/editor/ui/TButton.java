/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package editor.ui;

import java.awt.Insets;
import javax.swing.Action;
import javax.swing.JButton;

/**
 * Tiled button extends on JButton in that it allows for not accepting
 * any text from an attached action. It also doesn't display any margins.
 *
 * @version $Id$
 */
public class TButton extends JButton
{
    private static final long serialVersionUID = -2185047668490485707L;
	private boolean showText;

    public TButton() {
        setMargin(new Insets(0, 0, 0, 0));
    }

    public TButton(boolean showText) {
        this();
        this.showText = showText;
    }

    public TButton(Action action) {
        this(action, false);
    }

    public TButton(Action action, boolean showText) {
        this(showText);
        setAction(action);
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    @Override
	public void setText(String text) {
        if (showText) {
            super.setText(text);
        }
    }
}
