package com.handmadeoctopus.Engine;


import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;

import java.math.BigInteger;

public class HistoryEntry {
    final Array<Ball> balls;
    BigInteger year = BigInteger.ZERO;
    static BigInteger nextYear = BigInteger.ZERO;

    public HistoryEntry (Array<Ball> array) {
        balls = array;
    }

    public String getYear() {
        synchronized(year) {
            return year.toString();
        }
    }

    public Array<Ball> getBalls() {
        return balls;
    }

    public void resetYear() {
        nextYear = BigInteger.ZERO;
    }

    public void addYear(int e) {
        synchronized(nextYear) {
            year = nextYear;
            nextYear = nextYear.add(BigInteger.valueOf(e));
        }
    }
}
