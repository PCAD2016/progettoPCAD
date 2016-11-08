package acquario;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by marco on 07/11/16.
 */
public interface ClientInterface extends Remote{
    void updateGraph() throws RemoteException;
}
