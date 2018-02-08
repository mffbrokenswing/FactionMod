package factionmod.chat;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.ForgeHooks;

public class ChatChannel {

    protected final String                                              prefix;
    protected final String                                              format;
    protected final BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> filter;

    public ChatChannel(String prefix, String format, BiFunction<EntityPlayerMP, EntityPlayerMP, Boolean> filter) {
        this.prefix = prefix;
        this.filter = filter;
        this.format = format;
    }

    public ITextComponent format(String sender, String message) {
        return new TextComponentTranslation(this.format, sender, ForgeHooks.newChatWithLinks(message.substring(prefix.length()).trim()));
    }

    public String getPrefix() {
        return prefix;
    }

    public List<EntityPlayerMP> filter(EntityPlayerMP sender, Collection<? extends EntityPlayerMP> connectedPlayers) {
        return connectedPlayers.stream().filter(e -> this.filter.apply(sender, e)).collect(Collectors.toList());
    }

}
