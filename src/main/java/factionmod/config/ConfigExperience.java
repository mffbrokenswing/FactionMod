package factionmod.config;

import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.FactionMod;
import factionmod.utils.ServerUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * It's the configuration of the experience earned when doing specific actions.
 *
 * @author BrokenSwing
 *
 */
public class ConfigExperience {

    private static final HashMap<String, Integer> EXP_VALUES    = new HashMap<>();
    private static final HashMap<String, String>  STRING_VALUES = new HashMap<>();

    /**
     * Loads all the values from a {@link JsonObject}.
     *
     * @param element
     *            The JsonObject
     */
    public static void loadFromJson(final JsonObject element) {
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
     * Loads the configuration of experience using the Forge configuration system.
     *
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(final Configuration config) {
        ServerUtils.getProfiler().startSection("experience");

        EXP_VALUES.clear();
        STRING_VALUES.clear();

        config.setCategoryComment(CAT, "This section concerns how factions earn experience and how things are calculated from experience");

        Property p;

        p = config.get(CAT, "kill_enemy", 7);
        p.setComment("Experience earned when a player kills a player of an other faction");
        EXP_VALUES.put("kill_enemy", p.getInt());

        final IForgeRegistry<EntityEntry> registry = ForgeRegistries.ENTITIES;

        config.getCategory(CAT).forEach((name, prop) -> {
            if (name.startsWith("kill_") && !name.equals("kill_enemy")) {
                final String value = name.substring(5).replaceFirst("_", ":");

                if (value.trim().isEmpty())
                    return;

                if (registry.containsKey(new ResourceLocation(value))) {
                    prop.setComment("Experience earned when killing " + value);
                    EXP_VALUES.put("kill_" + value, prop.getInt());
                    FactionMod.getLogger().debug("The amount of experience earned when killing " + value + " is set to " + prop.getInt());
                } else {
                    prop.setComment("Warning : " + value + " is not a valid entity");
                    FactionMod.getLogger().warn("The entity " + value + " doesn't exists. Remember to indicate the modid");
                }
            }
        });

        p = config.get(CAT, "experience_from_level", "100 + (%1$s * %1$s * LOG(%1$s)) / 8");
        p.setComment("The expression for the amount of experience needed to reach the level which is represented by %1$s");
        STRING_VALUES.put("experience_from_level", p.getString());

        p = config.get(CAT, "chunk_count_from_level", "1.3 * %1$s + 3");
        p.setComment("The maximum of chunk a faction can have at the level which is represented by %1$s");
        STRING_VALUES.put("chunk_count_from_level", p.getString());

        ServerUtils.getProfiler().endSection();
    }

    /**
     * Returns the amount of experience earned for the specified action.
     *
     * @param action
     *            The action
     * @return the amount of experience earned
     */
    public static int getExpFor(final String action) {
        return EXP_VALUES.containsKey(action) ? EXP_VALUES.get(action).intValue() : 0;
    }

    /**
     * Returns the expression with the specified name
     *
     * @param name
     *            The name of the expression
     * @return the expression
     */
    public static String getExpression(final String name) {
        return STRING_VALUES.containsKey(name) ? STRING_VALUES.get(name) : "";
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
    public static int getInt(final String name, final JsonObject element, final int defaultValue) {
        if (element.has(name)) {
            final JsonElement el = element.get(name);
            if (el.isJsonPrimitive()) {
                final JsonPrimitive prim = el.getAsJsonPrimitive();
                if (prim.isNumber())
                    return prim.getAsInt();
            }
        }
        return defaultValue;
    }

}
