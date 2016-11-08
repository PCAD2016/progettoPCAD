package acquario;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by marco on 08/06/16.
 */
public class Sala implements Serializable
{
    private int numero;
    private String nome;
    private int tempoMedioVisita;
    private int contaVisite;
    private ConcurrentHashMap<String,CopyOnWriteArrayList<Integer>> visitatori;
    private CopyOnWriteArrayList<User> utentiPresenti;


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
        CopyOnWriteArrayList<Integer> auxArray = visitatori.get(utente);
        if(auxArray == null) auxArray = new CopyOnWriteArrayList<>();

        auxArray.add(nuovoTempo);

        visitatori.put(utente,auxArray);

        tempoMedioVisita *= contaVisite;

        tempoMedioVisita = (tempoMedioVisita + nuovoTempo) / ++contaVisite;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == this) return true;
        else if ( o instanceof Sala && ((Sala) o).getNumero() == getNumero()) return true;
        else return false;
    }

    @Override
    public String toString() {
        return "Sala "+ getNumero() + ": " + getNome();
    }

    public boolean addUtentePresente(User utente) {
        return utentiPresenti.add(utente);
    }

    public boolean removeUtentePresente(User utente) {
        Iterator<User> it = utentiPresenti.iterator();
        while (it.hasNext()){
            User usr = it.next();
            if(usr.equals(utente)) utentiPresenti.remove(usr);
            return true;
        }
        return false;
    }

    public CopyOnWriteArrayList<User> getUtentiPresenti() {
        return utentiPresenti;
    }

    public ConcurrentHashMap<String, CopyOnWriteArrayList<Integer>> getVisitatori() {
        return visitatori;
    }

    public int tempoTotaleVisita() {
        Collection<CopyOnWriteArrayList<Integer>> arrayTempi = visitatori.values();
        int tempoTotale = 0;
        for(CopyOnWriteArrayList<Integer> tempi : arrayTempi)
        {
            for(int tempo : tempi)
            tempoTotale += tempo;
        }
        return tempoTotale;
    }

    public int getContaVisite() {
        return contaVisite;
    }
}
