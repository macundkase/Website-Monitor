package websiteMonitor;

import websiteMonitor.Channels.ResponseChannel;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private int frequency;
    private List<Subscription> subscriptions = new ArrayList<>();
    private List<ResponseChannel> channels = new ArrayList<>();
    private int counter = 0;

    public User(String name, int frequency, Subscription subscription, ResponseChannel channel) {
        this.name = name;
        this.frequency = frequency;
        this.subscriptions.add(subscription);
        this.channels.add(channel);
        subscription.addSubscriber(this);
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.addSubscriber(this);
    }

    public void addResponseChannel(ResponseChannel channel) {
        channels.add(channel);
    }

    public void checkUpdate() {
        counter++;
        if (counter >= frequency) {
            for (Subscription sub : subscriptions) {
                if (sub.hasChanged()) {
                    notifyChannels("Website updated: " + sub.getWebsite());
                }
            }
            counter = 0;
        }
    }

    private void notifyChannels(String message) {
        for (ResponseChannel channel : channels) {
            channel.notify(name + ": " + message);
        }
    }
}
