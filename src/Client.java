import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by marco on 10/05/16.
 */
public class Client {
    private static ServerInterface stub;
    private static String login;

    public Client() {
        System.setProperty("java.security.policy", "file:./file.policy");
        System.setProperty("java.rmi.server.codebase", "file:${workspace_loc}/MyClient/");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.setProperty("java.rmi.server.hostname", "localhost");
        try {
            String name = "Server";
            Registry r = LocateRegistry.getRegistry(8001);
            stub = (ServerInterface) r.lookup(name);

        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }

    private static String registration(String username, String password) throws RemoteException {
        return stub.registration(username,password);

    }

    private static String login(String username, String password) throws RemoteException {
        login = username;
        return stub.login(login,password);

    }

    private static String logout() throws RemoteException {
        return stub.logout(login);

    }

    public static void main(String args[]) throws RemoteException {
        //Client client = new Client();
        Client client1 = new Client();
        //System.err.println(client1.login("yas", "zaza"));
        System.err.println(client1.registration("yas", "zaza"));

        //System.err.println(client.registration("marco", "azaz"));

        System.err.println(client1.login("yas", "zaza"));

        //System.err.println(client.login("marco", "azaz"));

        //System.err.println(client1.login("ya", "zaza"));

        //System.err.println(client1.login("yas", "zaza"));

        System.err.println(client1.logout());






    }
}

