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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

/**
 * This class contains the configuration relative to the faction chest.
 * 
 * @author BrokenSwing
 *
 */
public class ConfigFactionInventory {

    private static final ArrayList<Item> ITEMS = new ArrayList<Item>();

    public static boolean isItemValid(Item item) {
        return !ITEMS.contains(item);
    }

    /**
     * Loads the configuration from a JsonArray.
     * 
     * @param list
     *            The JsonArray
     */
    public static void loadFromJson(JsonArray list) {
        ServerUtils.getProfiler().startSection("inventory");
        
        ITEMS.clear();

        IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;
        for(int i = 0; i < list.size(); i++) {
            JsonElement el = list.get(i);
            if (el.isJsonPrimitive()) {
                JsonPrimitive prim = el.getAsJsonPrimitive();
                if (prim.isString()) {
                    String name = prim.getAsString();
                    String[] split = name.split(":");
                    if (split.length == 2) {
                        ResourceLocation location = new ResourceLocation(split[0], split[1]);
                        if (registry.containsKey(location)) {
                            Item item = registry.getValue(location);
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
     * Loads the configuration for the faction chets using the Forge
     * configuration system.
     * 
     * @param config
     *            The configuration
     */
    public static void loadFromConfig(Configuration config) {
        ServerUtils.getProfiler().startSection("inventory");
        
        ITEMS.clear();

        IForgeRegistry<Item> registry = ForgeRegistries.ITEMS;

        ConfigCategory c = config.getCategory(CAT + Configuration.CATEGORY_SPLITTER + "unvalid-items");
        c.forEach((key, prop) -> {
            if (prop.isList()) {
                String[] list = prop.getStringList();
                for(String name : list) {
                    String[] split = name.split(":");
                    if (split.length == 2) {
                        ResourceLocation location = new ResourceLocation(split[0], split[1]);
                        if (registry.containsKey(location)) {
                            Item item = registry.getValue(location);
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
