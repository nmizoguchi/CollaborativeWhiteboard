package server;

import java.awt.Rectangle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ServerGUI extends JFrame {

    private final JLabel externalIP;
    private final JLabel externalIPLabel;
    private final JLabel localIP;
    private final JLabel localIPLabel;
    private final JLabel serverPortLabel;
    private final JTextField serverPort;
    private final JButton startServer;

    public ServerGUI() {
        this.setBounds(new Rectangle(300, 300));

        externalIPLabel = new JLabel("External IP:");
        localIPLabel = new JLabel("Local IP:");

        externalIP = new JLabel("External IP:");
        localIP = new JLabel("Local IP:");

        serverPortLabel = new JLabel("Port:");
        serverPort = new JTextField();
        startServer = new JButton("Start");

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        panel.setLayout(layout);

        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(externalIPLabel)
                                .addComponent(externalIP))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(localIPLabel)
                                .addComponent(localIP))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(serverPortLabel)
                                .addComponent(serverPort))
                                .addComponent(startServer));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(externalIPLabel)
                                .addComponent(externalIP))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(localIPLabel)
                                .addComponent(localIP))
                .addGroup(
                        layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(serverPortLabel)
                                .addComponent(serverPort))
                                .addComponent(startServer));
        this.add(panel);
    }
}
