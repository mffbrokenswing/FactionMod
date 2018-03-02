package factionmod.network;

import factionmod.FactionMod;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ModNetwork {

    public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper(FactionMod.MODID);

    public static void registerPackets() {}

}
