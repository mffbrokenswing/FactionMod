package factionmod.utils;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

/**
 * This class provide an easy way to create {@link ITextComponent} objects with
 * a color and a prefix.
 *
 * @author BrokenSwing
 *
 */
public class MessageHelper {

    public static final String         PREFIX      = "[Faction] ";
    public static final TextFormatting INFO_COLOR  = TextFormatting.BLUE;
    public static final TextFormatting WARN_COLOR  = TextFormatting.YELLOW;
    public static final TextFormatting ERROR_COLOR = TextFormatting.RED;

    /** Represents an informations. */
    public static final int INFO  = 0;
    /** Represents a warning. */
    public static final int WARN  = 1;
    /** Represents an error. */
    public static final int ERROR = 2;

    /**
     * Creates a component with the {@link MessageHelper#INFO} level.
     *
     * @param message
     *            The text of the message
     * @return a chat component
     */
    public static ITextComponent info(final String message) {
        return new TextComponentString(PREFIX).appendSibling(ForgeHooks.newChatWithLinks(message)).setStyle(new Style().setColor(INFO_COLOR));
    }

    /**
     * Creates a component with the {@link MessageHelper#WARN} level.
     *
     * @param message
     *            The text of the message
     * @return a chat component
     */
    public static ITextComponent warn(final String message) {
        return new TextComponentString(PREFIX).appendSibling(ForgeHooks.newChatWithLinks(message)).setStyle(new Style().setColor(WARN_COLOR));
    }

    /**
     * Creates a component with the {@link MessageHelper#ERROR} level.
     *
     * @param message
     *            The text of the message
     * @return a chat component
     */
    public static ITextComponent error(final String message) {
        return new TextComponentString(PREFIX).appendSibling(ForgeHooks.newChatWithLinks(message)).setStyle(new Style().setColor(ERROR_COLOR));
    }

    /**
     * Returns an {@link ITextComponent} with a style relative to the level.
     *
     * @param message
     *            The message to display
     * @param level
     *            The level : {@link MessageHelper#INFO},
     *            {@link MessageHelper#WARN}, {@link MessageHelper#ERROR}
     * @return the {@link ITextComponent} containing the formatted message
     */
    public static ITextComponent message(final String message, final int level) {
        switch (level) {
        case INFO:
            return info(message);
        case WARN:
            return warn(message);
        case ERROR:
            return error(message);
        default:
            return new TextComponentString(PREFIX).appendSibling(ForgeHooks.newChatWithLinks(message));
        }
    }

}
