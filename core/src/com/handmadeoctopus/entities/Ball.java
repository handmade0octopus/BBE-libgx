package com.handmadeoctopus.entities;

// Ball class which contains all ball variables and movement

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.Engine.HistoryEntry;
import com.handmadeoctopus.Engine.MainEngine;
import com.handmadeoctopus.Engine.Settings;
import org.jblas.FloatMatrix;
import org.jblas.Geometry;

import java.util.List;
import java.util.Random;

public class Ball {
    // Position, size and speed of ball//
    Position position;
    public float radius, mass, x, y, speedX, speedY, speedZ, xMin, xMax, yMin, yMax, zMin, zMax, x1, y1, projection;
    public float gravity = 0, force = 0, springiness = 1, speed = 6, massScale = 1;
    public Texture texture = MainEngine.TEXTURE;
    public Boolean grow = false, gravitation = true, hits = true, forces = true, touchable = false;
    Settings settings;
    Box box;
    Random rnd = new Random();
    Color clr;

    // Tail
    public int tail = 0;

    // Constructor with random values of color and speed
    public Ball(float radius, float x, float y, Settings settings) {
        this.settings = settings;
        randomSpeedXY();
        randomColour();
        set(radius, x, y, 0);
    }


    public Ball (Settings settings) {
        this.settings = settings;
        setBallParameters(settings.getSetting(Settings.SettingsEnum.GRAVITY).getValue(),
                settings.getSetting(Settings.SettingsEnum.SPRINGINESS).getValue(),
                settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue(),
                settings.getSetting(Settings.SettingsEnum.FORCES).getValue(),
                settings.getSetting(Settings.SettingsEnum.SPEED).getValue(),
                settings.getSetting(Settings.SettingsEnum.GRAVITY).getValueBool(),
                settings.getSetting(Settings.SettingsEnum.FORCES).getValueBool(),
                settings.box);
        float x = rnd.nextFloat()*box.width + box.xMin, y= rnd.nextFloat()*box.height + box.yMin, z= rnd.nextFloat()*box.depth + box.zMin;
        while(x < box.xMin && x > box.xMax) {
            x = rnd.nextFloat()*box.width;
        }
        while(y < box.yMin && y > box.yMax) {
            y = rnd.nextFloat()*box.height;
        }
        randomSpeedXY();
        randomColour();
        set(settings.getSetting(Settings.SettingsEnum.BALLSSIZE).getValue(), x, y, z);

    }

    public Ball (Ball ball) {
        radius = ball.radius;
        mass = ball.mass;
        this.settings = ball.settings;
        x = ball.x;
        y = ball.y;
        position = new Position(ball.position);
        speedX = ball.speedX;
        speedY = ball.speedY;
        speedZ = ball.speedZ;
        xMin = ball.xMin;
        xMax = ball.xMax;
        yMin = ball.yMin;
        yMax = ball.yMax;
        gravity = ball.gravity;
        force = ball.force;
        springiness = ball.springiness;
        texture = ball.texture;
        speed = ball.speed;
        grow = ball.grow;
        gravitation = ball.gravitation;
        hits = ball.hits;
        forces = ball.forces;
        touchable = ball.touchable;
        box = ball.box;
        clr = ball.clr;
        tail = ball.tail;

    }


