package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

// Zoom class handles all zoom instances
public class Zoom {
    public OrthographicCamera camera, uiCamera;
    boolean zooming = false;

    // Mix and max zoom
    static final float MIN_ZOOM = 0.01f, MAX_ZOOM = 1f;

    // Variables for calculating zoom
    public float x, y, x1, y1, xP, yP, baseX = 0, baseY = 0, baseRotation = 0, z, q, maxZoom = 100;

    BoundingBox left, right, top, bottom = null;

    // Function set world bounds for ensuring our camera won't leave boundaries
    public void setWorldBounds(int left, int bottom, float width, float height) {
        float top = bottom + height;
        float right = left + width;

        reset();

        this.left = new BoundingBox(new Vector3(left, 0, 0), new Vector3(left, top, 0));
        this.right = new BoundingBox(new Vector3(right , 0, 0), new Vector3(right , top, 0));
        this.top = new BoundingBox(new Vector3(0, top, 0), new Vector3(right, top , 0));
        this.bottom = new BoundingBox(new Vector3(0, bottom, 0), new Vector3(right, bottom, 0));
        camera.position.set((bottom+top)/2f, (left+right)/2f, 0);
        camera.update();
        firstPosition.set(camera.position.x, camera.position.y, 0);

        setMaxZoom();
    }


    private void setMaxZoom() {
        while (!(camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(right)
                || camera.frustum.boundsInFrustum(bottom) || camera.frustum.boundsInFrustum(top)
                || camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(bottom))) {
            camera.zoom += 0.01;
            camera.update();
        }
        maxZoom = camera.zoom;
        camera.update();
    }

    Vector3 lastPosition = new Vector3(), firstPosition = new Vector3();
    float lastZoom;

    // Translate camera within safe boundingBox
    public void translateSafe(float x, float y, double scale) {
        lastPosition.set(camera.position.x, camera.position.y, 0);
        lastZoom = camera.zoom;
        camera.translate(x, y);
        camera.update();
        ensureBounds();
        camera.zoom /= scale;
        camera.update();
        ensureZoom();
    }

    // Checks if camera is within the boudns.
    public void ensureBounds() {
        if(camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(right)
                || camera.frustum.boundsInFrustum(bottom) || camera.frustum.boundsInFrustum(top)
                || camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(bottom)) {
            //camera.position.set(lastPosition);
            camera.update();
        }
        checkCamera();
    }

    public void ensureZoom() {
        checkCamera();
        while(camera.frustum.boundsInFrustum(left)) {
            camera.position.x += 0.2;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(right)) {
            camera.position.x -= 0.2;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(bottom)) {
            camera.position.y += 0.2;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(top)) {
            camera.position.y -= 0.2;
            camera.update();
        }

        if(camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(right)
                || camera.frustum.boundsInFrustum(bottom) || camera.frustum.boundsInFrustum(top)
                || camera.frustum.boundsInFrustum(left) || camera.frustum.boundsInFrustum(bottom)) {
           // camera.position.set(lastPosition);
            camera.update();
        }
    }


    // Constructor with cameras we need to use.
    public Zoom (OrthographicCamera camera, OrthographicCamera uiCamera) {
        this.camera = camera;
        this.uiCamera = uiCamera;
    }


    // Sets base point which is position of screen atm
    public void setPoint(float x, float y, float x1, float y1) {
        if (!zooming) {
            this.x = x;
            this.y = y;
            this.x1 = x1;
            this.y1 = y1;
            xP = (x+x1)/2;
            yP = (y+y1)/2;
            zooming = true;
        }
    }

    // Sets zoom from points.
    public void setZoom(float x, float y, float x1, float y1) {
        double oldDist = (Math.pow(this.x-this.x1,2) + Math.pow(this.y-this.y1,2));
        double newDist = (Math.pow(x-x1,2) + Math.pow(y-y1,2));
        double scale = Math.sqrt(newDist / oldDist);
        float xPnew = (x+x1)/2;
        float yPnew = (y+y1)/2;
        float xMoveBy = (xP - xPnew)*camera.zoom;
        float yMoveBy = (yPnew - yP)*camera.zoom;

        translateSafe(xMoveBy, yMoveBy, scale);

	/*
	    if (x > x1) {
	        z = x;
	        x = x1;
	        x1 = z;
	    }
	    if (y > y1) {
	        z = y;
	        y = y1;
	        y1 = z;
	    }
	    double angle1 = Math.atan2(y - y1, x - x1);
		double angle2 = Math.atan2(this.y - this.y1, this.x - this.x1);
		double angle = angle2 - angle1;
		camera.rotate((float) angle*60);
		baseRotation += angle*60; */


        baseX += (xP - xPnew)*camera.zoom;
        baseY += (yP - yPnew)*camera.zoom;

        xP = (x+x1)/2;
        yP = (y+y1)/2;
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;


    }

    // Helps update and ensure position of camera.
    public void touchUpAction (int pointer) {
        if (zooming && pointer == 0) {
            zooming = false;
            if (camera.zoom < 1.1 && camera.zoom > 0.9 && baseX > -100 && baseX < 100 && baseY > -100
                    && baseY < 100 && camera.zoom > 0.5f) {
                reset();
            }
	/*	    	if (zoom.baseRotation > -5 && zoom.baseRotation < 5) {
		    		camera.rotate(-baseRotation);
		    		zoom.baseRotation = 0;
		    	}*/

            checkCamera();
            camera.update();
        }
    }


    // Checks if camera zoom is within set borders.
    public void checkCamera() {
        if (camera.zoom < MIN_ZOOM) { camera.zoom = MIN_ZOOM; }
        else if (camera.zoom > maxZoom) { camera.zoom = maxZoom; }
        camera.update();
    }

    // Called when you want to move camera x, y is position of first and x1, y1 of second finger
    public void dragged(float x, float y, float x1, float y1) {
        setPoint(x, y, x1, y1);
        setZoom(x, y, x1, y1);
    }

    // Resets camera to default value
    public void reset() {
        camera.zoom = 1;
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        baseX = 0;
        baseY = 0;
        camera.update();
    }

    public void scrolled(int amount, boolean newBall) {
        x = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).x;
        y = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).y;
        float currentX = camera.position.x;
        float currentY = camera.position.y;
        int direction = amount > 0 ? -1 : 1;
        float moveX = direction*(x - currentX)/10;
        float moveY = direction*(y - currentY)/10;

        if(true) {
            camera.update();
            float zoomScaler = Math.max(25, 1/camera.zoom);
            translateSafe(moveX, moveY, (zoomScaler-amount)/(zoomScaler));
            camera.update();
            ensureZoom();
            checkCamera();
            camera.update();
        }

    }
}
