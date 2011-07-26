package anuThreadSub;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Form;

/**
 *
 * @author Anuja
 */
public class MessageReceiver extends Thread {
    private InputStream thisIs;
    private Form thisForm;
    private StringBuffer strBuf;

    public MessageReceiver(InputStream is, Form passForm) {
        this.thisIs = is;
        this.thisForm = passForm;
    }

    public synchronized void run() {
        System.out.println("Message Receiver Thread");

        // This while loop enable the reply facility for a received chat
        while (true) {

            int msgByteInt = 0;
            // A string buffer implements a mutable sequence of characters.
            // String buffers are safe for use by multiple threads.
            strBuf = new StringBuffer();

            try {

                // When we use the read() method once, it can only read max 255 bytes.
                // But a chat message size can be more than that. So we need a while loop to read the whole message
                while (true) {

                    // read() reads the next byte of data from the input stream.
                    // The value byte is returned as an int in the range 0 to 255.
                    // If no byte is available because the end of the stream has been reached, the value -1 is returned.
                    msgByteInt = thisIs.read();

                    // Read one stream and append it to StringBuffer
                    strBuf.append((char) msgByteInt);

                    // Detecting end of a stream
                    if (msgByteInt == -1 || msgByteInt == '\n') {
                        break;
                    }
                }
                thisForm.append(strBuf.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
