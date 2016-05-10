import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by marco on 10/05/16.
 */
public class Client {
    public static void main(String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Server";
            Registry registry = LocateRegistry.getRegistry(8081);
            RemoteMethods remoteMethods = (RemoteMethods) registry.lookup(name);
            //remoteMethods.registration("marco","azaz");

        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}
