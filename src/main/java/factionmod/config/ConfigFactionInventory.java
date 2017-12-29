package factionmod.config;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import factionmod.utils.ServerUtils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This class contains the configuration relative to the faction chest.
 *
 * @author BrokenSwing
 *
 */
public class ConfigFactionInventory {

    private static final ArrayList<Item> ITEMS   = new ArrayList<>();
    private static boolean               enabled = true;

    public static boolean isItemValid(final Item item) {
        return !ITEMS.contains(item);
    }

    public static boolean isFactionChestEnabled() {
        return enabled;
    }

    /**
     * Loads the configuration from a JsonArray.
     *
     * @param list
     *            The JsonArray
     */
    public static void loadFromJson(final JsonArray list) {
        ServerUtils.getProfiler().startSection("inventory");

        ITEMS.clear();

        final IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;
        for (int i = 0; i < list.size(); i++) {
            final JsonElement el = list.get(i);
            if (el.isJsonPrimitive()) {
                final JsonPrimitive prim = el.getAsJsonPrimitive();
                if (prim.isString()) {
                    final String name = prim.getAsString();
                    final String[] split = name.split(":");
                    if (split.length == 2) {
                        final ResourceLocation location = new ResourceLocation(split[0], split[1]);
                        if (registry.containsKey(location)) {
                            final Item item = registry.getValue(location);
                            ITEMS.add(item);
                        }
                    }
                }
            }
        }

        ServerUtils.getProfiler().endSection();
    }

    private static final String CAT = "faction-chests";

    /**
     * Loads the configuration for the faction chests using the Forge configuration
     * system.
     *
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(final Configuration config) {
        ServerUtils.getProfiler().startSection("inventory");

        ITEMS.clear();

        config.setCategoryComment(CAT, "This section concerns the faction chest, here you can define which items aren't allowed in the chest or disable faction chest.");

        final Property p = config.get(CAT, "enabled_faction_chest", true);
        p.setComment("Set it to false to disable faction chest");
        enabled = p.getBoolean();

        final IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;

        final ConfigCategory c = config.getCategory(CAT + Configuration.CATEGORY_SPLITTER + "unvalid-items");
        c.forEach((key, prop) -> {
            if (prop.isList()) {
                final String[] list = prop.getStringList();
                for (final String name : list) {
                    final String[] split = name.split(":");
                    if (split.length == 2) {
                        final ResourceLocation location = new ResourceLocation(split[0], split[1]);
                        if (registry.containsKey(location)) {
                            final Item item = registry.getValue(location);
                            if (!ITEMS.contains(item))
                                ITEMS.add(item);
                        }
                    }
                }
            }
        });

        ServerUtils.getProfiler().endSection();
    }

}
