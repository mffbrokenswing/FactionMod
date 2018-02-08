package factionmod.chat;

import java.util.HashMap;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import factionmod.FactionMod;
import factionmod.api.FactionModAPI.FactionAPI;
import net.minecraft.entity.player.EntityPlayerMP;

public class Filters {

    private static volatile Filters instance = null;

    public static Filters instance() {
        if (instance == null) {
            synchronized (Filters.class) {
                if (instance == null)
                    instance = new Filters();
            }
        }
        return instance;
    }

    private HashMap<String, BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>>                  functions;
    private HashMap<String, LogicalOperator<BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>>> operators;

    private Filters() {
        functions = new HashMap<>();
        operators = new HashMap<>();

        functions.put("same_faction", (sender, player) -> FactionAPI.hasPlayerFaction(sender.getUniqueID()) && FactionAPI.getFactionOf(sender.getUniqueID()).isMember(player.getUniqueID()));
        functions.put("same_world", (sender, player) -> sender.getEntityWorld().provider.getDimension() == player.getEntityWorld().provider.getDimension());
        functions.put("true", (sender, player) -> Boolean.TRUE);
        functions.put("false", (sender, player) -> Boolean.FALSE);
        functions.put("self", (sender, player) -> sender.getUniqueID().equals(player.getUniqueID()));
        functions.put("overworld", (sender, player) -> player.getEntityWorld().provider.getDimension() == 0);
        functions.put("nether", (sender, player) -> player.getEntityWorld().provider.getDimension() == 1);
        functions.put("end", (sender, player) -> player.getEntityWorld().provider.getDimension() == -1);

        operators.put("OR", new LogicalOperator<>(Filters::combineOr, 0));
        operators.put("||", new LogicalOperator<>(Filters::combineOr, 0));
        operators.put("AND", new LogicalOperator<>(Filters::combineAnd, 1));
        operators.put("&&", new LogicalOperator<>(Filters::combineAnd, 1));
    }

    /**
     * Parses a chain of characters and creates the corresponding bifunction.
     * 
     * @param chain
     *            The chain to parse
     * @return the bifunction or null if the chain was errored
     */
    @Nullable
    public BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> parse(String chain) {
        LogicalParser<BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>> parser = new LogicalParser<>(new LogicalTokenizer(chain));
        parser.addFunctions(this.functions);
        parser.addOperators(this.operators);
        try {
            return parser.parse();
        } catch (Exception e) {
            FactionMod.getLogger().debug("Error while parsing logical expression : {}", chain);
            e.printStackTrace();
        }
        return null;
    }

    public static BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> combineAnd(BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> func1, BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> func2) {
        return (sender, player) -> func1.apply(sender, player) && func2.apply(sender, player);
    }

    public static BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> combineOr(BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> func1, BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> func2) {
        return (sender, player) -> func1.apply(sender, player) || func2.apply(sender, player);
    }

}
