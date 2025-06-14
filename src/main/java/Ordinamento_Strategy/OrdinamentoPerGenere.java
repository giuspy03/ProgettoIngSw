package Ordinamento_Strategy;

import Basic.Libro;

public class OrdinamentoPerGenere implements OrdinamentoLibriIF{

    @Override
    public int compare(Libro l1, Libro l2) {
        return l1.getGenere().compareTo(l2.getGenere());
    }
}
