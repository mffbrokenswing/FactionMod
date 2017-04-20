package factionmod.network;

import factionmod.handler.ModdedClients;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet sended by the client to inform the server he's modded.
 * 
 * @author BrokenSwing
 *
 */
public class PacketModdedClient implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<PacketModdedClient, IMessage> {

		@Override
		public IMessage onMessage(PacketModdedClient message, MessageContext ctx) {
			ModdedClients.addModdedClient(ctx.getServerHandler().player);
			return null;
		}

	}

}
