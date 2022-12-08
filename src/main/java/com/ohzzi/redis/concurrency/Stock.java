package com.ohzzi.redis.concurrency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "quantity", nullable = false)
    private long remain;

    public Stock(final long remain) {
        this.remain = remain;
    }

    public void decrease(final long quantity) {
        if ((remain - quantity) < 0) {
            throw new IllegalArgumentException();
        }
        remain -= quantity;
    }
}
