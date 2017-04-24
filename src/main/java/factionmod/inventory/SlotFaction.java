package factionmod.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import factionmod.config.ConfigFactionInventory;

public class SlotFaction extends Slot {

	public SlotFaction(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return ConfigFactionInventory.isItemValid(stack.getItem());
	}

}
