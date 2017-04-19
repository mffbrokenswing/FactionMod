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
import factionmod.enums.EnumPermission;
import factionmod.enums.EnumRelationType;
import factionmod.faction.Faction;
import factionmod.faction.Grade;
import factionmod.handler.EventHandlerFaction;
import factionmod.utils.DimensionalBlockPos;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;

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
		return "/faction";
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
	 * </ul>
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (args.length >= 1) {

				// Faction creation
				if (args[0].equalsIgnoreCase("create")) {
					if (args.length >= 2) {
						String name = args[1];
						String desc = "";
						for(int i = 2; i < args.length; i++) {
							desc += args[i] + " ";
						}
						boolean descCutted = false;
						if (desc.length() > 50) {
							descCutted = true;
							desc.substring(0, 50);
						}
						if (name.length() > 15) {
							sender.sendMessage(MessageHelper.error("The maximum length of the name of the faction is 15."));
							return;
						}
						ActionResult<String> result = EventHandlerFaction.createFaction(name, desc, ((EntityPlayer) sender).getPersistentID());
						if (result.getType().equals(EnumActionResult.SUCCESS)) {
							if (descCutted) {
								sender.sendMessage(MessageHelper.warn("The description has too many characters, it will be automatically reduced."));
							}
							sender.sendMessage(MessageHelper.info("The faction " + name + " now exists."));
						} else {
							sender.sendMessage(MessageHelper.error(result.getResult()));
						}
					} else {
						throw new WrongUsageException("/faction create <name> [desc ...]");
					}
				}

				// Faction disbanding
				else if (args[0].equalsIgnoreCase("disband")) {
					ActionResult<String> result = EventHandlerFaction.removeFaction(getFactionOf(player.getUniqueID()), player.getUniqueID());
					handleResponse(result, player);
				}

				// Faction invitation
				else if (args[0].equalsIgnoreCase("invite")) {
					if (args.length >= 2) {
						ActionResult<String> result = EventHandlerFaction.inviteUserToFaction(getFactionOf(player.getUniqueID()), player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]));
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction invite <player>", new Object[0]);
					}
				}

				// Faction joining
				else if (args[0].equalsIgnoreCase("join")) {
					if (args.length >= 2) {
						ActionResult<String> result = EventHandlerFaction.joinFaction(args[1], player.getUniqueID());
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction join <faction>", new Object[0]);
					}
				}

				// Faction opening
				else if (args[0].equalsIgnoreCase("open")) {
					ActionResult<String> result = EventHandlerFaction.open(getFactionOf(player.getUniqueID()), player.getUniqueID());
					handleResponse(result, player);
				}

				// Faction leaving
				else if (args[0].equalsIgnoreCase("leave")) {
					ActionResult<String> result = EventHandlerFaction.leaveFaction(getFactionOf(player.getUniqueID()), player.getUniqueID());
					handleResponse(result, player);
				}

				// Faction claiming
				else if (args[0].equalsIgnoreCase("claim")) {
					ActionResult<String> result = EventHandlerFaction.claimChunk(getFactionOf(player.getUniqueID()), player.getUniqueID(), DimensionalPosition.from(player));
					handleResponse(result, player);
				}

				// Faction unclaiming
				else if (args[0].equalsIgnoreCase("unclaim")) {
					ActionResult<String> result = EventHandlerFaction.unclaimChunk(player.getUniqueID(), DimensionalPosition.from(player));
					handleResponse(result, player);
				}

				// Faction home setting
				else if (args[0].equalsIgnoreCase("sethome")) {
					ActionResult<String> result = EventHandlerFaction.setHome(getFactionOf(player.getUniqueID()), player.getUniqueID(), DimensionalBlockPos.from(player));
					handleResponse(result, player);
				}

				// Faction informations getting
				else if (args[0].equalsIgnoreCase("info")) {
					if (args.length >= 2) {
						ActionResult<List<String>> result = EventHandlerFaction.getInformationsAbout(args[1]);
						if (result.getType() == EnumActionResult.FAIL) {
							player.sendMessage(MessageHelper.error(result.getResult().get(0)));
						} else {
							for(String message : result.getResult()) {
								player.sendMessage(new TextComponentString(message));
							}
						}
					} else {
						throw new WrongUsageException("/faction info <faction>", new Object[0]);
					}
				}

				// Faction home teleport
				else if (args[0].equalsIgnoreCase("home")) {
					ActionResult<String> result = EventHandlerFaction.goToHome(getFactionOf(player.getUniqueID()), player);
					handleResponse(result, player);
				}

				// Faction kicking
				else if (args[0].equalsIgnoreCase("kick")) {
					if (args.length >= 2) {
						ActionResult<String> result = EventHandlerFaction.removeUserFromFaction(getFactionOf(player.getUniqueID()), player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]));
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction kick <player>", new Object[0]);
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
						ActionResult<String> result = EventHandlerFaction.setGrade(getFactionOf(player.getUniqueID()), player.getUniqueID(), new Grade(args[1], hierachyLevel, perms.toArray(new EnumPermission[0])));
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction set-grade <name> <level> [permissions ...]", new Object[0]);
					}
				}

				// Faction member-promoting
				else if (args[0].equalsIgnoreCase("promote")) {
					if (args.length >= 3) {
						ActionResult<String> result = EventHandlerFaction.promote(getFactionOf(player.getUniqueID()), player.getUniqueID(), UUIDHelper.getUUIDOf(args[1]), args[2]);
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction promote <player> <grade>", new Object[0]);
					}
				}

				// Faction grade removing
				else if (args[0].equalsIgnoreCase("remove-grade")) {
					if (args.length >= 2) {
						ActionResult<String> result = EventHandlerFaction.removeGrade(getFactionOf(player.getUniqueID()), player.getUniqueID(), args[1]);
						handleResponse(result, player);
					} else {
						throw new WrongUsageException("/faction remove-grade <grade>", new Object[0]);
					}
				}

				// Faction relation set
				else if (args[0].equalsIgnoreCase("set-relation")) {
					if (args.length >= 3) {
						EnumRelationType type = null;
						for(EnumRelationType t : EnumRelationType.values()) {
							if (t.name().equalsIgnoreCase(args[2])) {
								type = t;
								break;
							}
						}
						if (type == null) {
							sender.sendMessage(MessageHelper.error("The relation " + args[2] + " doesn't exists."));
						} else {
							ActionResult<String> result = EventHandlerFaction.createRelation(getFactionOf(player.getUniqueID()), args[1], player.getUniqueID(), type);
							handleResponse(result, player);
						}
					} else {
						throw new WrongUsageException("/faction set-relation <faction> <relation>", new Object[0]);
					}
				}

				// Giving experience
				else if (args[0].equals("exp")) {
					Faction f = EventHandlerFaction.getFaction(getFactionOf(player.getUniqueID()));
					if (f != null) {
						f.increaseExp(200);
					}
				}

				else {
					if (!EventHandlerFaction.hasUserFaction(player.getUniqueID())) {
						throw new WrongUsageException("/faction <create | join | info>", new Object[0]);
					} else {
						throw new WrongUsageException("/faction <disband | invite | leave | open | claim | sethome | info | home | kick | set-grade>", new Object[0]);
					}
				}
			} else {
				throw new WrongUsageException("You should add some arguments ...");
			}
		} else {
			sender.sendMessage(MessageHelper.error("You have to be a player."));
		}
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("f");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			if (!EventHandlerFaction.hasUserFaction(player.getUniqueID())) {
				if (args.length == 1) {
					return AutoCompleter.complete(args[0], new String[] { "create", "join", "info" });
				}
			} else {
				Faction faction = EventHandlerFaction.getFaction(getFactionOf(player.getUniqueID()));
				if (args.length == 1) {
					return AutoCompleter.complete(args[0], new String[] { "disband", "invite", "open", "leave", "kick", "claim", "unclaim", "sethome", "home", "set-grade", "remove-grade", "promote", "info" });
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("remove-grade")) {
						List<Grade> grades = faction.getGrades();
						String[] names = new String[grades.size() + 1];
						for(int i = 0; i < names.length - 1; i++) {
							names[i] = grades.get(i).getName();
						}
						names[names.length - 1] = Grade.MEMBER.getName();
						return AutoCompleter.complete(args[1], names);
					} else if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("kick") || args[0].equalsIgnoreCase("invite")) {
						return AutoCompleter.completePlayer(args[1]);
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("promote")) {
						List<Grade> grades = faction.getGrades();
						String[] names = new String[grades.size() + 1];
						for(int i = 0; i < names.length - 1; i++) {
							names[i] = grades.get(i).getName();
						}
						names[names.length - 1] = Grade.MEMBER.getName();
						return AutoCompleter.complete(args[2], names);
					} else if (args[0].equalsIgnoreCase("set-relation")) {
						return AutoCompleter.complete(args[2], EnumRelationType.values());
					}
				} else if (args.length >= 4) {
					if (args[0].equalsIgnoreCase("set-grade")) {
						return AutoCompleter.complete(args[args.length - 1], EnumPermission.values());
					}
				}
			}
		}
		return new ArrayList<String>();
	}

	public void handleResponse(ActionResult<String> result, EntityPlayerMP player) {
		if (result.getType().equals(EnumActionResult.SUCCESS)) {
			player.sendMessage(MessageHelper.info(result.getResult()));
		} else {
			player.sendMessage(MessageHelper.error(result.getResult()));
		}
	}

}
