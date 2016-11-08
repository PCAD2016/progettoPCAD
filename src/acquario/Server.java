package acquario;

import org.jgrapht.graph.ListenableDirectedGraph;
import javax.security.auth.login.LoginException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server extends UnicastRemoteObject implements ServerInterface
{
    private static final long serialVersionUID = 1L;

    private ConcurrentHashMap<String, String> utentiRegistrati = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<User> listaUser = new CopyOnWriteArrayList<>();

    private Registry r = null;

    private String serverName = "Server";

    private Statement cmd;

    private ListenableDirectedGraph<Sala,CustomEdge> graph;

    protected Server() throws RemoteException, ClassNotFoundException, SQLException
    {

    }

    protected void connectToDatabase() throws ClassNotFoundException, SQLException
    {

        String driver = "org.postgresql.Driver";

        Class.forName(driver);

        String url = "jdbc:postgresql://127.0.0.1:5432/mydb";

        Connection con = DriverManager.getConnection(url, "postgres", "postgres");

        cmd = con.createStatement();
    }

    @Override
    public boolean registration(String username, String password) throws RemoteException
    {
        String message;

        if(utentiRegistrati.get(username) == null)
        {
            utentiRegistrati.put(username, password);

            storeUser(username,password);

            message = "Aggiunto utente: "+ username;

            System.err.println(message);

            return true;
        }
        else
        {

            message = "Username già esistente";

            System.err.println(message);

            return false;
        }
    }

    @Override
    public boolean login(String username, String password, ClientInterface stub) throws RemoteException, SQLException
    {
        String message;

        String log = utentiRegistrati.get(username);

        System.err.println("log: "+ log);
        if(log == null)
        {
            message = "Username "+ username +" non esistente";

            System.err.println(message);

            return false;
        }
        else if(!log.equals(password))
        {
            message = "Password errata!";

            System.err.println(message);

            return false;
        }
        else
        {
            //recupera info utente dal database
            ResultSet resultSet = eseguiQuery("SELECT * FROM acquariopcad.utente WHERE username = '" + username + "';");

            if(resultSet.next()) listaUser.add(new User(username,resultSet.getInt("posizione"),stub));

            message = "Login effettuato!";

            System.err.println(message);

            return true;
        }
    }

    private ResultSet eseguiQuery(String query)
    {
        ResultSet resultSet = null;

        try {
            resultSet = cmd.executeQuery(query);
        } catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }

        return resultSet;
    }


    @Override
    public String logout(String username) throws LoginException
    {
        Iterator<User> it = listaUser.iterator();

        String message;

        while (it.hasNext())
        {
            User user = it.next();

            if (user.getUsername().equals(username))
            {
                //salva info utente su DB prima di disconnetterlo
                String query = "UPDATE acquariopcad.utente SET posizione = "+ user.getLastPosition() + " = WHERE username = " + username + ";";

                try {
                    cmd.executeQuery(query);
                } catch (SQLException ignored) {

                }
                listaUser.remove(user);

                message = "Logout effettuato per " + username;

                System.err.println(message);

                return message;
            }
        }
        throw new LoginException("Non è possibile effettuare il logout se prima non si è fatto il login");
    }


    @Override
    public String updatePosition(String login, int nuovaSala, int tempoVisita) throws LoginException
    {
        String message = null;

        User user = getUser(login);

        int vecchiaSala = user.getLastPosition();

        if (vecchiaSala != 0 && vecchiaSala != -1)
        {
            Sala vecchiasala = trovaSala(vecchiaSala);

            vecchiasala.visita(login, tempoVisita);

            vecchiasala.removeUtentePresente(user);

            user.addVisit(vecchiasala, tempoVisita);

            message = login + " ha visitato la sala " + vecchiaSala + " in " + user.getSaleVisitate().get(vecchiasala) / 1000 + " secondi";

            System.err.println(message);
        }

        user.setLastPosition(nuovaSala);

        if (nuovaSala != -1) trovaSala(nuovaSala).addUtentePresente(user);

        return message != null ? message : "";
    }

    @Override
    public User getUser(String login) throws LoginException
    {
        for (User usr : listaUser) {

            if (usr.getUsername().equals(login)) return usr;
        }

        throw new LoginException();
    }

    protected void closeServer() throws RemoteException, SQLException
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

    private void storeUser(String username,String password) throws RemoteException
    {
        // la funzione va chiamata subito dopo il primo login
        String query;

        query = "INSERT INTO acquariopcad.utente VALUES('" + username + "','"+ password +"');";

        eseguiQuery(query);
    }

    protected void getUsers() throws SQLException
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

                if (utentiRegistrati.get(username) == null)
                {
                    utentiRegistrati.put(username, password);

                    message = "Utente: " + username + " trovato nel database";

                    System.err.println(message);
                }
            } while (resultSet.next());
        }
    }



    protected void startServer() throws RemoteException, ClassNotFoundException, SQLException
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
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        System.out.println("Server bound");
    }

    protected ListenableDirectedGraph<Sala,CustomEdge> createGraph() throws SQLException
    {
        String query = "SELECT s1.numero AS numero1, s1.nome AS nome1, s1.tempovisita AS tempovisita1, s2.numero AS numero2, s2.nome AS nome2, s2.tempovisita AS tempovisita2\n" +
                "FROM acquariopcad.sala AS s1 JOIN acquariopcad.successore ON s1.numero = acquariopcad.successore.sala1 JOIN acquariopcad.sala AS s2 ON acquariopcad.successore.sala2 = s2.numero";

        ResultSet resultSet = cmd.executeQuery(query);

        String message;

        graph = new ListenableDirectedGraph<>(CustomEdge.class);

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


    protected void visualizzaGrafo()
    {


        /*// create a visualization using JGraph, via the adapter
        JGraphXAdapter<Sala,DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        layout.execute(jgxAdapter.getDefaultParent());
        JFrame frame = new JFrame();
        frame.getContentPane().add(graphComponent);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/

// Add an Instance to the View

    }

    protected Sala trovaSala(int numero)
    {
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

    @Override
    public ListenableDirectedGraph<Sala, CustomEdge> getGraph()
    {
        return graph;
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
            query = "UPDATE acquariopcad.sala SET tempovisita = " +sala.getTempoMedioVisita()+  "WHERE sala.numero = "+ sala.getNumero()+";";
            try {
                cmd.executeQuery(query);
            } catch (SQLException e) {
            }
        }
    }

    protected String posizione()
    {
        String message = "";
        int numerosala;

        Iterator<User> it = listaUser.iterator();

        if(listaUser.isEmpty()) message = "Nessun utente connesso\n";
        while (it.hasNext())
        {
            User usr = it.next();

            numerosala = usr.getLastPosition();

            if(numerosala == 0) message += new StringBuilder().append("L'utente ").append(usr.getUsername()).append(" non ha ancora iniziato la visita\n");

            else if(numerosala == -1) message += new StringBuilder().append("L'utente ").append(usr.getUsername()).append(" ha terminato la visita\n");

            else message += new StringBuilder().append("L'utente ").append(usr.getUsername()).append(" si trova nella sala ").append(trovaSala(usr.getLastPosition()).getNome()).append("\n").toString();
        }
        return message;
    }

    protected void refreshGraph() throws SQLException, RemoteException {
        createGraph();
        for(User user : listaUser) user.getStub().updateGraph();

    }

    protected String generaStatistiche()
    {
        StringBuilder sb = new StringBuilder();
        Set<Sala> salaSet = graph.vertexSet();
        for(Sala sala: salaSet)
        {
            sb.append("SALA ").append(sala.getNome()).append(":\n").append("Numero visitatori: ").append(sala.getContaVisite()).append("\nTempo medio visita: ").append(sala.getTempoMedioVisita()/1000).append(" s").append("\nTempo totale visita: ").append(sala.tempoTotaleVisita()/1000).append(" s\n\n");
        }
        return sb.toString();

    }

    public static void main(String[] args) throws RemoteException, ClassNotFoundException, SQLException
    {
        Server server = new Server();
        server.startServer();

        server.connectToDatabase();

        server.getUsers();

        server.createGraph();

        System.out.println(server.posizione());

    }
}