import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableUndirectedGraph;
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.random;


public class Server extends UnicastRemoteObject implements ServerInterface
{
    private static final long serialVersionUID = 1L;
    private ConcurrentHashMap<String, String> utentiRegistrati = new ConcurrentHashMap<>();
    private CopyOnWriteArrayList<User> listaUser = new CopyOnWriteArrayList<>();
    private Registry r = null;
    private String serverName = "Server";
    private Statement cmd;
    ListenableDirectedGraph<Sala,DefaultEdge> graph;

    private Server() throws RemoteException, ClassNotFoundException, SQLException
    {
        startServer();
        connectToDatabase();
        getUsers();
        createGraph();
        visualizzaGrafo();
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
        if(utentiRegistrati.get(username) == null)
        {
            utentiRegistrati.put(username, password);
            storeUser(username,password);
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
    public String login(String username, String password, ServerInterface stub) throws RemoteException, SQLException {
        String message;
        String log = utentiRegistrati.get(username);
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
            //recupera info utente dal database
            ResultSet resultSet = eseguiQuery("SELECT * FROM acquariopcad.utente WHERE username = '" + username + "';");
            if(resultSet.next()) listaUser.add(new User(username,resultSet.getInt("posizione"),stub));
            message = "Login effettuato!";
            System.err.println(message);
            return message;
        }
    }

    private ResultSet eseguiQuery(String query) {
        ResultSet resultSet = null;
        try {
            resultSet = cmd.executeQuery(query);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultSet;
    }


    @Override
    public String logout(String username) throws Exception
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
                } catch (SQLException e) {

                }
                listaUser.remove(user);
                message = "Logout effettuato per " + username;
                System.err.println(message);
                return message;
            }
        }
        throw new Exception("Non è possibile effettuare il logout se prima non si è fatto il login");
    }


    @Override
    public void visitGraph(String login) throws RemoteException, InterruptedException {
        //TODO
        //Visit starts from node 1
        updatePosition(login,1,0);

        long visitTime = (long)random()*1000;

        Thread.sleep(visitTime);

        User usr = getUser(login);

        Set<DefaultEdge> edges = graph.outgoingEdgesOf(trovaSala(usr.getLastPosition()));

        while(!edges.isEmpty())
        {
            List<DefaultEdge> list = Arrays.asList(edges.toArray(new DefaultEdge[0]));

            Collections.shuffle(list);

            Iterator it = list.iterator();

            updatePosition(login,graph.getEdgeTarget((DefaultEdge) it.next()).getNumero(),(int) visitTime);
        }
    }

    @Override
    public void updatePosition(String login,int nuovaSala, int tempoVisita) throws RemoteException
    {

        User user = getUser(login);
        if(user == null){/*TODO*/}

        int vecchiaSala = user.getLastPosition();

        System.err.println(login + " ha visitato la sala " + vecchiaSala + "in " + tempoVisita/1000 + " secondi");

        if(vecchiaSala != 0)
        {
            Sala vecchiasala = trovaSala(vecchiaSala);
            vecchiasala.visita(login, tempoVisita);
            vecchiasala.removeUtentePresente(login);
            user.addVisit(vecchiasala,tempoVisita);
        }

        user.setLastPosition(nuovaSala);
        Sala nuovasala = trovaSala(nuovaSala);
        nuovasala.addUtentePresente(login);
    }

    private User getUser(String login) {
        Iterator it = listaUser.iterator();
        while (it.hasNext()){
            User usr = (User) it.next();
            if(usr.getUsername().equals(login)) return usr;
        }
        return null;
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

    private synchronized void storeUser(String username,String password) throws RemoteException
    {   // la funzione va chiamata subito dopo il primo login
        String query;
        query = "INSERT INTO acquariopcad.utente VALUES('" + username + "','"+ password +"');";
        eseguiQuery(query);
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
                if (utentiRegistrati.get(username) == null)
                {
                    utentiRegistrati.put(username, password);
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

    private ListenableDirectedGraph<Sala,DefaultEdge> createGraph() throws SQLException {
        String query = "SELECT s1.numero AS numero1, s1.nome AS nome1, s1.tempovisita AS tempovisita1, s2.numero AS numero2, s2.nome AS nome2, s2.tempovisita AS tempovisita2\n" +
                "FROM acquariopcad.sala AS s1 JOIN acquariopcad.successore ON s1.numero = acquariopcad.successore.sala1 JOIN acquariopcad.sala AS s2 ON acquariopcad.successore.sala2 = s2.numero";
        ResultSet resultSet = cmd.executeQuery(query);
        String message;
        graph = new ListenableDirectedGraph<>(DefaultEdge.class);
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


    public void visualizzaGrafo(){


        // create a visualization using JGraph, via the adapter
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
        frame.setVisible(true);

// Add an Instance to the View

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
            query = "UPDATE acquariopcad.sala SET tempovisita = " +sala.getTempoMedioVisita()+  "WHERE sala.numero = "+ sala.getNumero()+";";
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