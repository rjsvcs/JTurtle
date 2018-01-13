import static turtle.turtle.*;

public class HTree {

    public static void init(int length, int depth) {

    }

    public static void drawHTree(double length, int depth) {
        if(depth > 0) {
            turtle.forward(length / 2);
            turtle.left(90);
            turtle.forward(length / 2);
            turtle.right(90);

            drawHTree(length / 2, depth - 1);

            turtle.right(90);
            turtle.forward(length);
            turtle.left(90);

            drawHTree(length / 2, depth - 1);

            turtle.left(90);
            turtle.forward(length / 2);
            turtle.left(90);
            turtle.forward(length);
            turtle.right(90);
            turtle.forward(length / 2);
            turtle.right(90);

            drawHTree(length / 2, depth - 1);

            turtle.right(90);
            turtle.forward(length);
            turtle.left(90);

            drawHTree(length / 2, depth - 1);

            turtle.left(90);
            turtle.forward(length / 2);
            turtle.right(90);
            turtle.forward(length / 2);
        }
    }

    public static void main(String[] args) {
        turtle.speed(10);
        drawHTree(100, 3);
    }
}
