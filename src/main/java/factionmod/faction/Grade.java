package factionmod.faction;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import factionmod.enums.EnumPermission;

/**
 * A grade of a faction member, a grade has permissions associated to it.
 * 
 * @author BrokenSwing
 *
 */
public class Grade {

	public static final Grade			OWNER	= new Grade("Owner", 0, EnumPermission.values());
	public static final Grade			MEMBER	= new Grade("Member", -1, new EnumPermission[0]);

	private String						name;
	private int							priority;
	private final List<EnumPermission>	permissions;

	public Grade(String name, int priority, EnumPermission[] permissions) {
		this.name = name;
		this.priority = priority;
		this.permissions = Lists.newArrayList(permissions);
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public List<EnumPermission> getPermissions() {
		return permissions;
	}

	public void addPermission(EnumPermission perm) {
		if (!this.permissions.contains(perm)) {
			this.permissions.add(perm);
		}
	}

	public boolean hasPermission(EnumPermission perm) {
		return this.permissions.contains(perm);
	}

	public void removePermission(EnumPermission perm) {
		this.permissions.remove(perm);
	}
	
	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("name", this.name);
		nbt.setInteger("priority", this.priority);
		NBTTagList list = new NBTTagList();
		for(EnumPermission perm : this.permissions) {
			list.appendTag(new NBTTagString(perm.name()));
		}
		nbt.setTag("perms", list);
		return nbt;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive(this.name));
		obj.add("priority", new JsonPrimitive(this.priority));
		JsonArray perms = new JsonArray();
		for(EnumPermission permission : permissions) {
			perms.add(new JsonPrimitive(permission.name()));
		}
		obj.add("perms", perms);
		return obj;
	}

	public static Grade fromJson(JsonObject obj) {
		String name = obj.get("name").getAsString();
		int priority = obj.get("priority").getAsInt();
		JsonArray perms = obj.get("perms").getAsJsonArray();
		EnumPermission[] list = new EnumPermission[perms.size()];
		for(int i = 0; i < perms.size(); i++) {
			String permName = perms.get(i).getAsString();
			list[i] = EnumPermission.valueOf(permName);
		}
		return new Grade(name, priority, list);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if ((obj instanceof Grade))
			return false;
		Grade g = (Grade)obj;
		return g.getName().equalsIgnoreCase(this.name);
	}

	public static boolean canAffect(Grade executor, Grade executed) {
		if (executor.getPriority() == executed.getPriority())
			return false;
		if (executed.getPriority() == -1)
			return true;
		return executor.getPriority() < executed.getPriority();
	}

}
