package icbt;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Form;

public class MessageReciever extends Thread {

    private InputStream is;
    private Form chatForm;

    public MessageReciever(InputStream is, Form chatForm) {
        this.is = is;
        this.chatForm = chatForm;
    }

    public void run() {

        while (true) {
            int c = 0;
            StringBuffer msg = new StringBuffer();
            try {
                while (true) {
                    c = is.read();
                    msg.append((char) c);
                    if (c == -1 || c == '\n') {
                        break;
                    }
                }
                chatForm.append(msg.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
