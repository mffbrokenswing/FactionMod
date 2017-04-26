package factionmod.config;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import factionmod.utils.ServerUtils;

/**
 * This class contains the configuration relative to the faction chest.
 * 
 * @author BrokenSwing
 *
 */
public class ConfigFactionInventory {

	private static final ArrayList<Item>	ITEMS	= new ArrayList<Item>();

	public static boolean isItemValid(Item item) {
		return !ITEMS.contains(item);
	}

	/**
	 * Loads the configuration from a JsonArray.
	 * 
	 * @param list
	 *            The JsonArray
	 */
	public static void load(JsonArray list) {
		ServerUtils.getProfiler().startSection("inventory");

		IForgeRegistry<Item> registry = GameRegistry.findRegistry(Item.class);
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

}
