package Basic;

import Caricamento_Strategy.CaricaIF;
import Caricamento_Strategy.CaricamentoFallitoException;
import Creazionali_Libro_FactoryMethod.LibroDaLeggere;
import Creazionali_Libro_FactoryMethod.LibroFactory;
import Filtro_Libreria_Builder.FiltroLibri;
import Ordinamento_Strategy.OrdinamentoLibriIF;
import Salvataggio_Visitor.LibroVisitor;
import Salvataggio_Visitor.LibroVisitorJSON;
import Salvataggio_Visitor.SalvataggioFallitoException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Uso del Pattern Singleton per la gestione di una singola istanza di Libreria
public class Libreria {
    private static Libreria instance;
    private final Map<String, Libro> libri;
    private LibroFactory libroFactory = new LibroDaLeggere();

    private Libreria() {
        libri = new HashMap<>();
    }

    public static synchronized Libreria getInstance() {
        if (instance == null) {
            instance = new Libreria();
        }
        return instance;
    }

    /*Uso del pattern Factory Method: i libri differiscono tra loro per lo stato di lettura, la factory è impostata
    nella GUI in seguito alla selezione dello stato di lettura*/
    public void setLibroFactory(LibroFactory factory) {
        this.libroFactory = factory;
    }


    public void aggiungiLibro(String titolo, String autore, String ISBN, String genere) {
        Libro nuovoLibro = libroFactory.inserisciLibro(titolo, autore, ISBN, genere);
        if (libri.containsKey(nuovoLibro.getISBN())) {
            throw new InserimentoNonValidoException("Il libro che stai tentando di inserire è già presente in libreria");
        }
        libri.put(ISBN,nuovoLibro);
    }

    public void eliminaLibro(String ISBN) {
        libri.remove(ISBN);
    }

    public Libro getLibro(String ISBN, boolean isbn) {
        if(!libri.containsKey(ISBN)) {
            throw new LibroNonValidoException("Libro non trovato");
        }
        return libri.get(ISBN);
    }

    public Libro getLibro(String titolo){
        for (Libro libro : libri.values()) {
            if (libro.getTitolo().equals(titolo)) {
                return libro;
            }
        }
        throw new LibroNonValidoException("Libro non trovato");
    }

    public void aggiungiValutazione(String ISBN, int val){
        Libro lNew = libri.get(ISBN);
        lNew.setValutazione(val);
        libri.put(ISBN, lNew);
    }

    public void aggiungiLibro(Libro libro) {
        libri.put(libro.getISBN(), libro);
    }

    public List<Libro> getLibri() {
        return new ArrayList<>(libri.values());
    }


    /*
    L'uso del pattern Builder è visibile solo dalla GUI, il filtro implementa il metodo DaInserire che valuta
    i campi inseriti dall'utente e filtra ogni libro.
    */
    public List<Libro> filtraLibri(FiltroLibri filtro) {
        List<Libro> risultati = new ArrayList<>();

        for (Libro libro : libri.values()) {
            if (filtro.daInserire(libro)) {
                risultati.add(libro);
            }
        }
        return risultati;
    }



    /*
    L'oggetto di ordinamento è in realtà il risultato del design pattern Strategy, implementato con un if tra due
    condizioni nella GUI (CSV/JSON)
     */
    public List<Libro> ordinaLibri(OrdinamentoLibriIF ordinamento) {
        List<Libro> risultato = new ArrayList<>(libri.values());
        risultato.sort(ordinamento);
        return risultato;
    }


    /*
    L'uso del pattern Visitor permette di salvare su file ogni libro, iterando sulla struttura ad oggetti che li contiene:
    la responsabilità dell'attraversamento è affidata alla struttura dati.
     */
    public void salvaSuFile(String percorso, LibroVisitor visitor){
        List<Libro> libriDaSalvare = new ArrayList<>(libri.values());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(percorso))) {

            if (visitor instanceof LibroVisitorJSON) {
                writer.write("[");
                writer.newLine();
            }

            for (int i = 0; i < libriDaSalvare.size(); i++) {
                Libro libro = libriDaSalvare.get(i);
                String line = libro.accept(visitor);

                // Aggiunta virgola per JSON (eccetto ultimo elemento)
                if (visitor instanceof LibroVisitorJSON && i < libriDaSalvare.size() - 1) {
                    line += ",";
                }

                writer.write(line);
                writer.newLine();
            }

            if (visitor instanceof LibroVisitorJSON) {
                writer.write("]");
            }
        } catch (Exception e) {
            throw new SalvataggioFallitoException("Salvataggio fallito, si prega di inserire un percorso valido.");
        }
    }

    /*
    Si ricorre nuovamente all'uso del patern Strategy per decidere sulla base del tipo di file (CSV/JSON)
     */
    public void caricaDaFile(String percorso, CaricaIF strategia){
        try (BufferedReader reader = new BufferedReader(new FileReader(percorso))) {
            List<Libro> libri = strategia.parse(reader);
            for (Libro libro : libri) {
                this.aggiungiLibro(libro);
            }
        } catch (Exception e) {
            throw new CaricamentoFallitoException("Caricamento fallito, si prega di inserire un percorso corretto.");
        }
    }
}