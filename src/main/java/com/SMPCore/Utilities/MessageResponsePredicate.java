package com.SMPCore.Utilities;

public interface MessageResponsePredicate<T> {

    String message(T test);

    default boolean test(T test) {
        return message(test) == null;
    }

}
