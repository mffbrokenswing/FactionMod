package factionmod.faction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import factionmod.config.Config;
import factionmod.data.FactionModDatas;
import factionmod.event.FactionLevelUpEvent;
import factionmod.event.GradeChangeEvent;
import factionmod.handler.EventHandlerExperience;
import factionmod.inventory.FactionInventory;
import factionmod.utils.DimensionalBlockPos;
import factionmod.utils.DimensionalPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Represents a group of players.
 * 
 * @author BrokenSwing
 *
 */
public class Faction implements INBTSerializable<NBTTagCompound> {

    private final ArrayList<Member>              members     = new ArrayList<Member>();
    private final ArrayList<UUID>                invitations = new ArrayList<UUID>();
    private final ArrayList<DimensionalPosition> chunks      = new ArrayList<DimensionalPosition>();
    private DimensionalBlockPos                  homePos     = null;
    private final ArrayList<Grade>               grades      = new ArrayList<Grade>();
    private FactionInventory                     inventory;

    private String                               name;
    private String                               description;
    private boolean                              opened      = false;
    private int                                  level       = 1;
    private int                                  exp         = 0;
    private int                                  damages     = 0;

    public Faction(String name, String desc, Member owner) {
        this.name = name;
        this.description = desc;
        this.inventory = new FactionInventory(name);
        this.members.add(owner);
    }

