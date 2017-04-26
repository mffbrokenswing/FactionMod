package factionmod.network;

import factionmod.utils.ServerUtils;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Registers all the packets to the network.
 * 
 * @author BrokenSwing
 *
 */
public class PacketRegistering {

	public static void registerPackets(SimpleNetworkWrapper network) {
		ServerUtils.getProfiler().startSection("network");
		ServerUtils.getProfiler().startSection("packetsRegistering");

		network.registerMessage(PacketModdedClient.Handler.class, PacketModdedClient.class, 0, Side.SERVER);
		network.registerMessage(PacketFaction.Handler.class, PacketFaction.class, 1, Side.CLIENT);
		network.registerMessage(PacketFactionInformations.Handler.class, PacketFactionInformations.class, 2, Side.SERVER);
		network.registerMessage(PacketFactionInformations.Handler.class, PacketFactionInformations.class, 3, Side.CLIENT);
		network.registerMessage(PacketExperience.Handler.class, PacketExperience.class, 4, Side.CLIENT);

		ServerUtils.getProfiler().endSection();
		ServerUtils.getProfiler().endSection();
	}

}
