package org.rdlopes.n26.challenge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;

/**
 * Entry point.
 * <p>
 * A {@link Cache} is
 */
@SpringBootApplication
public class N26ChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(N26ChallengeApplication.class, args);
    }

    @Bean
    public ConcurrentMapCache transactionsCache(
            @Value("${spring.cache.allow-null-values: false}") boolean nullValuesAllowed) {
        return new ConcurrentMapCache("transaction", nullValuesAllowed);
    }

}
