package net.events;


import net.IClient;
import net.message.Message;
import net.message.MessageType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageDistributor {
    private final Map<MessageType, IMessageHandler> registry;
    private MessageDistributor globalHandler;
    private IMessageHandler defaultHandler;

    public MessageDistributor() {
        registry = new HashMap<MessageType, IMessageHandler>();
    }

    public synchronized void onReceive(Message message, IClient client) throws IOException {

        if (globalHandler != null) {
            globalHandler.onReceive(message, client);
        }

        final IMessageHandler messageHandler = registry.get(message.getMessageType());

        if (messageHandler != null) {
            messageHandler.handleMessage(message, client);
        } else if (defaultHandler != null) {
            defaultHandler.handleMessage(message, client);
        }
    }


    public synchronized void addHandler(MessageType messageType, final IMessageHandler MessageHandler) {
        if (registry.containsKey(messageType)) {
            throw new IllegalArgumentException("Handler for ID: " + messageType + " already exists");
        }
        registry.put(messageType, MessageHandler);
    }

    public synchronized IMessageHandler getHandler(final MessageType messageType) {
        return registry.get(messageType);
    }

    public synchronized void clearHandlers() {
        registry.clear();
    }

    public synchronized void setDefaultHandler(final IMessageHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    public synchronized MessageDistributor getGlobalHandler() {
        return globalHandler;
    }

    public synchronized void setGlobalHandler(final MessageDistributor globalHandler) {
        this.globalHandler = globalHandler;
    }
}