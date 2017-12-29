package factionmod.command;

import java.util.List;

import com.google.common.collect.Lists;

import factionmod.config.ConfigLoader;
import factionmod.utils.MessageHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;

public class CommandReloadConfig implements ICommand {

    @Override
    public int compareTo(final ICommand arg0) {
        return 0;
    }

    @Override
    public String getName() {
        return "freload";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/freload";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        ConfigLoader.loadConfigFile();
        sender.sendMessage(MessageHelper.info("Configuration reloaded !"));
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        if (sender instanceof EntityPlayerMP)
            return PermissionAPI.hasPermission((EntityPlayerMP) sender, "factionmod.command.freload");
        return true;
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, final BlockPos targetPos) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(final String[] args, final int index) {
        return false;
    }

}
