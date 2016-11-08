package acquario;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marco on 08/06/16.
 */
public class User implements Serializable
{
    private String username;
    private String password;
    private ClientInterface stub;
    private int lastPosition;
    private ConcurrentHashMap<Sala,Integer> saleVisitate = new ConcurrentHashMap();

    public User(String username, int lastPosition, ClientInterface stub) {
        this.username=username;
        this.stub = stub;
        this.lastPosition = lastPosition;
    }

    public User(String username, ClientInterface stub) {
        this.username=username;
        this.stub = stub;
    }

    public boolean addVisit(Sala sala,Integer tempoVisita){
        if(saleVisitate.contains(sala)) return false;
        return (saleVisitate.put(sala,tempoVisita) != null);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientInterface getStub() {
        return stub;
    }

    public void setStub(ClientInterface stub) {
        this.stub = stub;
    }

    public ConcurrentHashMap<Sala, Integer> getSaleVisitate() {
        return saleVisitate;
    }

    public void setSaleVisitate(ConcurrentHashMap<Sala, Integer> saleVisitate) {
        this.saleVisitate = saleVisitate;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public String toString(){
        return username;
    }
}
