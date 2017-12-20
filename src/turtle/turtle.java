package turtle;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class turtle {

    private static final turtle THE_TURTLE = new turtle();

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private double x;
    private double y;
    private double angle;

    private boolean notDisplayed;

    private TurtleCanvas canvas;

    private turtle() {
        x = 0;
        y = 0;
        angle = 0;

        canvas = new TurtleCanvas(this);
        canvas.setHeight(HEIGHT);
        canvas.setWidth(WIDTH);

        notDisplayed = true;
    }

    public static void fd(double distance) {
        forward(distance);
    }

    public static void forward(double distance) {
        THE_TURTLE.display();
        THE_TURTLE.canvas.repaint();
    }

    public static void bk(double distance) {
        backward(distance);
    }

    public static void back(double distance) {
        backward(distance);
    }

    public static void backward(double distance) {

    }

    public static void rt(double degrees) {
        right(degrees);
    }

    public static void right(double degrees) {

    }

    public static void lt(double degrees) {
        left(degrees);
    }

    public static void left(double degrees) {

    }

    public static void goTo(double x, double y) {
        setPosition(x, y);
    }

    public static void setPos(double x, double y) {
        setPosition(x, y);
    }

    public static void setPosition(double x, double y) {

    }

    public static void setX(double x) {

    }

    public static void setY(double y) {

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

    public static class TurtleApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            Pane root = new Pane();
            root.getChildren().add(THE_TURTLE.canvas);

            Scene scene = new Scene(root);

            primaryStage.setTitle("JTurtle!");
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.show();
        }
    }

    private static class TurtleCanvas extends Canvas {
        private turtle turtle;

        TurtleCanvas(turtle turtle) {
            this.turtle = turtle;
        }

        private void repaint() {
            GraphicsContext g = getGraphicsContext2D();

            g.clearRect(0, 0, getWidth(), getHeight());

            g.setFill(Color.RED);

            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
