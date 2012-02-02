package editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

public final class Config
{
    public static final int RECENT_FILE_COUNT = 8;

    private static final Preferences prefs = Preferences.userRoot().node("tactical/editor");

    private Config() {
    }


    public static Preferences getNode(String pathName) {
        return prefs.node(pathName);
    }

    public static Preferences root() {
        return prefs;
    }
}