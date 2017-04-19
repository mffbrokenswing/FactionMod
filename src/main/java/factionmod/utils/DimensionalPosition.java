package factionmod.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Represents a {@link ChunkPos} in a specified dimension.
 * 
 * @author BrokenSwing
 *
 */
public class DimensionalPosition {

	private ChunkPos	pos;
	private int			dimension;

	public DimensionalPosition(ChunkPos pos, int dimension) {
		this.pos = new ChunkPos(pos.chunkXPos, pos.chunkZPos);
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DimensionalPosition other = (DimensionalPosition) obj;
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
		obj.add("x", new JsonPrimitive(this.pos.chunkXPos));
		obj.add("y", new JsonPrimitive(this.pos.chunkZPos));
		return obj;
	}

	public static DimensionalPosition fromJson(JsonObject obj) {
		int dimension = obj.get("dim").getAsInt();
		int chunkX = obj.get("x").getAsInt();
		int chunkZ = obj.get("y").getAsInt();
		return new DimensionalPosition(new ChunkPos(chunkX, chunkZ), dimension);
	}

	public static DimensionalPosition from(World world, BlockPos pos) {
		ChunkPos chunkPos = world.getChunkFromBlockCoords(pos).getPos();
		return new DimensionalPosition(chunkPos, world.provider.getDimension());
	}

	public static DimensionalPosition from(Entity entity) {
		return from(entity.getEntityWorld(), entity.getPosition());
	}

	public static DimensionalPosition from(ICommandSender sender) {
		return from(sender.getEntityWorld(), sender.getPosition());
	}

}
