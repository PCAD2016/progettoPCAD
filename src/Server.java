import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server extends UnicastRemoteObject implements ServerInterface {
    ConcurrentHashMap<String, String> utenti = new ConcurrentHashMap<String,String>();

    CopyOnWriteArrayList<String> listaLogin = new CopyOnWriteArrayList<String>(); // lista dei utenti collegati..

    protected Server() throws RemoteException {
    }


    @Override
    public String registration(String username, String password) throws RemoteException {
        String message;
        if(utenti.get(username) == null){
            utenti.put(username, password); // username = Key and password  Value
            message = "Aggiunto utente: "+ username;
            System.err.println(message);
            return message;
        }
        else {
            message = "Username già esistente";
            System.err.println(message);
            return message;
        }
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        String message;
        String log = utenti.get(username); // lo passiamo un username e ci restituisce la sua password.
        System.err.println("log: "+ log);
        if(log == null){
            message = "Username "+ username +" non esistente";
            System.err.println(message);
            return message;
        }


        else if(!log.equals(password) ){
            message = "Password errata!";
            System.err.println(message);
            return message;
        }
        else{
            listaLogin.add(username);
            message = "Login effettuato!";
            System.err.println(message);
            return message;
        }
    }


    @Override
    public String logout(String login) throws RemoteException {

        listaLogin.remove(login);
        String message = "Logout effettuato per " + login;
        System.err.println(message);
        return message;


    }

    @Override
    public void updatePosition() throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        System.setProperty("java.security.policy","file:./file.policy");
        System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/MyServer/");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

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
            r.rebind(name, server);/*r.rebind() crea un collegamento tra un nome simbolico ("server") ed
                                       un riferimento all’oggetto. Se esiste già un collegamento per lo stesso oggetto
                                        all’interno dello stesso registry, tale collegamento viene sovrascritto */
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Server bound");

    }
}

