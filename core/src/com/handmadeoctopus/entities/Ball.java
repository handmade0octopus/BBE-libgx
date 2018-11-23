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
    public float radius, mass, speedX, speedY, speedZ, xMin, xMax, yMin, yMax, zMin, zMax, x1, y1, projection, rotation;
    public float gravity = 0, force = 0, springiness = 1, speed = 6, massScale = 1;
    public Texture texture = MainEngine.TEXTURE;
    public Boolean grow = false, gravitation = true, hits = true, forces = true, touchable = false, moving = true;
    Settings settings;
    Box box;
    Random rnd = new Random();
    Color clr;

    // Tail
    public int tail = 0;

    // Constructor with random values of color and speed
    public Ball(float radius, float x, float y, Settings settings) {
        this.settings = settings;
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
        copyBall(ball, true);
    }

    private void copyBall(Ball ball, boolean newPosition) {
        radius = ball.radius;
        mass = ball.mass;
        settings = ball.settings;
        if (newPosition){
            position = new Position(ball.position);
        } else {
            position.set(ball.position);
        }
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
        updateRotation();
    }

    // Changes colour to random
    private void randomColour() {
        clr = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 1f);
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
        updateRotation();
    }

    // Sets all ball values
    public void set(float radius, float x, float y, float z, float speedX, float speedY, float speedZ, Color clr) {
        this.radius = radius;
        updateMass();
        this.position = new Position(x, y, z);
        this.clr = clr;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
        touchable = true;
        updateRotation();
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

        this.gravity = gravity/1000f;
        this.gravitation = true;

        this.springiness = spring/100f;

        this.force = force/10000f;
        this.forces = true;

        this.tail = tail;
        updateRotation();
    }

    public void setBallParameters(Settings settings) {
        this.settings = settings;
        setBallParameters(settings.getSetting(Settings.SettingsEnum.GRAVITY).getValue(),
                settings.getSetting(Settings.SettingsEnum.SPRINGINESS).getValue(),
                settings.getSetting(Settings.SettingsEnum.BALLSTAIL).getValue(),
                settings.getSetting(Settings.SettingsEnum.FORCES).getValue(),
                settings.getSetting(Settings.SettingsEnum.SPEED).getValue(),
                settings.getSetting(Settings.SettingsEnum.GRAVITY).getValueBool(),
                settings.getSetting(Settings.SettingsEnum.FORCES).getValueBool(),
                settings.box);
    }

    // Moves ball
    public Ball move() {
        if(moving) {
            gravity();

            checkBoxBoundaries();

            position.x += speedX;
            position.y += speedY;
            position.z += speedZ;
        }

        return this;
    }

    private void checkBoxBoundaries() {
        // When ball collides with box
        if (position.x + radius + speedX > box.xMax || position.x - radius + speedX < box.xMin) {
            speedX = -springiness * speedX;
            if (position.x - radius < box.xMin) { position.x = box.xMin + radius; }
            else if (position.x + radius > box.xMax) { position.x = box.xMax - radius; }

        }
        if (position.y + radius + speedY > box.yMax || position.y - radius + speedY < box.yMin) {
            speedY = -springiness * speedY;
            if (position.y - radius < box.yMin) { position.y = box.yMin + radius; }
            else if (position.y + radius + speedY > box.yMax) { position.y = box.yMax - radius; }
            updateRotation();
        }
        if (position.z + radius + speedZ > box.zMax || position.z - radius + speedZ < box.zMin) {
            speedZ = -springiness * speedZ;
            if (position.z - radius < box.zMin) { position.z = box.zMin + radius; }
            else if (position.z + radius + speedZ > box.zMax) { position.z = box.zMax - radius; }
            updateRotation();
        }
    }

    private void updateRotation() {
        rotation = (float) Math.toDegrees(Math.atan2(speedX, speedY));
        if(speedX >= 0) {
            rotation = -rotation;
        } else {
            rotation = (float) -(360f+ rotation);
        }
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
    public void hit(Ball otherBall, float distance, float totalRadius) {
        // Balls collide with each other
            float distSpeed = ( (position.x + speedX )
                    - (otherBall.position.x + otherBall.speedX))*( (position.x + speedX )
                    - (otherBall.position.x + otherBall.speedX))
                    + ( (position.y + speedY)
                    - (otherBall.position.y + otherBall.speedY))*( (position.y + speedY)
                    - (otherBall.position.y + otherBall.speedY));

            if(hits && totalRadius >= distSpeed) {
                FloatMatrix n = new FloatMatrix(new float[][] {{position.x - otherBall.position.x, position.y - otherBall.position.y}});
                FloatMatrix v1 = new FloatMatrix(new float[][] {{speedX, speedY}});
                FloatMatrix v2 = new FloatMatrix(new float[][] {{otherBall.speedX, otherBall.speedY}});
                Geometry.normalize(n);
                float a1 = v1.dot(n);
                float a2 = v2.dot(n);
                float optimizedP = (2.0f * (a1 - a2)) / (mass + otherBall.mass);

                FloatMatrix v1p = v1.sub(n.mul(optimizedP*otherBall.mass));
                FloatMatrix v2p = v2.add(n.mul(optimizedP*mass));


                if(mass*1000 < otherBall.mass) {
                    speedX = v1p.get(0);
                    speedY = v1p.get(1);
                } else if(mass > otherBall.mass*1000) {
                    otherBall.speedX = v2p.get(0);
                    otherBall.speedY = v2p.get(1);
                } else {
                    speedX = v1p.get(0);
                    speedY = v1p.get(1);
                    otherBall.speedX = v2p.get(0);
                    otherBall.speedY = v2p.get(1);
                }



                while (totalRadius > distance) {
                    float move = 1f;
                    if ((this.position.x) - (otherBall.position.x) > 0) {
                        if(mass < otherBall.mass) {
                            this.position.x += move;
                        } else {
                            otherBall.position.x -= move;
                        }

                    } else if ((this.position.x) - (otherBall.position.x) < 0) {
                        if(mass < otherBall.mass) {
                            this.position.x -= move;
                        } else {
                            otherBall.position.x += move;
                        }
                    }
                    if ((this.position.y) - (otherBall.position.y) > 0) {
                        if(mass < otherBall.mass) {
                            this.position.y += move;
                        } else {
                            otherBall.position.y -= move;
                        }
                    } else if ((this.position.y) - (otherBall.position.y) < 0) {
                        if(mass < otherBall.mass) {
                            this.position.y -= move;
                        } else {
                            otherBall.position.y += move;
                        }
                    }

                    distance = (( (this.position.x - (otherBall.position.x) ))*( (this.position.x - (otherBall.position.x) ))
                            + ( (this.position.y) - (otherBall.position.y ))*( (this.position.y) - (otherBall.position.y )));
                    totalRadius = (radius + otherBall.radius)*(radius + otherBall.radius);

                    }

                    slow();
                    otherBall.slow();
                    updateRotation();
                    otherBall.updateRotation();
                }



    }

    // Performs all ball actions
    public Ball act(Ball otherBall) {
        if(otherBall != null) {
            float distance = (( (this.position.x - (otherBall.position.x) ))*( (this.position.x - (otherBall.position.x) ))
                    + ( (this.position.y) - (otherBall.position.y ))*( (this.position.y) - (otherBall.position.y )));
            float totalRadius = (radius + otherBall.radius)*(radius + otherBall.radius);

            hit(otherBall, distance, totalRadius);
            forces(otherBall, distance, totalRadius);
            return this;
        } else { return this; }
    }

    private void forces(Ball otherBall, float distance, float totalRadius) {
        if (forces && totalRadius < distance) {
            FloatMatrix n = new FloatMatrix(new float[][]{{position.x - otherBall.position.x, position.y - otherBall.position.y}});
            Geometry.normalize(n);
            float force =  ((mass * otherBall.mass)) / (distance);
            FloatMatrix a1 = n.mul( (-1f*(force / mass)));
            FloatMatrix a2 = n.mul( (force / otherBall.mass));
            speedX += this.force*(a1.get(0));
            speedY += this.force*(a1.get(1));
            otherBall.speedX += this.force*(a2.get(0));
            otherBall.speedY += this.force*(a2.get(1));
            updateRotation();
            otherBall.updateRotation();
        }
    }

    // Growing functions to be called from outside to change ball size.
    public void startGrowing() {
        grow = true;
        growing = true;
    }

    public void stopGrowing() {
        grow = false;
        if (radius < 0.1f) { radius = 0.1f; }
        updateMass();
    }

    boolean growing;

    public void grow() {
        if (grow && growing) {
            radius += 0.3*settings.zoom.camera.zoom;
        }
        updateMass();
    }

    // Performs gravity decrease in speedY
    public void gravity() {
        if (gravitation && position.y - radius > yMin) { speedY -= gravity; }
    }

    // Sets ball speed by new X, Y
    public void setSpeedByPosition(float x1, float y1) {
        this.x1 = x1;
        this.y1 = y1;

        float distance = (( (this.position.x - x1 ))*( (this.position.x - x1 ))
                + ( (this.position.y) - y1)*( (this.position.y) - (y1 )));
        float totalRadius = radius*radius > 81*settings.zoom.camera.zoom ? (radius)*(radius) : settings.zoom.camera.zoom;


        if(totalRadius >= distance) {
            speedX = 0;
            speedY = 0;
            growing = true;
        } else {
            FloatMatrix n = new FloatMatrix(new float[][]{{position.x - x1, position.y - y1}});
            Geometry.normalize(n);
            FloatMatrix a = n.mul((float) (Math.sqrt((distance) - totalRadius))/50f);
            speedX = a.get(0);
            speedY = a.get(1);
            growing = false;
        }

    /*    if (Math.abs(position.x - x1) < radius ) { speedX = 0; }
        else {
            if(position.x > x1) {
                speedX = (position.x - x1 - radius) / 50f;
            } else {
                speedX = (position.x - x1 + radius) / 50f;
            }
        }
        if (Math.abs(position.y - y1) < radius) { speedY = 0; }
        else {
            if(position.y > y1) {
                speedY = (position.y - y1 - radius) / 50f;
            } else {
                speedY = (position.y - y1 + radius) / 50f;
            }
        }*/


        if (settings.getSetting(Settings.SettingsEnum.SPEED).getValue() == 0) {
            settings.age.flush();
        }
        updateRotation();
    }

    // Sets new position for ball
    public void setPosition(float x, float y) {
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


    // Update mass from the radius.
    public void updateMass() {
        mass = radius*radius*radius * 3.14f * massScale;
    }

    // Draws ball
    public void draw(ShapeRenderer renderer) {
        renderer.setColor(clr);
        renderer.circle(position.x, position.y, radius, 180);
    }

    // Draws ball BATCH
    public void draw(SpriteBatch batch) {

        batch.setColor(this.clr.r, this.clr.g, this.clr.b, this.clr.a);
        batch.draw(texture, position.x-radius, position.y-radius, radius, radius, 2*radius, 2*radius, 1, 1, rotation, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
      //  batch.draw(texture, position.x-radius, position.y-radius, 2*radius, 2*radius);
    }

    // Draws tail for ball from List of history entries, d is this ball ID.
    public void drawTail(SpriteBatch batch, List<HistoryEntry> historyEntries, int d) {
        for (int i = 0; i < historyEntries.size(); i++) {
            float alpha = clr.a*((1f+i)/historyEntries.size());
            batch.setColor(new Color(clr.r, clr.g, clr.b, alpha));
            if (d < historyEntries.get(i).getBalls().size) {
                Ball ball = historyEntries.get(i).getBalls().get(d);
                batch.draw(texture, ball.position.x-alpha*radius, ball.position.y-alpha*radius, alpha*2*ball.radius, alpha*2*ball.radius);
            }
        }
    }

    public void drawTail(ShapeRenderer renderer, List<HistoryEntry> historyEntries, int d) {
        for (int i = 0; i < historyEntries.size(); i++) {
            if (d < historyEntries.get(i).getBalls().size && i + 1 < historyEntries.size() && d < historyEntries.get(i+1).getBalls().size ) {
                float alpha = clr.a*((1f+i)/historyEntries.size());
                renderer.setColor(new Color(clr.r, clr.g, clr.b, alpha));
                Ball ball = historyEntries.get(i).getBalls().get(d);
        //      renderer.circle(ball.position.x, ball.position.y, ball.radius, 180);
                Ball ball2 = historyEntries.get(i + 1).getBalls().get(d);
                float angle = (float) Math.toDegrees(Math.atan((ball.position.y - ball2.position.y) / (ball.position.x - ball2.position.x)));
                float width = (float) Math.sqrt(Math.pow(ball.position.x - ball2.position.x, 2) + Math.pow(ball.position.y - ball2.position.y, 2));
                renderer.rect((ball.position.x + ball2.position.x) / 2 - (width / 2),
                            (ball.position.y + ball2.position.y) / 2 - settings.zoom.camera.zoom, (width / 2), settings.zoom.camera.zoom, width,
                            settings.zoom.camera.zoom * 2, 1f, 1f, angle);
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

    public void drawPath(ShapeRenderer renderer, List<HistoryEntry> historyEntries, int d) {
        for (int i = historyEntries.size()-1; i >= 0; i--) {
            float alpha = clr.a*(((historyEntries.size()-i/1f)/historyEntries.size()));
            renderer.setColor(new Color(clr.r, clr.g, clr.b, alpha));
            if (d < historyEntries.get(i).getBalls().size) {
                Ball ball = historyEntries.get(i).getBalls().get(d);
         //     renderer.circle(ball.position.x, ball.position.y, settings.zoom.camera.zoom, 180);
                if (i > 0) {
                    Ball ball2 = historyEntries.get(i-1).getBalls().get(d);
                    float angle = (float) Math.toDegrees(Math.atan((ball.position.y - ball2.position.y)/(ball.position.x - ball2.position.x)));
                    float width = (float) Math.sqrt(Math.pow(ball.position.x - ball2.position.x, 2)+Math.pow(ball.position.y - ball2.position.y, 2));
                    renderer.rect((ball.position.x + ball2.position.x)/2 - (width/2),
                            (ball.position.y + ball2.position.y)/2 - settings.zoom.camera.zoom, (width/2), settings.zoom.camera.zoom, width,
                            settings.zoom.camera.zoom*2,  1f, 1f, angle );
                }

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
