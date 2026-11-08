package de.nicknames;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Speichert Spielername (klein geschrieben) -> Spitzname. */
public final class Nicknames {
    private static final Map<String, String> MAP = new TreeMap<>();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nicknames.txt");

    private Nicknames() {}

    public static String get(String playerName) {
        return MAP.get(playerName.toLowerCase());
    }

    public static void set(String playerName, String nick) {
        MAP.put(playerName.toLowerCase(), nick);
        save();
    }

    public static boolean remove(String playerName) {
        boolean had = MAP.remove(playerName.toLowerCase()) != null;
        if (had) save();
        return had;
    }

    public static Map<String, String> all() {
        return MAP;
    }

    public static void load() {
        MAP.clear();
        try {
            if (!Files.exists(FILE)) return;
            for (String line : Files.readAllLines(FILE)) {
                int i = line.indexOf('=');
                if (i > 0) MAP.put(line.substring(0, i), line.substring(i + 1));
            }
        } catch (IOException ignored) {}
    }

    private static void save() {
        List<String> lines = new ArrayList<>();
        MAP.forEach((k, v) -> lines.add(k + "=" + v));
        try {
            Files.createDirectories(FILE.getParent());
            Files.write(FILE, lines);
        } catch (IOException ignored) {}
    }
}
