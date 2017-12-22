package turtle;

import com.sun.javafx.application.PlatformImpl;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class turtle {
    private static final double WIDTH = 500;
    private static final double HEIGHT = 500;

    private static final double PIXELS_PER_SECOND = 100;

    private static final double DURATION = 1000;

    public static final turtle turtle = new turtle();

    private Shape turtleShape;
    private double angle;
    private double x;
    private double y;

    private Group root;

    private Animator animator;

    private boolean penDown;

    private boolean notDisplayed;

    private turtle() {

        angle = 0;
        x = WIDTH / 2;
        y = HEIGHT / 2;

        root = new Group();

        turtleShape = new Polygon(0, 0, -3.75, -5, 10, 0, -3.75, 5);
        turtleShape.setFill(Color.BLACK);
        turtleShape.setTranslateX(250);
        turtleShape.setTranslateY(250);

        root.getChildren().add(turtleShape);

        penDown = true;

        animator = new Animator();
        Thread animationThread = new Thread(animator);
        animationThread.setDaemon(true);
        animationThread.start();

        notDisplayed = true;
    }

    public void pd() {
        penDown();
    }

    public void down() {
        penDown();
    }

    public void penDown() {
        penDown = true;
    }

    public void pu() {
        penUp();
    }

    public void up() {
        penUp();
    }

    public void penUp() {
        penDown = false;
    }

    public void fd(double distance) {
        forward(distance);
    }

    public void forward(double distance) {
        display();
    }

    public void bk(double distance) {
        backward(distance);
    }

    public void back(double distance) {
        backward(distance);
    }

    public void backward(double distance) {

    }

    public void rt(double degrees) {
        right(degrees);
    }

    public void right(double degrees) {
        angle += degrees;

        display();
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(DURATION),
                new KeyValue(turtleShape.rotateProperty(), angle)));
        animator.addAnimation(animation);
    }

    public void lt(double degrees) {
        left(degrees);
    }

    public void left(double degrees) {
        angle -= degrees;

        display();
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(DURATION),
                new KeyValue(turtleShape.rotateProperty(), angle)));
        animator.addAnimation(animation);
    }

    public void goTo(double x, double y) {
        setPosition(x, y);
    }

    public void setPos(double x, double y) {
        setPosition(x, y);
    }

    public void setPosition(double newX, double newY) {
        display();

        double realX = newX += WIDTH / 2;
        double realY = newY += HEIGHT / 2;

        Timeline animation = new Timeline();

        KeyValue[] keyValues = new KeyValue[penDown ? 4 : 2];
        keyValues[0] = new KeyValue(turtleShape.translateXProperty(), realX);
        keyValues[1] = new KeyValue(turtleShape.translateYProperty(), realY);

        if(penDown) {
            Line line = new Line(x, y, x, y);
            line.setStroke(Color.TRANSPARENT);

            animation.getKeyFrames().add(new KeyFrame(Duration.ONE,
                    new KeyValue(line.strokeProperty(), Color.BLACK)));

            root.getChildren().add(line);
            keyValues[2] = new KeyValue(line.endXProperty(), realX);
            keyValues[3] = new KeyValue(line.endYProperty(), realY);
        }

        x = realX;
        y = realY;

        animation.getKeyFrames().add(
                new KeyFrame(Duration.millis(DURATION), keyValues));

        animator.addAnimation(animation);
    }

    public void setX(double newX) {
        setPosition(newX, y);
    }

    public void setY(double newY) {
        setPosition(x, newY);
    }

    private double getDuration(double startX, double startY,
                               double endX, double endY) {
        double distance = euclidianDistance(startX, startY, endX, endY);

        return distance / PIXELS_PER_SECOND * 1000;
    }

    private double euclidianDistance(double startX, double startY,
                                     double endX, double endY) {
        Point2D start = new Point2D(startX, startY);
        Point2D end = new Point2D(endX, endY);

        return start.distance(end);
    }

    private void display() {
        if(notDisplayed) {
            PlatformImpl.startup(() -> {
            });
            Platform.runLater(() -> {
                try {
                    new TurtleApp().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            notDisplayed = false;
        }
    }

    private static class TurtleApp extends Application {

        @Override
        public void start(Stage primaryStage) {
            Scene scene = new Scene(turtle.root, WIDTH, HEIGHT,
                    Color.GHOSTWHITE);

            primaryStage.setTitle("JTurtle!");
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.show();
        }

        @Override
        public void stop() throws Exception {
            // TODO: kill the animator?
        }
    }

    private static class Animator implements Runnable,
            EventHandler<ActionEvent> {
        private final List<Animation> queue;
        private boolean running;

        Animator() {
            queue = new LinkedList<>();
            running = true;
        }

        @Override
        public void run() {
            synchronized(queue) {
                while(running) {
                    if (queue.size() > 0) {
                        Animation next = queue.remove(0);
                        next.setOnFinished(this);
                        next.play();
                    }

                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void handle(ActionEvent event) {
            synchronized(queue) {
                queue.notify();
            }
        }

        private void addAnimation(Animation animation) {
            synchronized(queue) {
                queue.add(animation);
                queue.notify();
            }
        }
    }
}