    // Changes colour to random
    private void randomColour() {
        clr = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 0.9f);
    }

    // Changes speed to random
    private void randomSpeedXY() {
        float sX = 0,  sY = 0, sZ = 0;

        while (sX == 0 && sY == 0) {
            sX = rnd.nextFloat() * speed - speed/2f;
            sY = rnd.nextFloat() * speed - speed/2f;
            sZ = rnd.nextFloat() * speed - speed/2f;
        }
        speedX = sX;
        speedY = sY;
        speedZ = sZ;
    }

    // Sets all ball values
    public void set(float radius, float x, float y, float z, float speedX, float speedY, float speedZ, Color clr) {
        this.radius = radius;
        updateMass();
        this.position = new Position(x, y, z);
        this.x = x;
        this.y = y;
        this.clr = clr;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        touchable = true;
    }

    // Sets radius, x and y
    public void set(float radius, float x, float y, float z) {
        set(radius, x, y, z, speedX, speedY, speedZ, clr);
    }

    // Sets other ball parameters
    public void setBallParameters(int gravity, int spring, int tail, int force, int speed, boolean gravitation, boolean forces, Box box) {
        this.force = force;
        this.box = box;
        this.gravitation = gravitation;
        this.forces = forces;

        xMax = box.xMax;
        yMax = box.yMax;
        xMin = box.xMin;
        yMin = box.yMin;

        if (gravity > Settings.MIN_GRAVITY && gravity <= Settings.MAX_GRAVITY) {
            this.gravity = gravity/1000f;
            this.gravitation = true;
        }
        else {
            this.gravity = 0;
            this.gravitation = false;
        }

        if (spring > Settings.MIN_SPRINGINESS && spring <= Settings.MAX_SPRINGINESS) { this.springiness = spring/100f; }
        else { this.springiness = 1; }

        if (force > Settings.MIN_FORCES && gravity <= Settings.MAX_FORCES) {
            this.force = force/10000f;
            this.forces = true;
        } else {
            this.force = 0;
            this.forces = false;
        }

        this.tail = tail;

        if (speed >= Settings.MIN_SPEED && speed <= Settings.MAX_SPEED) {
            this.speed = 6;
            randomSpeedXY();
        } else { this.speed = 6; }
    }

    public void setBallParameters(Settings settings) {
        this.settings = settings;
        setBallParameters(settings.gravity, settings.springiness, settings.ballsTail, settings.forces, settings.speed, settings.gravitation, settings.ballsForces, settings.box);

    }

    // Moves ball
    public Ball move() {
        gravity();
        position.x += speedX;
        position.y += speedY;
        position.z += speedZ;
        return this;
    }

    // Slows ball if you need to slow it only a little, higher number = less slow, 1 = no change
    void slow(float less) {
        if (less <= 0) { less = 1; }
        speedY *= springiness /less;
        speedX *= springiness /less;
        speedZ *= springiness /less;
        if (Math.abs(speedX) < 0.01) { speedX = 0; }
        if (Math.abs(speedY) < 0.01) { speedY = 0; }
        if (Math.abs(speedZ) < 0.01) { speedZ = 0; }
    }

    // Slows ball after hit
    void slow() {
        slow(1);
    }

    // Checks if ball is hit
    public void hit(Ball otherBall) {
        // Boundaries
        xMax = box.xMax;
        yMax = box.yMax;
        xMin = box.xMin;
        yMin = box.yMin;
        zMin = box.zMin;
        zMax = box.zMax;

        // When ball collides with box
        if (position.x + radius + speedX > xMax || position.x - radius + speedX < xMin) {
            speedX = -springiness * speedX;
            if (position.x - radius < xMin) { position.x = xMin + radius; }
            else if (position.x + radius > xMax) { position.x = xMax - radius; }
        }
        if (position.y + radius + speedY > yMax || position.y - radius + speedY < yMin) {
            speedY = -springiness * speedY;
            if (position.y - radius < yMin) { position.y = yMin + radius; }
            else if (position.y + radius + speedY > yMax) { position.y = yMax - radius; }
        }

        if (position.z + radius + speedZ > zMax || position.z - radius + speedZ < zMin) {
            speedZ = -springiness * speedZ;
            if (position.z - radius < zMin) { position.z = zMin + radius; }
            else if (position.z + radius + speedZ > zMax) { position.z = zMax - radius; }
        }

        // Balls collide with each other
        if (otherBall != null) {
            double totalRadius = Math.pow(radius + otherBall.radius, 2);
            double distSpeed = Math.pow( (position.x + speedX )
                    - (otherBall.position.x + otherBall.speedX), 2)
                    + Math.pow( (position.y + speedY)
                    - (otherBall.position.y + otherBall.speedY), 2);
            double distance = Math.pow( (this.position.x - (otherBall.position.x) ), 2.0) + Math.pow( (this.position.y) - (otherBall.position.y ), 2.0);

            if(hits && totalRadius >= distSpeed) {
                FloatMatrix n = new FloatMatrix(new float[][] {{position.x - otherBall.position.x, position.y - otherBall.position.y}});
                FloatMatrix v1 = new FloatMatrix(new float[][] {{speedX, speedY}});
                FloatMatrix v2 = new FloatMatrix(new float[][] {{otherBall.speedX, otherBall.speedY}});
                Geometry.normalize(n);
                float a1 = v1.dot(n);
                float a2 = v2.dot(n);
                float optimizedP = (float) (2.0 * (a1 - a2)) / (mass + otherBall.mass);

                FloatMatrix v1p = v1.sub(n.mul(optimizedP*otherBall.mass));
                FloatMatrix v2p = v2.add(n.mul(optimizedP*mass));

                speedX = v1p.get(0);
                speedY = v1p.get(1);
                otherBall.speedX = v2p.get(0);
                otherBall.speedY = v2p.get(1);

                if (totalRadius >= distance) {
                    float move = 1f;
                    if ((this.position.x) - (otherBall.position.x) > 0) {
                        this.position.x += move;
                        otherBall.position.x -= move;
                    } else if ((this.position.x) - (otherBall.position.x) < 0) {
                        this.position.x -= move;
                        otherBall.position.x += move;
                    }
                    if ((this.position.y) - (otherBall.position.y) > 0) {
                        this.position.y += move;
                        otherBall.position.y -= move;
                    } else if ((this.position.y) - (otherBall.position.y) < 0) {
                        this.position.y -= move;
                        otherBall.position.y += move;
                    }
                }
                slow();
                otherBall.slow();
            }

            if (forces && totalRadius + 1 < distance) {
                FloatMatrix n = new FloatMatrix(new float[][]{{position.x - otherBall.position.x, position.y - otherBall.position.y}});
                Geometry.normalize(n);
                double force =  ((mass * otherBall.mass)) / (distance);
                FloatMatrix a1 = n.mul((float) (-1.0*(force / mass)));
                FloatMatrix a2 = n.mul((float) (force / otherBall.mass));
                speedX += this.force*(a1.get(0));
                speedY += this.force*(a1.get(1));
                otherBall.speedX += this.force*(a2.get(0));
                otherBall.speedY += this.force*(a2.get(1));
            }
        }
    }

    // Growing functions to be called from outside to change ball size.
    public void startGrowing() {
        grow = true;
    }

    public void stopGrowing() {
        grow = false;
        if (radius < 0.1f) { radius = 0.1f; }
        updateMass();
    }

    public void grow() {
        if (grow && Math.abs(x - x1) < 9*settings.zoom.camera.zoom && Math.abs(y - y1) < 9*settings.zoom.camera.zoom) {
            radius += 0.1*settings.zoom.camera.zoom;
        }
        updateMass();
    }

    // Performs gravity decrease in speedY
    public void gravity() {
        if (gravitation && y - radius > yMin) { speedY -= gravity; }
    }

    // Sets ball speed by new X, Y
    public void setSpeedByPosition(float x1, float y1) {
        this.x1 = x1;
        this.y1 = y1;
        if (Math.abs(x - x1) < radius/3 ) { speedX = 0; }
        else { speedX = (x - x1) / 50f; }
        if (Math.abs(y - y1) < radius/3) { speedY = 0; }
        else { speedY = (y - y1) / 50f; }
        if (settings.getSetting(Settings.SettingsEnum.SPEED).getValue() == 0) {
            settings.age.flush();
        }
    }

    // Sets new position for ball
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.x1 = x;
        this.y1 = y;
        position = new Position(x, y, 0);
    }

    // Check if X, Y position is inside ball
    public Ball clicked(float x1, float y1) {
        if (Math.pow(radius, 2) >=
                Math.pow(position.x - x1, 2.0) + Math.pow(position.y - y1, 2.0)) {
            return this;
        }
        return null;
    }

    // Draws ball
    public void draw(ShapeRenderer renderer) {
        move();
        renderer.setColor(clr);
        renderer.circle(position.x, position.y, radius, 180);
    }

    // Draws ball BATCH
    public void draw(SpriteBatch batch) {

        batch.setColor(this.clr.r, this.clr.g, this.clr.b, this.clr.a);
        batch.draw(texture, position.x-radius, position.y-radius, 2*radius, 2*radius);
    }

    // Performs all ball actions
    public void act(Ball otherBall) {
        grow();
        hit(otherBall);
    }

    // Update mass from the radius.
    public void updateMass() {
        mass = radius*radius*radius * 3.14f * massScale;
    }

    // Draws tail for ball from List of history entries, d is this ball ID.
    public void drawTail(SpriteBatch batch, List<HistoryEntry> historyEntries, int d) {
        for (int i = 0; i < historyEntries.size(); i++) {
            float alpha = clr.a*((1f+i)/historyEntries.size());
            batch.setColor(new Color(clr.r, clr.g, clr.b, alpha));
            if (d < historyEntries.get(i).getBalls().size) {
                Ball ball = historyEntries.get(i).getBalls().get(d);
                batch.draw(texture, ball.position.x-radius, ball.position.y-radius, 2*ball.radius, 2*ball.radius);
            }
        }
    }

    // Draws path in front of ball
    public void drawPath(SpriteBatch batch, List<HistoryEntry> historyEntries, int d) {
        for (int i = historyEntries.size()-1; i >= 0; i--) {
            float alpha = clr.a*(((historyEntries.size()-i/1f)/historyEntries.size()));
            batch.setColor(new Color(clr.r, clr.g, clr.b, alpha));
            if (d < historyEntries.get(i).getBalls().size) {
                Ball ball = historyEntries.get(i).getBalls().get(d);
                batch.draw(texture, ball.position.x-0.5f*settings.zoom.camera.zoom, ball.position.y-0.5f*settings.zoom.camera.zoom, settings.zoom.camera.zoom, settings.zoom.camera.zoom);
            }
        }
    }

    public float getZ() {
        return position.z;
    }

    public void setProjection(float projection) {
        this.projection = projection/getZ();

    }
}
