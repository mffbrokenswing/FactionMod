package factionmod.chat;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import factionmod.api.FactionModAPI.FactionAPI;
import net.minecraft.entity.player.EntityPlayerMP;

@SuppressWarnings("unchecked")
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

    private HashMap<String, BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>>                                                                  functions;
    private HashMap<String, Function<BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>[], BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>>> operators = null;

    private Filters() {
        functions = new HashMap<>();
        operators = new HashMap<>();

        functions.put("same_faction", (sender, player) -> FactionAPI.hasPlayerFaction(sender.getUniqueID()) && FactionAPI.getFactionOf(sender.getUniqueID()).isMember(player.getUniqueID()));
        functions.put("same_world", (sender, player) -> sender.getEntityWorld().provider.getDimension() == player.getEntityWorld().provider.getDimension());
        functions.put("true", (sender, player) -> Boolean.TRUE);
        functions.put("false", (sender, player) -> Boolean.FALSE);

        operators.put("OR", Filters::combineOr);
        operators.put("||", Filters::combineOr);
        operators.put("AND", Filters::combineAnd);
        operators.put("&&", Filters::combineAnd);
    }

    /**
     * Parses a chain of characters and creates the corresponding bifuntion.
     * 
     * @param chain
     *            The chain to parse
     * @return the bifunction or null if the chain was errored
     */
    @Nullable
    public BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> parse(String chain) {
        String[] elements = StringUtils.normalizeSpace(chain).split(" ");
        BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> func = null;
        boolean lastIsOperator = false;
        boolean lastIsFunction = false;
        String operator = "";
        for (int i = 0; i < elements.length; i++) {

            // Found a function
            if (functions.containsKey(elements[i])) {

                // Case : first element
                if (func == null) {
                    func = functions.get(elements[i]);
                }

                // If the function is the right assignment of an operator
                else if (lastIsOperator) {
                    func = operators.get(operator).apply(asArray(func, functions.get(elements[i])));
                }

                // Same as "if(lastIsFunction)"
                else {
                    return null;
                }

                lastIsFunction = true;
                lastIsOperator = false;
            }

            // Found an operator
            else if (operators.containsKey(elements[i])) {

                // Operator must have a function as left assignment
                if (!lastIsFunction) {
                    return null;
                }

                operator = elements[i];
                lastIsOperator = true;
                lastIsFunction = false;
            }

            // Found nothing
            else {
                return null;
            }
        }

        // Returns null if the last element is an operator
        return lastIsFunction ? func : null;
    }

    public static BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>[] asArray(BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>... functions) {
        return functions;
    }

    public static BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> combineAnd(BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>... functions) {
        return (sender, player) -> {
            int i = 0;
            while (functions[i].apply(sender, player) && ++i < functions.length);
            return i >= functions.length;
        };
    }

    public static BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> combineOr(BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean>... functions) {
        return (sender, player) -> {
            boolean flag = false;
            int i = functions.length;
            while (--i >= 0)
                flag |= functions[i].apply(sender, player);
            return flag;
        };
    }

}
