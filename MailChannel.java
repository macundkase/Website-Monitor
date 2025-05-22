package website.monitor.Channels;

public class MailChannel implements ResponseChannel {
    @Override
    public void notify(String message) {
        System.out.println("Mail: " + message);
    }
}
