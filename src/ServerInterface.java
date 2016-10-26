
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

/**
 * Created by marco on 10/05/16.
 */
public interface ServerInterface extends Remote
{
    String registration(String username, String password) throws RemoteException;

    String login(String username, String password, ServerInterface stub) throws RemoteException, SQLException;

    String logout(String login) throws Exception;

    void visitGraph(String login) throws RemoteException, InterruptedException;

    void updatePosition(String login, int nuovaSala, int tempoVisita) throws RemoteException;

    void closeServer() throws RemoteException, SQLException;
}