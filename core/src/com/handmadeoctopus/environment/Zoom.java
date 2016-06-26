package com.handmadeoctopus.environment;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.handmadeoctopus.entities.Box;

public class Zoom {

    OrthographicCamera camera, uiCamera;
    boolean zooming = false;
    Box box;

    static final float MIN_ZOOM = 1f, MAX_ZOOM = 0.01f;

    public float x, y, x1, y1, xP, yP, baseX = 0, baseY = 0, baseRotation = 0, z;

    BoundingBox left, right, top, bottom = null;

    public void setWorldBounds(int left, int bottom, float width, float height) {
        float top = bottom + height;
        float right = left + width;

        this.left = new BoundingBox(new Vector3(left, 0, 0), new Vector3(left, top, 0));
        this.right = new BoundingBox(new Vector3(right , 0, 0), new Vector3(right , top, 0));
        this.top = new BoundingBox(new Vector3(0, top, 0), new Vector3(right, top , 0));
        this.bottom = new BoundingBox(new Vector3(0, bottom, 0), new Vector3(right, bottom, 0));

    }

    Vector3 lastPosition = new Vector3();
    float lastZoom;

    public void translateSafe(float x, float y) {
        lastPosition.set(camera.position.x, camera.position.y, 0);
        lastZoom = camera.zoom;
        camera.translate(x, y);
        camera.update();
        ensureBounds();
        camera.update();
    }

    public void ensureBounds() {
        while(camera.frustum.boundsInFrustum(left)) {
            camera.position.x += 0.1;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(right)) {
            camera.position.x -= 0.1;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(bottom)) {
            camera.position.y += 0.1;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(top)) {
            camera.position.y -= 0.1;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(left)) {
            camera.position.x += 0.01;
            camera.update();
        }
        while(camera.frustum.boundsInFrustum(bottom)) {
            camera.position.y += 0.01;
            camera.update();
        }
        checkCamera();

    }

    public Zoom (OrthographicCamera camera, OrthographicCamera uiCamera) {
        this.camera = camera;
        this.uiCamera = uiCamera;
    }

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

    public void setZoom(float x, float y, float x1, float y1) {
        double oldDist = (Math.pow(this.x-this.x1,2) + Math.pow(this.y-this.y1,2));
        double newDist = (Math.pow(x-x1,2) + Math.pow(y-y1,2));
        double scale = Math.sqrt(newDist / oldDist);
        camera.zoom /= scale;
        float xPnew = (x+x1)/2;
        float yPnew = (y+y1)/2;
        float xMoveBy = (xP - xPnew)*camera.zoom;
        float yMoveBy = (yPnew - yP)*camera.zoom;


      //  box.moveZoom(xMoveBy, (yP - yPnew)*camera.zoom, camera.zoom, xP, yP);


        translateSafe(xMoveBy, yMoveBy);






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

    void checkCamera() {
        if (camera.zoom > MIN_ZOOM) { camera.zoom = MIN_ZOOM; }
        else if (camera.zoom < MAX_ZOOM) { camera.zoom = MAX_ZOOM; }


     /*   if (baseY > box.yZoomMin) {
            box.yZoomMin = baseY;
        } else if (baseY < box.yZoomMax) {
            box.yZoomMax = baseY;
        }  else if (baseX > box.xZoomMin) {
            box.xZoomMin = baseX;
        } else if (baseX < box.xZoomMax) {
            box.xZoomMax = baseX;
        }*/
    }


    public void dragged(float x, float y, float x1, float y1) {
        setPoint(x, y, x1, y1);
        setZoom(x, y, x1, y1);
    }

    public void reset() {
        camera.zoom = 1;
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        baseX = 0;
        baseY = 0;
    }

    public void setBox(Box box) {
        this.box = box;
    }
}
