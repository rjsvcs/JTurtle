import static turtle.turtle.*;

public class turtleTest {

    public static void main(String[] args) {
        turtle.speed(10);

        turtle.penSize(2);
        turtle.colorMode(COLOR_MODE_1);

        int sides = 10;
        int distance = 100;
        while(distance > 1) {
            turtle.color(Math.random(), Math.random(), Math.random());
            turtle.backward(distance);
            distance = distance - 1;
            turtle.right(360/sides);
        }
    }
}
