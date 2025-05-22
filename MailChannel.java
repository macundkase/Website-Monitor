package de.frauas.Channels;

public class MailChannel implements IResponseChannel {
    @Override
    public void notify(String message) {
        System.out.println("Mail: " + message);
    }
}
