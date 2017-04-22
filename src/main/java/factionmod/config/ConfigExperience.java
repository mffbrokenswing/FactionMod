package factionmod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * It's the configuration of the experience earned when doing specific actions.
 * 
 * @author BrokenSwing
 *
 */
public class ConfigExperience {

	public static int	killDragon;
	public static int	killWitherSkeleton;
	public static int	killWither;
	public static int	killEnemy;
	public static int	killWithArrowBonus;

	/**
	 * Loads all the values from a {@link JsonObject}.
	 * 
	 * @param element
	 *            The JsonObject
	 */
	public static void loadFromJson(JsonObject element) {
		killDragon = getInt("kill_dragon", element, 2000);
		killWitherSkeleton = getInt("kill_wither_skeleton", element, 3);
		killWither = getInt("kill_wither", element, 400);
		killEnemy = getInt("kill_enemy", element, 7);
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
