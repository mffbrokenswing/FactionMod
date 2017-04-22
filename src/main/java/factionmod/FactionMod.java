package factionmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import org.apache.logging.log4j.Logger;

import factionmod.command.CommandAdmin;
import factionmod.command.CommandFaction;
import factionmod.command.CommandSafeZone;
import factionmod.command.CommandWarZone;
import factionmod.config.Config;
import factionmod.data.InventoryData;
import factionmod.handler.EventHandlerChunk;
import factionmod.handler.EventHandlerFaction;
import factionmod.handler.EventHandlerRelation;
import factionmod.network.PacketRegistering;

/**
 * This is the main class of the Mod.
 * 
 * @author BrokenSwing
 *
 */
@Mod(modid = FactionMod.MODID, useMetadata = true, serverSideOnly = true, acceptableRemoteVersions = "*")
public class FactionMod {

	public static final String			MODID	= "facmod";

	private static String				configDir;
	private static Logger				logger	= null;
	private static SimpleNetworkWrapper	network;

	/**
	 * Returns the network of the mod. Its name is the same as the mod ID of the
	 * mod.
	 * 
	 * @return the network
	 */
	public static SimpleNetworkWrapper getNetwork() {
		return network;
	}

	/**
	 * Returns the logger of the mod.
	 * 
	 * @return ht logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * Indicates the path to the directory containing all configuration files of
	 * the mod.
	 * 
	 * @return the path to the configuration directory
	 */
	public static String getConfigDir() {
		return configDir + "/factionmod";
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		configDir = event.getSuggestedConfigurationFile().getParentFile().getAbsolutePath();
		Config.initDirectory();
		Config.loadConfigFile();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		Config.loadZones("zones.json");
		network = NetworkRegistry.INSTANCE.newSimpleChannel(FactionMod.MODID);
		PacketRegistering.registerPackets(network);
	}

	@EventHandler
	public void onIMCMessage(IMCEvent event) {
		for(IMCMessage message : event.getMessages()) {
			if (message.isStringMessage()) {
				String fileName = message.getStringValue();
				if (!fileName.contains("\\") && !fileName.contains("/")) {
					Config.loadZones(fileName);
				} else {
					FactionMod.getLogger().warn("The file containing the zones have to be directly in the config directory.");
				}
			}
		}
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSafeZone());
		event.registerServerCommand(new CommandWarZone());
		event.registerServerCommand(new CommandFaction());
		event.registerServerCommand(new CommandAdmin());
		Config.loadFactions();
		Config.loadChunkManagers();
		Config.loadRelations();
		InventoryData.load(event);
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		Config.saveChunkManagers();
		Config.saveFactions();
		Config.saveRelations();
		EventHandlerChunk.clearRegistry();
		EventHandlerFaction.clearRegistry();
		EventHandlerRelation.clearRegistry();
		InventoryData.save();
	}

}
