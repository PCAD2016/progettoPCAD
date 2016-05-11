import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by marco on 10/05/16.
 */
public interface ServerInterface extends Remote {
    void registration(String username, String password) throws RemoteException;
    void login(String username, String password) throws RemoteException;
    void logout() throws RemoteException;
    void updatePosition() throws RemoteException;
}
