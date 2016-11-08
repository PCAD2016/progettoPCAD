package acquario;

import javax.swing.*;

/**
 * Created by marco on 08/11/16.
 */
public class UpdateGraphWorker extends SwingWorker<String,String> {
    private final Server server;
    private final ServerFrame serverFrame;

    public UpdateGraphWorker(Server server, ServerFrame serverFrame) {
        this.server = server;
        this.serverFrame = serverFrame;
    }

    @Override
    protected String doInBackground() throws Exception {
        serverFrame.getRicaricaMappaSaleButton().setEnabled(false);
        server.refreshGraph();
        return "";
    }

    @Override
    protected void done() {
        serverFrame.getRicaricaMappaSaleButton().setEnabled(true);
        serverFrame.getTextArea1().append("Mappa sale aggiornata\n");
    }
}
