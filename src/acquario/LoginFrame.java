package acquario;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * Created by marco on 02/11/16.
 */
public class LoginFrame extends JFrame{

    private JPanel rootPanel;
    private JTextField nomeUtenteTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Client client;

    public LoginFrame() throws RemoteException {

        super("Login Acquario");
        client = new Client();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(client.login(nomeUtenteTextField.getText(), String.valueOf(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(null,"Login effettuato per "+ client.getLogin());
                        setVisible(false);
                        new VisitFrame(client);
                    }
                    else JOptionPane.showMessageDialog(null,"Login non effettuato");

                } catch (RemoteException | SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(client.registration(nomeUtenteTextField.getText(), String.valueOf(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(null, "Utente " + nomeUtenteTextField.getText() + " registrato");

                    }
                    else JOptionPane.showMessageDialog(null,"Errore di registrazione");
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setContentPane(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }

    public static void main(String[] args) throws RemoteException {
        LoginFrame loginFrame = new LoginFrame();
    }
}

