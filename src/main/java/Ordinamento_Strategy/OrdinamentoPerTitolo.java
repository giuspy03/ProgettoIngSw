package Ordinamento_Strategy;

import Basic.Libro;

public class OrdinamentoPerTitolo implements OrdinamentoLibriIF{
    @Override
    public int compare(Libro l1, Libro l2) {
        return l1.getTitolo().compareTo(l2.getTitolo());
    }
}
