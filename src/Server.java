import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements RemoteMethods {
    ConcurrentHashMap<String, String> utenti = new ConcurrentHashMap<String,String>();

    @Override
    public void registration(String username, String password) throws RemoteException {
        if(utenti.get(username) == null) utenti.put(username, password);
        return;//nome utente già in uso
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

    }
}
