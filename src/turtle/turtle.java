/*
 * JavaFX turtle implementation. Provides an animated turtle object that can
 * be programmatically controlled to draw pictures using a turtle.
 *
 * @author Bobby St. Jacques
 */
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

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * A JavaFX implementation of Turtle Graphics.
 */
public class turtle {
    /**
     * The color mode used if RGB values should be specified as numbers
     * between 0.0 and 1.0. This is the default color mode.
     */
    public static final double COLOR_MODE_1 = 1.0;

    /**
     * The color mode to use if RGB values should be specified as numbers
     * between 0.0 and 255.0.
     */
    public static final double COLOR_MODE_255 = 255.0;

    /**
     * The default width of the turtle's world in pixels.
     */
    private static final double WIDTH = 700;

    /**
     * The default height of the turtle's world.
     */
    private static final double HEIGHT = 700;

    /**
     * Default speed in pixels per second.
     */
    private static final double PIXELS_PER_SECOND = 200;

    /**
     * The default speed at which the turtle turns in degrees per second.
     */
    private static final double DEGREES_PER_SECOND = 180;

    /**
     * The static (singleton) turtle.
     */
    public static final turtle turtle = new turtle();

    /**
     * The polygon that represents the turtle's arrowhead.
     */
    private Shape turtleShape;

    /**
     * The angle that the turtle is currently facing.
     */
    private double angle;

    /**
     * The turtle's current x-coordinate position (this is the real x
     * coordinate with 0,0 in the top left of the window, NOT the turtle
     * coordinate with 0,0 in the center of the window).
     */
    private double x;

    /**
     * The turtle's current y-coordinate position (this is the real y
     * coordinate with 0,0 in the top left of the window, NOT the turtle
     * coordinate with 0,0 in the center of the window).
     */
    private double y;

    /**
     * The JavaFX groupt to which all shapes are added to be rendered.
     */
    private Group root;

    /**
     * Helper {@link Thread} that insures that animations are completed one at
     * a time, in order.
     */
    private Animator animator;

    /**
     * The current state of the pen; true if the pen is down.
     */
    private boolean penDown;

    /**
     * The color mode. Set to either 1.0 for 0.0-1.0 RGB values or 255 for
     * 0-255 color values.
     */
    private double colorMode;

    /**
     * The color used for the turtle's pen.
     */
    private Color penColor;

    /**
     * The color used for the turtle's fills.
     */
    private Color fillColor;

    /**
     * The speed at which the turtle moves.
     */
    private double speed;

    /**
     * Indicates whether or not the tracer is enabled; if true, the turtle
     * draws at its configured speed. If disabled, the turtle draws nearly
     * instantly.
     */
    private boolean tracer;

    /**
     * The current visibility of the turtle's world; false if the turle's
     * world is not currently being displayed.
     */
    private boolean notDisplayed;

    /**
     * Initialized the turtle with its default settings.
     */
    private turtle() {
        // facing east
        angle = 0;

        //in the center of the canvas
        x = WIDTH / 2;
        y = HEIGHT / 2;

        // the group used to draw all of the various lines and shapes
        root = new Group();

        // sets the default color mode to 1.0
        colorMode = COLOR_MODE_1;

        // set the default pen color and fill color
        penColor = Color.BLACK;
        fillColor = Color.BLACK;

        speed = PIXELS_PER_SECOND;

        // initialize the turtle to be an arrow head.
        turtleShape = new Polygon(0, 0, -3.75, -5, 10, 0, -3.75, 5);
        turtleShape.setStroke(penColor);
        turtleShape.setFill(fillColor);
        turtleShape.setTranslateX(x);
        turtleShape.setTranslateY(y);

        // add the turtle to the root node...
        root.getChildren().add(turtleShape);

        // the pen starts in the down state by default
        penDown = true;

        // the tracer is enabled by default
        tracer = true;

        // the animator is a producer/consumer thread that insures that
        // animations are executed in order.
        animator = new Animator();
        Thread animationThread = new Thread(animator);
        animationThread.setDaemon(true);
        animationThread.start();

        // by default the turtle is not displayed.
        notDisplayed = true;
    }

    /**
     * Enables or disables the tracer. If the tracer is enabled, the turtle's
     * drawings are animated. If the tracer is disabled, the drawings are
     * essentially instantaneous.
     *
     * @param tracer True if the tracer should be enabled, false otherwise.
     */
    public void tracer(boolean tracer) {
        this.tracer = tracer;
    }

    /**
     * Returns the tracer's current state.
     *
     * @return True if the tracer is enabled, false otherwise.
     */
    public boolean tracer() {
        return tracer;
    }

