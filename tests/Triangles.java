import static turtle.Turtle.Turtle;

/**
 * The faculty solution for the Triangles activity; a line-for-line
 * port of the triangles.py program.
 */
public class Triangles {
    /**
     * Draws a number of concentric triangles based on the specified recursion
     * depth.
     * @param depth The recursion depth.
     * @param size The size of the legs of the next triangle.
     */
    public static void triangles(int depth, int size) {
        System.out.println(depth + ", " + size);
        if(depth <= 0) {
            return;
        } else {
            Turtle.down();
            Turtle.forward(size);
            Turtle.left(120);
            Turtle.forward(size);
            Turtle.left(120);
            Turtle.forward(size);
            Turtle.left(120);
            Turtle.up();
            Turtle.forward(size/2);
            Turtle.left(60);
            triangles(depth-1, size/2);
            Turtle.right(60);
            Turtle.back(size/2);
        }
    }

    /**
     * Prompts the user to enter a depth and a size. Positions the turtle, and
     * then calls the {@link #triangles(int, int)} function.
     *
     * @param args Command line arguments. Ignored.
     */
    public static void main(String[] args) {
        int depth = 9;
        int size = 300;

        Turtle.up();
        Turtle.back(0);

        Turtle.up();
        Turtle.left(90);
        Turtle.back(size/2);
        Turtle.right(90);
        Turtle.back(size/2);

        triangles(depth, size);

        System.out.println("Done!");
    }
}
