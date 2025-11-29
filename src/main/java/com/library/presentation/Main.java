package com.library.presentation;

import com.library.domain.FileStorage;
import com.library.service.AuthService;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.FineService;
import com.library.service.BorrowingService;

public class Main {

    public static void main(String[] args) {


        FileStorage storage = new FileStorage("src/main/resources/DB");


        AuthService authService = new AuthService(storage);
        BookService bookService = new BookService(storage);
        LoanService loanService = new LoanService(storage);
        FineService fineService = new FineService(storage);

        BorrowingService borrowingService = new BorrowingService(loanService, fineService);
        ConsoleMenu menu = new ConsoleMenu(authService, bookService, loanService, fineService);
        menu.run();
    }
}
