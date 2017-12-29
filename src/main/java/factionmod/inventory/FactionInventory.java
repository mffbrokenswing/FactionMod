package factionmod.inventory;

import java.util.ListIterator;

import factionmod.config.ConfigLang;
import factionmod.data.FactionModDatas;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.util.INBTSerializable;

public class FactionInventory implements IInventory, IInteractionObject, INBTSerializable<NBTTagCompound> {

    private NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);

    private String name;

    public FactionInventory(final String name) {
        this.name = name;
    }

    public FactionInventory(final NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.name);
        ItemStackHelper.saveAllItems(nbt, this.stacks);
        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        this.name = nbt.getString("name");
        ItemStackHelper.loadAllItems(nbt, this.stacks);
    }

    @Override
    public String getName() {
        return String.format(ConfigLang.translate("faction.chest.name"), this.name);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public int getSizeInventory() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        final ListIterator<ItemStack> it = stacks.listIterator();
        while (it.hasNext())
            if (!it.next().isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(final int index) {
        return stacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(final int index, final int count) {
        this.markDirty();
        return ItemStackHelper.getAndSplit(stacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(final int index) {
        this.markDirty();
        return ItemStackHelper.getAndRemove(stacks, index);
    }

    @Override
    public void setInventorySlotContents(final int index, final ItemStack stack) {
        this.stacks.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());
        this.markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        FactionModDatas.save();
    }

    @Override
    public boolean isUsableByPlayer(final EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(final EntityPlayer player) {}

    @Override
    public void closeInventory(final EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return false;
    }

    @Override
    public int getField(final int id) {
        return 0;
    }

    @Override
    public void setField(final int id, final int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        this.markDirty();
    }

    @Override
    public Container createContainer(final InventoryPlayer playerInventory, final EntityPlayer player) {
        return new FactionContainer(playerInventory, this, player);
    }

    @Override
    public String getGuiID() {
        return "minecraft:container";
    }

}
