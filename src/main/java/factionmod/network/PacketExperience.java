package factionmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExperience implements IMessage {

	private int	exp;
	private int	level;

	public PacketExperience() {}

	public PacketExperience(int exp, int level) {
		this.exp = exp;
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.exp = buf.readInt();
		this.level = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.exp);
		buf.writeInt(this.level);
	}

	public static class Handler implements IMessageHandler<PacketExperience, IMessage> {

		@Override
		public IMessage onMessage(PacketExperience message, MessageContext ctx) {
			return null;
		}

	}

}