    public Faction(NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    /**
     * Returns the inventory of the faction
     * 
     * @return the inventory
     */
    public FactionInventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns a list containing each {@link Grade} created by the faction.
     * 
     * @return all grades
     */
    public List<Grade> getGrades() {
        return Collections.unmodifiableList(this.grades);
    }

    /**
     * Damages the faction.
     * 
     * @param damage
     *            The amount of damage
     */
    public void damageFaction(int damage) {
        if (damage <= 0)
            return;
        this.damages += damage;
        if (damages > Config.maxFactionDamages)
            damages = Config.maxFactionDamages;
        FactionModDatas.save();
    }

    /**
     * Returns the damages of the faction.
     * 
     * @return
     */
    public int getDamages() {
        return this.damages;
    }

    /**
     * Decreases the damages of the faction.
     * 
     * @param count
     *            The amount to decrease
     */
    public void decreaseDamages(int count) {
        this.damages -= count;
        if (this.damages < 0) {
            this.damages = 0;
        }
        FactionModDatas.save();
    }

    /**
     * Sets the damages to 0.
     */
    public void resetDamages() {
        this.damages = 0;
        FactionModDatas.save();
    }

    /**
     * Adds a grade to the faction.
     * 
     * @param grade
     *            The grade to add
     */
    public void addGrade(Grade grade) {
        Grade g = getGrade(grade.getName());
        if (g != null) {
            for(Member m : members) {
                if (m.getGrade().equals(g)) {
                    m.setGrade(grade);
                }
            }
            grades.remove(g);
        }
        grades.add(grade);
        FactionModDatas.save();
    }

    /**
     * Returns the grade with the given name or null if any grade has this name.
     * 
     * @param name
     *            The name of the grade
     * @return a grade or null
     */
    public Grade getGrade(String name) {
        for(Grade grade : grades) {
            if (grade.getName().equalsIgnoreCase(name)) {
                return grade;
            }
        }
        return null;
    }

    /**
     * Removes a grade from the faction. If members had this grade, they now
     * have the grade {@link Grade#MEMBER}.
     * 
     * @param grade
     *            The grade to remove
     */
    public void removeGrade(Grade grade) {
        for(Member m : members) {
            if (m.getGrade().equals(grade)) {
                MinecraftForge.EVENT_BUS.post(new GradeChangeEvent(this, m.getUUID(), m.getGrade(), grade));
                m.setGrade(Grade.MEMBER);
            }
        }
        this.grades.remove(grade);
        FactionModDatas.save();
    }

    /**
     * Returns the description of the faction. If no description was set, will
     * return an empty {@link String}.
     * 
     * @return the description or an empty {@link String}
     */
    public String getDesc() {
        return description;
    }

    /**
     * Sets the current level of the faction. Then call
     * {@link Faction#increaseExp(0)} to change the level if the maximum
     * experience si reached.
     * 
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
        this.increaseExp(0, null);
        FactionModDatas.save();
    }

    /**
     * Returns the current level of the faction.
     * 
     * @return the level of the faction
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Sets the current experience to the given amount. Then calls
     * {@link Faction#increaseExp(0)} to change the level if the experience is
     * too high.
     * 
     * @param exp
     *            The new amount of experience of the faction
     */
    public void setExp(int exp) {
        this.exp = exp;
        this.increaseExp(0, null);
        FactionModDatas.save();
    }

    /**
     * Increase the experience from the given amount. If the experience needed
     * to level up is reached, the level of the faction is increased and the
     * experience is consumed, then
     * {@link EventHandlerExperience#onLevelUp(Faction)} is fired. If the
     * specified amount of experience is equals to or lower than 0, the fonction
     * will do nothing.
     * 
     * @param exp
     *            The amount of experience to add
     * @param member
     *            The UUID of the member who made the faction win the experience
     */
    public void increaseExp(int exp, UUID member) {
        if (exp < 0)
            return;
        if (member != null) {
            Member m = getMember(member);
            if (m != null)
                m.addExperience(exp);
        }
        this.exp += exp;
        int neededXp = Levels.getExpNeededForLevel(this.level + 1);
        if (this.exp >= neededXp) {
            this.level++;
            MinecraftForge.EVENT_BUS.post(new FactionLevelUpEvent(this));
            this.exp -= neededXp;
            this.increaseExp(0, member);
        }
        FactionModDatas.save();
    }

    /**
     * Returns the current experience of the faction. See
     * {@link Levels#getExpNeededForLevel(int)} to know the experience needed to
     * level up.
     * 
     * @return The amount of experience
     */
    public int getExp() {
        return this.exp;
    }

    /**
     * Returns the owner of the faction.
     * 
     * @return the owner
     */
    public Member getOwner() {
        for(Member member : members) {
            if (member.getGrade() == Grade.OWNER) {
                return member;
            }
        }
        return null;
    }

    /**
     * Adds a chunk claimed by the faction.
     * 
     * @param position
     *            The position of the chunk
     */
    public void addChunk(DimensionalPosition position) {
        this.chunks.add(position);
        FactionModDatas.save();
    }

    /**
     * Removes a chunk claimed by the faction. Removes the home if it was in the
     * given chunk.
     * 
     * @param position
     *            The position of the chunk
     */
    public void removeChunk(DimensionalPosition position) {
        this.chunks.remove(position);
        if (this.homePos != null) {
            if (this.homePos.toDimensionnalPosition().equals(position)) {
                this.homePos = null;
            }
        }
        FactionModDatas.save();
    }

    /**
     * Returns all positions of the chunks claimed by the faction.
     * 
     * @return a {@link List}
     */
    public List<DimensionalPosition> getChunks() {
        return Collections.unmodifiableList(this.chunks);
    }

    /**
     * Returns the {@link Member} with the given {@link UUID}, or null if not
     * found.
     * 
     * @param uuid
     *            The {@link UUID} of the {@link Member}
     * @return the {@link Member} or null
     */
    public Member getMember(UUID uuid) {
        for(Member m : members) {
            if (m.getUUID().equals(uuid))
                return m;
        }
        return null;
    }

    /**
     * Changes the position of the home.
     * 
     * @param pos
     *            The position of the new home
     */
    public void setHome(DimensionalBlockPos pos) {
        this.homePos = pos;
        FactionModDatas.save();
    }

    /**
     * Returns the position of the home of the faction. It can return a null
     * object if the home isn't set.
     * 
     * @return the position of the home or null if it isn't set
     */
    public DimensionalBlockPos getHome() {
        return this.homePos;
    }

    /**
     * Changes the description of the faction.
     * 
     * @param desc
     *            The new decription
     */
    public void setDesc(String desc) {
        this.description = desc;
        FactionModDatas.save();
    }

    /**
     * Returns the name of the faction, as written when created.
     * 
     * @return a {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Toogles the invitation for a player :
     * <ul>
     * <li>The player is already invited : un-invite him</li>
     * <li>The player isn't invited : invite the player</li>
     * </ul>
     * 
     * @param uuid
     *            The UUID of the player
     * @return true if the player is invited<br />
     *         false if he's not
     */
    public boolean toogleInvitation(UUID uuid) {
        if (invitations.contains(uuid)) {
            invitations.remove(uuid);
            FactionModDatas.save();
            return false;
        }
        invitations.add(uuid);
        FactionModDatas.save();
        return true;
    }

    /**
     * Indicates if a player is invited.
     * 
     * @param uuid
     *            The {@link UUID} of the player
     * @return true if the player is invited
     */
    public boolean isInvited(UUID uuid) {
        return invitations.contains(uuid);
    }

    /**
     * Indicates if a player is a member.
     * 
     * @param uuid
     *            The {@link UUID} of the player
     * @return true if the player is a member
     */
    public boolean isMember(UUID uuid) {
        for(Member member : members) {
            if (member.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a member in the faction. This fonction call
     * {@link Faction#addMember(Member)}, the instance of member which is
     * created has the {@link Grade} : {@link Grade#MEMBER}.
     * 
     * @param uuid
     *            The {@link UUID} of the player to add
     */
    public void addMember(UUID uuid) {
        this.addMember(new Member(uuid, Grade.MEMBER));
    }

    /**
     * Adds a member in the faction.
     * 
     * @param member
     *            The instance of the member.
     */
    public void addMember(Member member) {
        this.members.add(member);
        this.invitations.remove(member.getUUID());
        FactionModDatas.save();
    }

    /**
     * Removes a member from the faction.
     * 
     * @param uuid
     *            The {@link UUID} of the player
     */
    public void removeMember(UUID uuid) {
        Member toRemove = null;
        for(Member m : members) {
            if (m.getUUID().equals(uuid)) {
                toRemove = m;
                break;
            }
        }
        if (toRemove != null) {
            members.remove(toRemove);
        }
        FactionModDatas.save();
    }

    /**
     * Returns all the members of the faction.
     * 
     * @return a {@link List} containing all the members
     */
    public List<Member> getMembers() {
        return Collections.unmodifiableList(this.members);
    }

    /**
     * Sets the faction opened. When the faction is opened, anyone can join it
     * without invitation.
     * 
     * @param opened
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
        FactionModDatas.save();
    }

    /**
     * Indicates if the faction is opened.
     * 
     * @return true if the faction si opened
     */
    public boolean isOpened() {
        return this.opened;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Faction))
            return false;
        return ((Faction) obj).getName().equalsIgnoreCase(this.name);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.name = nbt.getString("name");
        this.description = nbt.getString("description");
        this.opened = nbt.getBoolean("opened");
        this.level = nbt.getInteger("level");
        this.exp = nbt.getInteger("exp");
        this.damages = nbt.getInteger("damages");

        NBTTagList gradesList = nbt.getTagList("grades", NBT.TAG_COMPOUND);
        for(int i = 0; i < gradesList.tagCount(); i++) {
            this.grades.add(new Grade(gradesList.getCompoundTagAt(i)));
        }

        NBTTagList membersList = nbt.getTagList("members", NBT.TAG_COMPOUND);
        for(int i = 0; i < membersList.tagCount(); i++) {
            this.members.add(new Member(membersList.getCompoundTagAt(i), this));
        }

        NBTTagList invitationsList = nbt.getTagList("invitations", NBT.TAG_COMPOUND);
        for(int i = 0; i < invitationsList.tagCount(); i++) {
            this.invitations.add(NBTUtil.getUUIDFromTag(invitationsList.getCompoundTagAt(i)));
        }

        NBTTagList chunksList = nbt.getTagList("chunks", NBT.TAG_COMPOUND);
        for(int i = 0; i < chunksList.tagCount(); i++) {
            this.chunks.add(new DimensionalPosition(chunksList.getCompoundTagAt(i)));
        }

        this.inventory = new FactionInventory(nbt.getCompoundTag("inventory"));

        if (nbt.hasKey("home"))
            this.homePos = new DimensionalBlockPos(nbt.getCompoundTag("home"));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.name);
        nbt.setString("description", this.description);
        nbt.setBoolean("opened", this.opened);
        nbt.setInteger("level", this.level);
        nbt.setInteger("exp", this.exp);
        nbt.setInteger("damages", this.damages);

        NBTTagList gradesList = new NBTTagList();
        for(Grade grade : this.grades) {
            gradesList.appendTag(grade.serializeNBT());
        }
        nbt.setTag("grades", gradesList);

        NBTTagList membersList = new NBTTagList();
        for(Member member : this.members) {
            membersList.appendTag(member.serializeNBT());
        }
        nbt.setTag("members", membersList);

        NBTTagList invitationsList = new NBTTagList();
        for(UUID id : this.invitations) {
            invitationsList.appendTag(NBTUtil.createUUIDTag(id));
        }
        nbt.setTag("invitations", invitationsList);

        NBTTagList chunksList = new NBTTagList();
        for(DimensionalPosition pos : this.chunks) {
            chunksList.appendTag(pos.serializeNBT());
        }
        nbt.setTag("chunks", chunksList);

        nbt.setTag("inventory", this.inventory.serializeNBT());

        if (this.homePos != null) {
            nbt.setTag("home", this.homePos.serializeNBT());
        }

        return nbt;
    }

}
