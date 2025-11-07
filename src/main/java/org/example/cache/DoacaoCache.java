package org.example.cache;

import org.example.model.Doacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DoacaoCache {
    private static final Logger logger = LoggerFactory.getLogger(DoacaoCache.class);
    private static final Map<Integer, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = TimeUnit.MINUTES.toMillis(5);

    private static class CacheEntry {
        final Doacao doacao;
        final long timestamp;

        CacheEntry(Doacao doacao) {
            this.doacao = doacao;
            this.timestamp = Instant.now().toEpochMilli();
        }

        boolean isExpired() {
            return Instant.now().toEpochMilli() - timestamp > CACHE_DURATION;
        }
    }

    public static void add(Doacao doacao) {
        logger.debug("Adicionando doação {} ao cache", doacao.getId());
        cache.put(doacao.getId(), new CacheEntry(doacao));
    }

    public static Optional<Doacao> get(int id) {
        CacheEntry entry = cache.get(id);
        if (entry == null) {
            logger.debug("Cache miss para doação {}", id);
            return Optional.empty();
        }

        if (entry.isExpired()) {
            logger.debug("Cache expirado para doação {}", id);
            cache.remove(id);
            return Optional.empty();
        }

        logger.debug("Cache hit para doação {}", id);
        return Optional.of(entry.doacao);
    }

    public static void remove(int id) {
        logger.debug("Removendo doação {} do cache", id);
        cache.remove(id);
    }

    public static void clear() {
        logger.debug("Limpando cache de doações");
        cache.clear();
    }
}