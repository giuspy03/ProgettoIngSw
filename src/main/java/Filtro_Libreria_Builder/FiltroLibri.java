package Filtro_Libreria_Builder;

import Basic.Libro;

public class FiltroLibri {
    private String autore;
    private String genere;
    private Integer valutazioneMinima;
    private Integer valutazioneMassima;
    private Libro.StatoLettura statoLettura;

    FiltroLibri(BuilderImpl builder) {
        this.autore = builder.autore;
        this.genere = builder.genere;
        this.valutazioneMinima = builder.valutazioneMinima;
        this.valutazioneMassima = builder.valutazioneMassima;
        this.statoLettura = builder.statoLettura;
    }

    public boolean daInserire(Libro libro) {
        if (autore != null && !libro.getAutore().toLowerCase().contains(autore.toLowerCase())) {
            return false;
        }
        if (genere != null && !libro.getGenere().equalsIgnoreCase(genere)) {
            return false;
        }
        if (valutazioneMinima != null && (libro.getValutazione() == -1 || libro.getValutazione() < valutazioneMinima)) {
            return false;
        }
        if (valutazioneMassima != null && (libro.getValutazione() == -1 || libro.getValutazione() > valutazioneMassima)) {
            return false;
        }
        if (statoLettura != null && statoLettura!=libro.getStatoLettura()) {
            return false;
        }
        return true;
    }
}
