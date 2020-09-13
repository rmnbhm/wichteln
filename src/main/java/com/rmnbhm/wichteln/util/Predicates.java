package com.rmnbhm.wichteln.util;

import java.util.Objects;

public class Predicates {

    public static <T> boolean notEquals(T thisT, T otherT) {
        return !(Objects.equals(thisT, otherT));
    }
}
