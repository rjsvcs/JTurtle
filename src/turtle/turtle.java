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
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class turtle {
    private static final double WIDTH = 500;
    private static final double HEIGHT = 500;

    private static final double DURATION = 5000;

    public static final turtle turtle = new turtle();

    private Shape turtleShape;
    private double angle;
    private double x;
    private double y;

    private Group root;

    private Animator animator;

    private boolean notDisplayed;

    private turtle() {

        angle = 0;
        x = 0;
        y = 0;

        root = new Group();

        turtleShape = new Polygon(0, 0, 7.5, 5, 0, 10);
        turtleShape.setFill(Color.BLACK);
        turtleShape.setTranslateX(250);
        turtleShape.setTranslateY(250);

        root.getChildren().add(turtleShape);

        animator = new Animator();
        Thread animationThread = new Thread(animator);
        animationThread.start();

        notDisplayed = true;
    }

    public void fd(double distance) {
        forward(distance);
    }

    public void forward(double distance) {
        turtle.repaint();
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
        System.out.println(angle);

        repaint();
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
        System.out.println(angle);

        repaint();
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

    public void setPosition(double x, double y) {

    }

    public void setX(double x) {

    }

    public void setY(double y) {

    }

    private void repaint() {
        display();

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
