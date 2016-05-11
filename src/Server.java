import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;


public class Server implements ServerInterface {
    ConcurrentHashMap<String, String> utenti = new ConcurrentHashMap<String,String>();

    @Override
    public void registration(String username, String password) throws RemoteException {
        if(utenti.get(username) == null){
            utenti.put(username, password);
            System.err.println("Aggiunti "+ username + " " + password);
        }
        return;//TODO aggiungere eccezione se il nome utente è già in uso
    }

    @Override
    public void login(String username, String password) throws RemoteException {

    }

    @Override
    public void logout() throws RemoteException {

    }

    @Override
    public void updatePosition() throws RemoteException {

    }

    public static void main(String[] args) {
        System.setProperty("java.security.policy","file:./file.policy");
        System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/MyServer/");
        if (System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager());
        System.setProperty("java.rmi.server.hostname","localhost");
        Registry r = null;
        try {
            r = LocateRegistry.createRegistry(8001);
        } catch (RemoteException e) {
            try {
                r = LocateRegistry.getRegistry(8001);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
            String name = "Server";
            ServerInterface server = new Server();
        ServerInterface stub = null;
        try {
            stub = (ServerInterface) UnicastRemoteObject.exportObject(server,0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            r.rebind(name, stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Server bound");

    }
}

