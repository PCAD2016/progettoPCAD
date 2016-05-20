/**
 * Created by battister on 20/05/16.
 */
public class AulaAquario {
    private int tempo  ; //tempo medio
    private String nome ; // full name
    private static   int numero  ;

    public AulaAquario (int temp , String nam) {
        super();
        this.nome=nam;
        this.tempo=temp;
        numero++;

    }


    public int  getTempo () {

        return tempo ;
    }

    public String getNome() {

        return nome ;
    }

    public void setTempo(int t) {

        this.tempo=t;

    }

    public void setNome(String name) {

        this.nome = name;
    }




}
