
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

/**
 * Created by marco on 10/05/16.
 */
public class Client //TODO client interface
{
    private static ServerInterface stub;
    private String login;

    private Client()
    {
        connect();
    }

    private void connect()
    {
        System.setProperty("java.security.policy", "file:./file.policy");
        System.setProperty("java.rmi.server.codebase", "file:${workspace_loc}/MyClient/");

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        System.setProperty("java.rmi.server.hostname", "localhost");
        try
        {
            Registry r = LocateRegistry.getRegistry(8000);
            String serverName = "Server";
            stub = (ServerInterface) r.lookup(serverName);

        } catch (Exception e)
        {
            System.err.println("Server non trovato:");
            e.printStackTrace();
        }
    }

    private static String registration(String username, String password) throws RemoteException
    {
        return stub.registration(username,password);

    }

    private String login(String username, String password) throws RemoteException
    {
        login = username;
        return stub.login(login,password,stub);

    }

    private String logout() throws RemoteException
    {
        return stub.logout(login);

    }

    private void closeServer() throws RemoteException, SQLException
    {
        stub.closeServer();
    }

    public static void main(String args[]) throws RemoteException, SQLException
    {
        Client client = new Client();
        Client client1 = new Client();
        Client client3 = new Client();
        //System.err.println(client1.login("yas", "zaza"));
        /*System.err.println(client1.registration("ciao", "zaza"));

        System.err.println(client.registration("asdfas", "azaz"));

        System.err.println(client3.registration("yas", "zaza"));*/

        System.err.println(client.login("marco", "azaz"));

        //System.err.println(client1.login("ya", "zaza"));

        //System.err.println(client1.login("yas", "zaza"));
        //client1.closeServer();
        System.err.println(client1.logout());
    }
}