    /**
     * Sets the color mode to either 1.0 (all RGB values are specified as
     * floating point values between 0.0 and 1.0) or 255 (all RGB values are
     * specified as floating point values between 0.0 and 255.0). All other
     * values are ignored.
     *
     * @param colorMode Either 1.0 or 255. All other values are ignored.
     */
    public void colorMode(double colorMode) {
        if(colorMode == COLOR_MODE_1) {
            this.colorMode = COLOR_MODE_1;
        }
        else if(colorMode == COLOR_MODE_255) {
            this.colorMode = COLOR_MODE_255;
        }
    }

    /**
     * Returns the current color mode; either  {@link #COLOR_MODE_1} or
     * {@link #COLOR_MODE_255}.
     *
     * @return The current color mode.
     */
    public double colorMode() {
        return colorMode;
    }

    /**
     * Sets the pen's color to the specified RGB values using decimal values
     * between 0.0 and 1.0.
     *
     * @param red The value for the red channel.
     * @param green The value for the green channel.
     * @param blue The value for the red channel.
     */
    public void penColor(double red, double green, double blue) {
        penColor(makeColor(red, green, blue));
    }

    /**
     * Sets the pen's color to the color matching the specified string.
     *
     * @param color The name of the color to which the pen color should be
     *              set. Must be a valid color from the colors defined in the
     *              {@link Color} class.
     */
    public void penColor(String color) {
        penColor(makeColor(color));
    }

    /**
     * Sets the fill color to the specified RGB values using decimal values
     * between 0.0 and 1.0.
     *
     * @param red The value for the red channel.
     * @param green The value for the green channel.
     * @param blue The value for the red channel.
     */
    public void fillColor(double red, double green, double blue) {
        fillColor(makeColor(red, green, blue));
    }

    /**
     * Sets the fill color to the color matching the specified string.
     *
     * @param color The name of the color to which the pen color should be
     *              set. Must be a valid color from the colors defined in the
     *              {@link Color} class.
     */
    public void fillColor(String color) {
        fillColor(makeColor(color));
    }

    /**
     * Sets the pen into the down (drawing) position.
     */
    public void pd() {
        penDown();
    }

    /**
     * Sets the pen into the down (drawing) position.
     */
    public void down() {
        penDown();
    }

    /**
     * Sets the pen into the down (drawing) position.
     */
    public void penDown() {
        penDown = true;
    }

    /**
     * Lifts the pen into the up (not drawing) position.
     */
    public void pu() {
        penUp();
    }

    /**
     * Lifts the pen into the up (not drawing) position.
     */
    public void up() {
        penUp();
    }

    /**
     * Lifts the pen into the up (not drawing) position.
     */
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

        double duration = degrees / DEGREES_PER_SECOND * 1000;

