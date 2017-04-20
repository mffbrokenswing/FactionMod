package factionmod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import akka.japi.Pair;
import factionmod.handler.EventHandlerChunk;
import factionmod.manager.IChunkManager;
import factionmod.manager.ManagerSafeZone;
import factionmod.manager.instanciation.ChunkManagerCreator;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.MessageHelper;

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
	public String getUsage(ICommandSender sender) {
		return "/safezone [remove]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP))
			throw new WrongUsageException("You have to be a player", new Object[0]);

		DimensionalPosition position = DimensionalPosition.from(sender);
		if (args.length == 0) {
			if (EventHandlerChunk.getManagerFor(position) != null) {
				sender.sendMessage(MessageHelper.error("A chunk manager is already set to this chunk."));
			} else {
				String arguments = "";
				for(String str : args) {
					arguments += str + " ";
				}
				Pair<IChunkManager, ZoneInstance> pair = ChunkManagerCreator.createChunkHandler("safe", arguments);
				if (pair == null) {
					sender.sendMessage(MessageHelper.warn("Can't create a safezone, this zone doesn't exist anymore. Contact the administrator of the server."));
				} else {
					EventHandlerChunk.registerChunkManager(pair.first(), position, pair.second(), true);
					sender.sendMessage(MessageHelper.info("A safe zone was created."));
				}
			}
		} else if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("remove")) {
				IChunkManager manager = EventHandlerChunk.getManagerFor(position);
				if (manager == null)
					sender.sendMessage(MessageHelper.info("Nothing to remove here."));
				else if (!(manager instanceof ManagerSafeZone))
					sender.sendMessage(MessageHelper.error("This chunk is not a safezone"));
				else {
					EventHandlerChunk.unregisterChunkManager(position, true);
					sender.sendMessage(MessageHelper.info("Safe zone removed."));
				}

			} else {
				throw new WrongUsageException("/safezone [remove]", new Object[0]);
			}
		}
	}

}
