package com.library.service;

import com.library.domain.FileStorage;
import com.library.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReminderServiceTest {

    @TempDir
    Path tempDir;

    private FileStorage storage;
    private LoanService loanService;
    private CapturingEmailService emailService;
    private ReminderService reminderService;

    /**
     * EmailService مزيف – بدل ما يرسل إيميل حقيقي،
     * بس يخزن البيانات عشان التيست يتحقق منها.
     */
    static class CapturingEmailService extends EmailService {

        List<String> toList = new ArrayList<>();
        List<String> subjectList = new ArrayList<>();
        List<String> bodyList = new ArrayList<>();

        CapturingEmailService() {
            // قيم وهمية – مش رح نستخدمهم، بس لازم نمررهم للـ super
            super("test@example.com", "dummy-password");
        }

        @Override
        public void sendEmail(String to, String subject, String body) {
            toList.add(to);
            subjectList.add(subject);
            bodyList.add(body);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.write(tempDir.resolve("admins.txt"), List.of());
        Files.write(tempDir.resolve("librarians.txt"), List.of());
        Files.write(tempDir.resolve("books.txt"), List.of());
        Files.write(tempDir.resolve("fines.txt"), List.of());
        Files.write(tempDir.resolve("loans.txt"), List.of());

        storage = new FileStorage(tempDir.toString());
        loanService = new LoanService(storage);
        emailService = new CapturingEmailService();

        UserService fakeUsers = new FakeUserService();

        reminderService = new ReminderService(
                loanService,
                fakeUsers,
                emailService
        );
    }


    @Test
    void sendOverdueReminders_sendsOneEmailPerOverdueLoan() throws IOException {
        LocalDate today = LocalDate.now();

        // L1: متأخر
        // L2: مش متأخر (due بالمستقبل)
        List<String> loansLines = List.of(
                "L1;user1@example.com;B1;" +
                        today.minusDays(40) + ";" +
                        today.minusDays(5) + ";" +
                        "",
                "L2;user2@example.com;B2;" +
                        today.minusDays(5) + ";" +
                        today.plusDays(10) + ";" +
                        ""
        );
        Files.write(tempDir.resolve("loans.txt"), loansLines);

        int count = reminderService.sendOverdueReminders();

        assertEquals(1, count);
        assertEquals(1, emailService.toList.size());
        assertEquals("user1@example.com@example.com", emailService.toList.get(0));

    }

    @Test
    void sendOverdueReminders_whenNoOverdueLoans_sendsNoEmails() throws IOException {
        // ولا Loan متأخر
        LocalDate today = LocalDate.now();
        List<String> loansLines = List.of(
                "L1;user1@example.com;B1;" + today.minusDays(5) + ";" + today.plusDays(10) + ";"
        );

        Files.write(tempDir.resolve("loans.txt"), loansLines);

        int count = reminderService.sendOverdueReminders();

        assertEquals(0, count);
        assertEquals(0, emailService.toList.size());
    }

    static class FakeUserService extends UserService {
        public FakeUserService() {
            super(null); // ما بدنا FileStorage حقيقي
        }

        @Override
        public User findById(String userId) {
            // نرجع User وهمي بناءً على userId
            return new User(userId, "TestUser", userId + "@example.com", "pass");
        }
    }

}
