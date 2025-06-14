package Creazionali_Libro_FactoryMethod;

import Basic.Libro;

public class LibroInLettura implements LibroFactory{

    @Override
    public Libro inserisciLibro(String titolo, String autore, String ISBN, String genere) {
        return new Libro(titolo,autore,ISBN,genere,-1, Libro.StatoLettura.in_lettura);
    }
}
