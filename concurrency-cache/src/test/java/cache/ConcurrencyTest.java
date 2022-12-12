package cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookService bookService;

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
    }

    @Test
    void 동시에_100명이_책을_구매한다() throws InterruptedException {
        AtomicInteger failCount = new AtomicInteger(0);
        Long bookId = bookRepository.save(new Book("이펙티브 자바", 36_000, new Stock(100)))
                .getId();
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    bookService.purchase(bookId, 1);
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        assertThat(failCount.get()).isZero();
        assertThatThrownBy(() -> bookService.purchase(bookId, 1)).isInstanceOf(IllegalArgumentException.class);
    }
}
