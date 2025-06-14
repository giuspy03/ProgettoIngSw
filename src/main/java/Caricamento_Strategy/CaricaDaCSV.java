package Caricamento_Strategy;

import Basic.Libro;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CaricaDaCSV implements CaricaIF {

    @Override
    public List<Libro> parse(BufferedReader reader) throws IOException {
        List<Libro> libri = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            // Rimuovo le virgolette esterne e divido rispettando i campi racchiusi tra virgolette
            String[] dati = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            // Rimuovo le virgolette dai campi e sostituisco "" con "
            for (int i = 0; i < dati.length; i++) {
                dati[i] = dati[i].replaceAll("^\"|\"$", "").replace("\"\"", "\"");
            }

            // Creo il libro, se ci sono abbastanza campi (controllo sullo stato di consistenza del libro salvato nel file)
            if (dati.length >= 6) {
                Libro libro = new Libro(
                        dati[0], // titolo
                        dati[1], // autore
                        dati[2], // ISBN
                        dati[3], // genere
                        Integer.parseInt(dati[4]), // valutazione
                        Libro.StatoLettura.valueOf(dati[5]) // stato di lettura
                );
                libri.add(libro);
            }
        }
        return libri;
    }

}