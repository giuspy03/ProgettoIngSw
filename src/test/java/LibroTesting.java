import Basic.Libro;
import Basic.Libro.StatoLettura;
import Basic.LibroNonValidoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class LibroTesting {

    @Test
    void testCostruttoreCorretto() {
        Libro libro = new Libro("Le armi della persuasione", "Robert B. Cialdini",
                "9788809896840", "Psicologia", 5, StatoLettura.letto);

        assertEquals("Le armi della persuasione", libro.getTitolo());
        assertEquals("Robert B. Cialdini", libro.getAutore());
        assertEquals("9788809896840", libro.getISBN());
        assertEquals("Psicologia", libro.getGenere());
        assertEquals(5, libro.getValutazione());
        assertEquals(StatoLettura.letto, libro.getStatoLettura());
    }

    @Test
    void testCostruttoreISBNNonValido() {
        assertThrows(LibroNonValidoException.class, () -> {
            new Libro("Le armi della persuasione", "Robert B. Cialdini", "123", "Psicologia", 5, StatoLettura.da_leggere);
        });
    }

    @Test
    void testCostruttoreValutazioneAlta() {
        assertThrows(LibroNonValidoException.class, () -> {
            new Libro("Le armi della persuasione", "Robert B. Cialdini", "9788809896840", "Psicologia", 6, StatoLettura.letto);
        });
    }

    @Test
    void testCostruttoreValutazioneBassa() {
        assertThrows(LibroNonValidoException.class, () -> {
            new Libro("Le armi della persuasione", "Robert B. Cialdini", "9788809896840", "Psicologia", -10, StatoLettura.letto);
        });
    }

    @Test
    void testSetValutazione() {
        Libro libro = new Libro("Le armi della persuasione", "Robert B. Cialdini", "9788809896840", "Psicologia", 4, StatoLettura.letto);
        libro.setValutazione(4);
        assertEquals(4, libro.getValutazione());
    }

    @Test
    void testSetValutazioneInvalid() {
        Libro libro = new Libro("Titolo", "Autore", "1234567890123", "Genere", 3, StatoLettura.in_lettura);

        assertThrows(LibroNonValidoException.class, () -> {
            libro.setValutazione(6);
        });

        assertThrows(LibroNonValidoException.class, () -> {
            libro.setValutazione(-2);
        });

        //Check su valore originale, non dovrebbe essere variato a seguito di inserimenti errati
        assertEquals(3, libro.getValutazione());
    }

    @Test
    void testSetStatoLettura() {
        Libro libro = new Libro("Titolo", "Autore", "1234567890123", "Genere", 2, StatoLettura.da_leggere);

        libro.setStatoLettura(StatoLettura.in_lettura);
        assertEquals(StatoLettura.in_lettura, libro.getStatoLettura());

        libro.setStatoLettura(StatoLettura.letto);
        assertEquals(StatoLettura.letto, libro.getStatoLettura());
    }
}