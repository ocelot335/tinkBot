package edu.java.services.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class CounterService {
    private final Counter successfulRequestsCounter;

    public CounterService(MeterRegistry registry) {
        this.successfulRequestsCounter = registry.counter("server_successful_requests_count");
    }

    public void successfulRequestsCounterIncrement() {
        successfulRequestsCounter.increment();
    }
}
