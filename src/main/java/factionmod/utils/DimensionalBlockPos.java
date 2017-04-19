package factionmod.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * Represents a {@link BlockPos} in a specified dimension.
 * 
 * @author BrokenSwing
 *
 */
public class DimensionalBlockPos {

	private final int		dimension;
	private final BlockPos	pos;

	public DimensionalBlockPos(int dimension, BlockPos pos) {
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
	 * Returns an instance of {@link DimensionalPosition} with the same
	 * dimension and where the chunk contains the position of this block.
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DimensionalBlockPos other = (DimensionalBlockPos) obj;
		if (dimension != other.dimension)
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.add("dim", new JsonPrimitive(this.dimension));
		obj.add("x", new JsonPrimitive(this.pos.getX()));
		obj.add("y", new JsonPrimitive(this.pos.getY()));
		obj.add("z", new JsonPrimitive(this.pos.getZ()));
		return obj;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", this.dimension);
		nbt.setInteger("x", this.pos.getX());
		nbt.setInteger("y", this.pos.getY());
		nbt.setInteger("z", this.pos.getZ());
		return nbt;
	}

	public static DimensionalBlockPos fromJson(JsonObject obj) {
		BlockPos pos = new BlockPos(obj.get("x").getAsInt(), obj.get("y").getAsInt(), obj.get("z").getAsInt());
		return new DimensionalBlockPos(obj.get("dim").getAsInt(), pos);
	}

	public static DimensionalBlockPos from(Entity entity) {
		return new DimensionalBlockPos(entity.getEntityWorld().provider.getDimension(), entity.getPosition());
	}

}
