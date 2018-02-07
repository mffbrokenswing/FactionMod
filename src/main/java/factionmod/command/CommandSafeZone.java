package factionmod.command;

import akka.japi.Pair;
import factionmod.handler.EventHandlerChunk;
import factionmod.manager.IChunkManager;
import factionmod.manager.ManagerSafeZone;
import factionmod.manager.instanciation.ChunkManagerCreator;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * This command is only executable by the operators. It creates a safezone where
 * players can't take damages and affect the world.
 *
 * @author BrokenSwing
 *
 */
public class CommandSafeZone extends CommandBase {

    @Override
    public String getName() {
        return "safezone";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/safezone [remove]";
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        if (sender instanceof EntityPlayerMP)
            return PermissionAPI.hasPermission((EntityPlayerMP) sender, "factionmod.command.safezone");
        return true;
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        final DimensionalPosition position = DimensionalPosition.from(sender);
        if (args.length == 0) {
            if (EventHandlerChunk.getManagerFor(position) != null)
                sender.sendMessage(MessageHelper.error("A chunk manager is already set to this chunk."));
            else {
                String arguments = "";
                for (final String str : args)
                    arguments += str + " ";
                final Pair<IChunkManager, ZoneInstance> pair = ChunkManagerCreator.createChunkHandler("safe", arguments);
                if (pair == null)
                    sender.sendMessage(MessageHelper.warn("Can't create a safezone, this zone doesn't exist anymore. Contact the administrator of the server."));
                else {
                    EventHandlerChunk.registerChunkManager(pair.first(), position, pair.second(), true);
                    sender.sendMessage(MessageHelper.info("A safe zone was created."));
                }
            }
        } else if (args.length >= 1)
            if (args[0].equalsIgnoreCase("remove")) {
                final IChunkManager manager = EventHandlerChunk.getManagerFor(position);
                if (manager == null)
                    sender.sendMessage(MessageHelper.info("Nothing to remove here."));
                else if (!(manager instanceof ManagerSafeZone))
                    sender.sendMessage(MessageHelper.error("This chunk is not a safezone"));
                else {
                    EventHandlerChunk.unregisterChunkManager(position, true);
                    sender.sendMessage(MessageHelper.info("Safe zone removed."));
                }

            } else
                throw new WrongUsageException("/safezone [remove]", new Object[0]);
    }

}
