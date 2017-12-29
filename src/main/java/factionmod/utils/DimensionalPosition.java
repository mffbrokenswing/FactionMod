package factionmod.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Represents a {@link ChunkPos} in a specified dimension.
 *
 * @author BrokenSwing
 *
 */
public class DimensionalPosition implements INBTSerializable<NBTTagCompound> {

    private ChunkPos pos;
    private int      dimension;

    public DimensionalPosition(final NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    public DimensionalPosition(final ChunkPos pos, final int dimension) {
        this.pos = new ChunkPos(pos.x, pos.z);
        this.dimension = dimension;
    }

    public ChunkPos getPos() {
        return pos;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "[Dim:" + this.dimension + " ChunkPos:" + this.pos.toString() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + dimension;
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DimensionalPosition other = (DimensionalPosition) obj;
        if (dimension != other.dimension)
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return true;
    }

    public static DimensionalPosition from(final World world, final BlockPos pos) {
        final ChunkPos chunkPos = world.getChunkFromBlockCoords(pos).getPos();
        return new DimensionalPosition(chunkPos, world.provider.getDimension());
    }

    public static DimensionalPosition from(final Entity entity) {
        return from(entity.getEntityWorld(), entity.getPosition());
    }

    public static DimensionalPosition from(final ICommandSender sender) {
        return from(sender.getEntityWorld(), sender.getPosition());
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("x", this.pos.x);
        nbt.setInteger("z", this.pos.z);
        nbt.setInteger("dim", this.dimension);
        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        this.pos = new ChunkPos(nbt.getInteger("x"), nbt.getInteger("z"));
        this.dimension = nbt.getInteger("dim");
    }

}
