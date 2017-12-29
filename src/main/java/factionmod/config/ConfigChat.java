package factionmod.config;

import java.util.HashMap;

import factionmod.utils.ServerUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigChat {

    private static final HashMap<String, Boolean> BOOL_VALUES   = new HashMap<>();
    private static final HashMap<String, String>  STRING_VALUES = new HashMap<>();

    private static final String CAT = "chat";

    /**
     * Loads the general configuration using the Forge system.
     *
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(final Configuration config) {
        ServerUtils.getProfiler().startSection("chat");

        Property p;

        config.setCategoryComment(CAT, "This section concerns the chat system in the mod (not the translations)");

        p = config.get(CAT, "general_chat_format", "%1$s > %2$s");
        p.setComment("Permits to modify general chat format. %1$s is the player name, %2$s is the sended message");
        STRING_VALUES.put("general_chat_format", p.getString());

        p = config.get(CAT, "enable_faction_chat", true);
        p.setComment("Set it to true to allow faction's members to send messages to other members only");
        BOOL_VALUES.put("enable_faction_chat", p.getBoolean());

        p = config.get(CAT, "faction_chat_special_indicator", "!");
        p.setComment("If you enabled the faction chat, it's the characters to add before the message to send it in the faction chat. Example with default value '!' : !Hello my faction");
        STRING_VALUES.put("faction_chat_special_indicator", p.getString());

        p = config.get(CAT, "faction_chat_format", "%1$s > §l%2$s");
        p.setComment("Permits to modify faction chat format. %1$s is the player name, %2$s is the sended message");
        STRING_VALUES.put("faction_chat_format", p.getString());

        ServerUtils.getProfiler().endSection();
    }

    /**
     * Returns the String value linked to the specified key. Returns an empty String
     * by default.
     *
     * @param key
     *            The key
     * @return the String value
     */
    public static String getString(final String key) {
        return STRING_VALUES.containsKey(key) ? STRING_VALUES.get(key) : "";
    }

    /**
     * Returns the boolean value linked to the specified key. Returns false by
     * default.
     *
     * @param key
     *            The key
     * @return the boolean value
     */
    public static boolean getBool(final String key) {
        return BOOL_VALUES.containsKey(key) ? BOOL_VALUES.get(key) : false;
    }

}
