package acquario;

import org.jgrapht.graph.ListenableDirectedGraph;
import javax.security.auth.login.LoginException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;


public interface ServerInterface extends Remote
{
    boolean registration(String username, String password) throws RemoteException;

    boolean login(String username, String password, ClientInterface stub) throws RemoteException, SQLException;

    String logout(String login) throws Exception;

    User getUser(String login) throws RemoteException, LoginException;

    ListenableDirectedGraph<Sala, CustomEdge> getGraph() throws RemoteException;

    String updatePosition(String login, int nuovaSala, int tempoVisita) throws RemoteException, LoginException;

}