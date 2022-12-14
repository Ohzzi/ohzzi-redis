package lock;

import static org.assertj.core.api.Assertions.assertThat;

import lock.Book;
import lock.BookLockFacade;
import lock.BookRepository;
import lock.Stock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookLockFacade bookLockFacade;

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    void 동시에_100명이_책을_구매한다() throws InterruptedException {
        Long bookId = bookRepository.save(new Book("이펙티브 자바", 36_000, new Stock(100)))
                .getId();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    bookLockFacade.purchase(bookId, 1);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        Book actual = bookRepository.findById(bookId)
                .orElseThrow();

        assertThat(actual.getStock().getRemain()).isZero();
    }
}
