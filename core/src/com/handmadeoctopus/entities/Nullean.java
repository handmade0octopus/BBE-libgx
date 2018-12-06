package com.handmadeoctopus.entities;

/**
    Two purposes of this class. Makes ball null so it could be removed afterwards (so it doesn't show glitches)
    Also helps tell which ball is which as it's not copied but carried on to the next ball.
 */

class Nullean {
    private boolean beingNull = false;
    private boolean beingTrueNull = false;
    private int i = 0, becameNull = 0;

    public Nullean () {
    }

    void makeNull(int i) {
        beingNull = true;
        becameNull = i;
    }

    public void makeTrueNull(int i) {
        if(beingNull) {
            this.i += i;
            if (this.i > becameNull) {
                beingTrueNull = true;
            }

        }
    }

    boolean isNull() {
        return beingTrueNull;
    }

    boolean isBecomingNull() {
        return beingNull;
    }

    Nullean getNullean() {
        return this;
    }

    public void resetNullStatus() {
        beingNull = false;
        i = 0;
        becameNull = 0;
    }
}
