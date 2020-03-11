package net.events;

import net.IClient;
import net.message.Message;

import java.io.IOException;

public class DistributorListener implements INetListener {
    private final MessageDistributor messageDistributor;

    public DistributorListener(final MessageDistributor messageDistributor) {
        this.messageDistributor = messageDistributor;
    }

    @Override
    public void onConnect(IClient c) {
    }

    @Override
    public void onDisconnect(IClient c) {
    }

    @Override
    public void onReceive(Message m, IClient c) throws IOException {
        messageDistributor.onReceive(m, c);
    }

    public MessageDistributor getMessageDistributor() {
        return messageDistributor;
    }
}
