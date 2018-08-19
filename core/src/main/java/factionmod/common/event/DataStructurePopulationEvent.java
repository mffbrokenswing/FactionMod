package factionmod.common.event;

import factionmod.common.data.component.DataComponent;
import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DataStructurePopulationEvent extends Event {

    @Getter private final String structureName;
    private final Set<DataComponent<?>> components;

    public DataStructurePopulationEvent(String structureName) {
        this.structureName = structureName;
        this.components = new HashSet<>();
    }

    public void registerComponent(DataComponent<?> component) {
        this.components.add(component);
    }

    public Set<DataComponent<?>> getRegisteredComponents() {
        return Collections.unmodifiableSet(this.components);
    }

}
