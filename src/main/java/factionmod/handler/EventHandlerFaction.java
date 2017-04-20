package factionmod.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import akka.japi.Pair;

import com.google.common.collect.Lists;

import factionmod.FactionMod;
import factionmod.command.utils.UUIDHelper;
import factionmod.config.Config;
import factionmod.enums.EnumPermission;
import factionmod.enums.EnumRelationType;
import factionmod.faction.Faction;
import factionmod.faction.Grade;
import factionmod.faction.Levels;
import factionmod.faction.Member;
import factionmod.faction.RelationShip;
import factionmod.manager.IChunkManager;
import factionmod.manager.ManagerFaction;
import factionmod.manager.instanciation.ChunkManagerCreator;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalBlockPos;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;
import factionmod.utils.ServerUtils;
import factionmod.utils.TeleportationHelper;

/**
 * It handles the factions. You can process actions on faction through this
 * class.
 * 
 * @author BrokenSwing
 *
 */
@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerFaction {

	private static final HashMap<String, Faction>	factions		= new HashMap<String, Faction>();
	private static final HashMap<UUID, String>		usersFactions	= new HashMap<UUID, String>();

	// ---------- Fonctions modifying directly mappings --------------

	/* Theses fonctions shouldn't be used by an other mod */

	public static void addFaction(Faction faction) {
		factions.put(faction.getName().toLowerCase(), faction);
	}

	private static void removeFaction(Faction faction) {
		factions.remove(faction.getName().toLowerCase());
	}

	public static void addUserToFaction(Faction faction, UUID user) {
		usersFactions.put(user, faction.getName().toLowerCase());
		refreshDisplayNameOf(user);
	}

	private static void removeUser(UUID user) {
		usersFactions.remove(user);
		refreshDisplayNameOf(user);
	}

	public static void clearRegistry() {
		factions.clear();
		usersFactions.clear();
	}

	// ------------ Fonctions returning informations about Maps ------------

	/**
	 * Should not be used from an other class than {@link Config} !
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
		return factions.keySet().contains(factionName.toLowerCase());
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
			event.setDisplayname(TextFormatting.RED.toString() + "[ADMIN-MOD] " + TextFormatting.RESET.toString() + event.getDisplayname());
		}
		if (hasUserFaction(event.getEntityPlayer().getPersistentID())) {
			String factionName = getFactionOf(event.getEntityPlayer().getUniqueID());
			event.setDisplayname("[" + factionName + "] " + event.getDisplayname());
		}
	}

	/**
	 * Used to disable friendly-fire.
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerAttack(AttackEntityEvent event) {
		if (event.getTarget() instanceof EntityPlayer) {
			EntityPlayer target = (EntityPlayer) event.getTarget();
			if (!hasUserFaction(target.getUniqueID()) || !hasUserFaction(event.getEntityPlayer().getUniqueID()))
				return;
			if (getFactionOf(event.getEntityPlayer().getUniqueID()).equalsIgnoreCase(getFactionOf(target.getUniqueID())))
				event.setCanceled(true);
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
			return new ActionResult<String>(EnumActionResult.FAIL, "You are already in a faction.");
		if (doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " already exists.");
		Faction faction = new Faction(name, desc, new Member(owner, Grade.OWNER));
		addFaction(faction);
		addUserToFaction(faction, owner);
		ModdedClients.updateClient(owner);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The faction " + name + " was created.");
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
		if (!hasUserFaction(owner))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist");
		Faction faction = getFaction(name);
		if (!(faction.getMember(owner).getGrade() == Grade.OWNER))
			return new ActionResult<String>(EnumActionResult.FAIL, "You are not the owner of this faction.");
		List<Member> members = Lists.newArrayList(faction.getMembers());
		Member own = faction.getOwner();
		faction.removeMember(own.getUUID());
		ModdedClients.updateClient(own.getUUID());
		broadcastToFaction(faction, "The faction was removed by the owner.", MessageHelper.WARN);
		for(Member m : members) {
			removeUser(m.getUUID());
			ModdedClients.updateClient(m.getUUID());
		}
		removeFaction(faction);
		for(DimensionalPosition position : faction.getChunks()) {
			EventHandlerChunk.unregisterChunkManager(position, true);
		}
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The faction " + faction.getName() + " is now removed.");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		if (hasUserFaction(newMember))
			return new ActionResult<String>(EnumActionResult.FAIL, "This player is already in a faction.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You are not this faction.");
		Member m = faction.getMember(member);
		if (!m.hasPermission(EnumPermission.INVITE_USER))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to invit the player in the faction.");
		boolean invited = faction.toogleInvitation(newMember);
		if (invited) {
			EntityPlayer player = ServerUtils.getPlayer(newMember);
			if (player != null) {
				TextComponentString message = new TextComponentString(MessageHelper.PREFIX + "You are invited to ");
				ITextComponent join = new TextComponentString("join").setStyle(new Style().setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/faction join " + faction.getName())).setUnderlined(true));
				message.appendSibling(join);
				message.appendText(" the faction " + faction.getName() + ".");
				message.setStyle(new Style().setColor(MessageHelper.INFO_COLOR));
				player.sendMessage(message);
			}
		}
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The player is " + (invited ? "" : "no longer ") + "invited in the faction " + name + ".");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You are not in this faction.");
		if (!faction.isMember(toRemove))
			return new ActionResult<String>(EnumActionResult.FAIL, "The player is not in this faction.");
		Member m = faction.getMember(member);
		if (!m.hasPermission(EnumPermission.REMOVE_USER))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to remove a player from the faction.");
		Member mRemove = faction.getMember(toRemove);
		if (!Grade.canAffect(m.getGrade(), mRemove.getGrade()))
			return new ActionResult<String>(EnumActionResult.FAIL, "You are not enought graded.");
		faction.removeMember(toRemove);
		removeUser(toRemove);
		EntityPlayerMP player = ServerUtils.getPlayer(toRemove);
		if (player != null) {
			ModdedClients.updateClient(player);
			player.sendMessage(MessageHelper.error("You're no longer in the faction " + faction.getName() + "."));
		}
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The player is no longer a member of the faction " + name + ".");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You are not this faction.");
		Member m = faction.getMember(member);
		if (!m.hasPermission(EnumPermission.CHANGE_DESCRIPTION))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to change the description of the faction.");
		faction.setDesc(desc);
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The description was changed.");
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
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		Faction faction = getFaction(name);
		if (faction.isMember(uuid))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're already in the faction.");
		if (faction.isOpened() || faction.isInvited(uuid)) {
			broadcastToFaction(faction, "The player " + UUIDHelper.getNameOf(uuid) + " joined the faction.", MessageHelper.INFO);
			faction.addMember(uuid);
			addUserToFaction(faction, uuid);
			ModdedClients.updateFaction(faction);
			return new ActionResult<String>(EnumActionResult.SUCCESS, "You're now a member of the faction " + faction.getName() + ".");
		} else {
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not invited in this faction.");
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
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not a member of this faction.");
		if (faction.getMember(member).getGrade() == Grade.OWNER)
			return new ActionResult<String>(EnumActionResult.FAIL, "You're the owner of the faction, use the disband command.");
		faction.removeMember(member);
		removeUser(member);
		ModdedClients.updateFaction(faction);
		broadcastToFaction(faction, "The player " + UUIDHelper.getNameOf(member) + " left the faction.", MessageHelper.INFO);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "You're no longer a member of the faction " + name + ".");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exist.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not a member of this faction.");
		if (!faction.getMember(member).hasPermission(EnumPermission.CLAIM_CHUNK))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to claim chunks.");
		IChunkManager manager = EventHandlerChunk.getManagerFor(position);
		if (manager != null)
			return new ActionResult<String>(EnumActionResult.FAIL, "You can't claim this chunk.");
		if (faction.getChunks().size() >= Levels.getMaximumChunksForLevel(faction.getLevel()))
			return new ActionResult<String>(EnumActionResult.FAIL, "You can't claim more chunks.");
		Pair<IChunkManager, ZoneInstance> pair = ChunkManagerCreator.createChunkHandler("faction", faction.getName());
		EventHandlerChunk.registerChunkManager(pair.first(), position, pair.second(), true);
		faction.addChunk(position);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "Chunk claimed !");
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
			return new ActionResult<String>(EnumActionResult.FAIL, "This chunk isn't claimed.");
		ManagerFaction m = (ManagerFaction) manager;
		Faction faction = m.getFaction();
		if (!faction.isMember(member) && !isAdmin)
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not a member of this faction.");
		if (!faction.getMember(member).hasPermission(EnumPermission.CLAIM_CHUNK) && !isAdmin)
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to unclaim chunks.");
		EventHandlerChunk.unregisterChunkManager(position, true);
		faction.removeChunk(position);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "Chunk unclaimed !");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exists.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		if (!faction.getMember(member).hasPermission(EnumPermission.SET_HOME))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to set the home of the faction.");
		if (!faction.getChunks().contains(position.toDimensionnalPosition()))
			return new ActionResult<String>(EnumActionResult.FAIL, "This chunk isn't claimed by your faction.");
		faction.setHome(position);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The home of your faction changed.");
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
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exists.");
		Faction faction = getFaction(name);
		if (!faction.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		if (!faction.getMember(member).hasPermission(EnumPermission.INVITE_USER))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to do that.");
		faction.setOpened(!faction.isOpened());
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "Your faction is now " + (faction.isOpened() ? "opened." : "closed."));
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
			return new ActionResult<List<String>>(EnumActionResult.FAIL, Lists.newArrayList("The faction " + name + " doesn't exists."));
		List<String> list = Lists.newArrayList();
		Faction faction = getFaction(name);
		list.add("****** " + faction.getName() + " ******");
		if (!faction.getDesc().isEmpty())
			list.add("Description : " + faction.getDesc());
		list.add("Members : " + faction.getMembers().size());
		list.add("Level : " + faction.getLevel());
		list.add("Experience : " + faction.getExp() + "/" + Levels.getExpNeededForLevel(faction.getLevel() + 1));
		list.add("Chunks : " + faction.getChunks().size() + "/" + Levels.getMaximumChunksForLevel(faction.getLevel()));
		list.add("Opened : " + (faction.isOpened() ? "yes" : "no"));
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
	public static ActionResult<String> goToHome(String name, EntityPlayer player) {
		if (!hasUserFaction(player.getUniqueID()))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exists.");
		Faction faction = getFaction(name);
		if (!faction.isMember(player.getUniqueID()))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		DimensionalBlockPos pos = faction.getHome();
		if (pos == null)
			return new ActionResult<String>(EnumActionResult.FAIL, "This faction doesn't have a home.");
		if (player.getEntityWorld().provider.getDimension() != pos.getDimension())
			return new ActionResult<String>(EnumActionResult.FAIL, "You're in the wrong dimension.");
		TeleportationHelper.teleport(player, pos.getPosition(), 10);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "You will be teleported to your faction.");
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
		if (!hasUserFaction(executor))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(factionName))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + factionName + " doesn't exists.");
		Faction faction = getFaction(factionName);
		if (!faction.isMember(executor))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		Member member = faction.getMember(executor);
		if (member.getGrade() != Grade.OWNER)
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not the owner of the faction.");
		if (grade.getName().equalsIgnoreCase(Grade.OWNER.getName()) || grade.getName().equalsIgnoreCase(Grade.MEMBER.getName()))
			return new ActionResult<String>(EnumActionResult.FAIL, "This name isn't avalaible. Choose an other one.");
		if (grade.getPriority() < 1)
			return new ActionResult<String>(EnumActionResult.FAIL, "The hierarchy level has to be higher than 0.");
		faction.addGrade(grade);
		String perms = "";
		for(EnumPermission p : grade.getPermissions()) {
			perms += " " + p.name();
		}
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The grade " + grade.getName() + " has now a level of " + grade.getPriority() + " with " + (perms.isEmpty() ? "no " : "") + "permissions" + perms + ".");
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
		if (!hasUserFaction(executor))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction");
		if (!doesFactionExist(name))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + name + " doesn't exists.");
		Faction faction = getFaction(name);
		if (!faction.isMember(executor))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		if (!faction.isMember(executed))
			return new ActionResult<String>(EnumActionResult.FAIL, "This player is not in this faction.");
		if (!faction.getMember(executor).hasPermission(EnumPermission.PROMOTE))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to do that.");
		Member mExecutor = faction.getMember(executor);
		Member mExecuted = faction.getMember(executed);
		if (!Grade.canAffect(mExecutor.getGrade(), mExecuted.getGrade()))
			return new ActionResult<String>(EnumActionResult.FAIL, "You can't promote this player.");
		Grade grade = null;
		if (Grade.MEMBER.getName().equalsIgnoreCase(gradeName)) {
			grade = Grade.MEMBER;
		} else {
			grade = faction.getGrade(gradeName);
		}
		if (grade == null)
			return new ActionResult<String>(EnumActionResult.FAIL, "This grade doesn't exist.");
		mExecuted.setGrade(grade);
		EntityPlayer player = ServerUtils.getPlayer(executed);
		if (player != null) {
			player.sendMessage(MessageHelper.info("You were promoted to " + grade.getName() + "."));
		}
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "This player have been promoted to " + grade.getName() + ".");
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
		if (!hasUserFaction(owner))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(factionName))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + factionName + " doesn't exists.");
		Faction faction = getFaction(factionName);
		if (!faction.isMember(owner))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		Member member = faction.getMember(owner);
		if (member.getGrade() != Grade.OWNER)
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not the owner of the faction.");
		Grade g = faction.getGrade(gradeName);
		if (g == null)
			return new ActionResult<String>(EnumActionResult.FAIL, "This grade doesn't exist.");
		faction.removeGrade(g);
		ModdedClients.updateFaction(faction);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "The grade " + g.getName() + " was removed.");
	}

	/**
	 * Creates a relations between to factions.
	 * 
	 * @param faction
	 *            The name of the faction which wants to create the relation.
	 * @param faction2
	 *            The name of the faction which will be in relation with the
	 *            first one
	 * @param member
	 *            The UUID of the member of the first faction who executes the
	 *            command
	 * @param type
	 *            The type of relation wanted
	 * @return the result of the action : FAIL or SUCCESS with an associated
	 *         message
	 */
	public static ActionResult<String> createRelation(String faction1, String faction2, UUID member, EnumRelationType type) {
		if (!hasUserFaction(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in a faction.");
		if (!doesFactionExist(faction1))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + faction1 + " doesn't exists.");
		Faction factionOne = getFaction(faction1);
		if (!factionOne.isMember(member))
			return new ActionResult<String>(EnumActionResult.FAIL, "You're not in this faction.");
		Member m = factionOne.getMember(member);
		if (!m.getGrade().hasPermission(EnumPermission.CREATE_RELATION))
			return new ActionResult<String>(EnumActionResult.FAIL, "You don't have the permission to do that.");
		if (!doesFactionExist(faction2))
			return new ActionResult<String>(EnumActionResult.FAIL, "The faction " + faction2 + " doesn't exists.");
		if (!type.isResponseNeeded()) {
			EventHandlerRelation.setRelation(factionOne, getFaction(faction2), type);
			return new ActionResult<String>(EnumActionResult.SUCCESS, "The relation " + type.getDisplayName() + " was created with the faction " + faction2 + ".");
		}
		Faction factionTwo = getFaction(faction2);
		RelationShip relation = EventHandlerRelation.getPending(factionOne, factionTwo);
		if (relation != null) {
			if (relation.getType() != type || relation.getSecondFaction().equalsIgnoreCase(factionOne.getName())) {
				EventHandlerRelation.setPending(factionOne, factionTwo, type);
				broadcastToFaction(factionTwo, "The faction " + factionOne.getName() + " proposed you the relation " + type.getDisplayName() + ".", MessageHelper.INFO);
				return new ActionResult<String>(EnumActionResult.SUCCESS, "You sent a request to the faction " + factionTwo.getName() + " to have the relation " + type.getDisplayName() + ".");
			}
			EventHandlerRelation.setRelation(factionOne, factionTwo, type);
			broadcastToFaction(factionTwo, "Your relation with the faction " + factionOne.getName() + " is now " + type.getDisplayName(), MessageHelper.INFO);
			broadcastToFaction(factionOne, "Your relation with the faction " + factionTwo.getName() + " is now " + type.getDisplayName(), MessageHelper.INFO);
			return new ActionResult<String>(EnumActionResult.SUCCESS, "You accepted the relation with the faction " + factionTwo.getName() + ".");
		}
		EventHandlerRelation.setPending(factionOne, factionTwo, type);
		broadcastToFaction(factionTwo, "The faction " + factionOne.getName() + " proposed you the relation " + type.getDisplayName() + ".", MessageHelper.INFO);
		return new ActionResult<String>(EnumActionResult.SUCCESS, "You sent a request to the faction " + factionTwo.getName() + " to have the relation " + type.getDisplayName() + ".");
	}

}
