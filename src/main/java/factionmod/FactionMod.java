package factionmod;

import org.apache.logging.log4j.Logger;

import factionmod.command.CommandAdmin;
import factionmod.command.CommandFaction;
import factionmod.command.CommandReloadConfig;
import factionmod.command.CommandSafeZone;
import factionmod.command.CommandWarZone;
import factionmod.config.Config;
import factionmod.data.FactionModDatas;
import factionmod.handler.EventHandlerChunk;
import factionmod.utils.ServerUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * This is the main class of the Mod.
 * 
 * @author BrokenSwing
 *
 */
@Mod(modid = FactionMod.MODID, useMetadata = true, serverSideOnly = true, acceptableRemoteVersions = "*")
public class FactionMod {

    public static final String          MODID  = "facmod";

    private static String               configDir;
    private static Logger               logger = null;
    private static SimpleNetworkWrapper network;

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
        ServerUtils.init(); // Can't profile before

        ServerUtils.getProfiler().startSection(MODID);

        Config.initDirectory();
        Config.loadConfigFile();

        ServerUtils.getProfiler().endSection();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ServerUtils.getProfiler().startSection(MODID);
        ServerUtils.getProfiler().startSection("configuration");
        
        PermissionAPI.registerNode("factionmod.command.freload", DefaultPermissionLevel.OP, "Permission to execute the command /freload");
        PermissionAPI.registerNode("factionmod.command.admin", DefaultPermissionLevel.OP, "Permission to execute the command /admin");
        PermissionAPI.registerNode("factionmod.command.faction", DefaultPermissionLevel.ALL, "Permission to execute the command /faction");
        PermissionAPI.registerNode("factionmod.command.safezone", DefaultPermissionLevel.OP, "Permission to execute the command /safezone");
        PermissionAPI.registerNode("factionmod.command.warzone", DefaultPermissionLevel.OP, "Permission to execute the command /warzone");

        Config.loadZones("zones.json");

        ServerUtils.getProfiler().endSection();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(FactionMod.MODID);

        ServerUtils.getProfiler().endSection();
    }

    @EventHandler
    public void onIMCMessage(IMCEvent event) {
        ServerUtils.getProfiler().startSection(MODID);
        ServerUtils.getProfiler().startSection("IMCMessagesHandling");

        for(IMCMessage message : event.getMessages()) {
            if (message.isStringMessage()) {
                String fileName = message.getStringValue();
                if (!fileName.contains("\\") && !fileName.contains("/")) {
                    ServerUtils.getProfiler().startSection("configuration");
                    Config.loadZones(fileName);
                    ServerUtils.getProfiler().endSection();
                } else {
                    FactionMod.getLogger().warn("The file containing the zones have to be directly in the config directory.");
                }
            }
        }

        ServerUtils.getProfiler().endSection();
        ServerUtils.getProfiler().endSection();
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        ServerUtils.getProfiler().startSection(MODID);
        ServerUtils.getProfiler().startSection("commandsRegistering");

        if (EventHandlerChunk.getZone("safe") != null)
            event.registerServerCommand(new CommandSafeZone());
        if (EventHandlerChunk.getZone("war") != null)
            event.registerServerCommand(new CommandWarZone());
        if (EventHandlerChunk.getZone("faction") != null)
            event.registerServerCommand(new CommandFaction());
        event.registerServerCommand(new CommandAdmin());
        event.registerServerCommand(new CommandReloadConfig());

        ServerUtils.getProfiler().endStartSection("loadWorldSavedDatas");

        FactionModDatas.load();

        ServerUtils.getProfiler().endSection();
        ServerUtils.getProfiler().endSection();
    }

}
