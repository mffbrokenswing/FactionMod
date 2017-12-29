package factionmod.faction;

import factionmod.config.ConfigExperience;
import factionmod.math.Expression;

/**
 * This class is used to know everything about the levels of a faction.
 *
 * @author BrokenSwing
 *
 */
public class Levels {

    /**
     * Indicates the amount of experience need to level up.
     *
     * @param level
     *            The level to reach
     * @return the amount of experience needed
     */
    public static int getExpNeededForLevel(final int level) {
        final String exp = ConfigExperience.getExpression("experience_from_level");
        return new Expression(String.format(exp, level)).eval().intValue();
    }

    /**
     * Indicates the maximum of chunks that a faction can claim at a specified
     * level.
     *
     * @param level
     *            The level of the faction
     * @return the maximum count of chunks that can be claimed
     */
    public static int getMaximumChunksForLevel(final int level) {
        final String exp = ConfigExperience.getExpression("chunk_count_from_level");
        return new Expression(String.format(exp, level)).eval().intValue();
    }

}
