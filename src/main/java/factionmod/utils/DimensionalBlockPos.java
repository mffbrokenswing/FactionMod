package factionmod.utils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Represents a {@link BlockPos} in a specified dimension.
 *
 * @author BrokenSwing
 *
 */
public class DimensionalBlockPos implements INBTSerializable<NBTTagCompound> {

    private int      dimension;
    private BlockPos pos;

    public DimensionalBlockPos(final NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    public DimensionalBlockPos(final int dimension, final BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    public int getDimension() {
        return this.dimension;
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    /**
     * Returns an instance of {@link DimensionalPosition} with the same dimension
     * and where the chunk contains the position of this block.
     *
     * @return a DimensionalPosition
     */
    public DimensionalPosition toDimensionnalPosition() {
        return new DimensionalPosition(new ChunkPos(this.pos), this.dimension);
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
        final DimensionalBlockPos other = (DimensionalBlockPos) obj;
        if (dimension != other.dimension)
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dimension", this.dimension);
        nbt.setInteger("x", this.pos.getX());
        nbt.setInteger("y", this.pos.getY());
        nbt.setInteger("z", this.pos.getZ());
        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        this.dimension = nbt.getInteger("dimension");
        this.pos = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
    }

    public static DimensionalBlockPos from(final Entity entity) {
        return new DimensionalBlockPos(entity.getEntityWorld().provider.getDimension(), entity.getPosition());
    }

}
