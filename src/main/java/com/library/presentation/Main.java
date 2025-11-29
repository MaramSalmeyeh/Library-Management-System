package com.library.presentation;

import com.library.domain.FileStorage;
import com.library.service.AuthService;
import com.library.service.BookService;

public class Main {

    public static void main(String[] args) {


        FileStorage storage = new FileStorage("src/main/resources/DB");


        AuthService authService = new AuthService(storage);
        BookService bookService = new BookService(storage);


        ConsoleMenu menu = new ConsoleMenu(authService, bookService);
        menu.run();
    }
}
