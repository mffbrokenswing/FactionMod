package factionmod.common.util;

import factionmod.FactionMod;
import factionmod.common.data.component.BaseFactionComponents;
import factionmod.common.data.structure.DataStructure;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;

@UtilityClass
public class FactionUtils {

    public static Optional<DataStructure> getFaction(String name) {
        List<DataStructure> structures = FactionMod.proxy.getModData().getDataStructures("faction");
        return structures.stream().filter(s -> {
            Optional<String> value = s.getComponentValue(BaseFactionComponents.NAME);
            return value.isPresent() && value.get().equals(name);
        }).findFirst();
    }

    public boolean isNameAvailable(String name) {
        return !getFaction(name).isPresent();
    }

}
