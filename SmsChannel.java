package website.monitor.Channels;

public class SmsChannel implements ResponseChannel {
    @Override
    public void notify(String message) {
        System.out.println("SMS: " + message);
    }
}
