package factionmod.handler;

import java.util.List;

import factionmod.FactionMod;
import factionmod.chat.ChatChannel;
import factionmod.chat.ChatManager;
import factionmod.config.ConfigLang;
import factionmod.utils.MessageHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

@EventBusSubscriber(modid = FactionMod.MODID)
public class EventHandlerChat {

    @SubscribeEvent
    public static void clientChatReceived(ServerChatEvent event) {
        ChatChannel channel = ChatManager.instance().getChannelFor(event.getMessage());
        if (channel != null) {
            event.setCanceled(true);
            List<EntityPlayerMP> onlinePlayers = FMLServerHandler.instance().getServer().getPlayerList().getPlayers();
            List<EntityPlayerMP> targetedPlayers = channel.filter(event.getPlayer(), onlinePlayers);
            if (targetedPlayers.isEmpty()) {
                event.getPlayer().sendMessage(MessageHelper.warn(ConfigLang.translate("message.no.target")));
            } else {
                ITextComponent component = channel.format(event.getUsername(), event.getMessage());
                targetedPlayers.forEach(p -> p.sendMessage(component));
            }

        }
    }

}
