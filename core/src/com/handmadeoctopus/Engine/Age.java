package com.handmadeoctopus.Engine;

// Age class saves year and every ball position. It lets you calculateAll simulation further

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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Age {

    public int buffer = 300;
    private int year, calculatedYear, difference, lastSpeed = 1, drawBuffer = 50;
    private final int MIN_BUFFER = 10, MAX_BUFFER = 3000, MIN_DRAWB = 0, MAX_DRAWB = 3000;
    private float averageFPS = 0;
    private int numberOfFrames = 0;
    public HistoryEntry drawYear;
    private Array<Ball> newYear, balls;
    private List<HistoryEntry> history, tempPath;
    private boolean sameFrame = false;
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
        if (drawYear != null) { drawYear.resetYear(); }
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        year = 0;
        calculatedYear = 0;
        drawYear = HistoryEntry.getNewYear(settings.getSetting(Settings.SettingsEnum.BALLSQUANTITY).getValue(), settings);
        drawYear.resetYear();
        history.add(year, drawYear);
        difference = buffer - (calculatedYear - year);
        sem.release();
        flush();
    }

    // Updates balls parameters
    public void updateBalls(SettingEntry settingEntry) {
        Settings.SettingsEnum settingId = settingEntry.getSettingId();
        switch(settingId) {
            case RESET: settings.resetDefaults(); break;
            case BALLSQUANTITY:
                int diff = settingEntry.getValue() - mainEngine.getSetting(settingId);
                if(diff > 0) {
                    for(int i = 0; i < diff; i++) {
                        drawYear.getBalls().add(new Ball(settings));
                    }
                } else if (diff < 0) {
                    drawYear.getBalls().setSize(settingEntry.getValue());
                }
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
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

   //     writer.println(drawYear.getYear());
        balls = drawYear.getBalls();
        for (int i = 0; i < balls.size; i++) {
            if (year > 1  ) {
                balls.get(i).drawTail(batch, history.subList(year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 < 1 ? 0 : year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 - 1, year), i);
            }
            if (year+drawBuffer > calculatedYear && tempPath != null) {
                balls.get(i).drawPath(batch, tempPath, i);
            } else {
                balls.get(i).drawPath(batch, history.subList(year, Math.min(year+drawBuffer, history.size())), i);
            }

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

    public void drawCurrentYear(ShapeRenderer batch) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //     writer.println(drawYear.getYear());
        balls = drawYear.getBalls();
        for (int i = 0; i < balls.size; i++) {
            if (year > 1 && settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue() > 0) {
                balls.get(i).drawTail(batch,
                        history.subList(year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2 < 1 ? 0 : year-settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2, year+1 > calculatedYear ? year-1 : year + 1), i);
            }
            if (year+drawBuffer > calculatedYear && tempPath != null) {
                balls.get(i).drawPath(batch, tempPath, i);
            } else {
                balls.get(i).drawPath(batch, history.subList(year, Math.min(year+drawBuffer, history.size())), i);
            }

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
            numberOfFrames = 0;
            averageFPS = 0;
        }
    }

    // Adds i years to calculate and load drawYear
    private void addYear(int years) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        year += years;

        if (year >= calculatedYear) {
            year = calculatedYear;
            drawYear = history.get(year);
        } else if (year < calculatedYear) {
            drawYear = history.get(year);
            drawYear.addYear(years);
        }


        sem.release();
        threadCalculate();
    }

    // Calculates new history.
    private void calculate() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newYear = HistoryEntry.clone(history.get(calculatedYear).getBalls());

        for (int i = 0; i < newYear.size; i++) {
            for (int j = i+1; j < newYear.size; j++) {
                newYear.get(i).act(newYear.get(j)).grow();
            }
            if (newYear.size == 1) { newYear.get(0).grow(); }
            if (newYear.size == i+1) { newYear.get(i).grow(); }
            newYear.get(i).move();
        }
     /*   newYear.sort(new Comparator<Ball>() {
            @Override
            public int compare(Ball o1, Ball o2) {
                return (int) (o2.getZ() - o1.getZ());
            }
        }); */

        calculatedYear++;
        if(calculatedYear >= year + drawBuffer) {
            history.subList(calculatedYear, history.size()).clear();
            tempPath = null;
        }
        history.add(calculatedYear, new HistoryEntry(newYear));

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
        int maxHistory = settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue()*2;
        if (year > maxHistory + 100) {
            int difference = year - maxHistory;
            history.subList(0, difference).clear();
           // for(int i = 0; i < difference; i++) { history.remove(0); }
            year -= difference;
            calculatedYear -= difference;
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
        drawYear.getBalls().removeValue(ball, true);
        sem.release();
        flush();
    }

    // Recalculates years from drawYear starting
    public void flush() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(year+drawBuffer < calculatedYear){
            tempPath = history.subList(year, year+drawBuffer);
            List<HistoryEntry> flushHistory = history.subList(year, year+drawBuffer);
            tempPath = new ArrayList<HistoryEntry>();
            for(HistoryEntry hist : flushHistory) {
                tempPath.add(new HistoryEntry(HistoryEntry.clone(hist.getBalls())));
            }
            if(calculatedYear > year) {
                calculatedYear = year;
                history.subList(year+1, history.size()).clear();
            }
        }

        sem.release();
    }

    // Action down handled
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
            mainEngine.newBall.setBallParameters(settings);
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

    // Returns drawYear
    public int currentYear() {
        return year;
    }

    // Returns buffer
    public int getBufferSize() {
        return calculatedYear - settings.ballsTail;
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
