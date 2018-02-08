package factionmod.chat;

import java.util.Collections;
import java.util.LinkedList;

public class ChatManager {

    private static volatile ChatManager instance = null;

    public static ChatManager instance() {
        if (instance == null) {
            synchronized (ChatManager.class) {
                if (instance == null)
                    instance = new ChatManager();
            }
        }
        return instance;
    }

    private LinkedList<ChatChannel> channels = null;

    private ChatManager() {
        channels = new LinkedList<>();
    }

    public void registerChannel(ChatChannel channel) {
        this.channels.add(channel);
        // Revert-sorting on prefix, "!a" should be tested before "!"
        Collections.sort(this.channels, (p1, p2) -> -p1.getPrefix().compareTo(p2.getPrefix()));
    }
    
    public void clearChannels() {
        this.channels.clear();
    }

    public ChatChannel getChannelFor(String message) {
        for (ChatChannel channel : this.channels) {
            if (message.startsWith(channel.getPrefix()))
                return channel;
        }
        return null;
    }

}
