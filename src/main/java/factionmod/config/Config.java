package factionmod.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import factionmod.FactionMod;
import factionmod.faction.Faction;
import factionmod.faction.Member;
import factionmod.faction.RelationShip;
import factionmod.handler.EventHandlerChunk;
import factionmod.handler.EventHandlerFaction;
import factionmod.handler.EventHandlerRelation;
import factionmod.manager.IChunkManager;
import factionmod.manager.instanciation.Zone;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;

/**
 * The config of the mod, it loads and saves the state of the mod.
 * 
 * @author BrokenSwing
 *
 */
public class Config {

	private static int	immunityLevel	= 5;

	/**
	 * A faction can't be attacked until it reached the immunity level
	 * 
	 * @return the immunity level
	 */
	public static int getImmunityLevel() {
		return immunityLevel;
	}

	/**
	 * Loads a list of {@link Zone} from a file.
	 * 
	 * @param fileName
	 *            The name of the file
	 */
	public static final void loadZones(String fileName) {
		Zone z;
		JsonElement file = getFile(fileName);
		if (file != null) {
			JsonArray zones = file.getAsJsonArray();
			for(int i = 0; i < zones.size(); i++) {
				JsonObject zone = zones.get(i).getAsJsonObject();
				String name = zone.get("name").getAsString();
				String clazz = zone.get("class").getAsString();
				JsonElement element = zone.get("instance");
				try {
					if (element != null) {
						String instance = element.getAsString();
						z = new Zone(name, clazz, instance);
					} else {
						z = new Zone(name, clazz);
					}
					EventHandlerChunk.registerZone(z);
				} catch (Exception e) {
					FactionMod.getLogger().error("Couldn't load zone " + name);
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Saves the chunk managers to a file.
	 */
	public static void saveChunkManagers() {
		JsonArray root = new JsonArray();
		for(Entry<DimensionalPosition, ZoneInstance> entry : EventHandlerChunk.getZonesInstances().entrySet()) {
			JsonObject obj = new JsonObject();
			obj.add("key", entry.getKey().toJson());
			obj.add("value", entry.getValue().toJson());
			root.add(obj);
		}
		writeFile("managers.json", root.toString());
	}

	/**
	 * Loads chunk managers from a file.
	 */
	public static void loadChunkManagers() {
		HashMap<DimensionalPosition, ZoneInstance> map = new HashMap<DimensionalPosition, ZoneInstance>();

		JsonElement element = getFile("managers.json");
		if (element != null) {

			JsonArray root = element.getAsJsonArray();

			for(int i = 0; i < root.size(); i++) {
				JsonObject obj = root.get(i).getAsJsonObject();
				DimensionalPosition pos = DimensionalPosition.fromJson(obj.get("key").getAsJsonObject());
				ZoneInstance instance = ZoneInstance.fromJson(obj.get("value").getAsJsonObject());
				map.put(pos, instance);
			}

			for(Entry<DimensionalPosition, ZoneInstance> entry : map.entrySet()) {
				DimensionalPosition pos = entry.getKey();
				ZoneInstance instance = entry.getValue();
				final Zone zone = EventHandlerChunk.getZone(instance.getZoneName());
				if (zone != null) {
					IChunkManager manager;
					if (zone.isStandAlone()) {
						manager = zone.getInstance();
					} else {
						try {
							manager = zone.createInstance(instance.getArgs());
							EventHandlerChunk.registerChunkManager(manager, pos, instance, false);
						} catch (Exception e) {
							String listArgs = "";
							for(String str : instance.getArgs()) {
								listArgs += str + " ";
							}
							FactionMod.getLogger().warn("Cannot instanciate the zone " + zone.getName() + " with args : " + listArgs);
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Saves the factions to a file.
	 */
	public static void saveFactions() {
		JsonArray root = new JsonArray();
		for(Entry<String, Faction> entry : EventHandlerFaction.getFactions().entrySet()) {
			root.add(entry.getValue().toJson());
		}
		writeFile("factions.json", root.toString());
	}

	/**
	 * Loads the differents factions from the config file.
	 */
	public static void loadFactions() {
		JsonElement file = getFile("factions.json");
		if (file != null) {
			JsonArray root = file.getAsJsonArray();
			for(int i = 0; i < root.size(); i++) {
				JsonObject entry = root.get(i).getAsJsonObject();
				Faction faction = Faction.fromJson(entry);
				EventHandlerFaction.addFaction(faction);
				for(Member m : faction.getMembers()) {
					EventHandlerFaction.addUserToFaction(faction, m.getUUID());
				}
			}
		}
	}

	/**
	 * Saves the relations between the factions.
	 */
	public static void saveRelations() {
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		for(RelationShip relation : EventHandlerRelation.getRelations()) {
			array.add(relation.toJson());
		}
		object.add("relations", array);
		JsonArray arr = new JsonArray();
		for(RelationShip relation : EventHandlerRelation.getPendingRelations()) {
			arr.add(relation.toJson());
		}
		object.add("pending-relations", arr);
		writeFile("relations.json", object.toString());
	}

	/**
	 * Loads the differents relations between the factions.
	 */
	public static void loadRelations() {
		JsonElement element = getFile("relations.json");
		if (element != null) {
			if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				JsonArray array = obj.get("relations").getAsJsonArray();
				for(int i = 0; i < array.size(); i++) {
					EventHandlerRelation.setRelation(RelationShip.fromJson(array.get(i).getAsJsonObject()), false);
				}
				JsonArray arr = obj.get("pending-relations").getAsJsonArray();
				for(int i = 0; i < arr.size(); i++) {
					EventHandlerRelation.setRelation(RelationShip.fromJson(arr.get(i).getAsJsonObject()), true);
				}
			}
		}
	}

	/**
	 * Loads all the parameters from the configuration file.
	 */
	public static void loadConfigFile() {
		JsonElement element = getFile("configuration.json");
		JsonObject expObj = new JsonObject();
		if (element != null && element.isJsonObject()) {
			JsonElement el;
			JsonObject root = element.getAsJsonObject();
			if (root.has("immunity_level")) {
				el = root.get("immunity_level");
				if (el.isJsonPrimitive()) {
					JsonPrimitive prim = el.getAsJsonPrimitive();
					if (prim.isNumber()) {
						immunityLevel = prim.getAsInt();
					}
				}
			}
			if (root.has("exp")) {
				el = root.get("exp");
				if (el.isJsonObject()) {
					expObj = el.getAsJsonObject();
				}
			}
		}
		ConfigExperience.loadFromJson(expObj);
	}

	/**
	 * Loads a file from the config directory. Will return null if the file
	 * doesn't exist.
	 * 
	 * @param fileName
	 *            The name of the file
	 * @return a {@link JsonElement} or null if the file doesn't exist
	 */
	private static JsonElement getFile(String fileName) {
		File file = new File(FactionMod.getConfigDir() + "/" + fileName);
		if (file.exists()) {
			try {
				return new JsonParser().parse(new FileReader(file));
			} catch (Exception e) {
				FactionMod.getLogger().error("Error on loading file " + fileName);
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Writes text in a file.
	 * 
	 * @param fileName
	 *            The name of the file
	 * @param contents
	 *            The text to write
	 */
	private static void writeFile(String fileName, String contents) {
		File file = new File(FactionMod.getConfigDir() + "/" + fileName);
		try {
			file.createNewFile();

			FileWriter writer = new FileWriter(file);
			writer.write(contents);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			FactionMod.getLogger().error("Error on writing file " + fileName);
			e.printStackTrace();
		}
	}

	/**
	 * Creates the directory containing the configuration files of the mod.
	 * Creates the files zones.json if it doesn't exist.
	 */
	public static void initDirectory() {
		File dir = new File(FactionMod.getConfigDir());
		if (!dir.exists()) {
			dir.mkdirs();
			File file = new File(FactionMod.getConfigDir() + "/zones.json");
			if (!file.exists()) {
				JsonArray root = new JsonArray();
				JsonObject safeZone = new JsonObject();
				safeZone.add("name", new JsonPrimitive("safe"));
				safeZone.add("class", new JsonPrimitive("factionmod.manager.ManagerSafeZone"));
				safeZone.add("instance", new JsonPrimitive("DEFAULT"));

				JsonObject factionZone = new JsonObject();
				factionZone.add("name", new JsonPrimitive("faction"));
				factionZone.add("class", new JsonPrimitive("factionmod.manager.ManagerFaction"));

				JsonObject warZone = new JsonObject();
				warZone.add("name", new JsonPrimitive("war"));
				warZone.add("class", new JsonPrimitive("factionmod.manager.ManagerWarZone"));
				warZone.add("instance", new JsonPrimitive("DEFAULT"));

				root.add(safeZone);
				root.add(warZone);
				root.add(factionZone);

				writeFile("zones.json", root.toString());
			}
		}
	}

}
