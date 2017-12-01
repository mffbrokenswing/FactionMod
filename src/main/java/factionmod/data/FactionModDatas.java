package factionmod.data;

import java.util.HashMap;
import java.util.Map.Entry;

import factionmod.FactionMod;
import factionmod.event.FactionsLoadedEvent;
import factionmod.faction.Faction;
import factionmod.faction.Member;
import factionmod.handler.EventHandlerChunk;
import factionmod.handler.EventHandlerFaction;
import factionmod.manager.IChunkManager;
import factionmod.manager.instanciation.Zone;
import factionmod.manager.instanciation.ZoneInstance;
import factionmod.utils.DimensionalPosition;
import factionmod.utils.ServerUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * This class is used to save the inventories of the faction.
 * 
 * @author BrokenSwing
 *
 */
public class FactionModDatas extends WorldSavedData {

    public static final String     DATA_VERSION = "1";
    public static final String     NAME         = FactionMod.MODID;
    private static FactionModDatas SAVE         = new FactionModDatas(NAME);

    public static void save() {
        SAVE.markDirty();
    }

    public static void load() {
        ServerUtils.getProfiler().startSection("loadFactionMod");

        if (DimensionManager.getWorlds().length > 0) {
            MapStorage storage = DimensionManager.getWorlds()[0].getMapStorage();
            FactionModDatas data = (FactionModDatas) storage.getOrLoadData(FactionModDatas.class, NAME);
            if (data == null) {
                data = new FactionModDatas(NAME);
                storage.setData(NAME, data);
            }
            SAVE = data;
        }

        MinecraftForge.EVENT_BUS.post(new FactionsLoadedEvent());
        
        ServerUtils.getProfiler().endSection();
    }

    public FactionModDatas(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // Read factions first, it's necessary
        NBTTagList factions = nbt.getTagList("factions", NBT.TAG_COMPOUND);
        for(int i = 0; i < factions.tagCount(); i++) {
            Faction faction = new Faction(factions.getCompoundTagAt(i));
            EventHandlerFaction.addFaction(faction);
            for(Member member : faction.getMembers()) {
                EventHandlerFaction.addUserToFaction(faction, member.getUUID());
            }
        }

        HashMap<DimensionalPosition, ZoneInstance> instances = new HashMap<DimensionalPosition, ZoneInstance>();
        NBTTagList managersList = nbt.getTagList("managers", NBT.TAG_COMPOUND);
        for(int i = 0; i < managersList.tagCount(); i++) {
            NBTTagCompound compound = managersList.getCompoundTagAt(i);
            instances.put(new DimensionalPosition(compound.getCompoundTag("key")), new ZoneInstance(compound.getCompoundTag("value")));
        }

        for(Entry<DimensionalPosition, ZoneInstance> entry : instances.entrySet()) {
            DimensionalPosition pos = entry.getKey();
            ZoneInstance instance = entry.getValue();
            final Zone zone = EventHandlerChunk.getZone(instance.getZoneName());
            if (zone != null) {
                IChunkManager manager;
                if (zone.isStandAlone()) {
                    manager = zone.getInstance();
                } else {
                    try {
                        manager = zone.createInstance(instance.getArgs());
                    } catch (Exception e) {
                        String listArgs = "";
                        for(String str : instance.getArgs()) {
                            listArgs += str + " ";
                        }
                        FactionMod.getLogger().warn("Cannot instanciate the zone " + zone.getName() + " with args : " + listArgs);
                        e.printStackTrace();
                        continue;
                    }
                }
                EventHandlerChunk.registerChunkManager(manager, pos, instance, false);
            } else {
                FactionMod.getLogger().warn("Removed chunk manager at " + pos.toString() + " because the zone associated with it doens't exist.");
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setString("version", DATA_VERSION);
        NBTTagList managersList = new NBTTagList();
        for(Entry<DimensionalPosition, ZoneInstance> entry : EventHandlerChunk.getZonesInstances().entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("key", entry.getKey().serializeNBT());
            compound.setTag("value", entry.getValue().serializeNBT());
            managersList.appendTag(compound);
        }
        nbt.setTag("managers", managersList);

        NBTTagList factions = new NBTTagList();
        for(Faction faction : EventHandlerFaction.getFactions().values()) {
            factions.appendTag(faction.serializeNBT());
        }
        nbt.setTag("factions", factions);

        return nbt;
    }

}
