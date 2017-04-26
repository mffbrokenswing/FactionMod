package factionmod.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import factionmod.faction.Faction;
import factionmod.handler.EventHandlerFaction;
import factionmod.utils.ServerUtils;

/**
 * This class is used to save the inventories of the faction.
 * 
 * @author BrokenSwing
 *
 */
public class InventoryData extends WorldSavedData {

	public static final String		NAME	= "FACTION-INVENTORIES";
	private static InventoryData	SAVE;

	public static void save() {
		SAVE.markDirty();
	}

	public static void load() {
		ServerUtils.getProfiler().startSection("loadInventories");

		if (DimensionManager.getWorlds().length > 0) {
			MapStorage storage = DimensionManager.getWorlds()[0].getMapStorage();
			InventoryData data = (InventoryData) storage.getOrLoadData(InventoryData.class, NAME);
			if (data == null) {
				data = new InventoryData(NAME);
				storage.setData(NAME, data);
			}
			SAVE = data;
		}

		ServerUtils.getProfiler().endSection();
	}

	public InventoryData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			if (nbt.hasKey(faction.getName().toLowerCase())) {
				faction.getInventory().fromNBT(nbt.getCompoundTag(faction.getName().toLowerCase()));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			compound.setTag(faction.getName().toLowerCase(), faction.getInventory().toNBT());
		}
		return compound;
	}

}
