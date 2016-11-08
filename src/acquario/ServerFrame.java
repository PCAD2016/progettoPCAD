package acquario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * Created by marco on 07/11/16.
 */
public class ServerFrame extends JFrame {
    private JButton ricaricaMappaSaleButton;
    private JButton disconnettiButton;
    private JButton connettiButton;
    private JTextArea textArea1;
    private JButton mostraPosizioneUtentiButton;
    private JPanel rootPanel;
    private JButton visualizzaStatisticheButton;
    private Server server;
    private StartServerWorker startServerWorker;
    private UserPositionWorker userPositionWorker;
    private UpdateGraphWorker updateGraphWorker;
    private VisualizzaStatisticheWorker visualizzaStatisticheWorker;

    public ServerFrame()
    {
        super("Server Acquario");

        connettiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    server = new Server();
                } catch (RemoteException | ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                startServerWorker = new StartServerWorker(server,ServerFrame.this);
                connettiButton.setEnabled(false);
                startServerWorker.execute();
            }
        });

        mostraPosizioneUtentiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea1.append("Calcolo posizione utenti in corso...\n\n");
                userPositionWorker = new UserPositionWorker(server,ServerFrame.this);
                userPositionWorker.execute();

            }
        });

        ricaricaMappaSaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGraphWorker = new UpdateGraphWorker(server,ServerFrame.this);
                updateGraphWorker.execute();
            }
        });

        visualizzaStatisticheButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visualizzaStatisticheWorker = new VisualizzaStatisticheWorker(server,ServerFrame.this);
                visualizzaStatisticheWorker.execute();
            }
        });

        setContentPane(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public JTextArea getTextArea1() {
        return textArea1;
    }

    public JButton getDisconnettiButton() {
        return disconnettiButton;
    }

    public JButton getMostraPosizioneUtentiButton() {
        return mostraPosizioneUtentiButton;
    }

    public JButton getRicaricaMappaSaleButton() {
        return ricaricaMappaSaleButton;
    }

    public static void main(String[] args)
    {
        ServerFrame serverFrame = new ServerFrame();
    }

    public JButton getVisualizzaStatisticheButton() {
        return visualizzaStatisticheButton;
    }
}
