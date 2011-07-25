package icbt;

import java.io.IOException;
import java.io.OutputStream;

public class MessageSender extends Thread {

    private String msg;
    private OutputStream os;

    public synchronized void send(String msg) {
        this.msg = msg;
        notify();
    }

    public MessageSender(OutputStream os) {
        this.os = os;
    }

    public synchronized  void run() {

        while (true) {

            if (msg == null) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                os.write(msg.getBytes());
                os.write("\n".getBytes());
                os.flush();
                msg = null;
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        }

    }
}
