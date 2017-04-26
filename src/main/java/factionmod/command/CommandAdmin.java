package factionmod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import factionmod.config.ConfigLanguage;
import factionmod.handler.EventHandlerAdmin;
import factionmod.utils.MessageHelper;

/**
 * This command is only executable by the operators. It allows to become an
 * admin of the faction mod. Be an admin permit you to manage the mod.
 * 
 * @author BrokenSwing
 *
 */
public class CommandAdmin extends CommandBase {

	@Override
	public String getName() {
		return "admin";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/admin [on/off] [player]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (args.length == 0) {
				if (EventHandlerAdmin.isAdmin(player)) {
					EventHandlerAdmin.removeAdmin(player);
					player.sendMessage(MessageHelper.info(ConfigLanguage.noLongerAdmin));
				} else {
					EventHandlerAdmin.addAdmin(player);
					player.sendMessage(MessageHelper.info(ConfigLanguage.nowAdmin));
				}
			} else if (args.length >= 1) {
				boolean otherPlayer = args.length > 1;
				EntityPlayerMP target = null;
				if (otherPlayer) {
					target = getPlayer(server, sender, args[1]);
				} else {
					target = player;
				}
				if (args[0].equalsIgnoreCase("on")) {
					EventHandlerAdmin.addAdmin(target);
					if (target != player) {
						player.sendMessage(MessageHelper.info(String.format(ConfigLanguage.playerNowAdmin, target.getName())));
					}
					target.sendMessage(MessageHelper.info(ConfigLanguage.nowAdmin));
				} else if (args[0].equalsIgnoreCase("off")) {
					EventHandlerAdmin.removeAdmin(target);
					if (target != player) {
						player.sendMessage(MessageHelper.info(String.format(ConfigLanguage.playerNoLongerAdmin, target.getName())));
					}
					target.sendMessage(MessageHelper.info(ConfigLanguage.noLongerAdmin));
				} else {
					throw new WrongUsageException("/admin [on/off] [player]", new Object[0]);
				}
			}
		} else {
			throw new WrongUsageException("You have to be a player", new Object[0]);
		}
	}

}
