package factionmod.inventory;

import java.util.ListIterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class FactionInventory implements IInventory {

	private NonNullList<ItemStack>	stacks	= NonNullList.withSize(27, ItemStack.EMPTY);

	private String					name;

	public FactionInventory(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(name);
	}

	@Override
	public int getSizeInventory() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		ListIterator<ItemStack> it = stacks.listIterator();
		while (it.hasNext()) {
			if (!it.next().isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return stacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(stacks, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(stacks, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.stacks.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit())
			stack.setCount(this.getInventoryStackLimit());
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}

}
