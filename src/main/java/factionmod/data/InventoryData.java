package factionmod.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import factionmod.faction.Faction;
import factionmod.handler.EventHandlerFaction;

public class InventoryData extends WorldSavedData {

	public static final String		NAME	= "FACTION-INVENTORIES";
	private static InventoryData	SAVE;

	public static void save() {
		SAVE.markDirty();
	}

	public static void load(FMLServerStartingEvent event) {
		MapStorage storage = event.getServer().getEntityWorld().getMapStorage();
		InventoryData data = (InventoryData) storage.getOrLoadData(InventoryData.class, NAME);
		if (data == null) {
			data = new InventoryData();
			storage.setData(NAME, data);
		}
		SAVE = data;
	}

	public InventoryData() {
		super(NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			if (nbt.hasKey(faction.getName())) {
				faction.getInventory().fromNBT(nbt.getCompoundTag(faction.getName()));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			compound.setTag(faction.getName(), faction.getInventory().toNBT());
		}
		return compound;
	}

}
