import java.util.Observable;
import java.util.Observer;

// Define an Observer
public class ViewObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("View changed: "+o);
    }

}