package factionmod.command;

import static factionmod.handler.EventHandlerFaction.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import com.google.common.collect.Lists;

import factionmod.command.utils.AutoCompleter;
import factionmod.command.utils.UUIDHelper;
import factionmod.config.Config;
import factionmod.config.ConfigLanguage;
import factionmod.enums.EnumPermission;
import factionmod.faction.Faction;
import factionmod.faction.Grade;
import factionmod.handler.EventHandlerAdmin;
import factionmod.handler.EventHandlerFaction;
import factionmod.utils.DimensionalBlockPos;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;

/**
 * This command is executable by any player, it permits to use the faction
 * system.
 * 
 * @author BrokenSwing
 *
 */
public class CommandFaction extends CommandBase {

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public String getName() {
		return "faction";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (EventHandlerAdmin.isAdmin(player.getUniqueID()))
				return "/faction <faction> <disband | invite | leave | open | claim | sethome | info | home | kick | set-grade | chest>";
			if (EventHandlerFaction.hasUserFaction(player.getUniqueID()))
				return "/faction <disband | invite | leave | open | claim | sethome | info | home | kick | set-grade | chest>";
			return "/faction <create | join | info>";
		}
		return "You have to be player.";
	}

	/**
	 * Currently handled commands :
	 * <ul>
	 * <li>{@code /faction create <name> [description]}</li>
	 * <li>{@code /faction disband}</li>
	 * <li>{@code /faction invite <player>}</li>
	 * <li>{@code /faction join <faction>}</li>
	 * <li>{@code /faction open}</li>
	 * <li>{@code /faction leave}</li>
	 * <li>{@code /faction claim}</li>
	 * <li>{@code /faction unclaim}</li>
	 * <li>{@code /faction sethome}</li>
	 * <li>{@code /faction info <faction>}</li>
	 * <li>{@code /faction home}</li>
	 * <li>{@code /faction kick <player>}</li>
	 * <li>{@code /faction set-grade <name> <level> [permissions ...]}</li>
	 * <li>{@code /faction promote <player> <grade>}</li>
	 * <li>{@code /faction remove-grade <grade>}</li>
	 * <li>{@code /faction chest}</li>
	 * </ul>
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP))
			throw new WrongUsageException(this.getUsage(sender));

		EntityPlayerMP player = (EntityPlayerMP) sender;
		boolean admin = EventHandlerAdmin.isAdmin(player.getUniqueID());

		if (admin && args.length < 2 || args.length < 1)
			throw new WrongUsageException(this.getUsage(sender));

		String target = getFactionOf(player.getUniqueID());
		if (admin) {
			target = args[0];
			String[] temp = args;
			args = new String[args.length - 1];
			for(int i = 0; i < args.length; i++) {
				args[i] = temp[i + 1];
			}
		}

		ActionResult<String> result = null;

		// Faction creation
		if (args[0].equalsIgnoreCase("create")) {
			if (args.length >= 2) {
				String name = args[1];
				String desc = "";
				for(int i = 2; i < args.length; i++) {
					desc += args[i] + " ";
				}
				boolean descCutted = false;
				if (desc.length() > Config.factionDescriptionMaxLength) {
					descCutted = true;
					desc.substring(0, 50);
				}
				if (name.length() > Config.factionNameMaxLength) {
					sender.sendMessage(MessageHelper.error(String.format(ConfigLanguage.factionNameLengthWarning, Config.factionNameMaxLength)));
					return;
				}
				ActionResult<String> ret = EventHandlerFaction.createFaction(name, desc, ((EntityPlayer) sender).getPersistentID());
				if (ret.getType().equals(EnumActionResult.SUCCESS)) {
					if (descCutted) {
						sender.sendMessage(MessageHelper.warn(String.format(ConfigLanguage.factionDescriptionLengthWarning, Config.factionDescriptionMaxLength)));
					}
					sender.sendMessage(MessageHelper.info(ret.getResult()));
				} else {
					sender.sendMessage(MessageHelper.error(ret.getResult()));
				}
			} else {
				throw new WrongUsageException("/faction create <name> [desc ...]");
			}
		}

		// Faction disbanding
		else if (args[0].equalsIgnoreCase("disband")) {
			result = EventHandlerFaction.removeFaction(target, player.getUniqueID());
		}

		// Faction invitation
		else if (args[0].equalsIgnoreCase("invite")) {
			if (args.length >= 2) {
				result = EventHandlerFaction.inviteUserToFaction(target, player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]));
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "invite <player>");
			}
		}

		// Faction joining
		else if (args[0].equalsIgnoreCase("join")) {
			if (args.length >= 2) {
				result = EventHandlerFaction.joinFaction(args[1], player.getUniqueID());
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "join <faction>");
			}
		}

		// Faction opening
		else if (args[0].equalsIgnoreCase("open")) {
			result = EventHandlerFaction.open(target, player.getUniqueID());
		}

		// Faction leaving
		else if (args[0].equalsIgnoreCase("leave")) {
			result = EventHandlerFaction.leaveFaction(target, player.getUniqueID());
		}

		// Faction claiming
		else if (args[0].equalsIgnoreCase("claim")) {
			result = EventHandlerFaction.claimChunk(target, player.getUniqueID(), DimensionalPosition.from(player));
		}

		// Faction unclaiming
		else if (args[0].equalsIgnoreCase("unclaim")) {
			result = EventHandlerFaction.unclaimChunk(player.getUniqueID(), DimensionalPosition.from(player));
		}

		// Faction home setting
		else if (args[0].equalsIgnoreCase("sethome")) {
			result = EventHandlerFaction.setHome(target, player.getUniqueID(), DimensionalBlockPos.from(player));
		}

		// Faction informations getting
		else if (args[0].equalsIgnoreCase("info")) {
			if (args.length >= 2) {
				ActionResult<List<String>> ret = EventHandlerFaction.getInformationsAbout(args[1]);
				if (ret.getType() == EnumActionResult.FAIL) {
					player.sendMessage(MessageHelper.error(ret.getResult().get(0)));
				} else {
					for(String message : ret.getResult()) {
						player.sendMessage(new TextComponentString(message));
					}
				}
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "info <faction>");
			}
		}

		// Faction home teleport
		else if (args[0].equalsIgnoreCase("home")) {
			result = EventHandlerFaction.goToHome(target, player);
		}

		// Faction kicking
		else if (args[0].equalsIgnoreCase("kick")) {
			if (args.length >= 2) {
				result = EventHandlerFaction.removeUserFromFaction(target, player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]));
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "kick <player>");
			}
		}

		// Faction grade setting
		else if (args[0].equalsIgnoreCase("set-grade")) {
			if (args.length >= 3) {
				int hierachyLevel = parseInt(args[2]);
				ArrayList<EnumPermission> perms = new ArrayList<EnumPermission>();
				for(int i = 3; i < args.length; i++) {
					for(EnumPermission perm : EnumPermission.values()) {
						if (perm.name().equalsIgnoreCase(args[i]) && !perms.contains(perm)) {
							perms.add(perm);
						}
					}
				}
				result = EventHandlerFaction.setGrade(target, player.getUniqueID(), new Grade(args[1], hierachyLevel, perms.toArray(new EnumPermission[0])));
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "set-grade <name> <level> [permissions ...]");
			}
		}

		// Faction member-promoting
		else if (args[0].equalsIgnoreCase("promote")) {
			if (args.length >= 3) {
				result = EventHandlerFaction.promote(target, player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]), args[2]);
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "promote <player> <grade>");
			}
		}

		// Faction grade removing
		else if (args[0].equalsIgnoreCase("remove-grade")) {
			if (args.length >= 2) {
				result = EventHandlerFaction.removeGrade(target, player.getUniqueID(), args[1]);
			} else {
				throw new WrongUsageException("/faction " + (admin ? "<faction> " : "") + "remove-grade <grade>");
			}
		}

		// Faction chest displaying
		else if (args[0].equalsIgnoreCase("chest")) {
			result = EventHandlerFaction.showInventory(target, player.getUniqueID());
		}

		else {
			throw new WrongUsageException(this.getUsage(sender));
		}

		if (result != null) {
			handleResponse(result, player);
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("f");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		if (!(sender instanceof EntityPlayerMP))
			return new ArrayList<String>();

		EntityPlayerMP player = (EntityPlayerMP) sender;

		if (EventHandlerAdmin.isAdmin(player)) {
			if (args.length == 1)
				return AutoCompleter.completeFactions(args[0]);
			return new ArrayList<String>();
		}

		if (!hasUserFaction(player.getUniqueID())) {
			if (args.length == 1)
				return AutoCompleter.complete(args[0], new String[] { "create", "join", "info" });

			if (args.length == 2)
				if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("info"))
					return AutoCompleter.completeFactions(args[1]);

			if (args[0].equalsIgnoreCase("create")) {
				if (doesFactionExist(args[1]))
					player.sendMessage(MessageHelper.warn(String.format(ConfigLanguage.factionAlreadyExisting, args[1])));
				else if (!args[1].isEmpty())
					player.sendMessage(MessageHelper.info(String.format(ConfigLanguage.factionNameDisponible, args[1])));
				return new ArrayList<String>();
			}
		} else {
			// The player has a faction
			Faction faction = getFaction(getFactionOf(player.getUniqueID()));

			if (args.length == 1)
				return AutoCompleter.complete(args[0], new String[] { "disband", "invite", "open", "leave", "kick", "claim", "unclaim", "sethome", "home", "set-grade", "remove-grade", "promote", "info", "chest" });
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("remove-grade")) {
					List<Grade> grades = faction.getGrades();
					String[] names = new String[grades.size()];
					for(int i = 0; i < names.length; i++) {
						names[i] = grades.get(i).getName();
					}
					return AutoCompleter.complete(args[1], names);
				}

				if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("invite"))
					return AutoCompleter.completePlayer(args[1]);

				if (args[0].equalsIgnoreCase("set-grade")) {
					Grade grade = faction.getGrade(args[1]);
					if (grade != null)
						player.sendMessage(MessageHelper.info(String.format("%s(%s) : %s", grade.getName(), grade.getPriority(), grade.getPermissionsAsString())));
				}

				return new ArrayList<String>();
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("promote")) {
					List<Grade> grades = faction.getGrades();
					String[] names = new String[grades.size() + 1];
					for(int i = 0; i < names.length - 1; i++) {
						names[i] = grades.get(i).getName();
					}
					names[names.length - 1] = Grade.MEMBER.getName();
					return AutoCompleter.complete(args[2], names);
				}
				return new ArrayList<String>();
			}
			if (args.length >= 4) {
				if (args[0].equalsIgnoreCase("set-grade")) {
					return AutoCompleter.complete(args[args.length - 1], EnumPermission.values());
				}
			}
		}
		return new ArrayList<String>();
	}

	private void handleResponse(ActionResult<String> result, EntityPlayerMP player) {
		if (result.getType().equals(EnumActionResult.SUCCESS)) {
			player.sendMessage(MessageHelper.info(result.getResult()));
		} else {
			player.sendMessage(MessageHelper.error(result.getResult()));
		}
	}

}
