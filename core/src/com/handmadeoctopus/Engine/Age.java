package com.handmadeoctopus.Engine;

/** Age class saves year and every ball position. It lets you calculateAll simulation further
 * I decided for approach with a list of objects "HistoryEntry", each holding past and future of simulation of each ball.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Age {

    private int buffer = 300, drawBuffer = 100;; // Base value of buffer and drawBuffer, it's got changed
    private int year, calculatedYear, difference, lastSpeed = 1; // Variables to control the current year and year that is calculated
    private final int MIN_BUFFER = 10, MAX_BUFFER = 15000, MIN_DRAWB = 0, MAX_DRAWB = 10000; // Constants to control maximum and minimum buffer values
    private float averageFPS = 0;
    private int numberOfFrames = 0;
    public HistoryEntry drawYear; // drawYear is current year that is being drawn
    private Array<Ball> newYear, balls; // They hold temp balls
    private List<HistoryEntry> history, tempPath = null; // History is main list of HistoryEntries - meaning years of calculation, tempPath is not lazy programing (!!!), it's actually used only after input change to smooth out the visual.
    // Just placeholders.
    Settings settings;
    MainEngine mainEngine;
    ThreadPoolExecutor calculateThread;
    Runnable run;
    Semaphore sem;
    PrintWriter writer;

    // Main constructor
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

        history = new ArrayList<HistoryEntry>();
        sem = new Semaphore(1, true);
        run = new TaskRun(this);
        calculateThread = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        settings.setAge(this);
        reload();
        calculateThread.submit(run);
    }

    // Reloads game with new age
    public void reload() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (drawYear != null) { drawYear.resetYear(); }
        year = 0;
        calculatedYear = 0;
        drawYear = HistoryEntry.getNewYear(settings.getSetting(Settings.SettingsEnum.BALLSQUANTITY).getValue(), settings);
        drawYear.resetYear();
        tempPath = null;
        history.clear();
        history.add(year, drawYear);
        difference = buffer - (calculatedYear - year);
        sem.release();
        flush();
    }

    // Updates balls parameters
    public void updateBalls(SettingEntry settingEntry) {

        Settings.SettingsEnum settingId = settingEntry.getSettingId();
        // Case switch for every settings entry and how each settings affect game engine.
        switch(settingId) {
            case RESET: settings.resetDefaults(); break;
            case BALLSQUANTITY:
                try {
                    sem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int diff = settingEntry.getValue() - mainEngine.getSetting(settingId);
                if(diff > 0) {
                    for(int i = 0; i < diff; i++) {
                        drawYear.getBalls().add(new Ball(settings));
                    }
                } else if (diff < 0) {
                    int start = settingEntry.getValue();
                    if (start < 0) { start = 0; }
                    if (drawYear.getBalls().size-1 >= 0 && drawYear.getBalls().size-1 > start) {
                        drawYear.getBalls().removeRange(start, drawYear.getBalls().size-1);
                    }
                }
                sem.release();
                break;
            case BALLSSIZE:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    float newSize = settingEntry.getValue();
                    float oldSize = mainEngine.getSetting(settingId);
                    float changeSize = newSize/oldSize;
                    for(Ball ball : drawYear.getBalls()) {
                        ball.radius *= changeSize;
                        ball.updateMass();
                    }
                }

                break;
            case BALLSTAIL:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.tail = (settingEntry.getValue());
                    }
                }
                break;
            case MAXPATH:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.path = (settingEntry.getValue());
                    }
                }
                break;
            case SPRINGINESS:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.springiness = settingEntry.getValue()/100f;
                    }
                }
                break;
            case GRAVITY:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.gravity = settingEntry.getValue()/1000f;
                        ball.gravitation = settingEntry.getValueBool();
                    }
                }
                break;
            case FORCES:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.force = settingEntry.getValue()/10000f;
                        ball.forces = settingEntry.getValueBool();
                    }
                }
                break;
            case IMPACT:
                if(settingEntry.getValue() != mainEngine.getSetting(settingId)) {
                    for(Ball ball : drawYear.getBalls()) {
                        ball.impact = settingEntry.getValue();
                    }
                }
                break;

            case SPEED:
                break;
            case UNISCALE:
                if (mainEngine.getSetting(settingId) != settingEntry.getValue()) {
                    settings.universeScale = settingEntry.getValue();
                    settings.setUniScale();
                }
                break;
            case RELOAD:
                if(mainEngine != null) { mainEngine.reload(); }
                break;
        }
        if(settingEntry.getSettingId().flush) { flush(); }
    }

    // Draws all balls and performs actions on them BATCH
    public void drawCurrentYear(SpriteBatch batch) {
        // Semaphore used so thread of calculation won't cause issues with drawing.
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //     writer.println(drawYear.getYear());

        // Gets current year of balls and renders tail and path of them.
        balls = drawYear.getBalls();



        for (int i = 0; i < balls.size; i++) {
            if (year > 1 && settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue() > 0) {
                balls.get(i).drawTail(batch,
                        history.subList(year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 < 1 ? 0 : year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2, year+1 > calculatedYear ? year-1 : year + 1), i);
            }
            if (year+drawBuffer > calculatedYear && tempPath != null) {
                balls.get(i).drawPath(batch, tempPath.subList(year, Math.min(tempPath.size(), year + settings.getSetting(Settings.SettingsEnum.MAXPATH).getValue())), i);
            } else if (history.size() > year) {
                balls.get(i).drawPath(batch, history.subList(year, Math.min(calculatedYear, year + settings.getSetting(Settings.SettingsEnum.MAXPATH).getValue())), i);
            }

            // Finally renders balls from current year
            balls.get(i).draw(batch);
        }

        adjustBuffer();
        sem.release();
        if(mainEngine.newBall != null) {
            mainEngine.newBall.grow();
            mainEngine.newBall.draw(batch);
        }
        addYear(year + buffer < calculatedYear ? 0 : settings.getSetting(Settings.SettingsEnum.SPEED).getValue());
    }

    // Rendered if we don't want to use texture
    public void drawCurrentYear(ShapeRenderer renderer) {
        // Semaphore used so thread of calculation won't cause issues with drawing.
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //     writer.println(drawYear.getYear());

        // Gets current year of balls and renders tail and path of them.
        balls = drawYear.getBalls();

        // Dispose of old null balls

        for (HistoryEntry hist : history.subList(year+1, history.size())) {
            for(int i = 0; i < hist.getBalls().size; i++) {
                hist.getBalls().removeValue(hist.getBalls().get(i).isNull(), true);
            }
        }
    /*    if (tempPath != null) {
            for (HistoryEntry hist : tempPath.subList(year+1, history.size())) {
                for(int i = 0; i < hist.getBalls().size; i++) {
                    hist.getBalls().removeValue(hist.getBalls().get(i).isNull(), true);
                }
            }
        }*/


        for (int i = 0; i < balls.size; i++) {
            if (year > 1 && settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue() > 0) {
                balls.get(i).drawTail(renderer,
                        history.subList(year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 < 1 ? 0 : year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2, year+1 > calculatedYear ? year-1 : year + 1), i);
            }
            if (year+drawBuffer > calculatedYear && tempPath != null && tempPath.size() > year) {
                balls.get(i).drawPath(renderer, tempPath.subList(year, Math.min(tempPath.size(), year + settings.getSetting(Settings.SettingsEnum.MAXPATH).getValue())), i);
            } else {
                balls.get(i).drawPath(renderer, history.subList(year, Math.min(calculatedYear, year + settings.getSetting(Settings.SettingsEnum.MAXPATH).getValue())), i);
            }

            // Finally renders balls from current year
            balls.get(i).draw(renderer);
        }
        adjustBuffer();

        sem.release();
        if(mainEngine.newBall != null) {
            mainEngine.newBall.grow();
            mainEngine.newBall.draw(renderer);
        }
        addYear(year + buffer < calculatedYear ? 0 : settings.getSetting(Settings.SettingsEnum.SPEED).getValue());
    }

    // Adjust buffer if too big or too small
    private void adjustBuffer() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        averageFPS += deltaTime;
        numberOfFrames++;
        if(numberOfFrames > 1/deltaTime) {
            averageFPS /= numberOfFrames;
            if (averageFPS > 0.025f && buffer > Math.min(MIN_BUFFER, 2500f/(drawYear.getBalls().size))) {
                if(averageFPS > 0.05f) {
                    buffer -= Math.max(buffer / 5, 1);
                } else {
                    buffer--;
                }
                if(buffer < drawBuffer) { buffer = drawBuffer+1; }
                if(year+ buffer < calculatedYear) {
                    calculatedYear = year+ buffer;
                    history.subList(calculatedYear+1, history.size()).clear();
                }
            } else if (averageFPS < 0.02f && buffer < Math.min(MAX_BUFFER, (250000f/(drawYear.getBalls().size)))) {
                buffer += 1/deltaTime;
            }

            if (averageFPS > 0.025f && drawBuffer > Math.min(MIN_DRAWB, 2500f/(drawYear.getBalls().size))) {
                if(averageFPS > 0.05f) {
                    drawBuffer -= Math.max(drawBuffer / 5, 1);
                } else {
                    drawBuffer--;
                }
            } else if (averageFPS < 0.02f && drawBuffer < Math.min(MAX_DRAWB, (250000f/(drawYear.getBalls().size)))) {
                drawBuffer += 1/deltaTime;
            }
            if(buffer < drawBuffer) { buffer = drawBuffer+1; }
            numberOfFrames = 0;
            averageFPS = 0;
        }
    }

    // Adds i years to calculate and load drawYear. Numbers of years to add is based on SPEED
    private void addYear(int years) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int yearDiff = calculatedYear - year;
        year += years;

        if (year >= calculatedYear) {
            drawYear.addYear(yearDiff);
            year = calculatedYear;
            drawYear = history.get(year);
            for(Ball ball : drawYear.getBalls()) {
                ball.makeTrueNull(yearDiff);
            }
        } else {
            drawYear = history.get(year);
            drawYear.addYear(years);
            for(Ball ball : drawYear.getBalls()) {
                ball.makeTrueNull(years);
            }
        }


        sem.release();
        threadCalculate();
    }

    // Called multiple times from TaskRun
    public void calculateAll() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        difference = buffer - (calculatedYear - year);
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
            difference = buffer - (calculatedYear - year);
            sem.release();
            calculate();
        }
    }

    // Calculates new history.
    private void calculate() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newYear = HistoryEntry.clone(history.get(calculatedYear).getBalls());
        List<Ball> toRemove = new ArrayList<Ball>();

        // After cloning last calculated year, newYear is then calculated, each ball acts with each other
        for (int i = 0; i < newYear.size; i++) {
            for (int j = i+1; j < newYear.size; j++) {
                toRemove.add(newYear.get(i).act(newYear.get(j)));
            }
            if (newYear.size == 1) { newYear.get(0).grow(); }
            if (newYear.size == i+1) { newYear.get(i).grow(); }
            newYear.get(i).move();
        }


        for(Ball remove : toRemove) {
            removeBallInCalc(remove);
        }

        // To prevent memory flood, we only allow to calculate this much further
        calculatedYear++;
        if(calculatedYear > year + drawBuffer) {
            history.subList(calculatedYear, history.size()).clear();
            tempPath = null;
        }

        // tempPath is refreshed if calculatedYear caches up
        if (tempPath != null && calculatedYear >  drawBuffer ) {
            tempPath = null;
        }
        history.add(calculatedYear, new HistoryEntry(newYear));

        // Adjust tempPath for smooth change in path
        if(tempPath!=null && calculatedYear < tempPath.size()) {
            tempPath.set(calculatedYear, history.get(calculatedYear));
        } else if (tempPath!=null) {
            tempPath.add(calculatedYear, history.get(calculatedYear));
        }

        sem.release();

        eraseHistory();
    }

    // Erases history so it doesn't take much space.
    private void eraseHistory() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (calculatedYear < history.size() && calculatedYear >= year+drawBuffer) {
            history.subList(calculatedYear+1, history.size()).clear();
        }
        int maxHistory = settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 + 10;
        if (year > maxHistory) {
            int difference = year - maxHistory;
            history.subList(0, difference).clear();
            if (tempPath != null) { tempPath.subList(0, difference).clear(); }
           // for(int i = 0; i < difference; i++) { history.remove(0); }
            year -= difference;
            calculatedYear -= difference;
        }
        sem.release();
        threadCalculate();
    }

    // Refresh .wait() from calculateAll function
    public void threadCalculate() {
        synchronized(this) {
            this.notify();
        }
    }

    // Adds ball to the current year
    public void addBall(Ball ball) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
           drawYear.getBalls().add(ball);
        sem.release();
        flush();
    }

    // Removes ball from drawYear
    public void removeBall(Ball ball) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ball != null) {
            drawYear.getBalls().removeValue(ball, true);
        }
        sem.release();
        if (ball != null) {
            flush();
        }
    }

    // Removes ball from drawYear without semaphore
    public void removeBallInCalc(Ball ball) {
        if (ball != null) {
      //    newYear.removeValue(ball, true);
            newYear.set(newYear.indexOf(ball, true), new Ball(ball).nullBall(calculatedYear-year));
        }
    }

    // Recalculates years from drawYear starting
    public void flush() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // tempPath is for smooth path when changing the scene
        if(year < calculatedYear && tempPath == null){
            List<HistoryEntry> flushHistory = history.subList(0, Math.min(calculatedYear, drawBuffer+year));
                tempPath = new ArrayList<HistoryEntry>();
                for(HistoryEntry hist : flushHistory) {
                    tempPath.add(new HistoryEntry(HistoryEntry.clone(hist.getBalls())));
            }
        } else if (year < calculatedYear) {
            List<HistoryEntry> flushHistory = history.subList(0, Math.min(calculatedYear, drawBuffer+year));
            for(int i =0; i < flushHistory.size(); i++) {
                if(i < tempPath.size()){
                    tempPath.set(i, new HistoryEntry(HistoryEntry.clone(flushHistory.get(i).getBalls())));
                } else {
                    tempPath.add(i, new HistoryEntry(HistoryEntry.clone(flushHistory.get(i).getBalls())));
                }
            }
        }

        if(calculatedYear > year) {
            calculatedYear = year;
            history.subList(year+1, history.size()).clear();
        }

        sem.release();
    }


    // Returns drawYear
    public int currentYear() {
        return year;
    }

    // Returns buffer
    public int getBufferSize() {
        return calculatedYear - settings.ballsTail;
    }

    // Action down, up and button press handled by the engine
    public boolean actionDown(float x, float y) {
        for (Ball ball : drawYear.getBalls()) {
            if (mainEngine.newBall == null) {
                mainEngine.newBall = ball.clicked(x, y);
                if (mainEngine.newBall != null) {
                    if (settings.getSetting(Settings.SettingsEnum.SPEED).getValue() !=0) {
                        removeBall(mainEngine.newBall);
                    }
                    mainEngine.newBall.setPosition(x, y);
                    mainEngine.handlingBall = true;
                }
            }
        }

        if (mainEngine.newBall == null) {
            mainEngine.newBall = new Ball(1*settings.zoom.camera.zoom, x, y, settings);
            mainEngine.newBall.startGrowing();
            if(settings.getSetting(Settings.SettingsEnum.SPEED).getValue() == 0) { addBall(mainEngine.newBall); }
        }

        if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            removeBall(mainEngine.newBall);
            mainEngine.newBall = null;
            return false;
        }

        flush();
        return true;
    }

    public void actionUp(float x, float y) {
        mainEngine.newBall.stopGrowing();
        mainEngine.newBall.setSpeedByPosition(x, y);
        if(settings.getSetting(Settings.SettingsEnum.SPEED).getValue() != 0) { addBall(mainEngine.newBall); }
        mainEngine.newBall = null;
        mainEngine.handlingBall = false;
    }

    public void buttonDown(int keycode) {
        if (keycode  == Input.Keys.SPACE && settings.getSetting(Settings.SettingsEnum.SPEED).getValue()  == 0) {
            settings.getSetting(Settings.SettingsEnum.SPEED).setValue(lastSpeed);
        } else if (keycode  == Input.Keys.SPACE) {
            lastSpeed = Math.max(1, settings.getSetting(Settings.SettingsEnum.SPEED).getValue());
            settings.getSetting(Settings.SettingsEnum.SPEED).setValue(0);
        }
    }
}
