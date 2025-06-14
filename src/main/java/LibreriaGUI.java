
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import Basic.Libreria;
import Basic.Libro;
import Basic.LibroNonValidoException;
import Caricamento_Strategy.CaricaDaCSV;
import Caricamento_Strategy.CaricaDaJSON;
import Caricamento_Strategy.CaricaIF;
import Creazionali_Libro_FactoryMethod.LibroDaLeggere;
import Creazionali_Libro_FactoryMethod.LibroFactory;
import Creazionali_Libro_FactoryMethod.LibroInLettura;
import Creazionali_Libro_FactoryMethod.LibroLetto;
import Filtro_Libreria_Builder.FiltroLibri;
import Ordinamento_Strategy.*;
import Salvataggio_Visitor.LibroVisitor;
import Salvataggio_Visitor.LibroVisitorCSV;
import Salvataggio_Visitor.LibroVisitorJSON;
import Filtro_Libreria_Builder.BuilderImpl;

import java.util.Collections;
import java.util.List;

public class LibreriaGUI extends JFrame {
    private Libreria libreria;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> formatoSalvataggioComboBox;
    private JComboBox<String> ordinamentoComboBox;
    private JTextField filtroGenereField;
    private JTextField filtroValutazioneMinField;
    private JTextField filtroValutazioneMaxField;
    private JComboBox<String> filtroStatoComboBox;
    private JCheckBox ordineInversoCheckBox;

    private JTextField ricercaField;
    private JComboBox<String> tipoRicercaComboBox;
    private JButton ricercaButton;
    private JButton eliminaButton;
    private JButton mostraTuttiButton;

