/*
 * JavaFX turtle implementation. Provides an animated turtle object that can
 * be programmatically controlled to draw pictures using a turtle.
 *
 * @author Bobby St. Jacques
 */
package turtle;

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
     * The slowest configurable speed for the turtle (1).
     */
    public static final int SPEED_SLOWEST = 1;

    /**
     * The default speed for the turtle (5).
     */
    public static final int SPEED_DEFAULT = 5;

    /**
     * A fast speed for the turtle (10).
     */
    public static final int SPEED_FAST = 10;

    /**
     * The fastest speed for the turtle (0).
     */
    public static final int SPEED_FASTEST = 0;

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
    private static final double PIXELS_PER_UNIT_OF_SPEED = 100;

    /**
     * The default speed at which the turtle turns in degrees per second.
     */
    private static final double DEGREES_PER_SECOND = 90;

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
     * The current location of the turtle as a {@link Point2D}. This is the
     * location of the turtle in the turtle's world (with the origin (0,0)) in
     * the center of the canvas.
     */
    private Point2D location;

    /**
     * The JavaFX group to which all shapes are added to be rendered.
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
     * The width of the turtle's pen.
     */
    private int width;

    /**
     * The speed at which the turtle moves.
     */
    private double speed;

    /**
     * Indicates whether or not the tracer is enabled; if true, the turtle
     * draws at its configured speed. If disabled, the turtle draws nearly
     * instantaneously.
     */
    private boolean tracer;

    /**
     * The current visibility of the turtle's world; false if the turtle's
     * world is not currently being displayed.
     */
    private boolean notDisplayed;

    /**
     * The JavaFX application in which the turtle runs.
     */
    private TurtleApp application;

    /**
     * The handler that notifies the turtle as each animation finishes.
     */
    private OnFinishedHandler finisher;

    /**
     * Initializes the turtle with its default settings.
     */
    private turtle() {
        // facing east
        angle = 0;

        // default pen width
        width = 1;

        //in the center of the canvas
        location = new Point2D(0, 0);

        // the group used to draw all of the various lines and shapes
        root = new Group();

        // sets the default color mode to 1.0
        colorMode = COLOR_MODE_1;

        // set the default pen color and fill color
        penColor = Color.BLACK;
        fillColor = Color.BLACK;

        speed = SPEED_DEFAULT;

        // initialize the turtle to be an arrow head.
        turtleShape = new Polygon(0, 0, -3.75, -5, 10, 0, -3.75, 5);
        turtleShape.setStroke(penColor);
        turtleShape.setFill(fillColor);
        turtleShape.setTranslateX(WIDTH / 2);
        turtleShape.setTranslateY(HEIGHT / 2);

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

        // by default the turtle;s world is not displayed.
        notDisplayed = true;

        // the event handler that stops the turtle from blocking as animations
        // complete.
        finisher = new OnFinishedHandler();
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
     * Returns the current color mode; either {@link #COLOR_MODE_1} or
     * {@link #COLOR_MODE_255}.
     *
     * @return The current color mode.
     */
    public double colorMode() {
        return colorMode;
    }

    /**
     * @see #penColor(String).
     */
    public void color(String color) {
        turtle.penColor(color);
    }

    /**
     * @see #penColor(double, double, double)
     */
    public void color(double red, double green, double blue) {
        turtle.penColor(red, green, blue);
    }

    /**
     * Sets the pen's color to the specified RGB values using decimal values
     * based on the current color mode.
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
     * based on the current color mode.
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


    /**
     * @see #forward(double)
     */
    public void fd(double distance) {
        forward(distance);
    }

    /**
     * Moves the turtle the specified distance in the direction that it is
     * currently facing.
     *
     * @param distance The distance to move the turtle.
     */
    public void forward(double distance) {
        Point2D end = calculateEndPoint(angle, location, distance);

        setPosition(end.getX(), end.getY());
    }

    /**
     * @see #backward(double)
     */
    public void bk(double distance) {
        backward(distance);
    }

    /**
     * @see #backward(double)
     */
    public void back(double distance) {
        backward(distance);
    }

    /**
     * Moves the turtle backwards the specified distance.
     *
     * @param distance This distance to move the turtle backwards.
     */
    public void backward(double distance) {
        Point2D end = calculateEndPoint(angle + 180, location, distance);
        setPosition(end.getX(), end.getY());
    }

    /**
     * Turns the turtle right the specified number of degrees.
     *
     * @param degrees The number of degrees to turn the turtle to the right.
     */
    public void rt(double degrees) {
        right(degrees);
    }

    /**
     * Turns the turtle right the specified number of degrees.
     *
     * @param degrees The number of degrees to turn the turtle to the right.
     */
    public synchronized void right(double degrees) {
        display();

        angle += degrees;

        double duration = degrees / DEGREES_PER_SECOND * 1000;

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(duration),
                new KeyValue(turtleShape.rotateProperty(), angle)));
        animation.setOnFinished(finisher);
        animate(animation);
    }

    /**
     * Turns the turtle left the specified number of degrees.
     *
     * @param degrees The number of degrees to turn the turtle to the left.
     */
    public void lt(double degrees) {
        left(degrees);
    }

    /**
     * Turns the turtle left the specified number of degrees.
     *
     * @param degrees The number of degrees to turn the turtle to the left.
     */
    public void left(double degrees) {
        display();

        angle -= degrees;

        double duration = degrees / DEGREES_PER_SECOND * 1000;

        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(duration),
                new KeyValue(turtleShape.rotateProperty(), angle)));
        animation.setOnFinished(finisher);
        animate(animation);
    }

    /**
     * Moves the turtle to the specified x/y coordinate. If the pen is down,
     * the turtle draws a line.
     *
     * @param x The turtle's new x coordinate.
     * @param y The turtle's new y coordinate.
     */
    public void goTo(double x, double y) {
        setPosition(x, y);
    }

    /**
     * Moves the turtle to the specified x/y coordinate. If the pen is down,
     * the turtle draws a line.
     *
     * @param x The turtle's new x coordinate.
     * @param y The turtle's new y coordinate.
     */
    public void setPos(double x, double y) {
        setPosition(x, y);
    }

    /**
     * Moves the turtle to the specified x/y coordinate. If the pen is down,
     * the turtle draws a line.
     *
     * @param x The turtle's new x coordinate.
     * @param y The turtle's new y coordinate.
     */
    public void setPosition(double x, double y) {
        display();

        Point2D start = translateToCoordinates(location);
        location = new Point2D(x, y);
        Point2D end = translateToCoordinates(location);

        Timeline animation = new Timeline();

        KeyValue[] keyValues = new KeyValue[penDown ? 4 : 2];
        keyValues[0] = new KeyValue(turtleShape.translateXProperty(),
                end.getX());
        keyValues[1] = new KeyValue(turtleShape.translateYProperty(),
                end.getY());

        if(penDown) {
            Line line = new Line(start.getX(), start.getY(), start.getX(),
                    start.getY());
            line.setStrokeWidth(width);
            line.setStroke(Color.TRANSPARENT);

            animation.getKeyFrames().add(new KeyFrame(Duration.ONE,
                    new KeyValue(line.strokeProperty(), penColor)));

            root.getChildren().add(line);
            keyValues[2] = new KeyValue(line.endXProperty(), end.getX());
            keyValues[3] = new KeyValue(line.endYProperty(), end.getY());
        }

        Duration duration = getDuration(start.getX(), start.getY(),
                end.getX(), end.getY());

        animation.getKeyFrames().add(
                new KeyFrame(duration, keyValues));
        animator.addAnimation(animation);

        turtleShape.toFront();
    }

    /**
     * Moves the turtle to the specified x coordinate. The turtle's y
     * coordinate remains unchanged. If the pen is down, the turtle draws a
     * line.
     *
     * @param x The turtle's new x coordinate.
     */
    public void setX(double x) {
        setPosition(x, location.getY());
    }

    /**
     * Moves the turtle to the specified y coordinate. The turtle's x
     * coordinate remains unchanged. If the pen is down, the turtle draws a
     * line.
     *
     * @param y The turtle's new x coordinate.
     */
    public void setY(double y) {
        setPosition(location.getX(), y);
    }

    public void width(int width) {
        penSize(width);
    }

    public void penSize(int width) {
        if(width < 0) {
            throw new IllegalArgumentException("Pen width must be positive: "
                    + width);
        }
        this.width = width;
    }

    /**
     * Sets the turtle's speed to the specified value.
     *
     * @param speed The new speed of the turtle, a value between 1 (slow) and
     *              10 (fast) or 0 (fastest).
     */
    public void speed(int speed) {
        if(speed <= SPEED_FASTEST) {
            this.speed = SPEED_FASTEST;
        }
        else if( speed >= SPEED_FAST) {
            this.speed = SPEED_FAST;
        }
        else {
            this.speed = speed;
        }
    }

