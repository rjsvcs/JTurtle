import static turtle.Turtle.*;

public class FillTest {
    public static void main(String[] args) {
        Turtle.speed(5);
        Turtle.bgcolor(0, 0, 0);
        //Turtle.up();
        Turtle.color(1, 1, 1);
        Turtle.width(2);
        Turtle.fillColor(1, 0.5, 0.5);
        Turtle.beginFill();
        Turtle.forward(300);
        Turtle.left(144);
        Turtle.forward(300);
        Turtle.left(144);
        Turtle.forward(300);
        Turtle.left(144);
        Turtle.forward(300);
        Turtle.left(144);
        Turtle.forward(300);
        Turtle.left(144);
        Turtle.endFill();
        Turtle.bgcolor(0.5, 0.5, 0.5);

        Turtle.up();
        Turtle.setPosition(-200, -200);
        Turtle.down();
        Turtle.width(3);
        Turtle.beginFill();
        for(int i=0; i<4; i++) {
            Turtle.forward(100);
            Turtle.left(90);
        }
        Turtle.endFill();

    }

    private static void polygon(double length, int sides) {
        for(int s=0; s<sides; s++) {
            Turtle.forward(length);
            Turtle.left(360 / sides);
        }
    }
}
