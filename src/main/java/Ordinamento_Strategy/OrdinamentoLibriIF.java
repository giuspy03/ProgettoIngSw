package Ordinamento_Strategy;

import Basic.Libro;
import java.util.Comparator;

public interface OrdinamentoLibriIF extends Comparator<Libro> {

    default OrdinamentoLibriIF inverso() {
        return new OrdinamentoLibriIF() {
            @Override
            public int compare(Libro l1, Libro l2) {
                return OrdinamentoLibriIF.this.compare(l2, l1);
            }
        };
    }

    int compare(Libro l1, Libro l2);
    //Metodo compare sarà implementato nelle classi concrete

}