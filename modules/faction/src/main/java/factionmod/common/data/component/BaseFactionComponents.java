package factionmod.common.data.component;

import factionmod.common.FactionConstants;
import factionmod.common.event.DataStructurePopulationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FactionConstants.MOD_ID)
public class BaseFactionComponents {

    public static final DataComponent<String> NAME = DataComponent.ofString(FactionConstants.MOD_ID, "name", "");

    @SubscribeEvent
    public static void registerFactionComponents(DataStructurePopulationEvent event) {
        if(event.getStructureName().equals("faction")) {
            event.registerComponent(NAME);
        }
    }

}
