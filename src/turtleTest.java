import static turtle.Turtle.*;

public class turtleTest {
    public static void main(String[] args) {
        System.out.println("Turning left...");
        Turtle.left(360);
        System.out.println("left done, right...");
        Turtle.right(360);
        System.out.println("done");

        Turtle.setPosition(50, 50);

        Turtle.circle(100);
    }
}
