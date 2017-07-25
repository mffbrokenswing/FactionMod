package factionmod.network;

import factionmod.utils.DimensionalPosition;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRemoveChunkData implements IMessage {
    
    private DimensionalPosition position;
    
    public PacketRemoveChunkData() {}
    
    public PacketRemoveChunkData(DimensionalPosition pos) {
        this.position = pos;
    }
    
    public DimensionalPosition getPosition() {
        return this.position;
    }

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.position.serializeNBT());
    }
    
    public static class Handler implements IMessageHandler<PacketRemoveChunkData, IMessage>  {

        @Override
        public IMessage onMessage(PacketRemoveChunkData message, MessageContext ctx) {
            return null;
        }
        
    }

}
