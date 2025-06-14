package Ordinamento_Strategy;

import Basic.Libro;

public class OrdinamentoPerStatoLettura implements OrdinamentoLibriIF{
    @Override
    public int compare(Libro l1, Libro l2) {
        return l1.getStatoLettura().compareTo(l2.getStatoLettura());
    }
}
