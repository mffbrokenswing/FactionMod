package factionmod.manager.instanciation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.manager.IChunkManager;

/**
 * Used to instanciate an {@link IChunkManager} from the name of the
 * {@link Zone} an its arguments.
 * 
 * @author BrokenSwing
 *
 */
public class ZoneInstance {

	private final String	zoneName;
	private final String[]	args;

	public ZoneInstance(String name, String[] args) {
		this.zoneName = name;
		this.args = args;
	}

	public String getZoneName() {
		return zoneName;
	}

	public String[] getArgs() {
		return args;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive(zoneName));
		JsonArray array = new JsonArray();
		for(String s : args) {
			array.add(new JsonPrimitive(s));
		}
		obj.add("args", array);
		return obj;
	}

	public static ZoneInstance fromJson(JsonObject obj) {
		String name = obj.get("name").getAsString();
		JsonArray array = obj.get("args").getAsJsonArray();
		String[] args = new String[array.size()];
		for(int i = 0; i < array.size(); i++) {
			args[i] = array.get(i).getAsString();
		}
		return new ZoneInstance(name, args);
	}

}
