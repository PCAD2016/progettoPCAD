import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by marco on 10/05/16.
 */
public class Client {
    public static void main(String args[]) {
        System.setProperty("java.security.policy","file:./file.policy");
        System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/MyClient/");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.setProperty("java.rmi.server.hostname","localhost");
        try {
            String name = "Server";
            Registry r = LocateRegistry.getRegistry(8001);
            ServerInterface stub = (ServerInterface) r.lookup(name);
            stub.registration("marco","azaz");

        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}
