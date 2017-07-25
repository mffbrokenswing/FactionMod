package factionmod.network;

import factionmod.FactionMod;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetwork {

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(FactionMod.MODID);

    public static void registerPackets() {
        NETWORK.registerMessage(PacketUpdateChunkDatas.Handler.class, PacketUpdateChunkDatas.class, 0, Side.CLIENT);
        NETWORK.registerMessage(PacketRemoveChunkData.Handler.class, PacketRemoveChunkData.class, 1, Side.CLIENT);
    }

}
