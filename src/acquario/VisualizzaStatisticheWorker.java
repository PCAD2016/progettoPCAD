package acquario;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by marco on 08/11/16.
 */
public class VisualizzaStatisticheWorker extends SwingWorker<String,String>{
    private final Server server;
    private final ServerFrame serverFrame;

    public VisualizzaStatisticheWorker(Server server, ServerFrame serverFrame) {

        this.server = server;

        this.serverFrame = serverFrame;
    }

    @Override
    protected String doInBackground() throws Exception {
        serverFrame.getTextArea1().append("STATISTICHE:\n");
        serverFrame.getVisualizzaStatisticheButton().setEnabled(false);
        return server.generaStatistiche();
    }

    @Override
    protected void done() {
        try {
            serverFrame.getTextArea1().append(get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        serverFrame.getVisualizzaStatisticheButton().setEnabled(true);
    }
}
