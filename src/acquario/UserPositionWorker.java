package acquario;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by marco on 08/11/16.
 */
public class UserPositionWorker extends SwingWorker<String,String> {
    private final ServerFrame serverFrame;
    private final Server server;

    public UserPositionWorker(Server server, ServerFrame serverFrame) {
        this.server = server;
        this.serverFrame = serverFrame;
    }

    @Override
    protected String doInBackground() throws Exception {
        return server.posizione();
    }

    @Override
    protected void done() {
        try {
            serverFrame.getTextArea1().append(get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
