package com.handmadeoctopus.Engine;

// Age class saves year and every ball position. It lets you calculateAll simulation further

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Age {

    public int bufor = 300;
    private int year, calculatedYear, difference, fps;
    public HistoryEntry currentYear;
    private Array<Ball> newYear, balls;
    private LinkedList<HistoryEntry> history;
    private boolean sameFrame = false;
    Settings settings;
    MainEngine mainEngine;
    ThreadPoolExecutor calculateThread;
    Runnable run;
    Semaphore sem;
    PrintWriter writer;

    public Age (Settings settings, MainEngine mainEngine) {
        this.settings = settings;
        this.mainEngine = mainEngine;

        try {
            writer = new PrintWriter("dd.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        history = new LinkedList<HistoryEntry>();
        sem = new Semaphore(1, true);
        run = new TaskRun(this);
        calculateThread = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        settings.setAge(this);
        reload();
    }

    // Reloads game with new age
    public void reload() {
        year = 0;
        calculatedYear = 0;
        if (currentYear != null) { currentYear.resetYear(); }
        flush();
        currentYear = getNewYear();
        currentYear.resetYear();
        history.add(year, currentYear);
        difference = bufor - (calculatedYear - year);
        calculateThread.submit(run);
    }

    // Creates random new ball
    private Ball newRandomBall() {
        Ball ball = new Ball(settings);
        return ball;
    }

    public boolean updateBalls(Settings.SettingsEnum settingsEnum) {
        switch(settingsEnum) {
            case BALLSQUANTITY:
                int diff = settings.ballsQuantity - mainEngine.ballsQuantity;
                if(diff >= 0) {
                    for(int i = 0; i < diff; i++) {
                        currentYear.getBalls().add(newRandomBall());
                    }
                } else {
                    currentYear.getBalls().setSize(settings.ballsQuantity);
                }
                break;
            case BALLSSIZE:
                float newSize = settings.ballsSize;
                float oldSize = mainEngine.ballsSize;
                float changeSize = newSize/oldSize;
                for(Ball ball : currentYear.getBalls()) {
                    ball.radius *= changeSize;
                    ball.updateMass();
                }
                break;
            case BALLSTAIL:
                for(Ball ball : currentYear.getBalls()) {
                    ball.tail = (settings.ballsTail);
                }
                break;
            case SPRINGINESS:
                for(Ball ball : currentYear.getBalls()) {
                    ball.springiness = settings.springiness/100f;
                }
                break;
            case GRAVITY:
                for(Ball ball : currentYear.getBalls()) {
                    ball.gravity = settings.gravity/1000f;
                    ball.gravitation = settings.gravitation;
                }
                break;
            case FORCES:
                for(Ball ball : currentYear.getBalls()) {
                    ball.force = settings.forces/10000f;
                    ball.forces = settings.ballsForces;
                }
                break;
            case SPEED:
                return false;
        }
        flush();
        return true;
    }

    // Draws all balls and performs actions on them BATCH
    public void drawCurrentYear(SpriteBatch batch) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

   //     writer.println(currentYear.getYear());
        balls = currentYear.getBalls();
        for (int i = 0; i < balls.size; i++) {
            if (year > 1 && settings.ballsTail > 0) {
                balls.get(i).drawTail(batch, history.subList(0, year - 1), i);
            }
            balls.get(i).drawPath(batch, history.subList(year, calculatedYear), i);
            balls.get(i).draw(batch);
        }
        adjustBufor();
        sem.release();
        if(mainEngine.newBall != null) {
            mainEngine.newBall.grow();
            mainEngine.newBall.draw(batch);
        }
        addYear(year + bufor < calculatedYear ? 0 : settings.speed);
        //addYear(settings.speed == 0 ? 0 : (calculatedYear - year >= bufor ? settings.speed : 1));
    }

    private void adjustBufor() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (deltaTime > 0.025f && bufor > Math.min(50, 2500f/(currentYear.getBalls().size))) {
            if(deltaTime > 0.05f) {
                bufor -= bufor/10;
            } else {
                bufor--;
            }
            if(year+bufor < calculatedYear) {
                calculatedYear = year+bufor;
                history.subList(calculatedYear+1, history.size()).clear();
            }
        } else if (deltaTime < 0.02f && bufor < Math.min(2000, (25000f/(currentYear.getBalls().size)))) {
            bufor += 1;
        }
    }

    private void addYear(int i) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int e = 0;
        for(int d = 0; d < i; d++) {
            year++;
            e++;
        }

        if (year >= calculatedYear) {
            year = calculatedYear;
            currentYear = history.get(year);
        } else if (year < calculatedYear) {
            currentYear = history.get(year);
            currentYear.addYear(e);
        }


        sem.release();
        threadCalculate();
    }

    // Calculates history
    private void calculate() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newYear = clone(history.get(calculatedYear).getBalls());

        for (int i = 0; i < newYear.size; i++) {
            if (newYear.size == 1) { newYear.get(0).act(null); }
            for (int j = i+1; j < newYear.size; j++) {
                newYear.get(i).act(newYear.get(j));
            }
            if (newYear.size == i+1) { newYear.get(i).act(null); }
            newYear.get(i).move();
        }


        calculatedYear++;
        history.add(calculatedYear, new HistoryEntry(newYear));
        sem.release();


        threadCalculate();
        eraseHistory();
    }

    private void eraseHistory() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int maxHistory = settings.ballsTail*2;
        if (year > maxHistory) {
            int difference = year - maxHistory;
            history.subList(0, difference).clear();
            year -= difference;
            calculatedYear -= difference;
        }
        sem.release();
        threadCalculate();
    }

    public void calculateAll() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        difference = bufor - (calculatedYear - year);
        sem.release();
        if (difference <= 0) {
            try {
                synchronized(this){
                    this.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            difference = bufor - (calculatedYear - year);
            sem.release();
            calculate();
        }
    }

    public void threadCalculate() {
        synchronized(this) {
            this.notify();
        }
    }


    // Clones Array<Ball> to creates new list with new balls
    private Array<Ball> clone(Array<Ball> yearToCopy) {
        Array<Ball> newYear = new Array<Ball>();
        newYear.setSize(yearToCopy.size);
        for(int i = 0; i < yearToCopy.size; i++) {
            newYear.set(i, new Ball(yearToCopy.get(i)));
        }
        return newYear;
    }

    // Gets new Array<Ball> of year
    public HistoryEntry getNewYear() {
        Array<Ball> newYear = new Array<Ball>();
        newYear.setSize(settings.ballsQuantity);
        for(int i = 0; i < settings.ballsQuantity; i++) {
            newYear.set(i, newRandomBall());
        }
        return new HistoryEntry(newYear);
    }

    // Adds ball to the current year
    public void addBall(Ball ball) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        currentYear.getBalls().add(ball);
        settings.ballsQuantity++;
        sem.release();
        flush();
    }

    public void removeBall(Ball ball) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        currentYear.getBalls().removeValue(ball, true);
        sem.release();
        flush();
    }

    public void flush() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(calculatedYear > year) {
            calculatedYear = year;
            history.subList(year+1, history.size()).clear();
        }
        sem.release();
    }

    // Action down handled
    public boolean actionDown(float x, float y) {
        for (Ball ball : currentYear.getBalls()) {
            if (mainEngine.newBall == null) {
                mainEngine.newBall = ball.clicked(x, y);
                if (mainEngine.newBall != null) {
                    if (settings.speed !=0) {
                        removeBall(mainEngine.newBall);
                    }
                    mainEngine.newBall.setPosition(x, y);
                    mainEngine.handlingBall = true;
                }
            }
        }

        if (mainEngine.newBall == null) {
            mainEngine.newBall = new Ball(1*settings.zoom.camera.zoom, x, y, settings);
            mainEngine.newBall.setPosition(x,y);
            mainEngine.newBall.setBallParameters(settings);
            mainEngine.newBall.startGrowing();
            if(settings.speed == 0) { addBall(mainEngine.newBall); }
        }

        if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            removeBall(mainEngine.newBall);
            mainEngine.newBall = null;
            return false;
        }

      //  flush();
        return true;
    }

    public void actionUp(float x, float y) {
        mainEngine.newBall.stopGrowing();
        mainEngine.newBall.setSpeedByPosition(x, y);
        if(settings.speed != 0) { addBall(mainEngine.newBall); }
        mainEngine.newBall = null;
        mainEngine.handlingBall = false;
    }

    public int currentYear() {
        return year;
    }

    public int calculatedYear() {
        return calculatedYear - settings.ballsTail;
    }


}
