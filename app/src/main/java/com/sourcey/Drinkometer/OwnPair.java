package com.sourcey.Drinkometer;

import java.io.Serializable;

public class OwnPair<A, B> implements Serializable {
    private A frist;
    private B second;

    public OwnPair(A frist, B second) {
        this.frist = frist;
        this.second = second;
    }

    public A getFrist() {
        return frist;
    }

    public B getSecond() {
        return second;
    }

}
