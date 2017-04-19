package factionmod.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Contains usefull methods relatives to the {@link MinecraftServer}.
 * 
 * @author BrokenSwing
 *
 */
public class ServerUtils {

	/**
	 * Returns the instance of the Minecraft server.
	 * 
	 * @return the instance of the server
	 */
	public static MinecraftServer getServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance();
	}

	/**
	 * Returns the player with the given {@link UUID}
	 * 
	 * @param uuid
	 *            The UUID
	 * @return the player
	 */
	public static EntityPlayerMP getPlayer(UUID uuid) {
		return getServer().getPlayerList().getPlayerByUUID(uuid);
	}

}
