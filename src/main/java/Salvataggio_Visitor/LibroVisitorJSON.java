package Salvataggio_Visitor;

import Basic.Libro;

public class LibroVisitorJSON implements LibroVisitor {
    @Override
    public String visit(Libro libro) {
        return "{\"titolo\":\"" + escapeJsonField(libro.getTitolo()) +
                "\",\"autore\":\"" + escapeJsonField(libro.getAutore()) +
                "\",\"ISBN\":\"" + escapeJsonField(libro.getISBN()) +
                "\",\"genere\":\"" + escapeJsonField(libro.getGenere()) +
                "\",\"valutazione\":" + libro.getValutazione() +
                ",\"statoLettura\":\"" + escapeJsonField(libro.getStatoLettura().toString()) +
                "\"}";
    }


    //preso da internet per formattare JSON
    private String escapeJsonField(String field) {
        if (field == null) return "";
        return field.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}