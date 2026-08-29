package network.vonix.isleofberkperformance.internal;

import java.util.EnumMap;

/**
 * Reusable {@link EnumMap} for a single non-reentrant caller. Re-entrant or mismatched key
 * types allocate a fresh map so the in-use scratch is never aliased.
 */
public final class EnumMapScratch<K extends Enum<K>, V> {
    private final Class<K> keyType;
    private final EnumMap<K, V> map;
    private boolean inUse;

    public EnumMapScratch(Class<K> keyType) {
        this.keyType = keyType;
        this.map = new EnumMap<>(keyType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public EnumMap acquire(Class requestedKeyType) {
        if (requestedKeyType != this.keyType || this.inUse) {
            return new EnumMap(requestedKeyType);
        }
        this.inUse = true;
        this.map.clear();
        return this.map;
    }

    public void release() {
        this.inUse = false;
        this.map.clear();
    }
}
