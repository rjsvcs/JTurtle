import static turtle.turtle.*;

public class turtleTest {

    public static void main(String[] args) {
        turtle.speed(10);

        turtle.colorMode(COLOR_MODE_1);

        int sides = 10;
        int distance = 100;

        while(distance > 0) {
            turtle.color(Math.random(), Math.random(), Math.random());
            turtle.forward(distance);
            distance = distance - 1;
            turtle.left(360 / sides);
        }
    }
}
