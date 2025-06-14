package Salvataggio_Visitor;

import Basic.Libro;

public class LibroVisitorCSV implements LibroVisitor {

    @Override
    public String visit(Libro libro) {
        return escapeCsvField(libro.getTitolo()) + "," +
                escapeCsvField(libro.getAutore()) + "," +
                escapeCsvField(libro.getISBN()) + "," +
                escapeCsvField(libro.getGenere()) + "," +
                libro.getValutazione() + "," +
                escapeCsvField(libro.getStatoLettura().toString());
    }


    //preso da internet per formattare CSV
    private String escapeCsvField(String field) {
        if (field == null) return "";
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }
}