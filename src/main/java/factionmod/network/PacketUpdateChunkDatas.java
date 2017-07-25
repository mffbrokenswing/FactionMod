package factionmod.network;

import factionmod.manager.IChunkManager;
import factionmod.utils.DimensionalPosition;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateChunkDatas implements IMessage {
    
    private String chunkName;
    private DimensionalPosition position;
    private String zoneName;
    
    public PacketUpdateChunkDatas() {}
    
    public PacketUpdateChunkDatas(IChunkManager manager, DimensionalPosition pos, String zoneName) {
        this.position = pos;
        this.chunkName = manager.getName().getUnformattedText();
        this.zoneName = zoneName;
    }
    
    public String getZoneName() {
        return this.zoneName;
    }

    public String getChunkName() {
        return chunkName;
    }

    public DimensionalPosition getPosition() {
        return position;
    }

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.position.serializeNBT());
        ByteBufUtils.writeUTF8String(buf, this.chunkName);
        ByteBufUtils.writeUTF8String(buf, this.zoneName);
    }
    
    public static class Handler implements IMessageHandler<PacketUpdateChunkDatas, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateChunkDatas message, MessageContext ctx) {
            return null;
        }
        
    }

}
