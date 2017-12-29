package factionmod.command;

import factionmod.config.ConfigLang;
import factionmod.handler.EventHandlerAdmin;
import factionmod.utils.MessageHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

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
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        if (sender instanceof EntityPlayerMP)
            return PermissionAPI.hasPermission((EntityPlayerMP) sender, "factionmod.command.admin");
        return true;
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/admin [on/off] [player]";
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (sender instanceof EntityPlayerMP) {
            final EntityPlayerMP player = (EntityPlayerMP) sender;
            if (args.length == 0) {
                if (EventHandlerAdmin.isAdmin(player)) {
                    EventHandlerAdmin.removeAdmin(player);
                    player.sendMessage(MessageHelper.info(ConfigLang.translate("player.self.admin.nolonger")));
                } else {
                    EventHandlerAdmin.addAdmin(player);
                    player.sendMessage(MessageHelper.info(ConfigLang.translate("player.self.admin.became")));
                }
            } else if (args.length >= 1) {
                final boolean otherPlayer = args.length > 1;
                EntityPlayerMP target = null;
                if (otherPlayer)
                    target = getPlayer(server, sender, args[1]);
                else
                    target = player;
                if (args[0].equalsIgnoreCase("on")) {
                    EventHandlerAdmin.addAdmin(target);
                    if (target != player)
                        player.sendMessage(MessageHelper.info(String.format(ConfigLang.translate("player.other.admin.became"), target.getName())));
                    target.sendMessage(MessageHelper.info(ConfigLang.translate("player.self.admin.became")));
                } else if (args[0].equalsIgnoreCase("off")) {
                    EventHandlerAdmin.removeAdmin(target);
                    if (target != player)
                        player.sendMessage(MessageHelper.info(String.format(ConfigLang.translate("player.other.admin.nolonger"), target.getName())));
                    target.sendMessage(MessageHelper.info(ConfigLang.translate("player.self.admin.nolonger")));
                } else
                    throw new WrongUsageException("/admin [on/off] [player]", new Object[0]);
            }
        } else
            throw new WrongUsageException("You have to be a player", new Object[0]);
    }

}
