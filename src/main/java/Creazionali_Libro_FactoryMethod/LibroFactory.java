package Creazionali_Libro_FactoryMethod;

import Basic.Libro;

public interface LibroFactory {
    Libro inserisciLibro(String titolo, String autore, String ISBN, String genere);
}
