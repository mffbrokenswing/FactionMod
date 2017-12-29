package factionmod.manager.instanciation;

import factionmod.manager.IChunkManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Used to instanciate an {@link IChunkManager} from the name of the
 * {@link Zone} an its arguments.
 *
 * @author BrokenSwing
 *
 */
public class ZoneInstance implements INBTSerializable<NBTTagCompound> {

    private String   zoneName;
    private String[] args;

    public ZoneInstance(final String name, final String[] args) {
        this.zoneName = name;
        this.args = args;
    }

    public ZoneInstance(final NBTTagCompound nbt) {
        this.deserializeNBT(nbt);
    }

    public String getZoneName() {
        return zoneName;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("name", this.zoneName);

        final NBTTagList argsList = new NBTTagList();
        for (final String arg : this.args)
            argsList.appendTag(new NBTTagString(arg));
        nbt.setTag("args", argsList);
        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        this.zoneName = nbt.getString("name");

        final NBTTagList argsList = nbt.getTagList("args", NBT.TAG_STRING);
        this.args = new String[argsList.tagCount()];
        for (int i = 0; i < argsList.tagCount(); i++)
            this.args[i] = argsList.getStringTagAt(i);
    }

}
