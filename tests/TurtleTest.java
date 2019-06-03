import static turtle.Turtle.*;

public class TurtleTest {
    public static void main(String[] args) {
        System.out.println("Turning left...");
        Turtle.left(360);
        System.out.println("left done, right...");
        Turtle.right(360);
        System.out.println("done");

        Turtle.setPosition(50, 50);

        Turtle.penColor("red");

        Turtle.drawText("Testing 1...2...3...");

        Turtle.fillColor(0, 0, 1);
       // Turtle.beginFill();
        Turtle.circle(100);
       // Turtle.endFill();
    }
}
