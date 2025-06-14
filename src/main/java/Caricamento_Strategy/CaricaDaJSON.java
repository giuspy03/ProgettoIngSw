package Caricamento_Strategy;

import Basic.Libro;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CaricaDaJSON implements CaricaIF {
    @Override
    public List<Libro> parse(BufferedReader reader) throws IOException {
        List<Libro> libri = new ArrayList<>();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        //lista vuota se file non contiene nulla
        if (jsonBuilder.length() == 0) {
            return libri;
        }

        // Se il file contiene un array JSON
        if (jsonBuilder.toString().trim().startsWith("[")) {
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Libro libro = creaLibroDaJSON(jsonObj);
                libri.add(libro);
            }
        }
        // Se il file contiene un singolo oggetto JSON
        else {
            JSONObject jsonObj = new JSONObject(jsonBuilder.toString());
            Libro libro = creaLibroDaJSON(jsonObj);
            libri.add(libro);
        }

        return libri;
    }

    private Libro creaLibroDaJSON(JSONObject jsonObj) {
        return new Libro(
                jsonObj.getString("titolo"),
                jsonObj.getString("autore"),
                jsonObj.getString("ISBN"),
                jsonObj.getString("genere"),
                jsonObj.getInt("valutazione"),
                Libro.StatoLettura.valueOf(jsonObj.getString("statoLettura"))
        );
    }
}