package websiteMonitor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public class Subscription {
    private URI website;
    private String lastContent = "";
    private boolean changed = false;
    private Set<User> subscribers = new HashSet<>();

    public Subscription(URI website) {
        this.website = website;
    }

    public void addSubscriber(User user) {
        subscribers.add(user);
    }

    public URI getWebsite() {
        return website;
    }

    public boolean hasChanged() {
        return changed;
    }

    public void CheckUpdate() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(website).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String currentContent = response.body();
            if (!currentContent.equals(lastContent)) {
                changed = true;
                lastContent = currentContent;
            } else {
                changed = false;
            }

        } catch (IOException | InterruptedException e) {
            changed = false;
            System.out.println("Failed to fetch: " + website + " - " + e.getMessage());
        }
    }
}
