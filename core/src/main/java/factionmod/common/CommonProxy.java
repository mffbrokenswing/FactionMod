package factionmod.common;

import com.google.common.base.Preconditions;
import factionmod.common.command.CreateFactionCommand;
import factionmod.common.command.ListFactionsCommand;
import factionmod.common.save.ModData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.event.*;

public class CommonProxy {

    private ModData data = null;

    public void preInit(FMLPreInitializationEvent event) {
        FactionConstants.LOGGER = event.getModLog();
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {
        // Loading mod data from map storage
        MapStorage mapStorage = event.getServer().getWorld(0).getMapStorage();
        assert mapStorage != null : "This shouldn't happen";
        this.data = (ModData) mapStorage.getOrLoadData(ModData.class, ModData.ID);
        if(this.data == null) {
            this.data = new ModData(ModData.ID);
            mapStorage.setData(ModData.ID, this.data);
        }

        event.registerServerCommand(new CreateFactionCommand());
        event.registerServerCommand(new ListFactionsCommand());
    }

    public void serverStopping(FMLServerStoppingEvent event) {
        this.data = null;
    }

    public ModData getModData() {
        Preconditions.checkState(data != null, "You shouldn't get mod data while the world isn't loaded.");
        return this.data;
    }

}
