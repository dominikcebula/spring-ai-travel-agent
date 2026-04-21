package com.dominikcebula.spring.ai.orders.orders;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ConcurrentMap<String, AtomicInteger> dailyCounters = new ConcurrentHashMap<>();
    private final Clock clock;

    public OrderNumberGenerator() {
        this(Clock.systemDefaultZone());
    }

    OrderNumberGenerator(Clock clock) {
        this.clock = clock;
    }

    public String next() {
        String datePart = LocalDate.now(clock).format(DATE_FORMAT);
        int sequence = dailyCounters.computeIfAbsent(datePart, key -> new AtomicInteger()).incrementAndGet();
        return "ORD-%s-%04d".formatted(datePart, sequence);
    }
}
