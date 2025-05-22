package websiteMonitor;

import websiteMonitor.Channels.IResponseChannel;
import websiteMonitor.Channels.MailChannel;
import websiteMonitor.Channels.SmsChannel;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebsiteMonitor {

    private final HashMap<String, User> userRegistry = new HashMap<>();
    private final HashMap<String, Subscription> activeSubscriptions = new HashMap<>();

    private WebsiteMonitor() {}

    public void initiateMonitoring() {
        System.out.println("[Monitor] Starting...");
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(this::checkForWebsiteUpdates, 0, Settings.MONITOR_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
        scheduler.scheduleAtFixedRate(this::sendNotificationsToUsers, 1, Settings.NOTIFICATION_INTERVAL, TimeUnit.of(Settings.TIME_UNIT));
    }

    // ---- User Registration Methods ----
    public WebsiteMonitor addUser(String userName, int frequency, URI website, ResponseChannel responseChannel) {
        Subscription subscription = findOrCreateSubscription(website);
        userRegistry.put(userName, new User(userName, frequency, subscription, responseChannel));
        System.out.println("User added: " + userName + " monitoring " + website);
        return this;
    }

    public WebsiteMonitor removeUser(String userName) {
        if (userRegistry.containsKey(userName)) {
            userRegistry.remove(userName);
            System.out.println("User removed: " + userName);
        }
        return this;
    }

    // ---- User Customization Methods ----
    public WebsiteMonitor attachWebsiteToUser(String userName, URI newSite) {
        if (userRegistry.containsKey(userName) && newSite != null) {
            userRegistry.get(userName).addSubscription(findOrCreateSubscription(newSite));
        }
        return this;
    }

    public WebsiteMonitor attachNotificationChannel(String userName, IResponseChannel newChannel) {
        if (userRegistry.containsKey(userName) && newChannel != null) {
            userRegistry.get(userName).addResponseChannel(newChannel);
        }
        return this;
    }

    // ---- Internal Logic ----
    private Subscription findOrCreateSubscription(URI uri) {
        return activeSubscriptions.computeIfAbsent(uri.toString(), k -> new Subscription(uri));
    }

    private void checkForWebsiteUpdates() {
        System.out.println("[Monitor] Checking all subscribed websites...");
        activeSubscriptions.values().forEach(Subscription::CheckUpdate);
    }

    private void sendNotificationsToUsers() {
        userRegistry.values().forEach(User::checkUpdate);
    }

    // ---- Main Method ----
    public static void main(String[] args) {
        WebsiteMonitor monitor = new WebsiteMonitor();

        URI site1 = URI.create("https://example.com");
        URI site2 = URI.create("https://yourcustomsite.org/news");

        monitor.addUser("Alice", 3, site1, new MailChannel())
               .attachWebsiteToUser("Alice", site2);

        monitor.addUser("Bob", 1, site1, new MailChannel())
               .attachNotificationChannel("Bob", new SmsChannel());

        monitor.initiateMonitoring();
    }
}
