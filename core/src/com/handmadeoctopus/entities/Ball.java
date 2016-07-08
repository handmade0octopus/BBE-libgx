package com.handmadeoctopus.entities;

// Ball class which contains all ball variables and movement

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.environment.Settings;
import org.jblas.FloatMatrix;
import org.jblas.Geometry;

import java.util.Random;

public class Ball {
    // Position, size and speed of ball
    public float radius, mass, x, y, speedX, speedY, xMin, xMax, yMin, yMax;
    public float gravity = 0, force = 0, springiness = 1;
    public int quality = Settings.MAX_QUALITY;
    public Boolean grow = false, gravitation = true, hits = true, forces = true, touchable = false;
    Box box;
    Random rnd = new Random();
    Color clr;

    // Tail
    int tail = 0;
    static int TAIL_QUALITY = 4;
    Array<Position> lastPosition = new Array<Position>();

    // Main constructor
    public Ball(float radius, float x, float y, float speedX, float speedY, Color clr) {
        set(radius, x, y, speedX, speedY, clr);
    }

    // Constructor with random values of color and speed
    public Ball(float radius, float x, float y) {
        randomSpeedXY();
        randomColour();
        set(radius, x, y);
    }

    // Constructor for random ball within the box
    public Ball (float radius, Box box) {
        this.box = box;
        float x = rnd.nextFloat()*box.width, y= rnd.nextFloat()*box.height;
        while(x < box.xMin && x > box.xMax) {
            x = rnd.nextFloat()*box.width;
        }
        while(y < box.yMin && y > box.yMax) {
            y = rnd.nextFloat()*box.height;
        }
        randomSpeedXY();
        randomColour();
        set(radius, x, y);
    }

