import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.rmi.NotBoundException;
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


public class Server extends UnicastRemoteObject implements ServerInterface
{
    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, String> utenti = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<User> listaUser = new CopyOnWriteArrayList<>();
    private Registry r = null;
    private String serverName = "Server";
    private Statement cmd;
    UndirectedGraph<Sala,DefaultEdge> graph;

    private Server() throws RemoteException, ClassNotFoundException, SQLException
    {
        startServer();
        connectToDatabase();
        getUsers();
        createGraph();
    }

    private void connectToDatabase() throws ClassNotFoundException, SQLException
    {
        String driver = "org.postgresql.Driver";
        Class.forName(driver);
        String url = "jdbc:postgresql://127.0.0.1:5432/mydb";
        Connection con = DriverManager.getConnection(url, "postgres", "postgres");
        cmd = con.createStatement();
    }

    @Override
    public String registration(String username, String password) throws RemoteException
    {
        String message;
        if(utenti.get(username) == null)
        {
            utenti.put(username, password);
            message = "Aggiunto utente: "+ username;
            System.err.println(message);
            return message;
        }
        else
        {
            message = "Username già esistente";
            System.err.println(message);
            return message;
        }
    }

    @Override
    public String login(String username, String password, ServerInterface stub) throws RemoteException
    {
        String message;
        String log = utenti.get(username);
        System.err.println("log: "+ log);
        if(log == null)
        {
            message = "Username "+ username +" non esistente";
            System.err.println(message);
            return message;
        }
        else if(!log.equals(password))
        {
            message = "Password errata!";
            System.err.println(message);
            return message;
        }
        else
        {
            listaUser.add(new User(username,stub));
            message = "Login effettuato!";
            System.err.println(message);
            return message;
        }
    }


    @Override
    public String logout(String username) throws RemoteException
    {
        //TODO salva info utente su DB
        Iterator<User> it = listaUser.iterator();
        while (it.hasNext())
        {
            User user = it.next();
            if (user.getUsername().equals(username))
            {
                it.remove();
                String message = "Logout effettuato per " + username;
                System.err.println(message);
                return message;
            }
        }
        return null;
    }

    @Override
    public void updatePosition() throws RemoteException
    {
        //TODO
    }

    public void closeServer() throws RemoteException, SQLException
    {
        try
        {
            r.unbind(serverName);
        } catch (NotBoundException e)
        {
            e.printStackTrace();
        }
        System.err.println("Server unbound");
    }

    private synchronized void storeUsers() throws RemoteException
    {
        Set<Entry<String, String>> entrySet = utenti.entrySet();
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        String values;
        String delim = "=";
        String query;
        String[] tokens;

        while(iterator.hasNext()){
            values = iterator.next().toString();
            tokens = values.split(delim);
            query = "INSERT INTO acquariopcad.utente VALUES('"+tokens[0]+"','"+tokens[1]+"');";
            try
            {
                cmd.executeQuery(query);
            } catch (SQLException e)
            {
                System.err.println("Utente già presente nel database");
            }
        }
    }

    private synchronized void getUsers() throws SQLException
    {
        String query = "SELECT * FROM acquariopcad.utente;";
        ResultSet resultSet = cmd.executeQuery(query);
        String username;
        String password;
        String message;
        if (resultSet.next())
        {
            do
            {
                username = resultSet.getString("username");
                password = resultSet.getString("password");
                if (utenti.get(username) == null)
                {
                    utenti.put(username, password);
                    message = "Utente: " + username + " trovato nel database";
                    System.err.println(message);
                }
            } while (resultSet.next());
        }
    }



    private void startServer() throws RemoteException, ClassNotFoundException, SQLException
    {
        System.setProperty("java.security.policy","file:./file.policy");
        System.setProperty("java.rmi.server.codebase","file:${workspace_loc}/MyServer/");
        if (System.getSecurityManager() == null) System.setSecurityManager(new SecurityManager());
        System.setProperty("java.rmi.server.hostname","localhost");
        try
        {
            r = LocateRegistry.createRegistry(8000);
        }
        catch (RemoteException e)
        {
            try
            {
                r = LocateRegistry.getRegistry(8000);
            }
            catch (RemoteException e1)
            {
                e1.printStackTrace();
            }
        }
        try
        {
            r.rebind(serverName, this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Server bound");
    }

    private UndirectedGraph<Sala,DefaultEdge> createGraph() throws SQLException {
        String query = "SELECT s1.numero AS numero1, s1.nome AS nome1, s1.tempovisita AS tempovisita1, s2.numero AS numero2, s2.nome AS nome2, s2.tempovisita AS tempovisita2\n" +
                "FROM acquariopcad.sala AS s1 JOIN acquariopcad.successore ON s1.numero = acquariopcad.successore.sala1 JOIN acquariopcad.sala AS s2 ON acquariopcad.successore.sala2 = s2.numero";
        ResultSet resultSet = cmd.executeQuery(query);
        String message;
        graph = new SimpleGraph<>(DefaultEdge.class);
        if (resultSet.next())
        {
            do
            {
                Sala sala1 = trovaSala(resultSet.getInt("numero1"));
                Sala sala2 = trovaSala(resultSet.getInt("numero2"));
                if(sala1 == null)
                {
                    sala1 = new Sala(resultSet.getInt("numero1"), resultSet.getString("nome1"),resultSet.getInt("tempovisita1"));
                    graph.addVertex(sala1);
                    message = "Sala: " + sala1.getNome() + " trovata nel database";
                    System.err.println(message);

                }
                if(sala2 == null)
                {
                    sala2 = new Sala(resultSet.getInt("numero2"), resultSet.getString("nome2"),resultSet.getInt("tempovisita2"));
                    graph.addVertex(sala2);
                    message = "Sala: " + sala2.getNome() + " trovata nel database";
                    System.err.println(message);
                }
                graph.addEdge(sala1,sala2);
                message = "Aggiunto arco tra sala " + sala1.getNome() + " e sala "+ sala2.getNome();
                System.err.println(message);

            }while (resultSet.next());
        }
        return graph;
    }

    private Sala trovaSala(int numero) {
        Set<Sala> salaSet = graph.vertexSet();
        Iterator<Sala> iterator = salaSet.iterator();
        Sala sala;
        while(iterator.hasNext())
        {
            sala = iterator.next();
            if(sala.getNumero() == numero ) return sala;
        }
        return null;
    }

    private void storeVisitTime()
    {
        //TODO funzione sbagliata
        Set<Sala> salaSet = graph.vertexSet();
        Iterator<Sala> iterator = salaSet.iterator();
        Sala sala;
        String query;

        while(iterator.hasNext()){
            sala = iterator.next();
            query = "UPDATE acquariopcad.sala SET tempovisita = " +sala.getTempoVisita()+  "WHERE sala.numero = "+ sala.getNumero()+";";
            try {
                cmd.executeQuery(query);
            } catch (SQLException e) {
            }
        }
    }

    public static void main(String[] args) throws RemoteException, ClassNotFoundException, SQLException
    {
        ServerInterface server = new Server();

    }
}