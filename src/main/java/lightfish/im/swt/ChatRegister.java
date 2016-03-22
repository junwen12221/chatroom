package lightfish.im.swt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatRegister extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(ChatRegister.class);
    JButton button = new JButton();
    JTextField content = new JTextField();

    static int chatCounter;

    public void launch() {
        button.setText("生成聊天窗口");

        this.setLocation(100, 600);
        this.setSize(400, 400);
        this.add(button, BorderLayout.SOUTH);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(content);
        this.content.setEditable(true);
        this.content.setSize(300, 300);
        this.content.setText("请输入 id:名字");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                chatCounter++;
                String str = content.getText();
                String id;
                String name;
                try {
                    String strArray[] = str.split(":");
                    id = strArray[0];
                    name = strArray[1];
                } catch (Exception ex) {
                    return;
                }
                new ChatClient(Integer.parseInt(id), name).launch();
            }
        });
    }

    public static void main(String[] args) {
        new ChatRegister().launch();
    }
}
