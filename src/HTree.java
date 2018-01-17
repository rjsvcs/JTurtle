import static turtle.Turtle.*;

public class HTree {

    public static void init(int length, int depth) {
        Turtle.setWorldCoordinates(200, 200, 200, 200 );
        Turtle.title("H-Tree, depth: " + depth);
    }

    public static void drawHTree(double length, int depth) {
        if(depth > 0) {
            Turtle.forward(length / 2);
            Turtle.left(90);
            Turtle.forward(length / 2);
            Turtle.right(90);

            drawHTree(length / 2, depth - 1);

            Turtle.right(90);
            Turtle.forward(length);
            Turtle.left(90);

            drawHTree(length / 2, depth - 1);

            Turtle.left(90);
            Turtle.forward(length / 2);
            Turtle.left(90);
            Turtle.forward(length);
            Turtle.right(90);
            Turtle.forward(length / 2);
            Turtle.right(90);

            drawHTree(length / 2, depth - 1);

            Turtle.right(90);
            Turtle.forward(length);
            Turtle.left(90);

            drawHTree(length / 2, depth - 1);

            Turtle.left(90);
            Turtle.forward(length / 2);
            Turtle.right(90);
            Turtle.forward(length / 2);
        }
    }

    public static void main(String[] args) {
        int length = 200;
        int depth = 3;

        init(length, depth);
        Turtle.speed(0);
        Turtle.penColor("orange");
        drawHTree(length, depth);
    }
}
