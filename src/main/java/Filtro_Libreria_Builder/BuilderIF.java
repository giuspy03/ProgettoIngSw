package Filtro_Libreria_Builder;

import Basic.Libro;

public interface BuilderIF {


    BuilderIF setAutore(String autore);

    BuilderIF setGenere(String genere);

    BuilderIF setValutazioneMinima(int minima);

    BuilderIF setValutazioneMassima(int massima);

    BuilderIF setStatoLettura(Libro.StatoLettura stato);

}
