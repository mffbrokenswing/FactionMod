package factionmod.config;

import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.utils.ServerUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * It's the configuration of the experience earned when doing specific actions.
 * 
 * @author BrokenSwing
 *
 */
public class ConfigExperience {

    private static final HashMap<String, Integer> EXP_VALUES = new HashMap<>();

    /**
     * Loads all the values from a {@link JsonObject}.
     * 
     * @param element
     *            The JsonObject
     */
    public static void loadFromJson(JsonObject element) {
        ServerUtils.getProfiler().startSection("experience");
        
        EXP_VALUES.clear();

        EXP_VALUES.put("kill_dragon", getInt("kill_dragon", element, 2000));
        EXP_VALUES.put("kill_wither_skeleton", getInt("kill_wither_skeleton", element, 3));
        EXP_VALUES.put("kill_wither", getInt("kill_wither", element, 400));
        EXP_VALUES.put("kill_enemy", getInt("kill_enemy", element, 7));

        ServerUtils.getProfiler().endSection();
    }

    private static final String CAT = "experience";

    /**
     * Loads the configuration of experience using the Forge configuration
     * system.
     * 
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(Configuration config) {
        ServerUtils.getProfiler().startSection("experience");
        
        EXP_VALUES.clear();

        Property p;

        p = config.get(CAT, "kill_dragon", 2000);
        p.setComment("Experience earned when a player kills the Ender Dragon");
        EXP_VALUES.put("kill_dragon", p.getInt());

        p = config.get(CAT, "kill_wither_skeleton", 3);
        p.setComment("Experience earned when a player kills a Wither Skeleton");
        EXP_VALUES.put("kill_wither_skeleton", p.getInt());

        p = config.get(CAT, "kill_wither", 400);
        p.setComment("Experience earned when a player kills a Wither Boss");
        EXP_VALUES.put("kill_wither", p.getInt());

        p = config.get(CAT, "kill_enemy", 7);
        p.setComment("Experience earned when a player kills a player of an other faction");
        EXP_VALUES.put("kill_enemy", p.getInt());

        ServerUtils.getProfiler().endSection();
    }

    /**
     * Returns the amount of experience earned for the specified action.
     * 
     * @param action
     *            The action
     * @return the amount of experience earned
     */
    public static int getExpFor(String action) {
        return EXP_VALUES.containsKey(action) ? EXP_VALUES.get(action).intValue() : 0;
    }

    /**
     * Reads the integer with the speficied name in the given JsonObject. If it
     * doesn't exist, it returns the default value.
     * 
     * @param name
     *            The name of the value
     * @param element
     *            The JsonObject
     * @param defaultValue
     *            The default value
     * @return the readed value or the default value
     */
    public static int getInt(String name, JsonObject element, int defaultValue) {
        if (element.has(name)) {
            JsonElement el = element.get(name);
            if (el.isJsonPrimitive()) {
                JsonPrimitive prim = el.getAsJsonPrimitive();
                if (prim.isNumber()) {
                    return prim.getAsInt();
                }
            }
        }
        return defaultValue;
    }

}
