package editor.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

public final class Prefs
{
    private static final Preferences prefs = Preferences.userRoot().node("tactical/editor");

    private Prefs() {
    }


    public static Preferences getNode(String pathName) {
        return prefs.node(pathName);
        
    }

    public static Preferences root() {
        return prefs;
    }
}
