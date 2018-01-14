import static turtle.turtle.*;

public class HTree {

    public static void init(int length, int depth) {
        turtle.title("H-Tree, depth: " + depth);
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
        int length = 200;
        int depth = 3;

        init(length, depth);
        turtle.speed(0);
        drawHTree(length, depth);
    }
}
