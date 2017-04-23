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
			System.out.println("New storage");
			data = new InventoryData(NAME);
			storage.setData(NAME, data);
		}
		SAVE = data;
	}

	public InventoryData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		System.out.println(nbt.getSize() + " tags to read");
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			System.out.println("Search " + faction.getName());
			if (nbt.hasKey(faction.getName().toLowerCase())) {
				System.out.println("Found " + faction.getName());
				faction.getInventory().fromNBT(nbt.getCompoundTag(faction.getName().toLowerCase()));
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for(Faction faction : EventHandlerFaction.getFactions().values()) {
			System.out.println("Write " + faction.getName());
			compound.setTag(faction.getName().toLowerCase(), faction.getInventory().toNBT());
		}
		System.out.println(compound.getSize() + " tags wrote");
		return compound;
	}

}
