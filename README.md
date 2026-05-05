# Gestione Libreria Personale

Progetto universitario per il corso di **Ingegneria del Software** — sviluppo di un'applicazione desktop in Java per la gestione di una libreria personale di libri, con interfaccia grafica Swing e persistenza su file (CSV e JSON).

Il progetto nasce con l'obiettivo di applicare in modo concreto i principali **Design Pattern** del catalogo GoF (Gang of Four), combinandoli in un sistema coerente e funzionale.

---

## Indice

- [Tecnologie Utilizzate](#tecnologie-utilizzate)
- [Struttura del Progetto](#struttura-del-progetto)
- [Architettura e Design Pattern](#architettura-e-design-pattern)
  - [Singleton — Libreria](#1-singleton--libreria)
  - [Factory Method — Creazione Libri](#2-factory-method--creazione-libri)
  - [Strategy — Ordinamento](#3-strategy--ordinamento)
  - [Strategy — Caricamento File](#4-strategy--caricamento-file)
  - [Visitor — Salvataggio File](#5-visitor--salvataggio-file)
  - [Builder — Filtro Ricerca](#6-builder--filtro-ricerca)
- [Modello del Dominio](#modello-del-dominio)
- [Interfaccia Grafica](#interfaccia-grafica)
- [Formato dei Dati](#formato-dei-dati)
- [Gestione delle Eccezioni](#gestione-delle-eccezioni)
- [Testing](#testing)
- [Come Eseguire il Progetto](#come-eseguire-il-progetto)

---

## Tecnologie Utilizzate

| Tecnologia | Versione | Utilizzo |
|---|---|---|
| Java | 21 | Linguaggio principale |
| Maven | 3.x | Build tool e gestione dipendenze |
| Swing | (JDK built-in) | Interfaccia grafica desktop |
| JUnit Jupiter | 5.9.3 | Framework di testing |
| org.json | 20240303 | Parsing e generazione JSON |

Il progetto non fa uso di framework pesanti (Spring, Jakarta EE, ecc.) — la scelta è deliberata per mantenere il focus sulla logica applicativa e sui design pattern, senza dipendenze esterne non necessarie.

---

## Struttura del Progetto

```
Progetto_Libreria_Ing_Sw/
├── pom.xml
├── test_libreria.csv          # Dataset di esempio in CSV
├── test_carica.json           # Dataset di esempio in JSON
└── src/
    ├── main/java/
    │   ├── Basic/                              # Dominio core
    │   │   ├── Libro.java                      # Entità principale
    │   │   ├── Libreria.java                   # Singleton — gestore centrale
    │   │   ├── LibroNonValidoException.java
    │   │   └── InserimentoNonValidoException.java
    │   ├── Creazionali_Libro_FactoryMethod/    # Pattern Factory Method
    │   │   ├── LibroFactory.java               # Interfaccia factory
    │   │   ├── LibroDaLeggere.java
    │   │   ├── LibroLetto.java
    │   │   └── LibroInLettura.java
    │   ├── Ordinamento_Strategy/               # Pattern Strategy (ordinamento)
    │   │   ├── OrdinamentoLibriIF.java         # Interfaccia strategy
    │   │   ├── OrdinamentoPerTitolo.java
    │   │   ├── OrdinamentoPerAutore.java
    │   │   ├── OrdinamentoPerGenere.java
    │   │   ├── OrdinamentoPerValutazione.java
    │   │   └── OrdinamentoPerStatoLettura.java
    │   ├── Caricamento_Strategy/               # Pattern Strategy (caricamento)
    │   │   ├── CaricaIF.java                   # Interfaccia strategy
    │   │   ├── CaricaDaCSV.java
    │   │   ├── CaricaDaJSON.java
    │   │   └── CaricamentoFallitoException.java
    │   ├── Salvataggio_Visitor/                # Pattern Visitor (salvataggio)
    │   │   ├── LibroVisitor.java               # Interfaccia visitor
    │   │   ├── LibroVisitorCSV.java
    │   │   ├── LibroVisitorJSON.java
    │   │   └── SalvataggioFallitoException.java
    │   ├── Filtro_Libreria_Builder/            # Pattern Builder (filtro)
    │   │   ├── BuilderIF.java                  # Interfaccia builder
    │   │   ├── BuilderImpl.java
    │   │   ├── FiltroLibri.java                # Prodotto del builder
    │   │   └── FiltroNonValidoException.java
    │   └── LibreriaGUI.java                    # Entry point — interfaccia Swing
    └── test/java/
        ├── LibroTesting.java                   # Unit test entità Libro
        └── LibreriaTesting.java                # Unit test logica Libreria
```

Ogni package corrisponde a un singolo design pattern applicato, rendendo immediata la navigazione e la comprensione della responsabilità di ogni componente.

---

## Architettura e Design Pattern

L'architettura è orientata alla separazione delle responsabilità: ogni pattern risolve un problema specifico e ben delimitato. La `Libreria` (Singleton) funge da orchestratore centrale, delegando la creazione, l'ordinamento, il filtraggio e la persistenza agli algoritmi concreti tramite le interfacce definite.

```
LibreriaGUI
     │
     ▼
 Libreria (Singleton)
     │
     ├──► Factory Method ──► crea Libro con StatoLettura corretto
     ├──► Strategy ──────────► ordina la lista con il comparatore scelto
     ├──► Strategy ──────────► carica libri da CSV o JSON
     ├──► Visitor ───────────► salva libri su CSV o JSON
     └──► Builder ───────────► costruisce e applica filtri di ricerca
```

---

### 1. Singleton — Libreria

**Package:** `Basic/`  
**File:** [`Basic/Libreria.java`](src/main/java/Basic/Libreria.java)

La `Libreria` è il cuore dell'applicazione. Gestisce l'intera collezione di libri tramite una `HashMap<String, Libro>` dove la chiave è l'ISBN. Viene implementata come Singleton per garantire che esista **una sola istanza** dell'archivio durante tutta l'esecuzione del programma.

```java
// Accesso all'unica istanza
Libreria libreria = Libreria.getInstance();
```

**Responsabilità:**
- `aggiungiLibro(titolo, autore, isbn, genere, val, factory)` — crea un libro via factory e lo inserisce, lanciando eccezione se l'ISBN è già presente
- `eliminaLibro(isbn)` — rimozione per ISBN
- `getLibro(isbn)` / `getLibroPerTitolo(titolo)` — ricerca
- `aggiungiValutazione(isbn, valutazione)` — aggiorna la valutazione di un libro esistente
- `filtraLibri(FiltroLibri)` — filtraggio tramite Builder
- `ordinaLibri(OrdinamentoLibriIF)` — ordinamento tramite Strategy
- `salvaSuFile(percorso, visitor)` — serializzazione tramite Visitor
- `caricaDaFile(percorso, strategia)` — deserializzazione tramite Strategy

---

### 2. Factory Method — Creazione Libri

**Package:** `Creazionali_Libro_FactoryMethod/`  
**File:** [`LibroFactory.java`](src/main/java/Creazionali_Libro_FactoryMethod/LibroFactory.java), [`LibroDaLeggere.java`](src/main/java/Creazionali_Libro_FactoryMethod/LibroDaLeggere.java), [`LibroLetto.java`](src/main/java/Creazionali_Libro_FactoryMethod/LibroLetto.java), [`LibroInLettura.java`](src/main/java/Creazionali_Libro_FactoryMethod/LibroInLettura.java)

Il pattern Factory Method viene usato per **disaccoppiare la logica di creazione dei libri** dalla logica della libreria. Ogni factory si occupa di creare un `Libro` con lo `StatoLettura` corretto già impostato.

```
«interface»
LibroFactory
    └── creaLibro(titolo, autore, isbn, genere, valutazione): Libro

LibroDaLeggere   ──► crea Libro con stato = da_leggere
LibroLetto       ──► crea Libro con stato = letto
LibroInLettura   ──► crea Libro con stato = in_lettura
```

La `Libreria.aggiungiLibro()` riceve la factory come parametro senza conoscere quale tipo concreto verrà creato. L'utente seleziona lo stato dalla GUI e viene passata la factory corrispondente.

```java
// Esempio di utilizzo
LibroFactory factory = new LibroLetto();
libreria.aggiungiLibro("Dune", "Frank Herbert", "9788804517627", "Fantascienza", 5, factory);
```

---

### 3. Strategy — Ordinamento

**Package:** `Ordinamento_Strategy/`  
**File:** [`OrdinamentoLibriIF.java`](src/main/java/Ordinamento_Strategy/OrdinamentoLibriIF.java) e implementazioni

Il pattern Strategy permette di **cambiare a runtime** l'algoritmo di ordinamento della lista libri. L'interfaccia `OrdinamentoLibriIF` estende `Comparator<Libro>`, permettendo di integrare il pattern direttamente con le API Java standard.

```
«interface» OrdinamentoLibriIF extends Comparator<Libro>
    └── default inverso(): OrdinamentoLibriIF   ← ordine inverso senza subclassi extra

OrdinamentoPerTitolo
OrdinamentoPerAutore
OrdinamentoPerGenere
OrdinamentoPerValutazione
OrdinamentoPerStatoLettura
```

Il metodo `default inverso()` nell'interfaccia sfrutta `Comparator.reversed()`, eliminando la necessità di creare 5 classi aggiuntive per l'ordine decrescente — la GUI espone semplicemente una checkbox "Ordine inverso".

```java
OrdinamentoLibriIF strategia = new OrdinamentoPerValutazione();
List<Libro> lista = libreria.ordinaLibri(strategia);               // crescente
List<Libro> listaInversa = libreria.ordinaLibri(strategia.inverso()); // decrescente
```

---

### 4. Strategy — Caricamento File

**Package:** `Caricamento_Strategy/`  
**File:** [`CaricaIF.java`](src/main/java/Caricamento_Strategy/CaricaIF.java), [`CaricaDaCSV.java`](src/main/java/Caricamento_Strategy/CaricaDaCSV.java), [`CaricaDaJSON.java`](src/main/java/Caricamento_Strategy/CaricaDaJSON.java)

Seconda applicazione del pattern Strategy, usata per **astrarre il formato di importazione**. La `Libreria` chiama `strategia.carica(percorso)` senza sapere se il file è CSV o JSON.

```
«interface» CaricaIF
    └── carica(percorso): List<Libro>

CaricaDaCSV   ──► parsing CSV, gestione header e virgolette
CaricaDaJSON  ──► parsing JSON tramite org.json
```

```java
CaricaIF strategia = new CaricaDaJSON();
libreria.caricaDaFile("test_carica.json", strategia);
```

---

### 5. Visitor — Salvataggio File

**Package:** `Salvataggio_Visitor/`  
**File:** [`LibroVisitor.java`](src/main/java/Salvataggio_Visitor/LibroVisitor.java), [`LibroVisitorCSV.java`](src/main/java/Salvataggio_Visitor/LibroVisitorCSV.java), [`LibroVisitorJSON.java`](src/main/java/Salvataggio_Visitor/LibroVisitorJSON.java)

Il pattern Visitor viene applicato al salvataggio per **separare la logica di serializzazione dall'entità `Libro`**. Ogni visitor sa come serializzare un libro nel formato specifico; la classe `Libro` espone solo il metodo `accept(visitor)`.

```
«interface» LibroVisitor
    └── visit(Libro): void

LibroVisitorCSV  ──► accumula righe CSV, scrive file a fine traversata
LibroVisitorJSON ──► accumula oggetti JSONObject, scrive array JSON a fine traversata

Libro
    └── accept(LibroVisitor visitor) { visitor.visit(this); }
```

La `Libreria.salvaSuFile()` itera i libri e chiama `accept()` su ciascuno, poi invoca il metodo finale del visitor per persistere il file. In questo modo aggiungere un nuovo formato (es. XML) richiede solo una nuova classe visitor, senza toccare `Libro` o `Libreria`.

```java
LibroVisitor visitor = new LibroVisitorJSON("output.json");
libreria.salvaSuFile("output.json", visitor);
```

---

### 6. Builder — Filtro Ricerca

**Package:** `Filtro_Libreria_Builder/`  
**File:** [`BuilderIF.java`](src/main/java/Filtro_Libreria_Builder/BuilderIF.java), [`BuilderImpl.java`](src/main/java/Filtro_Libreria_Builder/BuilderImpl.java), [`FiltroLibri.java`](src/main/java/Filtro_Libreria_Builder/FiltroLibri.java)

Il pattern Builder è usato per costruire l'oggetto `FiltroLibri` in modo fluente, validandone la consistenza al momento della `build()`. Questo evita di avere costruttori con molti parametri opzionali (telescoping constructor anti-pattern).

```
«interface» BuilderIF
    ├── setAutore(String)
    ├── setGenere(String)
    ├── setValutazioneMin(int)
    ├── setValutazioneMax(int)
    ├── setStatoLettura(StatoLettura)
    └── build(): FiltroLibri    ← lancia FiltroNonValidoException se nessun criterio è impostato

BuilderImpl   ──► implementazione concreta
FiltroLibri   ──► prodotto immutabile con i criteri di filtraggio
```

La logica di filtraggio applica i criteri in **AND** tra loro, e ricerca l'autore in modo case-insensitive tramite `contains()`, il genere tramite `equalsIgnoreCase()`. Libri senza valutazione (valore `-1`) vengono esclusi dai filtri su valutazione min/max.

```java
FiltroLibri filtro = new BuilderImpl()
    .setGenere("Fantasy")
    .setValutazioneMin(4)
    .build();

List<Libro> risultati = libreria.filtraLibri(filtro);
```

---

## Modello del Dominio

### Entità `Libro`

**File:** [`Basic/Libro.java`](src/main/java/Basic/Libro.java)

| Campo | Tipo | Validazione |
|---|---|---|
| `titolo` | `String` | non vuoto |
| `autore` | `String` | non vuoto |
| `isbn` | `String` | esattamente 13 cifre numeriche |
| `genere` | `String` | non vuoto |
| `valutazione` | `int` | valore in `{-1, 0, 1, 2, 3, 4, 5}` — `-1` = non valutato |
| `statoLettura` | `StatoLettura` | enum: `letto`, `da_leggere`, `in_lettura` |

La validazione avviene nel costruttore: campi non validi lanciano `LibroNonValidoException`. Solo `valutazione` e `statoLettura` sono mutabili dopo la creazione (tramite setter con validazione).

---

## Interfaccia Grafica

**File:** [`LibreriaGUI.java`](src/main/java/LibreriaGUI.java)

L'interfaccia è costruita con Java Swing e segue un layout a pannelli con una `JTable` centrale per la visualizzazione della collezione.

**Componenti principali:**

| Area | Componente | Funzione |
|---|---|---|
| Centro | `JTable` | Visualizza i libri con colonne: Titolo, Autore, ISBN, Genere, Valutazione, Stato |
| In alto | `JToolBar` / Pulsanti | Aggiungi, Modifica Valutazione, Elimina, Carica da File, Salva su File |
| Laterale | Pannello Ordinamento | `JComboBox` con 5 criteri + `JCheckBox` ordine inverso |
| Laterale | Pannello Filtri | Campi per genere, valutazione min/max, stato lettura |
| In basso | Barra di ricerca | Ricerca per ISBN o Titolo |

**Flusso GUI → Pattern:**

```
Utente seleziona stato e clicca "Aggiungi"
  └──► GUI instanzia la LibroFactory corrispondente
         └──► Libreria.aggiungiLibro(..., factory) [Factory Method]

Utente seleziona criterio ordinamento e clicca "Ordina"
  └──► GUI instanzia OrdinamentoLibriIF concreto (± inverso)
         └──► Libreria.ordinaLibri(strategia) [Strategy]

Utente imposta filtri e clicca "Filtra"
  └──► GUI chiama BuilderImpl con i criteri della UI
         └──► BuilderImpl.build() → FiltroLibri [Builder]
                └──► Libreria.filtraLibri(filtro)

Utente sceglie formato e clicca "Salva"
  └──► GUI instanzia LibroVisitorCSV o LibroVisitorJSON
         └──► Libreria.salvaSuFile(path, visitor) [Visitor]

Utente sceglie formato e clicca "Carica"
  └──► GUI instanzia CaricaDaCSV o CaricaDaJSON
         └──► Libreria.caricaDaFile(path, strategia) [Strategy]
```

---

## Formato dei Dati

### CSV

```csv
"Titolo","Autore","ISBN","Genere","Valutazione","StatoLettura"
"Le armi della persuasione","Robert B. Cialdini","9788809896840","Psicologia",5,"letto"
"Il Signore degli Anelli","J.R.R. Tolkien","9788845292613","Fantasy",-1,"da_leggere"
```

La prima riga è l'header. I valori stringa sono racchiusi tra virgolette. La valutazione `-1` indica un libro non ancora valutato.

### JSON

```json
[
  {
    "titolo": "Il Signore degli Anelli",
    "autore": "J.R.R. Tolkien",
    "ISBN": "9788845292613",
    "genere": "Fantasy",
    "valutazione": 3,
    "statoLettura": "letto"
  }
]
```

Array JSON di oggetti libro. I file di esempio `test_libreria.csv` e `test_carica.json` nella root del progetto possono essere usati per testare il caricamento.

---

## Gestione delle Eccezioni

Il progetto definisce eccezioni custom controllate (checked) per ogni dominio di errore:

| Eccezione | Package | Quando viene lanciata |
|---|---|---|
| `LibroNonValidoException` | `Basic` | ISBN non valido, valutazione fuori range, campi vuoti |
| `InserimentoNonValidoException` | `Basic` | Tentativo di inserire un libro con ISBN già presente |
| `FiltroNonValidoException` | `Filtro_Libreria_Builder` | `build()` chiamato senza aver impostato almeno un criterio |
| `CaricamentoFallitoException` | `Caricamento_Strategy` | Errore I/O o formato non valido durante il caricamento |
| `SalvataggioFallitoException` | `Salvataggio_Visitor` | Errore I/O durante il salvataggio |

---

## Testing

**Framework:** JUnit 5 (Jupiter)  
**File:** [`LibroTesting.java`](src/test/java/LibroTesting.java), [`LibreriaTesting.java`](src/test/java/LibreriaTesting.java)

### `LibroTesting` — 8 test

| Test | Cosa verifica |
|---|---|
| Costruttore valido | Libro creato correttamente con tutti i campi |
| ISBN non numerico | `LibroNonValidoException` su ISBN con lettere |
| ISBN troppo corto | `LibroNonValidoException` su ISBN < 13 cifre |
| ISBN troppo lungo | `LibroNonValidoException` su ISBN > 13 cifre |
| Valutazione fuori range | `LibroNonValidoException` per valori < -1 o > 5 |
| Setter valutazione valida | Aggiornamento corretto |
| Setter valutazione invalida | Eccezione su valore non valido |
| Setter stato lettura | Aggiornamento corretto |

### `LibreriaTesting` — 11 test

| Test | Cosa verifica |
|---|---|
| Aggiunta libro | Libro presente dopo inserimento |
| Singleton | `getInstance()` restituisce sempre la stessa istanza |
| Ricerca ISBN inesistente | Ritorna `null` |
| Ricerca titolo inesistente | Ritorna `null` |
| Eliminazione | Libro rimosso dopo `eliminaLibro()` |
| ISBN duplicato | `InserimentoNonValidoException` al secondo inserimento |
| Valutazione | Valore aggiornato correttamente |
| Filtraggio per genere | Solo libri del genere specificato nel risultato |
| Ordinamento per valutazione | Lista ordinata in modo crescente |
| Ordinamento inverso | Lista ordinata in modo decrescente |
| Salvataggio CSV | File scritto su disco senza errori |
| Caricamento JSON | Libri caricati correttamente da file |
| Percorso invalido | `CaricamentoFallitoException` su path inesistente |

Per eseguire i test:

```bash
mvn test
```

---

## Come Eseguire il Progetto

### Prerequisiti

- Java 21+
- Maven 3.6+

### Build e avvio

```bash
# Compilare il progetto
mvn compile

# Eseguire i test
mvn test

# Creare il JAR
mvn package

# Avviare la GUI (dalla root del progetto)
mvn exec:java -Dexec.mainClass="LibreriaGUI"
```

In alternativa, aprire il progetto con **IntelliJ IDEA** (i file `.idea/` sono già presenti nel repository) ed eseguire `LibreriaGUI.main()` direttamente dall'IDE.

---

## Riepilogo Pattern Implementati

| Pattern GoF | Categoria | Package | Problema risolto |
|---|---|---|---|
| **Singleton** | Creazionale | `Basic` | Una sola istanza della libreria per tutta l'applicazione |
| **Factory Method** | Creazionale | `Creazionali_Libro_FactoryMethod` | Creare libri con stato lettura corretto senza accoppiare la logica di creazione |
| **Strategy** | Comportamentale | `Ordinamento_Strategy` | Algoritmo di ordinamento intercambiabile a runtime |
| **Strategy** | Comportamentale | `Caricamento_Strategy` | Formato di importazione (CSV/JSON) intercambiabile a runtime |
| **Visitor** | Comportamentale | `Salvataggio_Visitor` | Serializzazione disaccoppiata dall'entità, estendibile senza modificare `Libro` |
| **Builder** | Creazionale | `Filtro_Libreria_Builder` | Costruzione incrementale e validata di criteri di filtraggio multipli |
