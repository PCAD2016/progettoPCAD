package acquario;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.random;

/**
 * Created by marco on 10/05/16.
 */
public class Client implements ClientInterface
{
    private static ServerInterface server;
    private ClientInterface stub;
    private ListenableDirectedGraph<Sala, CustomEdge> graph;
    private String login;

    public Client() throws RemoteException {
        connect();
        updateGraph();
    }

    private void connect()
    {
        System.setProperty("java.security.policy", "file:./file.policy");
        System.setProperty("java.rmi.server.codebase", "file:${workspace_loc}/MyClient/");

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        System.setProperty("java.rmi.server.hostname", "localhost");
        try
        {
            Registry r = LocateRegistry.getRegistry(8000);
            String serverName = "Server";
            server = (ServerInterface) r.lookup(serverName);
            stub = (ClientInterface) UnicastRemoteObject.exportObject(this,0);

        } catch (Exception e)
        {
            System.err.println("Server non trovato:");
            e.printStackTrace();
        }
    }

    public String getLogin()
    {
        return login;
    }

    public static boolean registration(String username, String password) throws RemoteException
    {
        return server.registration(username,password);

    }

    public boolean login(String username, String password) throws RemoteException, SQLException {
        login = username;
        return server.login(login,password, stub);

    }

    private String logout() throws Exception
    {
        return server.logout(login);
    }


    public Sala trovaSala(int numero) {
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

    public void visitGraph() throws InterruptedException, RemoteException, LoginException {

        server.updatePosition(login,1,0);

        double visitTime = random()*10000;

        Thread.sleep((long)visitTime);

        User usr = server.getUser(login);

        Set<CustomEdge> edges = graph.outgoingEdgesOf(trovaSala(usr.getLastPosition()));

        while(!edges.isEmpty())
        {
            List<DefaultEdge> list = Arrays.asList(edges.toArray(new DefaultEdge[0]));

            Collections.shuffle(list);

            Iterator it = list.iterator();

            server.updatePosition(login,graph.getEdgeTarget((CustomEdge) it.next()).getNumero(),(int) visitTime);

            visitTime = random()*10000;

            usr = server.getUser(login);

            edges = graph.outgoingEdgesOf(trovaSala(usr.getLastPosition()));

            Thread.sleep((long)visitTime);
        }

        server.updatePosition(login,-1,(int) visitTime);

        System.err.println(login + " ha finito la visita");


    }

    public void visualizzaGrafo() throws RemoteException {

        // create a visualization using JGraph, via the adapter
        JGraphXAdapter<Sala,CustomEdge> jgxAdapter = new JGraphXAdapter<>(server.getGraph());

        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
        graphComponent.setEnabled(false);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());
        JFrame frame = new JFrame();
        frame.getContentPane().add(graphComponent);
        frame.setTitle("Mappa dell'acquario");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

// Add an Instance to the View

    }

    @Override
    public void updateGraph() throws RemoteException {
        graph = server.getGraph();
    }

    public static void main(String args[]) throws Exception {
        Client client = new Client();
        System.err.println(client.login("ciao2", "zaza"));
        client.visualizzaGrafo();
        client.visitGraph();
        client.visitGraph();
        System.err.println(client.logout());

    }


}