    public LibreriaGUI() {
        libreria = Libreria.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Gestione Libreria");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //inizializzazione dei panel su cui inserire gli oggetti grafici
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));


        // Inizializzazione della tabella contente i libri
        String[] columnNames = {"Titolo", "Autore", "ISBN", "Genere", "Valutazione", "Stato Lettura"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        //Creazione dei pulsanti, delle caselle, dei textfield ecc...
        JButton aggiungiLibroButton = new JButton("Aggiungi Libro");
        JButton modificaValutazioneButton = new JButton("Modifica Valutazione");
        JButton caricaButton = new JButton("Carica da File");
        JButton salvaButton = new JButton("Salva su File");
        JButton filtraButton = new JButton("Filtra");
        JButton resetFiltriButton = new JButton("Reset Filtri");
        ordineInversoCheckBox = new JCheckBox("Ordine inverso");
        ricercaField = new JTextField(15);
        tipoRicercaComboBox = new JComboBox<>(new String[]{"ISBN", "Titolo"});
        ricercaButton = new JButton("Cerca");
        eliminaButton = new JButton("Elimina Libro");
        mostraTuttiButton = new JButton("Mostra Tutti");

        formatoSalvataggioComboBox = new JComboBox<>(new String[]{"JSON", "CSV"});
        ordinamentoComboBox = new JComboBox<>(new String[]{"Nessuno", "Titolo", "Autore", "Genere", "Valutazione"});

        filtroGenereField = new JTextField(15);
        filtroValutazioneMinField = new JTextField(5);
        filtroValutazioneMaxField = new JTextField(5);
        filtroStatoComboBox = new JComboBox<>(new String[]{"Tutti", "Letto", "Da leggere", "In lettura"});

        // Aggiunta dei componenti ai panel
        buttonPanel.add(aggiungiLibroButton);
        buttonPanel.add(modificaValutazioneButton);
        buttonPanel.add(caricaButton);
        buttonPanel.add(salvaButton);
        buttonPanel.add(new JLabel("Formato:"));
        buttonPanel.add(formatoSalvataggioComboBox);
        buttonPanel.add(new JLabel("Ordina per:"));
        buttonPanel.add(ordinamentoComboBox);
        buttonPanel.add(ordineInversoCheckBox);
        filterPanel.add(new JLabel("Genere:"));
        filterPanel.add(filtroGenereField);
        filterPanel.add(new JLabel("Valutazione min:"));
        filterPanel.add(filtroValutazioneMinField);
        filterPanel.add(new JLabel("Valutazione max:"));
        filterPanel.add(filtroValutazioneMaxField);
        filterPanel.add(new JLabel("Stato:"));
        filterPanel.add(filtroStatoComboBox);
        filterPanel.add(filtraButton);
        filterPanel.add(resetFiltriButton);
        buttonPanel.add(new JLabel("Cerca per:"));
        buttonPanel.add(tipoRicercaComboBox);
        buttonPanel.add(ricercaField);
        buttonPanel.add(ricercaButton);
        buttonPanel.add(eliminaButton);
        buttonPanel.add(mostraTuttiButton);

        // Aggiunta dei panel
        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(filterPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        add(mainPanel);

        // Gestione degli eventi
        aggiungiLibroButton.addActionListener(e -> mostraDialogAggiungiLibro());
        modificaValutazioneButton.addActionListener(e -> mostraDialogModificaValutazione());
        caricaButton.addActionListener(e -> caricaDaFile());
        salvaButton.addActionListener(e -> salvaSuFile());
        filtraButton.addActionListener(e -> applicaFiltri());
        resetFiltriButton.addActionListener(e -> aggiornaTabella());
        ordinamentoComboBox.addActionListener(e -> applicaOrdinamento());
        ricercaButton.addActionListener(e -> cercaLibro());
        eliminaButton.addActionListener(e -> eliminaLibro());
        mostraTuttiButton.addActionListener(e -> aggiornaTabella());

        // Caricamento iniziale dei dati
        aggiornaTabella();
    }



    //Utilizzo del pattern Factory method per la creazione dei libri
    private void mostraDialogAggiungiLibro() {
        JDialog dialog = new JDialog(this, "Aggiungi Libro", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField titoloField = new JTextField();
        JTextField autoreField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField genereField = new JTextField();
        JComboBox<String> statoComboBox = new JComboBox<>(new String[]{"letto", "da_leggere", "in_lettura"});

        dialog.add(new JLabel("Titolo:"));
        dialog.add(titoloField);
        dialog.add(new JLabel("Autore:"));
        dialog.add(autoreField);
        dialog.add(new JLabel("ISBN:"));
        dialog.add(isbnField);
        dialog.add(new JLabel("Genere:"));
        dialog.add(genereField);
        dialog.add(new JLabel("Stato lettura:"));
        dialog.add(statoComboBox);

        JButton aggiungiButton = new JButton("Aggiungi");
        JButton annullaButton = new JButton("Annulla");

        aggiungiButton.addActionListener(e -> {
            String titolo = titoloField.getText();
            String autore = autoreField.getText();
            String isbn = isbnField.getText();
            String genere = genereField.getText();
            String statoSelezionato = statoComboBox.getSelectedItem().toString();

            if (!titolo.isEmpty() && !autore.isEmpty() && !isbn.isEmpty() && !genere.isEmpty()) {
                // Crea la factory appropriata in base allo stato selezionato
                LibroFactory factory;
                switch (statoSelezionato) {
                    case "letto":
                        factory = new LibroLetto();
                        break;
                    case "in_lettura":
                        factory = new LibroInLettura();
                        break;
                    case "da_leggere":
                    default:
                        factory = new LibroDaLeggere();
                        break;
                }

                libreria.setLibroFactory(factory);
                libreria.aggiungiLibro(titolo, autore, isbn, genere);
                aggiornaTabella();

                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Tutti i campi sono obbligatori!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        annullaButton.addActionListener(e -> dialog.dispose());

        dialog.add(aggiungiButton);
        dialog.add(annullaButton);

        dialog.setVisible(true);
    }



    private void mostraDialogModificaValutazione() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro dalla tabella", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 2);
        Libro libro = libreria.getLibri().stream()
                .filter(l -> l.getISBN().equals(isbn))
                .findFirst()
                .orElse(null);

        if (libro != null) {
            String valutazioneStr = JOptionPane.showInputDialog(this,
                    "Inserisci la nuova valutazione (0-5) per " + libro.getTitolo(),
                    libro.getValutazione());

            try {
                int valutazione = Integer.parseInt(valutazioneStr);
                if (valutazione >= 0 && valutazione <= 5) {
                    libreria.aggiungiValutazione(libro.getISBN(),valutazione);
                    aggiornaTabella();
                } else {
                    JOptionPane.showMessageDialog(this, "La valutazione deve essere tra 0 e 5", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Inserisci un numero valido", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void cercaLibro() {
        String criterio = ricercaField.getText().trim();
        String tipoRicerca = (String) tipoRicercaComboBox.getSelectedItem();

        if (criterio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un criterio di ricerca", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Libro libro;
            if (tipoRicerca.equals("ISBN")) {
                libro = libreria.getLibro(criterio, true);
            } else {
                libro = libreria.getLibro(criterio);
            }

            // Mostra solo il libro trovato nella tabella, non la tabella intera
            List<Libro> risultato = Collections.singletonList(libro);
            aggiornaTabella(risultato);
            mostraTuttiButton.setEnabled(true);
            ricercaButton.setEnabled(false);

        } catch (LibroNonValidoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            aggiornaTabella();
        }
    }


    private void eliminaLibro() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleziona un libro dalla tabella", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 2);
        String titolo = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Sei sicuro di voler eliminare il libro '" + titolo + "'?",
                "Conferma eliminazione",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                libreria.eliminaLibro(isbn);
                aggiornaTabella();
                JOptionPane.showMessageDialog(this, "Libro eliminato con successo", "Successo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Errore durante l'eliminazione: " + e.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    //Utilizzo del Pattern Strategy per gestire l'ordinamento, implementazione in Libreria, qui solo la scelta della strategy
    private void caricaDaFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String formato = (String) formatoSalvataggioComboBox.getSelectedItem();
            String filePath = fileChooser.getSelectedFile().getPath();


            CaricaIF strategia = formato.equals("JSON") ? new CaricaDaJSON() : new CaricaDaCSV();
            libreria.caricaDaFile(filePath, strategia);


            aggiornaTabella();
            JOptionPane.showMessageDialog(this, "Libri caricati con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    //Utilizzo del Pattern Visitor per il salvataggio libro per libro, l'implementazione si trova in Libreria, qui la scelta del Visitor
    private void salvaSuFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String formato = (String) formatoSalvataggioComboBox.getSelectedItem();
            String filePath = fileChooser.getSelectedFile().getPath();

            LibroVisitor visitor = formato.equals("JSON") ? new LibroVisitorJSON() : new LibroVisitorCSV();
            libreria.salvaSuFile(filePath, visitor);
            JOptionPane.showMessageDialog(this, "Libri salvati con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    //Utilizzo effettivo del Builder, in Libreria verrà passato direttamente il filtro dopo la build()
    private void applicaFiltri() {
        String genere = filtroGenereField.getText().trim();
        String valutazioneMinStr = filtroValutazioneMinField.getText().trim();
        String valutazioneMaxStr = filtroValutazioneMaxField.getText().trim();
        String statoStr = (String) filtroStatoComboBox.getSelectedItem();


        BuilderImpl builder = new BuilderImpl();

        if (genere.isEmpty() && valutazioneMinStr.isEmpty() && valutazioneMaxStr.isEmpty() && statoStr.equals("Tutti")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Inserire almeno un criterio di filtraggio",
                    "Filtro non valido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!genere.isEmpty()) {
            builder.setGenere(genere);
        }

        if (!valutazioneMinStr.isEmpty()) {
            try {
                int valutazioneMin = Integer.parseInt(valutazioneMinStr);
                builder.setValutazioneMinima(valutazioneMin);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valutazione minima deve essere un numero", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (!valutazioneMaxStr.isEmpty()) {
            try {
                int valutazioneMax = Integer.parseInt(valutazioneMaxStr);
                builder.setValutazioneMassima(valutazioneMax);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valutazione massima deve essere un numero", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!statoStr.equals("Tutti")) {
            String statoEnum = statoStr.toLowerCase().replace(" ", "_");
            Libro.StatoLettura stato = Libro.StatoLettura.valueOf(statoEnum);
            builder.setStatoLettura(stato);
        }


        FiltroLibri filtro = builder.build();
        List<Libro> libriFiltrati = libreria.filtraLibri(filtro);
        aggiornaTabella(libriFiltrati);
    }


    //Secondo utilizzo del pattern Strategy
    private void applicaOrdinamento() {
        String ordinamentoSelezionato = (String) ordinamentoComboBox.getSelectedItem();
        OrdinamentoLibriIF strategiaOrdinamento = null;

        switch (ordinamentoSelezionato) {
            case "Titolo":
                strategiaOrdinamento = new OrdinamentoPerTitolo();
                break;
            case "Autore":
                strategiaOrdinamento = new OrdinamentoPerAutore();
                break;
            case "Genere":
                strategiaOrdinamento = new OrdinamentoPerGenere();
                break;
            case "Valutazione":
                strategiaOrdinamento = new OrdinamentoPerValutazione();
                break;
            case "Stato Lettura":
                strategiaOrdinamento = new OrdinamentoPerStatoLettura();
                break;
            default:
                break;
        }

        if (strategiaOrdinamento != null && ordineInversoCheckBox.isSelected()) {
            strategiaOrdinamento = strategiaOrdinamento.inverso();
        }

        if (strategiaOrdinamento != null) {
            List<Libro> libriOrdinati = libreria.ordinaLibri(strategiaOrdinamento);
            aggiornaTabella(libriOrdinati);
        } else {
            aggiornaTabella();
        }
    }


    private void aggiornaTabella() {
        aggiornaTabella(libreria.getLibri());
        mostraTuttiButton.setEnabled(false);
        ricercaButton.setEnabled(true);
    }

    private void aggiornaTabella(List<Libro> libri) {
        tableModel.setRowCount(0);

        for (Libro libro : libri) {
            Object[] rowData = {
                    libro.getTitolo(),
                    libro.getAutore(),
                    libro.getISBN(),
                    libro.getGenere(),
                    libro.getValutazione(),
                    libro.getStatoLettura().toString().replace("_", " ")
            };
            tableModel.addRow(rowData);
        }
        mostraTuttiButton.setEnabled(libri.size() != libreria.getLibri().size());
        ricercaButton.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibreriaGUI gui = new LibreriaGUI();
            gui.setVisible(true);
        });
    }
}