package factionmod.faction;

import java.util.UUID;

import factionmod.command.utils.UUIDHelper;
import factionmod.enums.EnumPermission;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents a member of a {@link Faction}.
 *
 * @author BrokenSwing
 *
 */
public class Member implements Comparable<Member> {

    private UUID  uuid;
    private Grade grade;
    private int   experience;

    public Member(final UUID uuid, final Grade grade) {
        this.uuid = uuid;
        this.grade = grade;
        this.experience = 0;
    }

    public Member(final NBTTagCompound nbt, final Faction faction) {
        this.deserializeNBT(nbt, faction);
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
    public void setGrade(final Grade grade) {
        this.grade = grade;
    }

    /**
     * Adds experience to the member. The experience of a member represents the
     * total of the experience he made won to his faction.
     *
     * @param experience
     *            The amount of experience to add
     */
    public void addExperience(final int experience) {
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

    /**
     * Serializes the member in a {@link NBTTagCompound}.
     *
     * @return the {@link NBTTagCompound}
     */
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("uuid", this.uuid);
        nbt.setString("grade", this.grade.getName());
        nbt.setInteger("experience", this.experience);
        return nbt;
    }

    /**
     * Deserializes the member from the {@link NBTTagCompound}.
     *
     * @param nbt
     *            The {@link NBTTagCompound}
     * @param faction
     *            The faction of the member
     */
    public void deserializeNBT(final NBTTagCompound nbt, final Faction faction) {
        this.uuid = nbt.getUniqueId("uuid");
        final String gradeName = nbt.getString("grade");
        if (Grade.OWNER.getName().equalsIgnoreCase(gradeName))
            this.grade = Grade.OWNER;
        else if (Grade.MEMBER.getName().equalsIgnoreCase(gradeName))
            this.grade = Grade.MEMBER;
        else {
            this.grade = faction.getGrade(gradeName);
            if (this.grade == null)
                this.grade = Grade.MEMBER;
        }
    }

    @Override
    public int compareTo(final Member m) {
        return this.getGrade().compareTo(m.getGrade()) != 0 ? this.getGrade().compareTo(m.getGrade()) : UUIDHelper.getNameOf(this.getUUID()).compareTo(UUIDHelper.getNameOf(m.getUUID()));
    }

}