        display();
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(duration),
                new KeyValue(turtleShape.rotateProperty(), angle)));
        animator.addAnimation(animation);
    }

    public void lt(double degrees) {
        left(degrees);
    }

    public void left(double degrees) {
        angle -= degrees;

        double duration = degrees / DEGREES_PER_SECOND * 1000;

        display();
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(duration),
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
        double realY = HEIGHT / 2 - newY;

        Timeline animation = new Timeline();

        KeyValue[] keyValues = new KeyValue[penDown ? 4 : 2];
        keyValues[0] = new KeyValue(turtleShape.translateXProperty(), realX);
        keyValues[1] = new KeyValue(turtleShape.translateYProperty(), realY);

        if(penDown) {
            Line line = new Line(x, y, x, y);
            line.setStroke(Color.TRANSPARENT);

            animation.getKeyFrames().add(new KeyFrame(Duration.ONE,
                    new KeyValue(line.strokeProperty(), penColor)));

            root.getChildren().add(line);
            keyValues[2] = new KeyValue(line.endXProperty(), realX);
            keyValues[3] = new KeyValue(line.endYProperty(), realY);
        }

        Duration duration = getDuration(x, y, realX, realY);

        animation.getKeyFrames().add(
                new KeyFrame(duration, keyValues));

        x = realX;
        y = realY;

        animator.addAnimation(animation);
    }

    public void setX(double newX) {
        setPosition(newX, HEIGHT / 2 - y);
    }

    public void setY(double newY) {
        setPosition(x - WIDTH / 2, newY);
    }

    /**
     * Given a starting and an ending point, returns the approriate duration
     * of the animation given the current speed and tracer settings.
     *
     * @param startX The starting x-coordinate.
     * @param startY The starting y-coordinate.
     * @param endX The ending y-coordinate.
     * @param endY The ending y-coordinate.
     *
     * @return The {@link Duration} that the animation should require.
     */
    private Duration getDuration(double startX, double startY,
                               double endX, double endY) {
        // if the tracer is enabled, the duration is calculated...
        if(tracer) {
            // it id the distance (in pixels) divided by the current speed.
            double distance = euclidianDistance(startX, startY, endX, endY);
            return Duration.millis(distance / speed * 1000);
        }
        else {
            // if the tracer is disabled, the speed is instantaneous (1 ms).
            return Duration.ONE;
        }
    }

    /**
     * Sets the turtle's pen color using an animation to insure that it occurs
     * at the appropriate point in time (otherwise the color would change in
     * the midst of other animations; nonsensical).
     *
     * @param color The new pen color.
     */
    private void penColor(Color color) {
        penColor = color;

        Timeline animation = new Timeline(new KeyFrame(Duration.ONE,
                new KeyValue(turtleShape.strokeProperty(), color)));
        animator.addAnimation(animation);
    }

    /**
     * Sets the turtle's fill color using an animation to insure that it
     * occurs at the appropriate point in time (otherwise the color would
     * change in the midst of other animations; nonsensical).
     *
     * @param color The new fill color.
     */
    private void fillColor(Color color) {
        fillColor = color;

        Timeline animation = new Timeline(new KeyFrame(Duration.ONE,
                new KeyValue(turtleShape.fillProperty(), color)));
        animator.addAnimation(animation);
    }

    /**
     * Helper method that makes a new {@Color color} from the provided RGB
     * values. The provided values must be compatible with the current color
     * mode.
     *
     * @param red The red value.
     * @param green The green value.
     * @param blue The blue value.
     *
     * @return The new {@link Color}.
     */
    private Color makeColor(double red, double green, double blue) {
        if(colorMode == COLOR_MODE_255) {
            red = (double)red / 255;
            green = (double)green / 255;
            blue = (double)blue / 255;
        }

        if(red < 0 || red > 1 ||
                green < 0 || green > 1 ||
                blue < 0 || blue > 1 ) {
            throw new IllegalArgumentException("bad color sequence: (" +
                    red + ", " + green + ", " + blue + ")");
        }

        return new Color(red, green, blue, 1.0);
    }

    private Color makeColor(String color) {
        String upperColor = color.toUpperCase();

        try {
            Field theColor = Color.class.getField(color.toUpperCase());
            return (Color)theColor.get(null);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("bad color string: " + color);
        }
    }

    private double euclidianDistance(double startX, double startY,
                                     double endX, double endY) {
        Point2D start = new Point2D(startX, startY);
        Point2D end = new Point2D(endX, endY);

        return start.distance(end);
    }

    /**
     * If the JavaFX turtle application is not yet displayed, this method will
     * display it. It is called automatically from any method that moves the
     * turtle.
     */
    private void display() {
        if(notDisplayed) {
            // initialize the JavaFX platform
            PlatformImpl.startup(() -> {
            });
            // launch the turtle application
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

    /**
     * The turtle application. Displays the turtle's world in a JavaFX window.
     */
    private static class TurtleApp extends Application {

        @Override
        public void start(Stage primaryStage) {
            Scene scene = new Scene(turtle.root, WIDTH, HEIGHT,
                    Color.WHITE);

            primaryStage.setTitle("JTurtle!");
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.show();
        }
    }

    /**
     * A helper {@link Thread} that implements the producer/consumer design
     * pattern to insure that each turtle command/animation is executed in
     * turn.
     */
    private static class Animator implements Runnable,
            EventHandler<ActionEvent> {

        /**
         * The queue of {@link Animation animations} that need to be consumed.
         */
        private final List<Animation> queue;

        /**
         * Indicates whether or not the most recent animation has yet
         * finished.
         */
        private boolean finished;

        /**
         * Used to determine when the animator should terminate (e.g. upon an
         * exception).
         */
        private boolean running;

        Animator() {
            queue = new LinkedList<>();
            finished = true;
            running = true;
        }

        @Override
        public void run() {
            synchronized(queue) {
                while(running) {
                    // if the most recent animation has finished and there is
                    // at least one more animation to animate...
                    if(finished && queue.size() > 0) {
                        // indicate that the animation that is about to start
                        // is not finished...
                        finished = false;
                        // get the next animation
                        Animation next = queue.remove(0);
                        // add the animator as the on finish handler...
                        next.setOnFinished(this);
                        // and start the animation
                        next.play();
                    }

                    // wait for the next animation to be queued.
                    try {
                        queue.wait();
                    }
                    catch (InterruptedException e) {
                        // terminate on exception
                        running = false;
                    }
                }
            }
        }

        /**
         * Called when each animation finishes so that the next animation can
         * be started (if there is one in the queue).
         *
         * @param event The event indicating that an animation has completed.
         */
        @Override
        public void handle(ActionEvent event) {
            synchronized(queue) {
                finished = true;
                queue.notify();
            }
        }

        /**
         * Adds the specified {@link Animation} to the queue of
         * {@link Animation animations} to be performed.
         *
         * @param animation The {@link Animation} to add to the queue of
         * {@link Animation animations} to be performed.
         */
        private void addAnimation(Animation animation) {
            synchronized(queue) {
                queue.add(animation);
                queue.notify();
            }
        }
    }
}
