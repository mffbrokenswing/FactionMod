package factionmod.faction;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.enums.EnumRelationType;

public class RelationShip {

	private EnumRelationType	type;
	private String				faction1;
	private String				faction2;

	public RelationShip(EnumRelationType type, String faction1, String faction2) {
		this.type = type;
		this.faction1 = faction1;
		this.faction2 = faction2;
	}

	public EnumRelationType getType() {
		return type;
	}

	public String getFirstFaction() {
		return faction1;
	}

	public String getSecondFaction() {
		return faction2;
	}

	public RelationShip swapFactions() {
		return new RelationShip(this.type, this.faction2, this.faction1);
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("type", new JsonPrimitive(this.type.name()));
		obj.add("faction1", new JsonPrimitive(this.faction1));
		obj.add("faction1", new JsonPrimitive(this.faction2));
		return obj;
	}

	public static RelationShip fromJson(JsonObject obj) {
		EnumRelationType type = EnumRelationType.valueOf(obj.get("type").getAsString());
		return new RelationShip(type, obj.get("faction1").getAsString(), obj.get("faction2").getAsString());
	}

}
