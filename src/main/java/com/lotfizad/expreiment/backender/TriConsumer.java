package com.lotfizad.expreiment.backender;


@FunctionalInterface
public interface TriConsumer<T, U, V,W> {
    public W accept(T t, U u, V v);
}