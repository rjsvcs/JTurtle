import static turtle.Turtle.Turtle;

public class StarTest {
    public static void main(String[] args) {
        Turtle.speed(5);
        Turtle.width(5);
        Turtle.right(108);
        for(int i=0; i<5; i++) {
            Turtle.forward(300);
            Turtle.left(144);
        }

        Turtle.hideTurtle();
        Turtle.setPosition(-100, -100);
        Turtle.showTurtle();
    }
}
