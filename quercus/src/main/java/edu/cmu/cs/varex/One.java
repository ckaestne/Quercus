package edu.cmu.cs.varex;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by ckaestne on 11/27/2015.
 */
public class One<T> implements V<T> {
    final T value;

    public One(T v) {
        this.value = v;
    }

    @Override
    public String toString() {
        return "Value(" + value + ")";
    }

    @Override
    public T getOne() {
        return value;
    }

    @Override
    public <U> V<U> map(Function<T, U> fun) {
        return new One(fun.apply(value));
    }

    @Override
    public <U> V<U> flatMap(Function<T, V<U>> fun) {
        return fun.apply(value);
    }

    @Override
    public void foreach(Consumer<T> fun) {
        fun.accept(value);
    }
}
