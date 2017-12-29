package factionmod.faction;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import factionmod.enums.EnumPermission;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * A {@link Grade} of a {@link Faction} {@link Member}, a {@link Grade} has
 * {@link EnumPermission}s associated to it.
 *
 * @author BrokenSwing
 *
 */
public class Grade implements INBTSerializable<NBTTagCompound>, Comparable<Grade> {

    public static final Grade OWNER  = new Grade("Owner", 0, EnumPermission.values());
    public static final Grade MEMBER = new Grade("Member", -1, new EnumPermission[0]);

    private String               name;
    private int                  priority;
    private List<EnumPermission> permissions;

    public Grade(final NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    public Grade(final String name, final int priority, final EnumPermission[] permissions) {
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

    public void addPermission(final EnumPermission perm) {
        if (!this.permissions.contains(perm))
            this.permissions.add(perm);
    }

    public boolean hasPermission(final EnumPermission perm) {
        return this.permissions.contains(perm);
    }

    public void removePermission(final EnumPermission perm) {
        this.permissions.remove(perm);
    }

    public NBTTagCompound toNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.name);
        nbt.setInteger("priority", this.priority);
        final NBTTagList list = new NBTTagList();
        for (final EnumPermission perm : this.permissions)
            list.appendTag(new NBTTagString(perm.name()));
        nbt.setTag("perms", list);
        return nbt;
    }

    public String getPermissionsAsString() {
        return Joiner.on(" ").join(this.permissions);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.name);
        nbt.setInteger("priority", this.priority);

        final NBTTagList permissionsList = new NBTTagList();
        for (final EnumPermission permission : this.permissions)
            permissionsList.appendTag(new NBTTagString(permission.name()));
        nbt.setTag("permissions", permissionsList);

        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        this.name = nbt.getString("name");
        this.priority = nbt.getInteger("priority");

        this.permissions = new ArrayList<>();
        final NBTTagList permissionsList = nbt.getTagList("permissions", NBT.TAG_STRING);
        for (int i = 0; i < permissionsList.tagCount(); i++)
            try {
                this.permissions.add(EnumPermission.valueOf(permissionsList.getStringTagAt(i)));
            } catch (final IllegalArgumentException e) {}
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Grade other = (Grade) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public static boolean canAffect(final Grade executor, final Grade executed) {
        if (executor.getPriority() == executed.getPriority())
            return false;
        if (executed.getPriority() == Grade.MEMBER.getPriority())
            return true;
        return executor.getPriority() < executed.getPriority();
    }

    @Override
    public int compareTo(final Grade g) {
        return g.priority > this.priority ? -1 : this.priority > g.priority ? 1 : this.name.compareTo(g.getName());
    }

}
