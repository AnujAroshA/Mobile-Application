package anuThreadSub;

import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.lcdui.TextField;

/**
 *
 * @author Anuja
 */
public class MessageSender extends Thread {
    private OutputStream thisOs;
    private String thisSrvChatMsg;
    private TextField thisTxtFld;

    // Chat Server and Client classes call this class with an OutputStream argument
    public MessageSender(OutputStream os, TextField passTxtFld) {
        this.thisOs = os;
        this.thisTxtFld = passTxtFld;
    }

    public synchronized void send(String srvChatMsg){
        this.thisSrvChatMsg = srvChatMsg;
        
        // Wakes up a single thread that is waiting on this object's monitor.
        notify();
    }

    // Synchronized methods enable a simple strategy for preventing thread interference and memory consistency errors.
    // if an object is visible to more than one thread, all reads or writes to that object's variables are done through synchronized methods.
    public synchronized void run() {
        System.out.println("Messege Sender Thread");

        // We have to run the OutputStream till end of the complete chat which can be longer than 255 bytes
        while (true) {

            // Clear the previous chat from the typing area in TextField
            if(!(thisTxtFld.getString().equals(""))){
                thisTxtFld.setString("");
            }

            if (thisSrvChatMsg == null) {
                try {
                    // Causes current thread to wait until another thread invokes the notify() method or the notifyAll() method for this object.
                    wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                try {
                    // Writes the specified byte to this output stream
                    thisOs.write(thisSrvChatMsg.getBytes());
                    // Tracking the new line
                    thisOs.write("\n".getBytes());

                    // Flushes this output stream and forces any buffered output bytes to be written out.
                    thisOs.flush();

                    // Assign the chat message value to null caz then it goes to wait stage
                    thisSrvChatMsg = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }       
    }
}
