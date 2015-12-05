package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;

/**
 * do not store null values; null are considered as undefined,
 * that is, the map cannot distinguish when an element is null or
 * simply undefined
 */
public class VHashMap<K, T> extends HashMap<K,V<? extends T>> implements VMap<K,T> {
    public VHashMap(int i) {
        super(i);
    }

    public VHashMap() {
        super();
    }

    @Override
    public void put(FeatureExpr ctx, K key, @Nullable T value) {
        put(ctx, key, V.one(value));
    }

    @Override
    public void put(FeatureExpr ctx, K key, @NonNull V<? extends T> value) {
        V<? extends T> oldVal = this.getOrDefault(key, V.one(null));
        this.put(key, V.choice(ctx, value, oldVal));
    }

  /**
   * modify getOrDefault behavior that it provides the default also for
   * the undefined part of partially undefined entries
   */
    @Override
    public @NonNull V<? extends T> getOrDefault(Object key, @NonNull final V<? extends T> defaultValue) {
        return super.getOrDefault(key, defaultValue).<T>flatMap(v -> v == null ? defaultValue : V.one(v));
    }
}
