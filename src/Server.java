import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server extends UnicastRemoteObject implements ServerInterface {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    ConcurrentHashMap<String, String> utenti = new ConcurrentHashMap<String,String>();
    CopyOnWriteArrayList<String> listaLogin = new CopyOnWriteArrayList<String>();

    String driver = "org.postgresql.Driver";
    String url = "jdbc:postgresql://127.0.0.1:5432/mydb";
    Connection con;
    Statement cmd;


    protected Server() throws RemoteException, ClassNotFoundException, SQLException {
        Class.forName(driver);
        con = DriverManager.getConnection(url, "postgres", "postgres");
        cmd = con.createStatement();
        getUsers();
    }

    @Override
    public String registration(String username, String password) throws RemoteException {
        String message;
        if(utenti.get(username) == null){
            utenti.put(username, password);
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
        String log = utenti.get(username);
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


        //listaLogin.remove(login);
    	
    	
    	
    	/*String message = "Numero Utenti " + listaLogin.size();
        System.err.println(message);
        return message;*/


        listaLogin.remove(login);
        String message = "Logout effettuato per " + login;
        System.err.println(message);
        return message;


    }

    @Override
    public void updatePosition() throws RemoteException {

    }

    public void closeServer() throws RemoteException, SQLException{
        storeUsers();
    }

    public void storeUsers() throws RemoteException {
        Set<Entry<String, String>> entrySet = utenti.entrySet();
        java.util.Iterator<Entry<String, String>> iterator = entrySet.iterator();
        String values = null;String delim = "=";
        String query;
        String[] tokens;

        while(iterator.hasNext()){
            values = iterator.next().toString();
            tokens = values.split(delim);
            query = "INSERT INTO acquariopcad.utente VALUES('"+tokens[0]+"','"+tokens[1]+"');"; /*')  WHERE NOT EXISTS SELECT username FROM acquariopcad.utente where username = '"+tokens[i-1]+"';";*/
            try {
                cmd.executeQuery(query);
            } catch (SQLException e) {

            }
        }
    }

    public void getUsers() throws SQLException {
        String query = "SELECT * FROM acquariopcad.utente;";
        ResultSet resultSet = cmd.executeQuery(query);
        String username;
        String password;
        String message;
        while(resultSet.next()){
            username = resultSet.getString("username");
            password = resultSet.getString("password");
            if(utenti.get(username) == null) {
                utenti.put(username, password);
                message = "Utente: " + username + " trovato nel database";
                System.err.println(message);
            }
        }
    }



    private void executeQuery(String query) {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args) throws RemoteException, ClassNotFoundException, SQLException {
        System.setProperty("java.security.policy","file:./file.policy");
        System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/MyServer/");
        if (System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager());
        System.setProperty("java.rmi.server.hostname","localhost");
        Registry r = null;
        try {
            r = LocateRegistry.createRegistry(8000);
        } catch (RemoteException e) {
            try {
                r = LocateRegistry.getRegistry(8000);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        String name = "Server";
        ServerInterface server = new Server();
        try {
            r.rebind(name, server);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Server bound");


    }
}