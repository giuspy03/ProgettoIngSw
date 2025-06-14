package Basic;

import Salvataggio_Visitor.LibroVisitor;

public class Libro {
    private final String titolo;
    private final String autore;
    private final String ISBN;
    private final String genere;
    private int valutazione;
    private StatoLettura statoLettura;

    public Libro(String titolo, String autore, String ISBN, String genere, int valutazione, StatoLettura stato) {
        if(ISBN.length() != 13){
            throw new LibroNonValidoException("Bisogna inserire un ISBN valido, cioè di 13 cifre");
        }
        //-1 è di default un libro senza alcuna valutazione
        if(valutazione < -1 || valutazione > 5){
            throw new LibroNonValidoException("Valutazione non valida, deve essere tra 0 e 5");
        }
        this.titolo = titolo;
        this.autore = autore;
        this.ISBN = ISBN;
        this.genere = genere;
        this.valutazione = valutazione;
        this.statoLettura = stato;
    }
    public enum StatoLettura {
        letto,
        da_leggere,
        in_lettura
    }

    public String accept(LibroVisitor visitor) {
        return visitor.visit(this);
    }

    public String getTitolo() {
        return titolo;
    }
    public String getAutore() {
        return autore;
    }
    public String getISBN() {
        return ISBN;
    }
    public String getGenere() {
        return genere;
    }
    public int getValutazione() {
        return valutazione;
    }
    public StatoLettura getStatoLettura() {
        return statoLettura;
    }
    public void setStatoLettura(StatoLettura statoLettura) {
        this.statoLettura = statoLettura;
    }
    public void setValutazione(int valutazione) {
        if (valutazione >= 0 && valutazione <= 5) {
            this.valutazione = valutazione;
        }
        else{
            throw new LibroNonValidoException("Valutazione non valida, deve essere tra 0 e 5");
        }
    }

    @Override
    public String toString() {
        return this.getISBN()+this.getTitolo()+this.getAutore()+this.getStatoLettura();
    }
}


