package Ordinamento_Strategy;

import Basic.Libro;

public class OrdinamentoPerValutazione implements OrdinamentoLibriIF{
    @Override
    public int compare(Libro l1, Libro l2) {
        return Integer.compare(l1.getValutazione(), l2.getValutazione());
    }
}
