package com.devausa.library_project.principal;

import com.devausa.library_project.model.*;
import com.devausa.library_project.repository.AuthorRepository;
import com.devausa.library_project.repository.BookRepository;
import com.devausa.library_project.service.APIConsumer;
import com.devausa.library_project.service.DataConverter;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private APIConsumer apiConsumer = new APIConsumer();
    private final String BASE_URL = "https://gutendex.com/books/";
    private DataConverter dataConverter = new DataConverter();
    private Set<Book> foundBooks = new HashSet<>();
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private Set<Book> books;

    @Autowired
    public Principal(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void showMenu() {
        var option = -1;
        while (option != 0) {
            var menu = """
                    *********** Menu Principal **************
                    1 - Buscar libro por titulo
                    2 - Listado de libros guardados
                    3 - Listado de autores guardados
                    4 - Lista autores vivos de un año determinado
                    5 - Mostar lista de libros por idioma
                    0 - Salir
                    """;
            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    searchBookByTitle();
                    break;
                case 2:
                    listBooks();
                    break;
                case 3:
                    listAuthors();
                    break;
                case 4:
                    authorsInSpecificYear();
                    break;
                case 5:
                    booksByLanguage();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicacion.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private BookData getBookData() {
        System.out.println("Ingresa el nombre del libro a buscar:");
        var bookName = scanner.nextLine();
        var json = apiConsumer.getData(BASE_URL + "?search=" + bookName.replace(" ", "+"));
        Data data = dataConverter.fetchData(json, Data.class);

        if (data.results().isEmpty()) {
            throw new RuntimeException("No se encontraron libros con ese titulo.");
        }

        BookData bookData = data.results().get(0);
        return new BookData(
                bookData.id(),
                bookData.title(),
                bookData.authors(),
                bookData.downloadCount(),
                bookData.languages()
        );
    }

    private void searchBookByTitle() {
        BookData data = getBookData();
        Set<Author> authors = data.authors().stream()
                .map(authorData -> new Author(authorData.name(), authorData.birthYear(), authorData.deathYear()))
                .collect(Collectors.toSet());
        Book book = new Book(data.title(), authors, data.downloadCount(), new HashSet<>(data.languages()));
        bookRepository.save(book);
        foundBooks.add(book);
        System.out.println(book.toString());
    }

    @Transactional(readOnly = true)
    private void listBooks() {
        books = new HashSet<>(bookRepository.findAll());

        // Initialize collections for each book
        books.forEach(book -> {
            bookRepository.findByIdWithAuthorsAndLanguages(book.getId()).ifPresent(bookWithAuthors -> {
                book.setAuthors(bookWithAuthors.getAuthors());
                book.setLanguages(bookWithAuthors.getLanguages());
            });
        });

        Comparator<Book> comparator = Comparator.comparing(book ->
                book.getAuthors().isEmpty() ? "" : book.getAuthors().iterator().next().getAuthorName()
        );

        books.stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }


    @Transactional(readOnly = true)
    private void listAuthors() {
        List<Author> authors = authorRepository.findAllWithBooks();
        if (authors.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            authors.forEach(author -> {
                System.out.println("Nombre: " + author.getAuthorName());
                System.out.println("Año de nacimiento: " + author.getBirthYear());
                System.out.println("Año de fallecimiento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
                if (author.getBook() != null) {
                    System.out.println("Libro: " + author.getBook().getTitle());
                } else {
                    System.out.println("Libro: N/A");
                }
                System.out.println();
            });
        }
    }

    @Transactional(readOnly = true)
    private void authorsInSpecificYear() {
        System.out.println("Ingresa el año que deseas buscar: ");
        var year = scanner.nextInt();
        scanner.nextLine();
        List<Author> authors = authorRepository.findByBirthYearLessThanOrDeathYearGreaterThanOrDeathYearNull(year);
        if (authors.isEmpty()) {
            System.out.println("No se encuentran autores con ese año: " + year);
        } else {
            authors.forEach(author -> {
                System.out.println("Nombre: " + author.getAuthorName());
                System.out.println("Año de nacimiento: " + author.getBirthYear());
                System.out.println("Año de fallecimiento: " + (author.getDeathYear() != null ? author.getDeathYear() : "N/A"));
                System.out.println("Libro: " + (author.getBook() != null ? author.getBook().getTitle() : "N/A"));
                System.out.println();
            });
        }
    }

    @Transactional(readOnly = true)
    private void booksByLanguage() {
        System.out.println("Ingrese el idioma del libro a buscar (es, en, fr, pt, etc.):");
        var languageInput = scanner.nextLine().toLowerCase();

        try {
            Language language = Language.fromString(languageInput);
            List<Book> books = bookRepository.findByLanguage(language);

            if (books.isEmpty()) {
                System.out.println("No existen libros con ese idioma: " + languageInput);
            } else {
                books.forEach(book -> {
                    Hibernate.initialize(book.getAuthors());
                    Hibernate.initialize(book.getLanguages());

                    System.out.println(
                            "***** Libro ******" +
                                    "\nTitulo: " + book.getTitle() +
                                    "\nAutor: " + book.getAuthors().stream().map(Author::getAuthorName).collect(Collectors.joining(", ")) +
                                    "\nIdiomas: " + book.getLanguages().stream().map(Language::getDescription).collect(Collectors.joining(", ")) +
                                    "\nCantidad de descargas " + book.getDownloadCount() +
                                    "\n***********************************"
                    );
                });
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma no reconocido: " + languageInput);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Principal.class, args);
    }
}
