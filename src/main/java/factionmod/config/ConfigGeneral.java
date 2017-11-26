package factionmod.config;

import java.util.HashMap;

import com.google.gson.JsonObject;

import factionmod.utils.ServerUtils;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigGeneral {

    private static final HashMap<String, Integer> INT_VALUES  = new HashMap<>();
    private static final HashMap<String, Boolean> BOOL_VALUES = new HashMap<>();

    private static final String                   CAT         = Configuration.CATEGORY_GENERAL;

    /**
     * Loads the general configuration using the Forge system.
     * 
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(Configuration config) {
        ServerUtils.getProfiler().startSection("general");

        Property p;

        p = config.get(CAT, "immunity_level", 5);
        p.setComment("If the level of a faction is equals to or lower than this level, nobody can take famages in their claim.");
        INT_VALUES.put("immunity_level", p.getInt());

        p = config.get(CAT, "faction_description_max_length", 50);
        p.setComment("The maximum length for the description of a faction. If the description is too long, it will be truncated.");
        INT_VALUES.put("faction_description_max_length", p.getInt());

        p = config.get(CAT, "faction_name_max_length", 15);
        p.setComment("The maximum length for the name of a faction. If the name is too long, the faction won't be created.");
        INT_VALUES.put("faction_name_max_length", p.getInt());

        p = config.get(CAT, "max_faction_damages", 15);
        p.setComment("The maximum damages a faction can have.");
        INT_VALUES.put("max_faction_damages", p.getInt());

        p = config.get(CAT, "damages_needed_to_counter_claim", 5);
        p.setComment("How much a faction needs to be damaged to allow others players to unclaim their chunks.");
        INT_VALUES.put("damages_needed_to_counter_claim", p.getInt());

        p = config.get(CAT, "teleportation_delay", 10);
        p.setComment("The time the player has to wait before being teleported.");
        INT_VALUES.put("teleportation_delay", p.getInt());
        
        p = config.get(CAT, "disable_friendly_fire", true);
        p.setComment("Set it to false if you want players of the same faction to be able to fight");
        BOOL_VALUES.put("disable_friendly_fire", p.getBoolean());

        ServerUtils.getProfiler().endSection();
    }

    /**
     * Returns the integer value linked to the specified key. Returns 0 by
     * default.
     * 
     * @param key
     *            The key
     * @return the integer value
     */
    public static int getInt(String key) {
        return INT_VALUES.containsKey(key) ? INT_VALUES.get(key).intValue() : 0;
    }

    /**
     * Returns the boolean value linked to the specified key. Returns false by
     * default.
     * 
     * @param key
     *            The key
     * @return the boolean value
     */
    public static boolean getBool(String key) {
        return BOOL_VALUES.containsKey(key) ? BOOL_VALUES.get(key) : false;
    }

    /**
     * Loads the general configuration from a JsonObject.
     * 
     * @param obj
     *            The JSON object
     */
    public static void loadFromJson(JsonObject obj) {
        ServerUtils.getProfiler().startSection("general");

        INT_VALUES.put("immunity_level", ConfigExperience.getInt("immunity_level", obj, 5));
        INT_VALUES.put("faction_description_max_length", ConfigExperience.getInt("factionDescriptionMaxLength", obj, 50));
        INT_VALUES.put("faction_name_max_length", ConfigExperience.getInt("factionNameMaxLength", obj, 15));
        INT_VALUES.put("max_faction_damages", ConfigExperience.getInt("maxFactionDamages", obj, 15));
        INT_VALUES.put("damages_needed_to_counter_claim", ConfigExperience.getInt("damagesNeededToCounterClaim", obj, 5));
        INT_VALUES.put("teleportation_delay", ConfigExperience.getInt("teleportationDelay", obj, 10));

        ServerUtils.getProfiler().endSection();
    }

}
