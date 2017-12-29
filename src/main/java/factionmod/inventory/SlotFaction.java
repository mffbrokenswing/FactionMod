package factionmod.inventory;

import factionmod.config.ConfigFactionInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotFaction extends Slot {

    public SlotFaction(final IInventory inventoryIn, final int index, final int xPosition, final int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(final ItemStack stack) {
        return ConfigFactionInventory.isItemValid(stack.getItem());
    }

}
