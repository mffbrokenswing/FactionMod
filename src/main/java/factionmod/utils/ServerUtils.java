package factionmod.utils;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Contains usefull methods relatives to the {@link MinecraftServer}.
 * 
 * @author BrokenSwing
 *
 */
public class ServerUtils {

	private static MinecraftServer	server;
	private static Profiler			profiler;

	public static void init() {
		server = FMLCommonHandler.instance().getMinecraftServerInstance();
		profiler = server.profiler;
	}

	/**
	 * Returns the instance of the Minecraft server.
	 * 
	 * @return the instance of the server
	 */
	public static MinecraftServer getServer() {
		return server;
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

	/**
	 * Returns the profiler of the server.
	 * 
	 * @return the profiler
	 */
	public static Profiler getProfiler() {
		return profiler;
	}
	
}
