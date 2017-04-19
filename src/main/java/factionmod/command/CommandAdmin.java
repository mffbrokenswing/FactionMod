package factionmod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import factionmod.command.utils.UUIDHelper;
import factionmod.handler.EventHandlerAdmin;
import factionmod.utils.MessageHelper;
import factionmod.utils.ServerUtils;

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
					player.sendMessage(MessageHelper.info("You're no longer admin."));
				} else {
					EventHandlerAdmin.addAdmin(player);
					player.sendMessage(MessageHelper.info("You're now an admin. This status will be canceled when you'll disconnect."));
				}
			} else if (args.length >= 1) {
				boolean otherPlayer = args.length > 1;
				EntityPlayerMP target = null;
				if (otherPlayer) {
					target = ServerUtils.getPlayer(UUIDHelper.getUUIDOf(args[1]));
				} else {
					target = player;
				}
				if (target == null) {
					player.sendMessage(MessageHelper.error("This player doesn't exist."));
				} else {
					if (args[0].equalsIgnoreCase("on")) {
						EventHandlerAdmin.addAdmin(target);
						if (target != player) {
							player.sendMessage(MessageHelper.info("The player " + target.getName() + " is now an admin."));
						}
						target.sendMessage(MessageHelper.info("You're now an admin. This status will be canceled when you'll disconnect."));
					} else if (args[0].equalsIgnoreCase("off")) {
						EventHandlerAdmin.removeAdmin(target);
						if (target != player) {
							player.sendMessage(MessageHelper.info("The player " + target.getName() + " is no longer an admin."));
						}
						target.sendMessage(MessageHelper.info("You're no longer an admin."));
					} else {
						throw new WrongUsageException("/admin [on/off] [player]", new Object[0]);
					}
				}
			}
		} else {
			throw new WrongUsageException("You have to be a player", new Object[0]);
		}
	}

}
