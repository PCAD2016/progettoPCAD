import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by marco on 10/05/16.
 */
public interface ServerInterface extends Remote {
    String registration(String username, String password) throws RemoteException;
    String login(String username, String password) throws RemoteException;

    String logout(String login) throws RemoteException;

    void updatePosition() throws RemoteException;
}
