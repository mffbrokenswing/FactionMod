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

    private String                 name;

    public FactionInventory(String name) {
        this.name = name;
    }

    public FactionInventory(NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.name);
        ItemStackHelper.saveAllItems(nbt, this.stacks);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
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
        this.markDirty();
        return ItemStackHelper.getAndSplit(stacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        this.markDirty();
        return ItemStackHelper.getAndRemove(stacks, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
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
        this.markDirty();
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
        return new FactionContainer(playerInventory, this, player);
    }

    @Override
    public String getGuiID() {
        return "minecraft:container";
    }

}
