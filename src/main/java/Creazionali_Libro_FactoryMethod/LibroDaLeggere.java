package Creazionali_Libro_FactoryMethod;

import Basic.Libro;

public class LibroDaLeggere implements LibroFactory {

    @Override
    public Libro inserisciLibro(String titolo, String autore, String ISBN, String genere) {
        return new Libro(titolo, autore, ISBN, genere, -1, Libro.StatoLettura.da_leggere );
    }
}
