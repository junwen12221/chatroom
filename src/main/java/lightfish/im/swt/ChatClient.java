package lightfish.im.swt;

import lightfish.im.Config;
import lightfish.im.core.dto.BaseMsg;
import lightfish.im.core.client.BaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.JTextField;



public class ChatClient extends JFrame {
    private static final Logger LOG = LoggerFactory.getLogger(ChatClient.class);
    private static final String LINE = System.getProperty("line.separator");
    JTextField msg = new JTextField();
    TextArea content = new TextArea();
    public final BaseMsg baseMsg;
    BaseClient teamSayClient;

    public ChatClient(int userId, String userName) throws HeadlessException {
        baseMsg = new BaseMsg(Long.valueOf(userId), (byte)0, "");
        this.setTitle(userName);
    }

    public void launch() {
        this.setLocation(400, 400);
        this.setSize(400, 400);
        content.setSize(300, 400);
        this.content.setLocation(300, 300);
        this.add(msg, BorderLayout.SOUTH);
        this.add(content);
        this.content.setEditable(false);
        //        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.msg.addActionListener(new TFListener());
        this.setVisible(true);

        // 连接
        this.connect();
    }

    private void connect() {
        // 广播地址
        InetSocketAddress address = new InetSocketAddress(Config.getInatAddress(), Config.getPORT());
        teamSayClient = new BaseClient(this);
        teamSayClient.bind(address);
    }

    public void showSayMsg(String msg) {
        LOG.info("showSayMsg:" + msg);
        StringBuilder text = new StringBuilder();
        text.append(content.getText());
        text.append(LINE);
        text.append(msg);
        content.setText(text.toString());
    }


    private class TFListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            baseMsg.setContent(msg.getText());
            msg.setText("");
            teamSayClient.getChannel().writeAndFlush(baseMsg);
        }
    }

    public static void main(String[] args) {
        int chatCounter = 1;
        new ChatClient(chatCounter++, String.valueOf(chatCounter)).launch();
    }
}
