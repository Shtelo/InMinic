package prj.shtelo.inminic.client.cameraobject;

import prj.shtelo.inminic.client.Root;
import prj.shtelo.inminic.client.root.Display;
import prj.shtelo.inminic.client.state.State;

public class Camera {
    private double x, y, zoom;
    private double targetX, targetY, targetZoom;
    private Root root;

    public Camera(double x, double y, double zoom, Root root) {
        this.x = x;
        this.targetX = x;
        this.y = y;
        this.targetY = y;
        this.zoom = zoom;
        this.targetZoom = zoom;
        this.root = root;
    }

    public void tick() {
        targetX = root.getCharacter().getX();
        targetY = root.getCharacter().getY();

        if (root.getStateManager().getState() == State.Main) {
            if (root.getKeyManager().isZoomUp()) targetZoom += 2 / root.getDisplay().getDisplayFps();
            if (root.getKeyManager().isZoomDown()) targetZoom -= 2 / root.getDisplay().getDisplayFps();
            if (root.getKeyManager().isResetZoom()) targetZoom = 2;
        }

        if (targetZoom < 0.5) targetZoom = 0.5;
        else if (targetZoom > 10) targetZoom = 10;

        x += (targetX - x) / (root.getDisplay().getDisplayFps() / Display.getMotionSpeed());
        y += (targetY - y) / (root.getDisplay().getDisplayFps() / Display.getMotionSpeed());
        zoom += (targetZoom - zoom) / (root.getDisplay().getDisplayFps() / Display.getMotionSpeed());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZoom() {
        return zoom;
    }

    public double getRelativeX(double x) {
        return (x - this.x) * zoom + root.getDisplay().getWidth() / 2.;
    }

    public double getRelativeY(double y) {
        return (y - this.y) * zoom + root.getDisplay().getHeight() / 2.;
    }
}