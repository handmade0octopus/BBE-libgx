package com.handmadeoctopus.entities;

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

    Nullean getNullean() {
        return this;
    }

}
