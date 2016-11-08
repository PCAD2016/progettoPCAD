package acquario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

/**
 * Created by marco on 03/11/16.
 */
public class VisitFrame extends JFrame{
    private JPanel rootPanel;
    private JTextArea textArea1;
    private JButton visualizzaGrafoButton;
    private JButton iniziaVisitaButton;
    private JButton interrompiVisitaButton;
    private Client client;
    private VisitWorker worker;

    public VisitFrame(Client client) {

        super("Acquario");
        this.client = client;

        visualizzaGrafoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.visualizzaGrafo();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        iniziaVisitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker = new VisitWorker(client,VisitFrame.this);
                worker.execute();
            }
        });
        setContentPane(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        interrompiVisitaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker.cancel(true);
            }
        });
    }


    public JTextArea getTextArea()
    {
        return textArea1;
    }

    public JButton getIniziaVisitaButton() {
        return iniziaVisitaButton;
    }

    public JButton getInterrompiVisitaButton() {
        return interrompiVisitaButton;
    }


}
