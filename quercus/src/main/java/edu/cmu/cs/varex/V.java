package edu.cmu.cs.varex;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * central abstraction for conditional/variational values
 * @param <T>
 */
public interface V<T> {

    @Deprecated
    T getOne();


    <U> V<U> map(Function<T,U> fun);
    <U> V<U> flatMap(Function<T,V<U>> fun);
    void foreach(Consumer<T> fun);
}
