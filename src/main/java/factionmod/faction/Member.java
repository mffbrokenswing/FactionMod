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
	private int			experience;

	public Member(final UUID uuid, Grade grade) {
		this.uuid = uuid;
		this.grade = grade;
		this.experience = 0;
	}

	/**
	 * Returns the UUID of the member.
	 * 
	 * @return an UUID
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Returns the {@link Grade} of the member.
	 * 
	 * @return a grade
	 */
	public Grade getGrade() {
		return this.grade;
	}

	/**
	 * Changes the {@link Grade} of the member.
	 * 
	 * @param grade
	 *            The new grade
	 */
	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	/**
	 * Adds experience to the member. The experience of a member represents the
	 * total of the experience he made won to his faction.
	 * 
	 * @param experience
	 *            The amount of experience to add
	 */
	public void addExperience(int experience) {
		this.experience += experience;
	}

	/**
	 * Returns the amount of experience the member made won to his faction.
	 * 
	 * @return the amount of experience
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * Indicates if the member has the specified permission.
	 * 
	 * @param permission
	 *            The permission to test
	 * @return true if the member has the permission
	 */
	public boolean hasPermission(final EnumPermission permission) {
		return grade.hasPermission(permission);
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", UUIDHelper.getNameOf(this.uuid));
		nbt.setString("grade", this.grade.getName());
		nbt.setInteger("experience", this.experience);
		return nbt;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("uuid", new JsonPrimitive(this.uuid.toString()));
		obj.add("grade", new JsonPrimitive(grade.getName()));
		obj.add("experience", new JsonPrimitive(this.experience));
		return obj;
	}

	public static Member fromJson(JsonObject obj, Faction faction) {
		UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
		String gradeName = obj.get("grade").getAsString();
		int exp = obj.get("experience").getAsInt();
		Grade grade;
		if (Grade.OWNER.getName().equalsIgnoreCase(gradeName)) {
			grade = Grade.OWNER;
		} else if (Grade.MEMBER.getName().equalsIgnoreCase(gradeName)) {
			grade = Grade.MEMBER;
		} else {
			grade = faction.getGrade(gradeName);
		}
		Member member = new Member(uuid, grade);
		member.addExperience(exp);
		return member;
	}

}
