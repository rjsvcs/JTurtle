import static turtle.Turtle.*;

public class FillTest {
    public static void main(String[] args) {
        Turtle.speed(5);
        //Turtle.up();
        Turtle.fillColor(1, 0.5, 0.5);
        Turtle.beginFill();
        polygon(100, 8);
        Turtle.endFill();
    }

    private static void polygon(int length, int sides) {
        for(int s=0; s<sides; s++) {
            Turtle.forward(length);
            Turtle.left(360 / sides);
        }
    }
}
