package factionmod.common.save;

import factionmod.common.FactionConstants;
import factionmod.common.data.structure.DataStructure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModData extends WorldSavedData {

    public static final String ID = FactionConstants.MOD_ID + ":" + "data";

    private Map<String, List<DataStructure>> structures;
    private boolean entriesChanged = false;

    public ModData(String name) {
        super(name);
        this.structures = new HashMap<>();
    }

    public List<DataStructure> getDataStructures(String structureName) {
        this.structures.putIfAbsent(structureName, new ArrayList<>());
        return this.structures.get(structureName);
    }

    public void addDataStructure(DataStructure structure) {
        this.structures.computeIfAbsent(structure.getName(), o -> new ArrayList<>()).add(structure);
        this.entriesChanged = true;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        this.structures.forEach((key, value) -> {
            NBTTagList list = new NBTTagList();
            value.forEach(s -> {
                list.appendTag(s.toNBT());
                s.setDirty(false);
            });
            compound.setTag(key, list);
        });
        this.entriesChanged = false;
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // Foreach structure type
        nbt.getKeySet().forEach(key -> {
            // If the type is saved
            if(nbt.hasKey(key, Constants.NBT.TAG_LIST)) {
                // Get a list containing all serialized structures for the current type
                NBTTagList list = nbt.getTagList(key, Constants.NBT.TAG_COMPOUND);

                // Foreach serialized structure, we deserialize and store it
                for(int i = 0; i < list.tagCount(); i++) {
                    DataStructure struct = new DataStructure(key);
                    struct.fromNBT(list.getCompoundTagAt(i));
                    this.structures.computeIfAbsent(key, o -> new ArrayList<>()).add(struct);
                }
            }
        });
    }

    @Override
    public boolean isDirty() {
        return this.entriesChanged || this.structures.values().stream()
                .flatMap(List::stream)
                .map(DataStructure::isDirty)
                .reduce(false, Boolean::logicalOr);
    }

}
