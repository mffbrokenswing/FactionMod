package factionmod.network;

import factionmod.faction.Faction;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFaction implements IMessage {

	private boolean			hasFaction;
	private NBTTagCompound	faction;

	public PacketFaction() {
		this.hasFaction = false;
	}

	public PacketFaction(NBTTagCompound faction) {
		this.hasFaction = true;
		this.faction = faction;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.hasFaction);
		if(this.hasFaction) {
			ByteBufUtils.writeTag(buf, this.faction);
		}
	}

	public boolean hasFaction() {
		return this.hasFaction;
	}

	public Faction getFaction() {
		return null;
	}

	public static class Handler implements IMessageHandler<PacketFaction, IMessage> {

		@Override
		public IMessage onMessage(PacketFaction message, MessageContext ctx) {
			return null;
		}

	}

}
