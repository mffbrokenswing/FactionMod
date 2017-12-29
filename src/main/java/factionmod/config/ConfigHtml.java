package factionmod.config;

import java.io.File;
import java.util.HashMap;

import factionmod.FactionMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHtml {

    private static final HashMap<String, String>  STRING_VALUES = new HashMap<>();
    private static final HashMap<String, Boolean> BOOL_VALUES   = new HashMap<>();
    private static final HashMap<String, Integer> INT_VALUES    = new HashMap<>();

    private static final String CAT = "html";

    public static void loadFromConfig(final Configuration cfg) {
        Property p;

        cfg.setCategoryComment(CAT, "This section concerns the generation of html files about factions");

        p = cfg.get(CAT, "enable_html_generation", false);
        p.setComment("Set it to true to enable the generation of html files containing informations about factions");
        BOOL_VALUES.put("enable_html_generation", p.getBoolean());

        p = cfg.get(CAT, "template_path", FactionMod.getConfigDir() + File.separator + "templates" + File.separator + "faction_informations.html.template");
        p.setComment("The path to the template of the html files. Please checkout the wiki to know how to create a template");
        STRING_VALUES.put("template_path", p.getString());

        if (BOOL_VALUES.get("enable_html_generation")) {
            final File file = new File(STRING_VALUES.get("template_path"));
            if (!file.exists())
                FactionMod.getLogger().warn("The specified template doesn't exist, can't generate any html file without template.");
        }

        p = cfg.get(CAT, "output_directory", FactionMod.getConfigDir() + File.separator + "output");
        p.setComment("The directory where html files will be generated");
        STRING_VALUES.put("output_directory", p.getString());

        p = cfg.get(CAT, "update_period", 200);
        p.setMinValue(1);
        p.setMaxValue(Integer.MAX_VALUE);
        p.setComment("The time between 2 updates of the html files (in ticks, there is 20 ticks in 1 second)");
        INT_VALUES.put("update_period", p.getInt());
    }

    /**
     * Returns the {@link String} linked to the specified key.
     *
     * @param key
     *            The key
     * @return the linked {@link String} or an empty {@link String} if the key
     *         doesn't exist
     */
    public static String getString(final String key) {
        return STRING_VALUES.containsKey(key) ? STRING_VALUES.get(key) : "";
    }

    /**
     * Returns the boolean linked to the specified key.
     *
     * @param key
     *            The key
     * @return the linked boolean or false if the key doesn't exist
     */
    public static boolean getBool(final String key) {
        return BOOL_VALUES.containsKey(key) ? BOOL_VALUES.get(key) : false;
    }

    /**
     * Returns the int linked to the specified key.
     *
     * @param key
     *            The key
     * @return the linked int or 0 if the key doesn't exist
     */
    public static int getInt(final String key) {
        return INT_VALUES.containsKey(key) ? INT_VALUES.get(key) : 0;
    }

}
