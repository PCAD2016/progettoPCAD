package acquario;

import javax.swing.*;

/**
 * Created by marco on 07/11/16.
 */
public class VisitWorker extends SwingWorker<String,String>
{
    private Client client;
    private VisitFrame visitFrame;

    VisitWorker(Client client,VisitFrame visitFrame)
    {
        this.client = client;
        this.visitFrame = visitFrame;
    }

    @Override
    protected String doInBackground() throws Exception {
        if (!isCancelled())
        {
            visitFrame.getIniziaVisitaButton().setEnabled(false);
            visitFrame.getInterrompiVisitaButton().setEnabled(true);
            visitFrame.getTextArea().append("Visita iniziata\n");
            client.visitGraph();

            return "Visita terminata";
        }
        else return "";
    }

    @Override
    protected void done()
    {
        if(isCancelled()) visitFrame.getTextArea().append("Visita interrotta\n");

        else visitFrame.getTextArea().append("Visita terminata\n");

        visitFrame.getIniziaVisitaButton().setEnabled(true);

        visitFrame.getInterrompiVisitaButton().setEnabled(false);

    }
}
