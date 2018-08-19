package factionmod.common.data.structure;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import factionmod.common.data.component.DataComponent;
import factionmod.common.event.DataStructurePopulationEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class DataStructure {

    private static final LoadingCache<String, Set<DataComponent<?>>> COMPONENTS_CACHE = CacheBuilder.newBuilder()
            .build(CacheLoader.from(key -> {
                DataStructurePopulationEvent event = new DataStructurePopulationEvent(key);
                MinecraftForge.EVENT_BUS.post(event);
                return event.getRegisteredComponents();
            }));

    private Map<DataComponent<?>, Object> components;
    @Getter @Setter private boolean dirty = false;
    private String structureName;

    public DataStructure(String structureName) {
        this.structureName = structureName;
        this.components = new IdentityHashMap<>();
        this.components.keySet().addAll(provideComponents());
        this.components.replaceAll((k, v) -> k.getBaseValue());
    }

    public String getName() {
        return this.structureName;
    }

    private Set<DataComponent<?>> provideComponents() {
        try {
            return COMPONENTS_CACHE.get(this.structureName);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    /**
     * Returns the value for the specified component.
     * @param component The component the value is queried
     * @return the optional value for the component
     */
    public <T> Optional<T> getComponentValue(DataComponent<T> component) {
        return Optional.ofNullable(
                component.getType().cast(components.get(component))
        );
    }

    /**
     * Sets the value for the specified component then returns the old value of the component.
     * @param component The component the value needs to be modified
     * @param value The new value for the component
     * @return the optional old value of the component
     */
    public <T> Optional<T> setComponentValue(DataComponent<T> component, @Nullable T value) {
        this.dirty |= Objects.deepEquals(value, components.get(component));
        return Optional.ofNullable(
                component.getType().cast(components.put(component, value))
        );
    }

    /**
     * @return all the components currently linked to a value (null or not)
     */
    public Set<DataComponent<?>> getComponents() {
        return Collections.unmodifiableSet(this.components.keySet());
    }

    private NBTTagCompound cachedNBT = null;

    public NBTTagCompound toNBT() {
        if(!this.dirty && cachedNBT != null)
            return this.cachedNBT;
        NBTTagCompound nbt = new NBTTagCompound();
        this.components.forEach((k, v) -> {
            if(v != null) {
                nbt.setTag(k.getNamespace(), k.serialize(v));
            }
        });
        return this.cachedNBT = nbt;
    }

    public void fromNBT(NBTTagCompound nbt) {
        this.components.replaceAll((k, v) -> {
            if(nbt.hasKey(k.getNamespace(), Constants.NBT.TAG_COMPOUND)) {
                return k.deserialize(nbt.getCompoundTag(k.getNamespace()));
            }
            return k.getBaseValue();
        });
        this.cachedNBT = nbt;
    }

}
