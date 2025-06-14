package Filtro_Libreria_Builder;

import Basic.Libro;

public class BuilderImpl implements BuilderIF {

    String autore;
    String genere;
    Integer valutazioneMinima;
    Integer valutazioneMassima;
    Libro.StatoLettura statoLettura;


    public BuilderIF setAutore(String autore) {
        this.autore = autore;
        return this;
    }

    public BuilderIF setGenere(String genere) {
        this.genere = genere;
        return this;
    }

    public BuilderIF setValutazioneMinima(int minima) {
        this.valutazioneMinima = minima;
        return this;
    }

    public BuilderIF setValutazioneMassima(int massima) {
        this.valutazioneMassima = massima;
        return this;
    }

    public BuilderIF setStatoLettura(Libro.StatoLettura stato) {
        statoLettura = stato;
        return this;
    }

    public FiltroLibri build() {
        if (autore == null &&
                genere == null &&
                valutazioneMinima == null &&
                valutazioneMassima == null &&
                statoLettura == null) {
            throw new FiltroNonValidoException("Il filtro che stai provando a inserire è vuoto.");
        }
        return new FiltroLibri(this);
    }
}
