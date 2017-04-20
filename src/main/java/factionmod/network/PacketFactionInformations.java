package factionmod.network;

import factionmod.faction.Faction;
import factionmod.handler.EventHandlerFaction;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sends informations about a faction to the client.
 * 
 * @author BrokenSwing
 *
 */
public class PacketFactionInformations implements IMessage {

	private String	name;
	private String	description;

	private int		level;
	private int		exp;
	private int		chunks;
	private int		memberCount;

	private boolean	opened;

	public PacketFactionInformations() {}

	public PacketFactionInformations(String name, String description, int level, int exp, int chunks, int memberCount, boolean opened) {
		this.name = name;
		this.description = description;
		this.level = level;
		this.exp = exp;
		this.chunks = chunks;
		this.memberCount = memberCount;
		this.opened = opened;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getLevel() {
		return level;
	}

	public int getExp() {
		return exp;
	}

	public int getChunks() {
		return chunks;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public boolean isOpened() {
		return opened;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.name = ByteBufUtils.readUTF8String(buf);
		this.description = ByteBufUtils.readUTF8String(buf);
		this.level = buf.readInt();
		this.exp = buf.readInt();
		this.chunks = buf.readInt();
		this.memberCount = buf.readInt();
		this.opened = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.name);
		ByteBufUtils.writeUTF8String(buf, this.description);
		buf.writeInt(this.level);
		buf.writeInt(this.exp);
		buf.writeInt(this.chunks);
		buf.writeInt(this.memberCount);
		buf.writeBoolean(this.opened);
	}

	public static class Handler implements IMessageHandler<PacketFactionInformations, PacketFactionInformations> {

		@Override
		public PacketFactionInformations onMessage(PacketFactionInformations message, MessageContext ctx) {
			if (EventHandlerFaction.doesFactionExist(message.getName())) {
				Faction faction = EventHandlerFaction.getFaction(message.getName());
				return new PacketFactionInformations(faction.getName(), faction.getDesc(), faction.getLevel(), faction.getExp(), faction.getChunks().size(), faction.getMembers().size(), faction.isOpened());
			}
			return new PacketFactionInformations("", "This faction doesn't exist.", 0, 0, 0, 0, false);
		}
	}

}
