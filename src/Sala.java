import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by marco on 08/06/16.
 */
public class Sala
{
    private int numero;
    private String nome;
    private int tempoVisita;
    private ConcurrentHashMap<String,Integer> visitatori;

    public Sala(int numero, String nome, int tempovisita) {
        this.numero = numero;
        this.nome = nome;
        this.tempoVisita = tempovisita;
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

    public int getTempoVisita() {
        return tempoVisita;
    }

    public void setTempoVisita(int tempoVisita) {this.tempoVisita = tempoVisita; }

    public void visita(String utente, int tempoVisita)
    {
        visitatori.put(utente,tempoVisita);
    }

    public static void main(String[] args)
    {

    }
}
