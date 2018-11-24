package com.handmadeoctopus.Engine;


import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;

import java.math.BigInteger;

public class HistoryEntry {
    final Array<Ball> balls;
    BigInteger year = BigInteger.ZERO;
    static BigInteger nextYear = BigInteger.ZERO;

    // Keeps current year of the ball list and keeps count of the year.
    public HistoryEntry (Array<Ball> array) {
        balls = array;
    }

    public BigInteger getYear() {
        synchronized(balls) {
            return year;
        }
    }

    public Array<Ball> getBalls() {
        return balls;
    }

    void resetYear() {
        nextYear = BigInteger.ZERO;
    }

    void setYear(BigInteger e) {
        year = e;
    }

    void addYear(int e) {
        synchronized(balls) {
            year = nextYear;
            nextYear = nextYear.add(BigInteger.valueOf(e));
        }
    }

    // Gets new year
    static HistoryEntry getNewYear(int size, Settings settings) {
        Array<Ball> newYear = new Array<Ball>();
        settings.setUniScale();
        newYear.setSize(size);
        for(int i = 0; i < size; i++) {
            newYear.set(i, new Ball(settings));
        }
        return new HistoryEntry(newYear);
    }

    // Clones Array<Ball> to creates new list with new balls
    public static Array<Ball> clone(Array<Ball> yearToCopy) {
        Array<Ball> newYear = new Array<Ball>();
        newYear.setSize(yearToCopy.size);
        for(int i = 0; i < yearToCopy.size; i++) {
            newYear.set(i, new Ball(yearToCopy.get(i)));
        }
        return newYear;
    }
}
