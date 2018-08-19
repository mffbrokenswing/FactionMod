package factionmod;

import factionmod.common.CommonProxy;
import factionmod.common.FactionConstants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;

@Mod(
        modid = FactionConstants.MOD_ID,
        name = FactionConstants.MOD_NAME,
        version = FactionConstants.VERSION,
        useMetadata = true
)
public class FactionMod {

    @SidedProxy(clientSide = "factionmod.client.ClientProxy", serverSide = "factionmod.server.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FactionMod.proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FactionMod.proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        FactionMod.proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        FactionMod.proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        FactionMod.proxy.serverStopping(event);
    }

}
