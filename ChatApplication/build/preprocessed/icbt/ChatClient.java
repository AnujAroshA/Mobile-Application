package icbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

public class ChatClient extends MIDlet implements CommandListener {

    private Display dsp;
    private Form clientForm;
    private TextField ipTf;
    private Command connectCmd;
    private Form chatForm;
    private TextField msgTf;
    private Command sendCmd;
    private MessageSender sender;

    public void startApp() {

        dsp = Display.getDisplay(this);

        clientForm = new Form("Chat Client");
        ipTf = new TextField("Ip", "", 15, TextField.ANY);
        clientForm.append(ipTf);

        connectCmd = new Command("Connect", Command.OK, 5);
        clientForm.addCommand(connectCmd);
        clientForm.setCommandListener(this);

        chatForm = new Form("Chatting..");
        msgTf = new TextField("", "", 100, TextField.ANY);
        sendCmd = new Command("Send", Command.OK, 5);

        chatForm.append(msgTf);
        chatForm.addCommand(sendCmd);
        chatForm.setCommandListener(this);

        dsp.setCurrent(clientForm);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {

        if (c == connectCmd) {
            new ClientThread().start();
        } else if (c == sendCmd) {
            String msg = msgTf.getString();
            sender.send(msg);
        }

    }

    private class ClientThread extends Thread {

        public void run() {

            String ip = ipTf.getString();

            if (ip != null && ip.length() > 0) {
                try {
                    SocketConnection sCon = (SocketConnection) Connector.open("socket://" + ip + ":60000");

                    InputStream is = sCon.openDataInputStream();
                    OutputStream os = sCon.openDataOutputStream();


                    sender = new MessageSender(os);
                    sender.start();

                    MessageReciever r = new MessageReciever(is, chatForm);
                    r.start();

                    Alert a = new Alert("Connected Successfully");

                    dsp.setCurrent(a, chatForm);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }


        }
    }
}
