package it.unical.ea_project.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginAttemptService {

    private final UserRepository userRepository;
    private final Cache<String, Integer> attemptsCache;

    private static final int WARNING_THRESHOLD = 5;

    public LoginAttemptService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();
    }

    public void loginFailed(String identifier) {
        String cacheKey = resolveToCanonicalKey(identifier);
        Integer currentAttempts = attemptsCache.getIfPresent(cacheKey);
        int attempts = (currentAttempts == null ? 0 : currentAttempts) + 1;
        attemptsCache.put(cacheKey, attempts);
        log.warn("Tentativo di login fallito per l'identificatore: {} (Tentativo {})", cacheKey, attempts);
        long randomDelayMs = ThreadLocalRandom.current().nextLong(1000, 2500);
        log.info("Applicato ritardo di {}ms per: {}", randomDelayMs, cacheKey);
        applyDelayMs(randomDelayMs);
        if (attempts >= WARNING_THRESHOLD) {
            sendSecurityAlert(cacheKey, attempts);
        }
    }

    public void loginSucceeded(String identifier) {
        String cacheKey = resolveToCanonicalKey(identifier);
        attemptsCache.invalidate(cacheKey);
    }

    private String resolveToCanonicalKey(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return "unknown";
        }
        String cleaned = identifier.trim().toLowerCase();
        Optional<User> user = userRepository.findByEmailIgnoreCase(cleaned);
        if (user.isEmpty()) {
            user = userRepository.findByUsernameIgnoreCase(cleaned);
        }
        return user.map(User::getEmail).orElse(cleaned);
    }

    private void applyDelayMs(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendSecurityAlert(String key, int attempts) {
        log.error("=======================================================================");
        log.error("AVVISO DI SICUREZZA: Rilevati {} tentativi falliti per: {}", attempts, key);
        log.error("=======================================================================");
    }
}