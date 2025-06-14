package Ordinamento_Strategy;

import Basic.Libro;

public class OrdinamentoPerAutore implements OrdinamentoLibriIF{
    @Override
    public int compare(Libro l1, Libro l2) {
        return l1.getAutore().compareTo(l2.getAutore());
    }
}
