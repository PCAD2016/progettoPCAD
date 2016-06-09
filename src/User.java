import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marco on 08/06/16.
 */
public class User
{
    private String username;
    private ServerInterface stub;
    private int lastPosition;
    private ConcurrentHashMap<Sala,Integer> saleVisitate = new ConcurrentHashMap();

    public User(String username, ServerInterface stub) {
        this.username=username;
        this.stub = stub;
        //TODO recupera posizione dal DB, se user presente
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ServerInterface getStub() {
        return stub;
    }

    public void setStub(ServerInterface stub) {
        this.stub = stub;
    }

    public ConcurrentHashMap<Sala, Integer> getSaleVisitate() {
        return saleVisitate;
    }

    public void setSaleVisitate(ConcurrentHashMap<Sala, Integer> saleVisitate) {
        this.saleVisitate = saleVisitate;
    }
}
