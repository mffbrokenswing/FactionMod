package factionmod.faction;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.command.utils.UUIDHelper;
import factionmod.enums.EnumPermission;

/**
 * Represents a member of a {@link Faction}.
 * 
 * @author BrokenSwing
 *
 */
public class Member {

	private final UUID	uuid;
	private Grade		grade;

	public Member(final UUID uuid, Grade grade) {
		this.uuid = uuid;
		this.grade = grade;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Grade getGrade() {
		return this.grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public void addPermission(final EnumPermission permission) {
		grade.addPermission(permission);
	}

	public void removePermission(final EnumPermission permission) {
		grade.removePermission(permission);
	}

	public boolean hasPermission(final EnumPermission permission) {
		return grade.hasPermission(permission);
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", UUIDHelper.getNameOf(this.uuid));
		nbt.setString("grade", this.grade.getName());
		return nbt;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("uuid", new JsonPrimitive(this.uuid.toString()));
		obj.add("grade", new JsonPrimitive(grade.getName()));
		return obj;
	}

	public static Member fromJson(JsonObject obj, Faction faction) {
		UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
		String gradeName = obj.get("grade").getAsString();
		Grade grade;
		if (Grade.OWNER.getName().equalsIgnoreCase(gradeName)) {
			grade = Grade.OWNER;
		} else if (Grade.MEMBER.getName().equalsIgnoreCase(gradeName)) {
			grade = Grade.MEMBER;
		} else {
			grade = faction.getGrade(gradeName);
		}
		return new Member(uuid, grade);
	}

}
