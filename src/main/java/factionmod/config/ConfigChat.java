package factionmod.config;

import java.util.HashMap;
import java.util.function.BiFunction;

import factionmod.chat.ChatChannel;
import factionmod.chat.ChatManager;
import factionmod.chat.Filters;
import factionmod.utils.ServerUtils;
import net.minecraft.entity.player.EntityPlayerMP;
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

        config.setCategoryComment(CAT, "This section concerns the chat system in the mod (not the translations)");
        
        ChatManager.instance().clearChannels();
        
        config.getCategory(CAT).getChildren().forEach(category -> {
            Property prop = category.get("prefix");
            if(prop == null) return;
            String prefix = prop.getString();
            
            prop = category.get("format");
            if(prop == null) return;
            String format = prop.getString();
            
            prop = category.get("filter");
            if(prop == null) return;
            prop.setComment("%1$s is the username of the player sending the message and %2$s is the sended message");
            String predicate = prop.getString();
            
            BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> bifunc = Filters.instance().parse(predicate);
            if(bifunc == null) return;
            
            ChatManager.instance().registerChannel(new ChatChannel(prefix, format, bifunc));
        });

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
