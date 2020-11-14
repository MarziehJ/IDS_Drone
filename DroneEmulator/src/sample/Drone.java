package sample;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Drone {

    private Point location;
    private boolean isPowerOn = false;

    public Drone(Point location) {
        this.location = location;
    }

    public Drone() {
        this(new Point(350, 300));

    }

    public Point getLocationInXY() {
        return new Point((int)this.location.getX()-250, (int)(300-this.location.getY()));
    }

    private void InitializeCanvas(GraphicsContext graphicsContext, javafx.scene.canvas.Canvas canvas) {
        graphicsContext.setStroke(Color.LIGHTGREY);
        graphicsContext.setLineWidth(1);

        graphicsContext.beginPath();
        graphicsContext.moveTo(100, 0);
        graphicsContext.lineTo(100, 250);
        graphicsContext.lineTo(canvas.getWidth(), 250);
        graphicsContext.lineTo(100, 250);
        graphicsContext.lineTo(0, canvas.getHeight());
        graphicsContext.stroke();

    }

    private void draw(javafx.scene.canvas.Canvas canvas, Color color, int lineWidth) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        InitializeCanvas(graphicsContext, canvas);

        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(lineWidth);

        graphicsContext.strokeOval(location.getX(), location.getY(), 50, 20);
        graphicsContext.beginPath();
        graphicsContext.moveTo(location.getX() + 25, location.getY() + 10);
        graphicsContext.lineTo(location.getX() + 25, location.getY() - 5);
        graphicsContext.lineTo(location.getX() - 5, location.getY() - 5);
        graphicsContext.lineTo(location.getX() + 50, location.getY() - 5);
        graphicsContext.strokeOval(location.getX() - 5, location.getY() - 8, 5, 7);
        graphicsContext.strokeOval(location.getX() + 47, location.getY() - 8, 5, 7);
        graphicsContext.stroke();
    }

    public void drawShadowDrone(javafx.scene.canvas.Canvas canvas) {
        this.draw(canvas, Color.LIGHTGRAY, 1);

    }

    public void initDrone( javafx.scene.canvas.Canvas canvas) {
        this.draw(canvas, Color.BLUE, 2);
        this.isPowerOn = true;

    }

    public void takeOff(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {
            this.location = new Point((int) this.location.getX(), (int) this.location.getY() - 150);
            this.draw(canvas,Color.BLUE, 2);
        }
    }

    public void moveLeft(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {
            this.location = new Point((int) this.location.getX()-10, (int) this.location.getY());
            this.draw(canvas, Color.BLUE, 2);

        }

    }

    public void moveRight(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {
            this.location = new Point((int) this.location.getX()+10, (int) this.location.getY());
            this.draw(canvas, Color.BLUE, 2);
        }

    }

    public void moveTop(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {
            this.location = new Point((int) this.location.getX(), (int) this.location.getY()-10);
            this.draw(canvas, Color.BLUE, 2);
        }

    }
    public void moveDown(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {
            this.location = new Point((int) this.location.getX(), (int) this.location.getY()+10);
            this.draw(canvas, Color.BLUE, 2);
        }

    }
    public void stop(javafx.scene.canvas.Canvas canvas) {
        if (this.isPowerOn) {

            this.location = new Point((int) this.location.getX(), 300);
            this.draw(canvas, Color.LIGHTGRAY, 1);
            this.isPowerOn = false;
        }

    }


}
