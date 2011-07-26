package anuMidSub;

import anuThreadSub.MessageReceiver;
import anuThreadSub.MessageSender;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

/**
 * @author Anuja
 */
public class ChatClient extends MIDlet implements CommandListener, Runnable {
    private Display display;
    private Form sendIpForm;
    private Command exitCmd;
    private TextField ipTxtFld;
    private Command connectCmd;
    private Form chatForm;
    private TextField chatMsgTxtFld;
    private Command sendCmd;
    private String chatMsg;
    private MessageSender msgSenClass;
    private String srvIp;
    private SocketConnection socCon;
    private DataInputStream is;
    private DataOutputStream os;
    private Alert infoAlert;

    public void startApp() {
        display = Display.getDisplay(this);

        //------------------- Sending server IP address form -------------------
        
        sendIpForm = new Form("Chat Client");
        ipTxtFld = new TextField("Type server IP here \n", "", 24, TextField.ANY);
        sendIpForm.append(ipTxtFld);

        exitCmd = new Command("Exit", Command.EXIT, 7);
        sendIpForm.addCommand(exitCmd);
        connectCmd = new Command("Connect", Command.OK, 4);
        sendIpForm.addCommand(connectCmd);

        sendIpForm.setCommandListener(this);
        display.setCurrent(sendIpForm);

        //----------------- Client chatting interface --------------------------

        chatForm = new Form("Client Chatiing...");
        chatMsgTxtFld = new TextField("Enter chat", "", 160, TextField.ANY);
        chatForm.append(chatMsgTxtFld);

        sendCmd = new Command("Send", Command.OK, 4);
        chatForm.addCommand(sendCmd);
        chatForm.addCommand(exitCmd);
        chatForm.setCommandListener(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if(c == exitCmd){
            notifyDestroyed();
        }else if(c == connectCmd){
            new Thread(this).start();
        }else if(c == sendCmd){
            chatMsg = chatMsgTxtFld.getString();
            msgSenClass.send(chatMsg);
        }
    }

    public void run() {

        srvIp = ipTxtFld.getString();

        if (srvIp != null && srvIp.length() > 0) {
            try {
                // Create a socket connection and open a port for that connection
                socCon = (SocketConnection) Connector.open("socket://" + srvIp + ":60000");

                // Because we want to receive byte stream
                is = socCon.openDataInputStream();

                // Because we need to send byte stream
                os = socCon.openDataOutputStream();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Sending chat messages
            msgSenClass = new MessageSender(os, chatMsgTxtFld);
            msgSenClass.start();

            MessageReceiver msgRecClass = new MessageReceiver(is, chatForm);
            msgRecClass.start();

            infoAlert = new Alert("Server connected successfully.");
            display.setCurrent(infoAlert, chatForm);
        }
    }
}
