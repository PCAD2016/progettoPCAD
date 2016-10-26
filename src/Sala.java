import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by marco on 08/06/16.
 */
public class Sala
{
    private int numero;
    private String nome;
    private int tempoMedioVisita;
    private int contaVisite;
    private ConcurrentHashMap<String,Integer> visitatori;
    private CopyOnWriteArrayList<String> utentiPresenti;

    public Sala(int numero, String nome, int tempovisita) {
        this.numero = numero;
        this.nome = nome;
        this.tempoMedioVisita = tempovisita;
        visitatori = new ConcurrentHashMap<>();
        utentiPresenti = new CopyOnWriteArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTempoMedioVisita() {
        return tempoMedioVisita;
    }

    public void setTempoMedioVisita(int tempoMedioVisita) {this.tempoMedioVisita = tempoMedioVisita; }

    public void visita(String utente, int nuovoTempo)
    {
        visitatori.put(utente,nuovoTempo);
        tempoMedioVisita *= contaVisite;
        tempoMedioVisita = (tempoMedioVisita + nuovoTempo) / ++contaVisite;
    }

    @Override
    public String toString() {
        return "Sala: " + getNumero() + "\n utenti presenti: "+ utentiPresenti.toString();
    }

    public boolean addUtentePresente(String utente) {
        return utentiPresenti.add(utente);
    }

    public boolean removeUtentePresente(String utente) {
        Iterator<String> it = utentiPresenti.iterator();
        while (it.hasNext()){
            String str = it.next();
            if(str.equals(utente)) utentiPresenti.remove(str);
            return true;
        }
        return false;
    }

    public CopyOnWriteArrayList<String> getUtentiPresenti() {
        return utentiPresenti;
    }

    public static void main(String[] args)
    {

    }
}
