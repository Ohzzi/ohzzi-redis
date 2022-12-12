package cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public BookService(final BookRepository bookRepository, final RedisTemplate<String, String> redisTemplate) {
        this.bookRepository = bookRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void purchase(final Long bookId, final long quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(IllegalArgumentException::new);
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String key = String.format("book:%d:stock", bookId);
        valueOps.setIfAbsent(key, String.valueOf(book.getStock().getRemain()));
        long remain = valueOps.decrement(key, quantity);
        if (remain < 0) {
            valueOps.increment(key, quantity);
            throw new IllegalArgumentException();
        }
    }
}
