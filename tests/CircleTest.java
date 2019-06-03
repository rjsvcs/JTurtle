import static turtle.Turtle.*;

public class CircleTest {
    public static void main(String[] args) {

        Turtle.up();
        Turtle.left(45);
        Turtle.down();
        Turtle.width(4);
        Turtle.penColor("RED");
        Turtle.fillColor("blue");
        Turtle.beginFill();
        Turtle.circle(100, 180);
        Turtle.endFill();

    }
}
