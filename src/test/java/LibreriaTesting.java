
import Basic.*;
import Basic.Libro.StatoLettura;
import Caricamento_Strategy.CaricaDaJSON;
import Caricamento_Strategy.CaricaIF;
import Caricamento_Strategy.CaricamentoFallitoException;
import Filtro_Libreria_Builder.FiltroLibri;
import Ordinamento_Strategy.OrdinamentoLibriIF;
import Ordinamento_Strategy.OrdinamentoPerValutazione;
import Filtro_Libreria_Builder.BuilderImpl;
import Salvataggio_Visitor.LibroVisitorCSV;
import Salvataggio_Visitor.LibroVisitorJSON;
import Salvataggio_Visitor.SalvataggioFallitoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibreriaTesting {
    private Libreria libreria;
    private Libro libro1;
    private Libro libro2;

    @BeforeEach
    public void setUp() {
        libreria = Libreria.getInstance();

        libro1 = new Libro("Le armi della persuasione", "Robert B. Cialdini",
                "9788809896840", "Psicologia", 5, StatoLettura.letto);
        libro2 = new Libro("Guerra senza odio", "Erwin Rommel",
                "9788861027435", "Guerra", 4, StatoLettura.in_lettura);
        for(Libro libro : libreria.getLibri()) {
            libreria.eliminaLibro(libro.getISBN());
        }
    }

    @Test
    public void testAggiungiLibro() {
        libreria.aggiungiLibro(libro1);
        assertEquals(1, libreria.getLibri().size());
    }

    @Test
    public void testGetLibroConISBNNonEsistente() {
        assertThrows(LibroNonValidoException.class, () -> libreria.getLibro("provaIsbnFalso", true));
    }

    @Test
    public void testGetLibroConTitoloNonEsistente() {
        assertThrows(LibroNonValidoException.class, () -> libreria.getLibro("TitoloFalso"));
    }


    @Test
    public void testEliminaLibro() {
        libreria.aggiungiLibro(libro1);
        libreria.eliminaLibro(libro1.getISBN());
        assertTrue(libreria.getLibri().isEmpty());
    }

    @Test
    public void testSingletonPattern() {
        Libreria instance1 = Libreria.getInstance();
        Libreria instance2 = Libreria.getInstance();
        assertSame(instance1, instance2);
    }


    @Test
    public void testAggiungiLibroConISBNDuplicato() {
        libreria.aggiungiLibro(libro1);
        assertThrows(InserimentoNonValidoException.class, () -> libreria.aggiungiLibro(libro1.getTitolo(), libro1.getAutore(), libro1.getISBN(), libro1.getGenere()));
    }


    @Test
    public void testAggiungiValutazione() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiValutazione(libro1.getISBN(), 3);
        assertEquals(3, libreria.getLibri().get(0).getValutazione());
    }

    @Test
    public void testFiltraLibri() {
        libreria.aggiungiLibro(libro1);
        libreria.aggiungiLibro(libro2);

        BuilderImpl builder = new BuilderImpl();
        builder.setGenere("Guerra");
        FiltroLibri filtro = builder.build();

        List<Libro> risultati = libreria.filtraLibri(filtro);
        assertEquals(1, risultati.size());
        assertEquals(libro2, risultati.get(0));
    }

    @Test
    public void testFiltraLibriConFiltroNullo() {
        libreria.aggiungiLibro(libro1);
        assertThrows(NullPointerException.class, () -> libreria.filtraLibri(null));
    }

    @Test
    public void testOrdinaLibri() {
        libreria.aggiungiLibro(libro2); //valutato 4
        libreria.aggiungiLibro(libro1); //valutato 5

        OrdinamentoLibriIF ordinamento = new OrdinamentoPerValutazione();
        List<Libro> ordinati = libreria.ordinaLibri(ordinamento);
        assertEquals(libro2, ordinati.getFirst());


        OrdinamentoLibriIF ordinamentoInverso = ordinamento.inverso();
        ordinati = libreria.ordinaLibri(ordinamentoInverso);
        assertEquals(libro1, ordinati.getFirst());

    }

    @Test
    public void testSalvaSuFileCSV() {
        libreria.aggiungiLibro(libro1);
        String tempFile = "test_libreria.csv";

        try {
            libreria.salvaSuFile(tempFile, new LibroVisitorCSV());
            assertTrue(new File(tempFile).exists(), "Il file CSV dovrebbe essere creato");

            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String firstLine = reader.readLine();
                System.out.println("Libri salvati: \n");
                System.out.println(firstLine);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCaricaDaFile() throws IOException {
        String tempFile = "test_carica.json";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("{\"titolo\":\"Il Signore degli Anelli\",\"autore\":\"J.R.R. Tolkien\",\"ISBN\":\"9788845292613\",\"genere\":\"Guerra\",\"valutazione\":3, \"statoLettura\":\"letto\"}");
        }

        CaricaIF strategia = reader -> new CaricaDaJSON().parse(reader);

        libreria.caricaDaFile(tempFile, strategia);
        assertEquals(1, libreria.getLibri().size(), "Dovrebbe aver caricato 1 libro");
    }

    @Test
    public void testCaricaDaFilePercorsoInvalido() {
        assertThrows(CaricamentoFallitoException.class, () -> libreria.caricaDaFile("percorso_inesistente.json", reader -> null));
    }
}