package acquario;

import javax.swing.*;
import java.util.List;

/**
 * Created by marco on 08/11/16.
 */
public class StartServerWorker extends SwingWorker<String,String> {
    private final Server server;
    private final ServerFrame serverFrame;

    public StartServerWorker(Server server, ServerFrame serverFrame) {
        this.server = server;
        this.serverFrame = serverFrame;
    }

    @Override
    protected String doInBackground() throws Exception {
        publish("Avvio server...\n");
        server.startServer();

        publish("Connessione al database...\n");
        server.connectToDatabase();

        publish("Recupero utenti registrati...\n");
        server.getUsers();

        publish("Recupero mappa stanze...\n");
        server.createGraph();
        return "";
    }

    @Override
    protected void process(List<String> chunks) {
        for(String message : chunks)
        {
            if(isCancelled()) break;
            serverFrame.getTextArea1().append(message);
        }
    }

    @Override
    protected void done() {
        serverFrame.getDisconnettiButton().setEnabled(true);
        serverFrame.getMostraPosizioneUtentiButton().setEnabled(true);
        serverFrame.getMostraPosizioneUtentiButton().setEnabled(true);
        serverFrame.getRicaricaMappaSaleButton().setEnabled(true);
        serverFrame.getVisualizzaStatisticheButton().setEnabled(true);
        serverFrame.getTextArea1().append("Server pronto\n\n");

    }
}
