import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;


public class Server implements RemoteMethods  {
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
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            try {
                String name = "Server";
                RemoteMethods server = new Server();
                RemoteMethods stub = (RemoteMethods) UnicastRemoteObject.exportObject(server,0);
                Registry registry = LocateRegistry.getRegistry();
                registry.rebind(name, stub);
                System.out.println("Server bound");
            }
            catch (Exception e) {
                System.err.println("Server exception:");
                e.printStackTrace();
            }
    }
}