    // Changes colour to random
    private void randomColour() {
        clr = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat(), 0.9f);
    }

    // Changes speed to random
    private void randomSpeedXY() {
        float sX = 0;
        float sY = 0;
        while (sX == 0 && sY == 0) {
            sX = rnd.nextFloat() * 4 - 2;
            sY = rnd.nextFloat() * 4 - 2;
        }
        speedX = sX;
        speedY = sY;
    }

    // Sets all ball values
    public void set(float radius, float x, float y, float speedX, float speedY, Color clr) {
        this.radius = radius;
        this.mass = 3.14f*radius*radius;
        this.x = x;
        this.y = y;
        this.clr = clr;
        this.speedX = speedX;
        this.speedY = speedY;
        touchable = true;
    }

    // Sets radius, x and y
    public void set(float radius, float x, float y) {
        set(radius, x, y, speedX, speedY, clr);
    }

    // Sets other ball parameters
    public void setBallParameters(int gravity, int spring, int tail, int force, int quality, boolean gravitation, boolean forces, Box box) {
        this.force = force;
        this.box = box;
        this.gravitation = gravitation;
        this.forces = forces;

        xMax = box.xMax;
        yMax = box.yMax;
        xMin = box.xMin;
        yMin = box.yMin;

        if (gravity > Settings.MIN_GRAVITY && gravity <= Settings.MAX_GRAVITY) {
            this.gravity = gravity/100f;
            gravitation = true;
        }
        else {
            this.gravity = 0;
            gravitation = false;
        }

        if (spring > Settings.MIN_SPRINGINESS && spring <= Settings.MAX_SPRINGINESS) { this.springiness = spring/100f; }
        else { this.springiness = 1; }

        if (force > Settings.MIN_FORCES && gravity <= Settings.MAX_FORCES) {
            this.force = force/100f;
            forces = true;
        } else {
            this.force = 0;
            forces = false;
        }

        if (tail >= Settings.MIN_TAIL && tail <= Settings.MAX_TAIL) {
            configTail(tail);
        } else { this.tail = 0; }

        if (quality >= Settings.MIN_QUALITY && tail <= Settings.MAX_QUALITY) {
            this.quality = quality;
        } else { this.quality = 360; }
    }

    // Moves ball
    public void move() {
        saveTail();
        x += speedX;
        y += speedY;
    }

    // Saves ball position
    private void saveTail() {
        if (tail > 0) {
            float tailQ = TAIL_QUALITY;
            for(int i = tail*TAIL_QUALITY-1; i >= TAIL_QUALITY; i--) {
                lastPosition.get(i).set(lastPosition.get(i-TAIL_QUALITY));
            }
            for(int i = 0; i < TAIL_QUALITY; i++) {
                lastPosition.get(i).set((float) (x-(i*speedX/(tailQ))), (float) (y-(i*speedY/(tailQ))));
            }
        }
    }

    // Slows ball if you need to slow it only a little, higher number = less slow, 1 = no change
    void slow(float less) {
        if (less <= 0) { less = 1; }
        speedY *= springiness /less;
        speedX *= springiness /less;
        if (Math.abs(speedX) < 0.01) { speedX = 0; }
        if (Math.abs(speedY) < 0.01) { speedY = 0; }
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

        // When ball collides with box
        if (x + radius + speedX > xMax || x - radius + speedX < xMin) {
            speedX = -springiness * speedX;
            if (x - radius < xMin) { x = xMin + radius; }
            else if (x + radius > xMax) { x = xMax - radius; }
        }
        if (y + radius + speedY > yMax || y - radius + speedY < yMin) {
            speedY = -springiness * speedY;
            if (y - radius < yMin) { y = yMin + radius; }
            else if (y + radius + speedY > yMax) { y = yMax - radius; }
        }

        // Balls collide with each other
        if (otherBall != null) {
            double totalRadius = Math.pow(radius + otherBall.radius, 2);
            double distSpeed = Math.pow( (x + speedX )
                    - (otherBall.x + otherBall.speedX), 2)
                    + Math.pow( (y + speedY)
                    - (otherBall.y + otherBall.speedY), 2);
            double distance = Math.pow( (this.x - (otherBall.x  ) ), 2.0) + Math.pow( (this.y    ) - (otherBall.y ), 2.0);

            if(hits && totalRadius >= distSpeed) {
                FloatMatrix n = new FloatMatrix(new float[][] {{x - otherBall.x, y - otherBall.y}});
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
                    if ((this.x) - (otherBall.x) > 0) {
                        this.x += move;
                        otherBall.x -= move;
                    } else if ((this.x) - (otherBall.x) < 0) {
                        this.x -= move;
                        otherBall.x += move;
                    }
                    if ((this.y) - (otherBall.y) > 0) {
                        this.y += move;
                        otherBall.y -= move;
                    } else if ((this.y) - (otherBall.y) < 0) {
                        this.y -= move;
                        otherBall.y += move;
                    }
                }
                slow();
                otherBall.slow();
            }

            if (forces && totalRadius + 1 < distance) {
                FloatMatrix n = new FloatMatrix(new float[][]{{x - otherBall.x, y - otherBall.y}});
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
        mass = (float) 3.14*radius*radius;
    }

    public void grow() {
        if (grow) {
            radius += 0.2;
        }
    }

    // Performs gravity decrease in speedY
    public void gravity() {
        if (gravitation && y + radius < yMax) { speedY -= gravity; }
    }

    // Sets ball speed by new X, Y
    public void setSpeedByPosition(float x1, float y1) {
        if (Math.abs(x - x1) < 2 ) { speedX = 0; }
        else { speedX = (x - x1) / 20; }
        if (Math.abs(y - y1) < 2) { speedY = 0; }
        else { speedY = (y - y1) / 20; }
    }

    // Sets new position for ball
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Check if X, Y position is inside ball
    public Ball clicked(float x1, float y1) {
        if (Math.pow(radius, 2) >=
                Math.pow(x - x1, 2.0) + Math.pow(y - y1, 2.0)) {
            return this;
        }
        return null;
    }

    // Setups tail
    public void configTail (int tail) {
        this.tail = tail;
        lastPosition.setSize(tail*TAIL_QUALITY);
        for(int i = 0; i < tail*TAIL_QUALITY; i++) {
            lastPosition.set(i, new Position(-100, -100));
        }
    }

    // Draws ball
    public void draw(ShapeRenderer renderer) {
        move();
        renderTail(renderer);
        renderer.setColor(clr);
        renderer.circle(x, y, radius, quality);
    }

    // Renders tail after ball
    private void renderTail(ShapeRenderer renderer) {
        float x, y, alphaC, tailQ, tailL, d;
        for(int i = 0; i < tail*TAIL_QUALITY; i++) {
            tailQ = TAIL_QUALITY;
            tailL = tail;
            d = i;
            alphaC = ((tailQ*tailL-d)/(tailQ*tailL));
            renderer.setColor(new Color(this.clr.r, this.clr.g, this.clr.b, this.clr.a*alphaC));
            x = lastPosition.get(i).x;
            y = lastPosition.get(i).y;
            renderer.circle(x, y, radius, quality);
        }
    }

    // Performs all ball actions
    public void act(Ball otherBall) {
        grow();
        gravity();
        hit(otherBall);
    }
}