//    public void circle(double radius) {
//        int circumferance = (int)Math.ceil(Math.PI * radius * 2);
//        double degrees = 360.0 / circumferance;
//
//        for(int i=0; i<circumferance; i++) {
//            turtle.forward(1);
//            turtle.left(degrees);
//        }
//    }

    /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS. Most of these translate turtlish stuff to JavaFX.  //
    /////////////////////////////////////////////////////////////////////////

    private synchronized void animate(Animation animation) {
        // set the on finished handler to stop the turtle from blocking
        animation.setOnFinished((e) -> {
            synchronized(turtle.this) {
                // this will wake the turtle from the wait state
                turtle.notify();
            }
        });
        // play the animation
        animation.play();
        // wait for notification that the animation is done
        waitForNotify();
    }

    private synchronized void waitForNotify() {
        try {
            wait();
        } catch (InterruptedException e) {
            // squash
        }
    }

    /**
     * The turtle uses a coordinate plane where the origin, (0,0) is in the
     * center of the canvas the y is positive in the UP direction. JavaFX uses
     * a coordinate plane where the origin, (0, 0), is in the top left corner
     * of the screen and y is positive in the DOWN direction. Given a turtle
     * coordinate as a {@link Point2D}, this method will translate it into a
     * JavaFX coordinate.
     *
     * @param point The turtle coordinate as a {@link Point2D}.
     *
     * @return The translated coordinate as a{@link Point2D}.
     */
    private Point2D translateToCoordinates(Point2D point) {
        return translateToCoordinates(point.getX(), point.getY());
    }

    /**
     * The turtle uses a coordinate plane where the origin, (0,0) is in the
     * center of the canvas the y is positive in the UP direction. JavaFX uses
     * a coordinate plane where the origin, (0, 0), is in the top left corner
     * of the screen and y is positive in the DOWN direction. Given a turtle
     * coordinate as an x/y pair, this method will translate it into a JavaFX
     * coordinate.
     *
     * @param x The turtle's x coordinate.
     * @param y The turtle's y coordinate.
     *
     * @return The translated coordinate as a {@link Point2D}.
     */
    private Point2D translateToCoordinates(double x, double y) {
        double realX = x + WIDTH / 2;
        double realY = HEIGHT / 2 - y;

        return new Point2D(realX, realY);
    }

    /**
     * Given an angle, a starting point, and a distance, calculates the end
     * point that a turtle would arrive at.
     *
     * @param angle The angle of the turtle.
     * @param start The starting point.
     * @param distance The distance that the turtle should move.
     * @return The end point.
     */
    private Point2D calculateEndPoint(double angle, Point2D start,
                                      double distance) {

        double radians = Math.toRadians(angle + 90);

        // calculate the distance in the x direction
        double sine = Math.sin(radians);
        double newX = distance * sine + start.getX();

        double cosine = Math.cos(radians);
        double newY = distance * cosine + start.getY();

        Point2D end = new Point2D(newX, newY);

        System.out.println("Calculating end point...");
        System.out.println("  distance: " + distance);
        System.out.println("  angle: " + angle);
        System.out.println("  start: " + start);
        System.out.println("  end: " + end);

        return end;
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
        if(tracer && speed != SPEED_FASTEST) {
            // calculate the distance
            double distance = euclidianDistance(startX, startY, endX, endY);
            // calculate the number of pixels that the turtle should travel
            // per second
            double pixels_per_second = speed != SPEED_FASTEST ?
                            PIXELS_PER_UNIT_OF_SPEED * speed : 1000;
            double seconds = distance / pixels_per_second;

            return Duration.millis(seconds * 1000.0);
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
     * Helper method that makes a new {@link Color color} from the provided
     * RGB values. The provided values must be compatible with the current
     * color mode.
     *
     * @param red The red value.
     * @param green The green value.
     * @param blue The blue value.
     *
     * @return The new {@link Color}.
     */
    private Color makeColor(double red, double green, double blue) {
        if(colorMode == COLOR_MODE_255) {
            red = red / 255.0;
            green = green / 255.0;
            blue = blue / 255.0;
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
        try {
            Field theColor = Color.class.getField(color.toUpperCase());
            return (Color)theColor.get(null);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("bad color string: " + color);
        }
    }

    /**
     * Calculates the Euclidian distance between two points.
     * @param startX The x coordinate of the first point.
     * @param startY The y coordinate of the first point.
     * @param endX The x coordinate of the second point.
     * @param endY The y coordinate of the second point.
     * @return The distance between the two points.
     */
    private double euclidianDistance(double startX, double startY,
                                     double endX, double endY) {
        Point2D start = new Point2D(startX, startY);
        Point2D end = new Point2D(endX, endY);

        return start.distance(end);
    }

    /**
     * If the JavaFX turtle application is not yet displayed, this method will
     * display it. It is called automatically from any method that moves the
     * turtle. This method blocks until the application has started.
     */
    private synchronized void display() {
        if(notDisplayed) {
            // initialize the JavaFX platform
            Platform.startup(() -> {
                synchronized(turtle.this) {
                    turtle.this.notify();
                }
            });
            // wait for the platform to startup
            waitForNotify();

            // launch the turtle application
            Platform.runLater(() -> {
                try {
                    application = new TurtleApp();
                    application.start(new Stage());
                } catch (Exception e) {
                    // squash
                }
                synchronized(turtle.this) {
                    turtle.this.notify();
                }
            });
            // wait for the application to start up
            waitForNotify();
            notDisplayed = false;
        }
    }

    /**
     * An {@link EventHandler} that notifies the turtle when an animation is
     * complete.
     */
    private class OnFinishedHandler implements EventHandler<ActionEvent> {
        /**
         * Called when an {@link Animation} is complete. Used to notify the
         * turtle so that it stops blocking.
         *
         * @param event The event indicating that the {@link Animation} is
         *              complete.
         */
        @Override
        public void handle(ActionEvent event) {
            synchronized (turtle.this) {
                turtle.this.notify();
            }
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
