package factionmod.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import akka.japi.Pair;
import factionmod.FactionMod;
import factionmod.command.utils.UUIDHelper;
import factionmod.config.ConfigGeneral;
import factionmod.config.ConfigLang;
import factionmod.config.ConfigLoader;
import factionmod.data.FactionModDatas;
import factionmod.enums.EnumPermission;
import factionmod.event.ClaimChunkEvent;
import factionmod.event.CreateFactionEvent;
import factionmod.event.DescriptionChangedEvent;
import factionmod.event.FactionCreatedEvent;
import factionmod.event.FactionDisbandedEvent;
import factionmod.event.FactionInfoEvent;
import factionmod.event.GradeChangeEvent;
import factionmod.event.HomeTeleportationEvent;
import factionmod.event.JoinFactionEvent;
import factionmod.event.LeaveFactionEvent;
import factionmod.event.SetHomeEvent;
import factionmod.event.UnclaimChunkEvent;
import factionmod.faction.Faction;
import factionmod.faction.Grade;
import factionmod.faction.Levels;
import factionmod.faction.Member;
import factionmod.manager.IChunkManager;
import factionmod.manager.ManagerFaction;
import factionmod.manager.instanciation.ChunkManagerCreator;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalBlockPos;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;
import factionmod.utils.ServerUtils;
import factionmod.utils.TeleportationHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/**
 * It handles the factions. You can process actions on faction through this
 * class.
 * 
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerFaction {

    private static final HashMap<String, Faction> factions      = new HashMap<String, Faction>();
    private static final HashMap<UUID, String>    usersFactions = new HashMap<UUID, String>();

    // ---------- Fonctions modifying directly mappings --------------

    /* Theses fonctions shouldn't be used by an other mod */

    public static void addFaction(Faction faction) {
        factions.put(faction.getName().toLowerCase(), faction);
        FactionModDatas.save();
    }

    private static void removeFaction(Faction faction) {
        factions.remove(faction.getName().toLowerCase());
        FactionModDatas.save();
    }

    public static void addUserToFaction(Faction faction, UUID user) {
        usersFactions.put(user, faction.getName().toLowerCase());
        refreshDisplayNameOf(user);
        FactionModDatas.save();
    }

    private static void removeUser(UUID user) {
        usersFactions.remove(user);
        refreshDisplayNameOf(user);
        FactionModDatas.save();
    }

    // ------------ Fonctions returning informations about Maps ------------

    /**
     * Should not be used from an other class than {@link ConfigLoader} !
     * 
     * @return
     */
    public static Map<String, Faction> getFactions() {
        return Collections.unmodifiableMap(factions);
    }

    /**
     * Indicates if a faction exists.
     * 
     * @param factionName
     *            The name of the faction
     * @return true if the faction exists, else false
     */
    public static boolean doesFactionExist(String factionName) {
        return !factionName.isEmpty() && factions.keySet().contains(factionName.toLowerCase());
    }

    /**
     * Indicates if a player is in a faction.
     * 
     * @param uuid
     *            The UUID of the player
     * @return true if the player is in a faction, else false
     */
    public static boolean hasUserFaction(UUID uuid) {
        return usersFactions.containsKey(uuid);
    }

    /**
     * Returns the faction with the given name, can return null if any faction
     * has this name.
     * 
     * @param factionName
     *            The name of the faction
     * @return the faction or null
     */
    public static Faction getFaction(String factionName) {
        return factions.get(factionName.toLowerCase());
    }

    /**
     * Indicates the name of the faction of a player. It will return an empty
     * string if the player isn't in a faction.
     * 
     * @param uuid
     *            The UUID of the player
     * @return the name of the faction or an empty string if the player isn't in
     *         a faction
     */
    public static String getFactionOf(UUID uuid) {
        String name = usersFactions.get(uuid);
        return name != null ? name : "";
    }

    // ------------------ EVENT PART -------------------

    /**
     * Used to update the display name. It shows the name of the faction of the
     * player before his name.
     * 
     * @param event
     */
    @SubscribeEvent
    public static void displayNameUpdate(NameFormat event) {
        if (EventHandlerAdmin.isAdmin((EntityPlayerMP) event.getEntityPlayer())) {
            event.setDisplayname(ConfigLang.translate("admin.prefix") + " " + TextFormatting.RESET + event.getDisplayname());
        }
        if (hasUserFaction(event.getEntityPlayer().getPersistentID())) {
            String factionName = getFaction(getFactionOf(event.getEntityPlayer().getUniqueID())).getName();
            event.setDisplayname("[" + factionName + "] " + event.getDisplayname());
        }
        if (event.getUsername().equals("BrokenSwing") && ServerUtils.getServer().isServerInOnlineMode()) {
            event.setDisplayname(TextFormatting.YELLOW + "[Faction's Mod Dev] " + TextFormatting.RESET + event.getDisplayname());
        }
    }

    /**
     * Used to disable friendly-fire.
     * 
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingHurt(LivingHurtEvent event) {
        if (ConfigGeneral.getBool("disable_friendly_fire")) {
            if (event.getEntity() instanceof EntityPlayer && event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) event.getEntity();
                EntityPlayer source = (EntityPlayer) event.getSource().getTrueSource();
                if (!hasUserFaction(target.getUniqueID()) || !hasUserFaction(source.getUniqueID()))
                    return;
                if (getFactionOf(source.getUniqueID()).equalsIgnoreCase(getFactionOf(target.getUniqueID())))
                    event.setCanceled(true);
            }
        }
    }

    private static final IdentityHashMap<Faction, AtomicInteger> TIMERS = new IdentityHashMap<>();

    /**
     * Faction's damages decrementation
     */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent event) {
        int time = ConfigGeneral.getInt("damages_persistence");
        if (time > 0) {
            factions.values().stream().filter(f -> !TIMERS.containsKey(f)).filter(f -> f.getDamages() > 0).forEach(f -> TIMERS.put(f, new AtomicInteger(time)));
        }
        Iterator<Entry<Faction, AtomicInteger>> it = TIMERS.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Faction, AtomicInteger> entry = it.next();
            int value = entry.getValue().decrementAndGet();
            if (value == 0)
                it.remove();
        }
    }

    /**
     * Used to disable friendly-fire.
     * 
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void livingAttack(LivingAttackEvent event) {
        if (ConfigGeneral.getBool("disable_friendly_fire")) {
            if (event.getEntity() instanceof EntityPlayer && event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer target = (EntityPlayer) event.getEntity();
                EntityPlayer source = (EntityPlayer) event.getSource().getTrueSource();
                if (!hasUserFaction(target.getUniqueID()) || !hasUserFaction(source.getUniqueID()))
                    return;
                if (getFactionOf(source.getUniqueID()).equalsIgnoreCase(getFactionOf(target.getUniqueID())))
                    event.setCanceled(true);
            }
        }
    }

    // -------------------- UTILS -----------------------

    /**
     * Refreshes the display name of a player.
     * 
     * @param uuid
     *            The UUID of the player
     */
    public static void refreshDisplayNameOf(UUID uuid) {
        EntityPlayer player = ServerUtils.getPlayer(uuid);
        if (player != null) {
            player.refreshDisplayName();
        }
    }

    /**
     * Broadcast a message to each member of a faction
     * 
     * @param name
     *            The name of the faction
     * @param message
     *            The message to broadcast
     * @param level
     *            The level of the message ({@link MessageHelper#INFO},
     *            {@link MessageHelper#WARN},{@link MessageHelper#ERROR})
     */
    public static void broadcastToFaction(String name, String message, int level) {
        if (doesFactionExist(name))
            broadcastToFaction(getFaction(name), message, level);
    }

    /**
     * Broadcast a message to each member of a faction
     * 
     * @param faction
     *            The faction
     * @param message
     *            The message to broadcast
     * @param level
     *            The level of the message ({@link MessageHelper#INFO},
     *            {@link MessageHelper#WARN},{@link MessageHelper#ERROR})
     */
    public static void broadcastToFaction(Faction faction, String message, int level) {
        PlayerList list = ServerUtils.getServer().getPlayerList();
        for(Member m : faction.getMembers()) {
            EntityPlayerMP player = list.getPlayerByUUID(m.getUUID());
            if (player != null) {
                player.sendMessage(MessageHelper.message(message, level));
            }
        }
    }

    // ------------ PUBLIC USAGE FONCTIONS --------------

    /**
     * Creates a faction. The player creating the faction have to be
     * faction-less and he will become the owner of the faction. The description
     * maximum length of the description should be 50, but it's not necessary.
     * 
     * @param name
     *            The name of the faction
     * @param desc
     *            The description of the faction
     * @param owner
     *            The {@link UUID} of the player who executed the command
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> createFaction(String name, String desc, UUID owner) {
        if (hasUserFaction(owner))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.has"));
        if (doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.create.name.taken"), name));
        CreateFactionEvent event = new CreateFactionEvent(name, owner, desc);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<String>(EnumActionResult.FAIL, event.getMessage());
        Faction faction = new Faction(name, desc, new Member(owner, Grade.OWNER));
        addFaction(faction);
        addUserToFaction(faction, owner);
        MinecraftForge.EVENT_BUS.post(new FactionCreatedEvent(faction, owner));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.create.success"), name));
    }

    /**
     * Removes a faction. The player have to be the owner of the faction.
     * 
     * @param name
     *            The name of the faction
     * @param owner
     *            The player who executed the command
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> removeFaction(String name, UUID owner) {
        boolean admin = EventHandlerAdmin.isAdmin(owner);
        if (!admin && !hasUserFaction(owner))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(owner))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !(faction.getMember(owner).getGrade() == Grade.OWNER))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.member.grade.owner.isnt"));
        MinecraftForge.EVENT_BUS.post(new FactionDisbandedEvent.Pre(faction, owner));
        List<Member> members = Lists.newArrayList(faction.getMembers());
        if (!admin) {
            Member own = faction.getMember(owner);
            faction.removeMember(own.getUUID());
        }
        if (!admin) {
            broadcastToFaction(faction, ConfigLang.translate("faction.removed.owner"), MessageHelper.WARN);
        } else {
            broadcastToFaction(faction, String.format(ConfigLang.translate("faction.removed.other"), UUIDHelper.getNameOf(owner)), MessageHelper.WARN);
        }
        for(Member m : members) {
            removeUser(m.getUUID());
        }
        removeFaction(faction);
        for(DimensionalPosition position : faction.getChunks()) {
            EventHandlerChunk.unregisterChunkManager(position, true);
        }
        MinecraftForge.EVENT_BUS.post(new FactionDisbandedEvent.Post(faction, owner));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.disband.success"), faction.getName()));
    }

    /**
     * Invites an user to the faction. The member need the permission
     * {@link EnumPermission#INVITE_USER}.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The {@link UUID} of the player executing the command
     * @param toRemove
     *            The {@link UUID} of the player to invite
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> inviteUserToFaction(String name, UUID member, UUID newMember) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        if (hasUserFaction(newMember))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.other.faction.has"));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        Member m = faction.getMember(member);
        if (!admin && !m.hasPermission(EnumPermission.INVITE_USER))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        boolean invited = faction.toogleInvitation(newMember);
        if (invited) {
            EntityPlayer player = ServerUtils.getPlayer(newMember);
            if (player != null) {
                ITextComponent join = new TextComponentString(String.format(ConfigLang.translate("player.self.invitation.received"), faction.getName())).setStyle(new Style().setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/faction join " + faction.getName())).setUnderlined(true));
                join.setStyle(new Style().setColor(MessageHelper.INFO_COLOR));
                player.sendMessage(join);
            }
        }
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format((invited ? ConfigLang.translate("faction.invite.success.invited") : ConfigLang.translate("faction.invite.success.notinvited")), faction.getName()));
    }

    /**
     * Removes an user from a faction. The member need the permission
     * {@link EnumPermission#REMOVE_USER}. The member's grade need an higher
     * level than the toRemove's one.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The {@link UUID} of the player executing the command
     * @param toRemove
     *            The {@link UUID} of the player to remove
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> removeUserFromFaction(String name, UUID member, UUID toRemove) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!faction.isMember(toRemove))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.other.member.isnt"), faction.getName()));
        Member m = faction.getMember(member);
        if (!admin && !m.hasPermission(EnumPermission.REMOVE_USER))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        Member mRemove = faction.getMember(toRemove);
        if (!admin && !Grade.canAffect(m.getGrade(), mRemove.getGrade()))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        faction.removeMember(toRemove);
        removeUser(toRemove);
        EntityPlayerMP player = ServerUtils.getPlayer(toRemove);
        if (player != null) {
            player.sendMessage(MessageHelper.error(String.format(ConfigLang.translate("player.self.member.nolonger"), faction.getName())));
        }
        MinecraftForge.EVENT_BUS.post(new LeaveFactionEvent(faction, toRemove));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("player.other.member.nolonger"), UUIDHelper.getNameOf(toRemove), faction.getName()));
    }

    /**
     * Changes the description of a faction. The player trying to change the
     * description needs the permission
     * {@link EnumPermission#CHANGE_DESCRIPTION)}. Please try to set the maximum
     * length of the description to 50.
     * 
     * @param name
     *            The name of the faction
     * @param desc
     *            The new description
     * @param member
     *            The {@link UUID} of the member
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> changeDescriptionOfFaction(String name, String desc, UUID member) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        Member m = faction.getMember(member);
        if (!admin && !m.hasPermission(EnumPermission.CHANGE_DESCRIPTION))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        DescriptionChangedEvent event = new DescriptionChangedEvent(faction, member, desc);
        MinecraftForge.EVENT_BUS.post(event);
        faction.setDesc(event.getNewDescription());
        return new ActionResult<String>(EnumActionResult.SUCCESS, ConfigLang.translate("faction.desc.changed"));
    }

    /**
     * Makes the given player join the given faction.
     * 
     * @param name
     *            The name of the faction
     * @param uuid
     *            The {@link UUID} of the player
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> joinFaction(String name, UUID uuid) {
        boolean admin = EventHandlerAdmin.isAdmin(uuid);
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (faction.isMember(uuid))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.has"));
        if (admin || faction.isOpened() || faction.isInvited(uuid)) {
            JoinFactionEvent event = new JoinFactionEvent(faction, uuid);
            if (MinecraftForge.EVENT_BUS.post(event))
                return new ActionResult<String>(EnumActionResult.FAIL, event.getMessage());
            broadcastToFaction(faction, String.format(ConfigLang.translate("player.other.faction.joined"), UUIDHelper.getNameOf(uuid)), MessageHelper.INFO);
            faction.addMember(uuid);
            addUserToFaction(faction, uuid);
            return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("player.self.member.became"), faction.getName()));
        } else if (!faction.getRecruitLink().isEmpty()) {
            return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.join.recruit"), faction.getRecruitLink()));
        } else {
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.invitation.hasnt"), faction.getName()));
        }
    }

    /**
     * Makes a player leave a faction.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The {@link UUID} of the player
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> leaveFaction(String name, UUID member) {
        if (!hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (faction.getMember(member).getGrade() == Grade.OWNER)
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        faction.removeMember(member);
        removeUser(member);
        broadcastToFaction(faction, String.format(ConfigLang.translate("player.other.member.nolonger"), UUIDHelper.getNameOf(member), faction.getName()), MessageHelper.INFO);
        MinecraftForge.EVENT_BUS.post(new LeaveFactionEvent(faction, member));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("player.self.member.nolonger"), faction.getName()));
    }

    /**
     * Claims a chunk for a faction. The player needs the permission
     * {@link EnumPermission#CLAIM_CHUNK}.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The member who claims the chunk
     * @param position
     *            The position of the chunk
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> claimChunk(String name, UUID member, DimensionalPosition position) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(member).hasPermission(EnumPermission.CLAIM_CHUNK))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        IChunkManager manager = EventHandlerChunk.getManagerFor(position);
        if (manager != null)
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("faction.claim.fail.nothere"));
        if (faction.getChunks().size() >= Levels.getMaximumChunksForLevel(faction.getLevel()))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("faction.claim.fail.maxreached"));
        ClaimChunkEvent event = new ClaimChunkEvent(faction, member, position);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<String>(EnumActionResult.FAIL, event.getMessage());
        Pair<IChunkManager, ZoneInstance> pair = ChunkManagerCreator.createChunkHandler("faction", faction.getName());
        EventHandlerChunk.registerChunkManager(pair.first(), position, pair.second(), true);
        faction.addChunk(position);
        return new ActionResult<String>(EnumActionResult.SUCCESS, ConfigLang.translate("faction.claim.success"));
    }

    /**
     * Unclaims a chunk for a faction. The player needs the permission
     * {@link EnumPermission#CLAIM_CHUNK}.
     * 
     * @param member
     *            The member who unclaims the chunk
     * @param position
     *            The position of the chunk
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> unclaimChunk(UUID member, DimensionalPosition position) {
        boolean isAdmin = EventHandlerAdmin.isAdmin(member);
        IChunkManager manager = EventHandlerChunk.getManagerFor(position);
        if (!(manager instanceof ManagerFaction))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("faction.chunk.notclaimed"));
        ManagerFaction m = (ManagerFaction) manager;
        Faction faction = m.getFaction();
        boolean isMember = faction.isMember(member);
        boolean overClaim = faction.getDamages() >= ConfigGeneral.getInt("damages_needed_to_counter_claim") && isChunkAtEdge(position) && !isMember;
        if (!isAdmin && !overClaim && !isMember)
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!isAdmin && isMember && !faction.getMember(member).hasPermission(EnumPermission.CLAIM_CHUNK))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        EventHandlerChunk.unregisterChunkManager(position, true);
        faction.removeChunk(position);
        if (overClaim)
            faction.decreaseDamages(ConfigGeneral.getInt("damages_needed_to_counter_claim"));
        MinecraftForge.EVENT_BUS.post(new UnclaimChunkEvent(faction, member, position));
        return new ActionResult<String>(EnumActionResult.SUCCESS, ConfigLang.translate("faction.unclaim.success"));
    }

    /**
     * Changes the position of the home of a faction. The player need the
     * permission {@link EnumPermission#SET_HOME}.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The member of the faction
     * @param position
     *            The new position of the home
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> setHome(String name, UUID member, DimensionalBlockPos position) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(member).hasPermission(EnumPermission.SET_HOME))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        if (!faction.getChunks().contains(position.toDimensionnalPosition()))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("faction.chunk.notclaimed"));
        SetHomeEvent event = new SetHomeEvent(faction, member, position);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<String>(EnumActionResult.FAIL, event.getMessage());
        faction.setHome(position);
        return new ActionResult<String>(EnumActionResult.SUCCESS, ConfigLang.translate("faction.home.location.changed"));
    }

    /**
     * Open the faction if closed, else close it. The player need the permission
     * {@link EnumPermission#INVITE_USER}.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The member of the faction
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> open(String name, UUID member) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(member).hasPermission(EnumPermission.INVITE_USER))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        faction.setOpened(!faction.isOpened());
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format((faction.isOpened() ? ConfigLang.translate("faction.opened.yes") : ConfigLang.translate("faction.opened.no")), faction.getName()));
    }

    /**
     * Gives a list of informations about a faction
     * 
     * @param name
     *            The name of the faction
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<List<String>> getInformationsAbout(String name) {
        if (!doesFactionExist(name))
            return new ActionResult<List<String>>(EnumActionResult.FAIL, Lists.newArrayList(String.format(ConfigLang.translate("faction.inexisting"), name)));
        ArrayList<String> list = new ArrayList<String>();
        Faction faction = getFaction(name);
        list.add("****** " + faction.getName() + " ******");
        if (!faction.getDesc().isEmpty())
            list.add(ConfigLang.translate("lang.description") + " : " + faction.getDesc());
        list.add(ConfigLang.translate("lang.members") + " : " + faction.getMembers().size());
        list.add(ConfigLang.translate("lang.level") + " : " + faction.getLevel());
        list.add(ConfigLang.translate("lang.experience") + " : " + faction.getExp() + "/" + Levels.getExpNeededForLevel(faction.getLevel() + 1));
        list.add("Chunks : " + faction.getChunks().size() + "/" + Levels.getMaximumChunksForLevel(faction.getLevel()));
        list.add(ConfigLang.translate("lang.opened") + " : " + (faction.isOpened() ? ConfigLang.translate("lang.yes") : ConfigLang.translate("lang.no")));
        list.add(ConfigLang.translate("damages") + " : " + faction.getDamages());
        FactionInfoEvent event = new FactionInfoEvent(faction, list);
        MinecraftForge.EVENT_BUS.post(event);
        return new ActionResult<List<String>>(EnumActionResult.SUCCESS, event.getInformations());
    }

    /**
     * Gives a list of the grades with their permissions.
     * 
     * @param name
     *            The name of the faction
     * @return the result of the action : error message if the faction doesn't
     *         exist, else the grades
     */
    public static ActionResult<List<String>> getGradesListFor(String name) {
        if (!doesFactionExist(name))
            return new ActionResult<List<String>>(EnumActionResult.FAIL, Lists.newArrayList(String.format(ConfigLang.translate("faction.inexisting"), name)));

        Faction faction = getFaction(name);
        ArrayList<Grade> grades = new ArrayList<Grade>(faction.getGrades());
        grades.add(Grade.MEMBER);
        grades.add(Grade.OWNER);
        Collections.sort(grades);

        ArrayList<String> list = new ArrayList<String>();
        list.add("****** " + faction.getName() + " ******");
        grades.forEach(g -> {
            list.add(String.format("%s (%d) :", g.getPermissions().stream().map(EnumPermission::name).collect(Collectors.joining(" "))));
        });

        return new ActionResult<List<String>>(EnumActionResult.SUCCESS, list);
    }

    /**
     * Teleports the player to the faction home
     * 
     * @param name
     *            The name of the faction
     * @param player
     *            The player to teleport
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> goToHome(String name, EntityPlayerMP player) {
        boolean admin = EventHandlerAdmin.isAdmin(player);
        if (!admin && !hasUserFaction(player.getUniqueID()))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(player.getUniqueID()))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        DimensionalBlockPos pos = faction.getHome();
        if (pos == null)
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.home.inexisting"), faction.getName()));
        if (player.getEntityWorld().provider.getDimension() != pos.getDimension())
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.location.dim.wrong"));
        HomeTeleportationEvent event = new HomeTeleportationEvent(faction, player);
        if (MinecraftForge.EVENT_BUS.post(event))
            return new ActionResult<String>(EnumActionResult.FAIL, event.getMessage());
        TeleportationHelper.teleport(player, pos.getPosition(), ConfigGeneral.getInt("teleportation_delay"));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.home.success.start"), faction.getName()));
    }

    /**
     * Adds or updates a grade to a faction. The player executing the commands
     * has to be the owner of the faction.
     * 
     * @param factionName
     *            The name of the faction
     * @param executor
     *            The owner of the faction
     * @param grade
     *            The grade to add or update
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> setGrade(String factionName, UUID executor, Grade grade) {
        boolean admin = EventHandlerAdmin.isAdmin(executor);
        if (!admin && !hasUserFaction(executor))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(factionName))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), factionName));
        Faction faction = getFaction(factionName);
        if (!admin && !faction.isMember(executor))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        Member member = faction.getMember(executor);
        if (!admin && member.getGrade() != Grade.OWNER)
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        if (grade.getName().equalsIgnoreCase(Grade.OWNER.getName()) || grade.getName().equalsIgnoreCase(Grade.MEMBER.getName()))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.grade.name.taken"), grade.getName()));
        if (grade.getPriority() < 1)
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.grade.level.invalid"), Grade.OWNER.getPriority()));
        faction.addGrade(grade);
        String perms = "";
        for(EnumPermission p : grade.getPermissions()) {
            perms += " " + p.name();
        }
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.grade.set.success"), grade.getName(), grade.getPriority(), perms));
    }

    /**
     * Promotes a player to a grade. The player execcuting the command need the
     * permission {@link EnumPermission#PROMOTE} and need a grade higher than
     * the player promoted.
     * 
     * @param name
     *            The name of the faction
     * @param executor
     *            The name of the player executing the command
     * @param executed
     *            The player being promoted
     * @param gradeName
     *            The name of the grade
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> promote(String name, UUID executor, UUID executed, String gradeName) {
        boolean admin = EventHandlerAdmin.isAdmin(executor);
        if (!admin && !hasUserFaction(executor))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(executor))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.nolonger"), faction.getName()));
        if (!faction.isMember(executed))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.other.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(executor).hasPermission(EnumPermission.PROMOTE))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        Member mExecutor = faction.getMember(executor);
        Member mExecuted = faction.getMember(executed);
        if (!admin && !Grade.canAffect(mExecutor.getGrade(), mExecuted.getGrade()))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        Grade grade = null;
        if (Grade.MEMBER.getName().equalsIgnoreCase(gradeName)) {
            grade = Grade.MEMBER;
        } else {
            grade = faction.getGrade(gradeName);
        }
        if (grade == null)
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.grade.inexisting"), gradeName));
        Grade previousGrade = mExecuted.getGrade();
        mExecuted.setGrade(grade);
        EntityPlayer player = ServerUtils.getPlayer(executed);
        if (player != null) {
            player.sendMessage(MessageHelper.info(String.format(ConfigLang.translate("player.self.member.grade.promoted"), grade.getName().toLowerCase())));
        }
        MinecraftForge.EVENT_BUS.post(new GradeChangeEvent(faction, executed, previousGrade, grade));
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("player.other.member.grade.promoted"), UUIDHelper.getNameOf(executed), grade.getName()));
    }

    /**
     * Removes a grade from a faction. The player executing the command has to
     * be the owner of the faction. All the players with this grade will be
     * promoted to {@link Grade#MEMBER}.
     * 
     * @param factionName
     *            The name of the faction
     * @param owner
     *            The UUID of the player executing the command
     * @param gradeName
     *            The name of the grade
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> removeGrade(String factionName, UUID owner, String gradeName) {
        boolean admin = EventHandlerAdmin.isAdmin(owner);
        if (!admin && !hasUserFaction(owner))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(factionName))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), factionName));
        Faction faction = getFaction(factionName);
        if (!admin && !faction.isMember(owner))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        Member member = faction.getMember(owner);
        if (!admin && member.getGrade() != Grade.OWNER)
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        Grade g = faction.getGrade(gradeName);
        if (g == null)
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.grade.inexisting"), gradeName));
        faction.removeGrade(g);
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.grade.remove.success"), g.getName()));
    }

    /**
     * Shows the inventory of the specified faction if the specified member has
     * the permission {@link EnumPermission#SHOW_CHEST}.
     * 
     * @param name
     *            The name of the faction
     * @param member
     *            The UUID of the member
     * @return the result of the action : FAIL or SUCCESS with an associated
     *         message
     */
    public static ActionResult<String> showInventory(String name, UUID member) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(member).getGrade().hasPermission(EnumPermission.SHOW_CHEST))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        EntityPlayerMP player = ServerUtils.getPlayer(member);
        if (player != null) {
            player.displayGUIChest(faction.getInventory());
        }
        return new ActionResult<String>(EnumActionResult.SUCCESS, String.format(ConfigLang.translate("faction.chest.displayed"), faction.getName()));
    }

    public static ActionResult<String> changeRecruitLink(String name, UUID member, String newLink) {
        boolean admin = EventHandlerAdmin.isAdmin(member);
        if (!admin && !hasUserFaction(member))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.faction.hasnot"));
        if (!doesFactionExist(name))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("faction.inexisting"), name));
        Faction faction = getFaction(name);
        if (!admin && !faction.isMember(member))
            return new ActionResult<String>(EnumActionResult.FAIL, String.format(ConfigLang.translate("player.self.member.isnt"), faction.getName()));
        if (!admin && !faction.getMember(member).getGrade().hasPermission(EnumPermission.INVITE_USER))
            return new ActionResult<String>(EnumActionResult.FAIL, ConfigLang.translate("player.self.permission.hasnt"));
        faction.setRecruitLink(newLink);
        return new ActionResult<String>(EnumActionResult.SUCCESS, ConfigLang.translate("faction.link.recruit.changed"));
    }

    private static boolean isChunkAtEdge(DimensionalPosition position) {
        IChunkManager manager = EventHandlerChunk.getManagerFor(position);
        if (manager instanceof ManagerFaction) {
            ManagerFaction fManager = (ManagerFaction) manager;
            int i = 0;
            int x = position.getPos().x;
            int z = position.getPos().z;
            for(DimensionalPosition pos : fManager.getFaction().getChunks()) {
                if (pos.getDimension() == position.getDimension()) {
                    int x2 = pos.getPos().x;
                    int z2 = pos.getPos().z;
                    if (x2 == x)
                        if (z2 == z - 1 || z2 == z + 1)
                            i++;
                    if (z2 == z)
                        if (x2 == x - 1 || x2 == x + 1)
                            i++;
                }
            }
            return i < 4;
        }
        return false;
    }
}
