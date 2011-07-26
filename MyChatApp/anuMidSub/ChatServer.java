package anuMidSub;

import anuThreadSub.MessageReceiver;
import anuThreadSub.MessageSender;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

/**
 * @author Anuja
 */
public class ChatServer extends MIDlet implements CommandListener, ItemCommandListener, Runnable {
    private Display display;
    private Form chatServerForm;
    private Command exitCmd;
    private StringItem ipStrItem;
    private StringItem startServerStrItem;
    private Command startCmd;
    private Form showIpForm;
    private Gauge gaugeItem;
    private Spacer spacerItem;
    private ServerSocketConnection serSocCon;
    private String deviceIp;
    private SocketConnection socCon;
    private DataInputStream is;
    private DataOutputStream os;
    private Form srvChattingFrm;
    private TextField chatTxtFld;
    private Alert infoAlert;
    private Command sendCmd;
    private String srvChatMsg;
    private MessageSender msgSenderClass;

    public void startApp() {
        display = Display.getDisplay(this);

        //-------------------------- Start chat server form --------------------
        chatServerForm = new Form("Chat Server");

        // \t use for getting tab space
        startServerStrItem = new StringItem("Start Chat Server \t", "Start", Item.BUTTON);
        chatServerForm.append(startServerStrItem);

        startCmd = new Command("Start", Command.ITEM, 8);
        startServerStrItem.setDefaultCommand(startCmd);
        startServerStrItem.setItemCommandListener(this);

        // Provide space as developer defined minWidth and minHeight
        spacerItem = new Spacer(200, 50);
        chatServerForm.append(spacerItem);

        // Continuous-running state of a non-interactive Gauge with indefinite range
        gaugeItem = new Gauge("Waiting for client... \n", false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
        // Set layout position for this gauge item
        gaugeItem.setLayout(Item.LAYOUT_CENTER);
        chatServerForm.append(gaugeItem);

        exitCmd = new Command("Exit", Command.EXIT, 7);
        chatServerForm.addCommand(exitCmd);

        chatServerForm.setCommandListener(this);
        display.setCurrent(chatServerForm);

        // ----------------------- Show IP form --------------------------------

        showIpForm = new Form("Chat Server");
        ipStrItem = new StringItem("Client must connect to this IP: \n\n", "My IP \n");
        ipStrItem.setLayout(Item.LAYOUT_CENTER);

        // setFont() sets the application's preferred font for rendering this StringItem.
        ipStrItem.setFont(Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE));
        showIpForm.append(ipStrItem);

        showIpForm.addCommand(exitCmd);
        showIpForm.setCommandListener(this);

        //------------------- Server chatting form -----------------------------

        srvChattingFrm = new Form("Server Chatting...");
        chatTxtFld = new TextField("Enter Chat", "", 160, TextField.ANY);
        srvChattingFrm.append(chatTxtFld);

        sendCmd = new Command("Send", Command.OK, 4);
        srvChattingFrm.addCommand(sendCmd);
        srvChattingFrm.addCommand(exitCmd);
        srvChattingFrm.setCommandListener(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if(c == exitCmd){
            notifyDestroyed();
        }else if(c == sendCmd){
            // getString() gets the contents of the TextField as a string value.
            srvChatMsg = chatTxtFld.getString();
            // Send the chat to send() in MessageSender class
            msgSenderClass.send(srvChatMsg);
        }
    }

    public void commandAction(Command c, Item item) {
        if(c == startCmd){
            new Thread(this).start();
            //Alert alert = new Alert("Working");
            //display.setCurrent(alert);
        }
    }

    public void run() {
        System.out.println("Runnig");
        try {
            // ServerSocketConnection interface defines the server socket stream connection.
            // Create the server listening socket for port 60000
            serSocCon = (ServerSocketConnection) Connector.open("socket://:60000");
            System.out.println("Open the socket...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            // Gets the local address to which the socket is bound.
            // The host address(IP number) that can be used to connect to this end of the socket connection from an external system.
            deviceIp = serSocCon.getLocalAddress();
            System.out.println("Get device IP...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        showIpForm.append(deviceIp);
        display.setCurrent(showIpForm);
        
        try {
            System.out.println("Waiting for client request...");
            // Wait for a connection.
            // A socket is accessed using a generic connection string with an explicit host and port number.
            // acceptAndOpen() inherited from interface javax.microedition.io.StreamConnectionNotifier
            // Returns a StreamConnection object that represents a server side socket connection.
            // The method blocks until a connection is made.
            socCon = (SocketConnection) serSocCon.acceptAndOpen();
            System.out.println("Accepted and open the connection...");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            // InputStream is an abstract class and it is the superclass of all classes representing an input stream of bytes.
            // openDataInputStream() inherited from interface javax.microedition.io.InputConnection
            // openDataInputStream() Open and return an input stream for a connection.
            is = socCon.openDataInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            // OutputStream is an abstract class and it is the superclass of all classes representing an output stream of bytes.
            // An output stream accepts output bytes and sends them to some sink.
            // openDataOutputStream() inherited from interface javax.microedition.io.OutputConnection
            // openDataOutputStream() open and return a data output stream for a connection.
            os = socCon.openDataOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Send our chat to other party
        // Initialization part have to doen begining caz Send command want to use the send() in MessageSender class
        // We pass the TextField as argement caz we want to clear the field before sent another chat
        msgSenderClass = new MessageSender(os, chatTxtFld);
        msgSenderClass.start();

        //clear();

        // Receive other party's chat and load our chatting interface
        MessageReceiver msgReceiverClass = new MessageReceiver(is, srvChattingFrm);
        msgReceiverClass.start();

        infoAlert = new Alert("Client connected successfully.");
        display.setCurrent(infoAlert, srvChattingFrm);
    }

    public void clear(){
        chatTxtFld.setString("");
        //msgSenderClass.start();
    }

    public TextField getChatTxtFld() {
        return chatTxtFld;
    }
}