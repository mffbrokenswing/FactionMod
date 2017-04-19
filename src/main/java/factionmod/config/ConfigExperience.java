package factionmod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConfigExperience {

	public static int	killDragon;
	public static int	killWitherSkeleton;
	public static int	killWither;
	public static int	killEnemy;
	public static int	killWithArrowBonus;

	public static void loadFromJson(JsonObject element) {
		killDragon = getInt("kill_dragon", element, 2000);
		killWitherSkeleton = getInt("kill_wither_skeleton", element, 3);
		killWither = getInt("kill_wither", element, 400);
		killEnemy = getInt("kill_enemy", element, 7);
	}

	private static int getInt(String name, JsonObject element, int defaultValue) {
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
