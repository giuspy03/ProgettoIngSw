package Caricamento_Strategy;

import Basic.Libro;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface CaricaIF {
    List<Libro> parse(BufferedReader reader) throws IOException;
}