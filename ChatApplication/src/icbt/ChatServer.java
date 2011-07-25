package icbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

public class ChatServer extends MIDlet implements CommandListener {

    private Display dsp;
    private Form serverForm;
    private Command startCmd;
    private Form chatForm;
    private TextField msgTf;
    private Command sendCmd;

    private MessageSender sender;

    public void startApp() {

        dsp = Display.getDisplay(this);

        serverForm = new Form("Chat Server");
        startCmd = new Command("Start", Command.OK, 5);

        serverForm.addCommand(startCmd);
        serverForm.setCommandListener(this);

        chatForm = new Form("Chatting..");
        msgTf = new TextField("", "", 100, TextField.ANY);
        sendCmd = new Command("Send", Command.OK, 5);

        chatForm.append(msgTf);
        chatForm.addCommand(sendCmd);
        chatForm.setCommandListener(this);

        dsp.setCurrent(serverForm);

    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == startCmd) {
            new ServerThread().start();
        } else if (c == sendCmd) {
            String msg = msgTf.getString();
            sender.send(msg);
        }
    }

    private class ServerThread extends Thread {

        public void run() {
            try {

                ServerSocketConnection serverSoc =(ServerSocketConnection) Connector.open("socket://:60000");

                String ip = serverSoc.getLocalAddress();
                serverForm.append(ip);
                serverForm.setTitle("Server Started, waiting for clients");

                SocketConnection socketCon = (SocketConnection)serverSoc.acceptAndOpen(); // blocking

                InputStream is = socketCon.openDataInputStream();
                OutputStream os = socketCon.openDataOutputStream();

                sender = new MessageSender(os);
                sender.start();

                MessageReciever r = new MessageReciever(is, chatForm);
                r.start();

                Alert a = new Alert("Client Connected Successfully");
                dsp.setCurrent(a, chatForm);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
