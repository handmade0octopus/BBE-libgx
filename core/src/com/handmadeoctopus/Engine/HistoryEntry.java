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

    void resetYear() {
        nextYear = BigInteger.ZERO;
    }

    void addYear(int e) {
        synchronized(nextYear) {
            year = nextYear;
            nextYear = nextYear.add(BigInteger.valueOf(e));
        }
    }

    // Gets new year
    static HistoryEntry getNewYear(int size, Settings settings) {
        Array<Ball> newYear = new Array<Ball>();
        newYear.setSize(size);
        for(int i = 0; i < size; i++) {
            newYear.set(i, newRandomBall(settings));
        }
        return new HistoryEntry(newYear);
    }

    // Creates random new ball
    static Ball newRandomBall(Settings settings) {
        Ball ball = new Ball(settings);
        return ball;
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
