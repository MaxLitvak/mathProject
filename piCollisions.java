import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/*
This is a simulation of two blocks of different mass and velocity colliding on a frictionless floor and with purely
elastic collisions which means that no energy is lost among during collisions. The main class is piCollisions which runs the
simulation with the help of the Blocks class which constructs two Blocks, each with its own mass and velocity. For
this simulation, Block[0] is the first stationary block and Block[1] is the first moving block. In this
simulation, the velocity vectors are negative to the left and positive to the right, so Block[1] has an initially
negative velocity. I commented above all of the important lines, telling you what they do. You can also change some of
the variables to see the effects on the circle. When you run the program, after the blocks have finished colliding,
a graph will pop up.
*/

public class piCollisions implements Runnable {

    // variables

    JFrame frame;
    Canvas canvas;
    BufferStrategy bufferStrategy;
    private double block_zero_mass;
    private double block_one_mass;
    private int block_zero_velocity;
    private double block_one_velocity;
    private int block_zero_xpos;
    private int block_one_xpos;
    private int block_zero_ypos;
    private int block_one_ypos;
    private double initial_energy;
    private Color block_0_color;
    private Color block_1_color;
    private int nOfCollisions;
    private block[] Block;
    private double power;
    private ArrayList<double[]> points = new ArrayList<>();
    private boolean finished = false;
    private double ARC_length = 0;
    private boolean STOP = false;
    private boolean stopRender = false;

    public piCollisions() {
        frame = new JFrame("Basic Game");

        JPanel panel = (JPanel) frame.getContentPane();
        panel.setPreferredSize(new Dimension(1000, 700));
        panel.setLayout(null);

        canvas = new Canvas();
        canvas.setBounds(0, 0, 1000, 700);
        canvas.setIgnoreRepaint(true);

        panel.add(canvas);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        canvas.requestFocus();

    }

    public static void main(String[] args) {
        piCollisions ex = new piCollisions();
        // make new thread
        new Thread(ex).start();
    }

    //thread
    public void run() {

        block_0_color = new Color(170, 170, 170); // Color white
        block_1_color = new Color(50, 50, 50);
        block_zero_mass = 1;
        // change the power to change the mass of Block[1]. The number of digits of pi in the number of collisions is equal to
        // power+1
        power = 1;
        block_one_mass = Math.pow(100, power);
        block_zero_velocity = 0;
        // if you change the starting velocity of Block[1], the circumference of the circle will change but this will not effect
        // the number of collisions
        block_one_velocity = 0.5;
        block_zero_xpos = 300;
        block_one_xpos = 500;
        block_zero_ypos = 400;
        block_one_ypos = 400;
        Block = new block[2];
        // creating blocks
        Block[0] = new block(block_zero_mass, block_zero_velocity, block_zero_xpos, block_zero_ypos);
        Block[1] = new block(block_one_mass, block_one_velocity, block_one_xpos, block_one_ypos);


        while (true) {
            if (!stopRender) {
                // paint the graphics
                render();
            }
            // moves blocks
            moveEverything();
            // prints the number of collisions
            System.out.println("number of collisions   "+nOfCollisions);


            // sleep - this statement controls the speed of the program
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    //paints things on the screen using bufferStrategy
    private void render() {

        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 800, 800);
        g.setColor(Color.BLACK);
        g.drawLine(100, 100, 100, 500);
        g.drawLine(100, 500, 700, 500);
        g.setColor(block_0_color);
        g.fill(Block[0].getRec());
        g.setColor(block_1_color);
        g.fill(Block[1].getRec());

        if (finished == true) {
            g.clearRect(0, 0, 1000, 1000);

            // creating the axes
            g.drawLine(100, 300, 900, 300);
            g.drawLine(500, 700, 500, 25);

            for (int x = 0; x < points.size(); x++) {

                // declaring instance variables (only can be accessed inside of this for loop)
                double ADJ = 0;
                double OPP = 0;
                // this variable is very important because it puts the circle into perspective. For a power of 0 and 1, you
                // can use 45 but for a power of 2, I would use 7. I have not tried any powers greater than 2 and I don't recommend
                // it unless you want your computer's fan to get really loud. Because the program continuously renders, the computer
                // works really hard to do all of these calculations at a very fast speed
                int sizing = 45;
                double sqrt0 = Math.sqrt(Block[0].getMass());
                double sqrt1 = Math.sqrt(Block[1].getMass());
                // X and Y are the coordinates for the graph. X2 and Y2 are also coordinates but they are expressed differently
                // if you switch out X for X2 or Y for Y2, the results will be the same but will only show up in one of the
                // quadrants in the graph because of the issue of square rooting negative
                // numbers. I have them to show that the algebra I did in algebra_2 in my attached folder is correct.
                double X = points.get(x)[0] * sqrt0;
                double Y = points.get(x)[1] * sqrt1 * -1;
                double X2 = Math.sqrt(2 * initial_energy - (Math.pow(points.get(x)[1] * -1, 2) * Block[1].getMass()));
                double Y2 = Math.sqrt(2 * initial_energy - (Math.pow(points.get(x)[0], 2) * Block[0].getMass()));
                // radius of the circle
                double radius = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
                // print radius
                System.out.println("radius   "+radius);
                // circumference of the circle
                double circumference = 2 * radius * Math.PI;
                // print circumference
                System.out.println("circumference    "+circumference);

                // point is a very small rectangle that will plot on the graph
                Rectangle2D.Double point = new Rectangle2D.Double(500 - X * sizing, 300 - Y * sizing, 2, 2);
                g.setColor(Color.RED);
                // draws "point"
                g.fill(point);

                // this if statement says that if a point is not the last one in the arraylist and it is odd, then find the
                // angle between the slanted line and constant line that intersect it
                if (x + 1 < points.size() && x % 2 == 0) {
                    double slope = (points.get(x + 1)[1] * -1 * sqrt1 - Y) / (points.get(x + 1)[0] * sqrt0 - X);
                    double opp = points.get(x + 1)[0] * -1 * slope;
                    double adj = points.get(x + 1)[0] * -1;
                    double angle = Math.toDegrees(Math.atan((opp * -1) / adj));
                    double arc_length = (-2 * angle * circumference) / 360;
                    ARC_length = arc_length;
                    ADJ = adj;
                    OPP = opp;

                    // prints the angle and then the arc length
                    System.out.println((2 * Math.PI * angle) / 360 + "   angle");
                    System.out.println(arc_length + "  arc length");
                }

                // this if statement finds the missing arc length between the second to last and last point on the graph
                if (x == points.size() - 1) {
                    double hypo1 = Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
                    double opp1 = X;
                    double angle1 = Math.toDegrees(Math.asin(opp1 / hypo1));
                    double hypo2 = Math.sqrt(Math.pow(points.get(x - 1)[1] * sqrt1, 2) + Math.pow(points.get(x - 1)[0] * sqrt0, 2));
                    double opp2 = points.get(x - 1)[0] * sqrt0;
                    double angle2 = Math.toDegrees(Math.asin(opp2 / hypo2));
                    double nextAngle = angle2 - angle1;
                    double arc_length = (nextAngle * circumference) / 360;

                    // this line shows that there is a small piece missing from circle that is not accounted for in the arcs derived
                    // from the angles between intersecting lines
                    System.out.println("arc lengths add up to circumference " + (Math.abs(arc_length) + points.size() * ARC_length));
                }

                // draws numbers next to the points that correspond to collisions (2 is the first collision)
                g.setColor(Color.BLACK);
                if (X * sqrt0 < 0) {
                    g.drawString(Integer.toString(x + 1), (int) (500 - X * sizing) + 5, (int) (300 - Y * sizing));
                } else {
                    g.drawString(Integer.toString(x + 1), (int) (500 - X * sizing) - 15, (int) (300 - Y * sizing));
                }

                // draws the lines between points - green for constant lines and blue for sloped lines
                if (x + 1 < points.size()) {
                    if (X * -1 == points.get(x + 1)[0]) {
                        g.setColor(Color.GREEN);
                    } else {
                        g.setColor(Color.BLUE);
                    }
                    g.draw(new Line2D.Double(500 - X * sizing, 300 - Y * sizing, 500 - points.get(x + 1)[0] * sqrt0 * sizing, 300 - points.get(x + 1)[1] * -1 * sqrt1 * sizing));
                }

                // if you run the program with a power of 1, this highlights the sides that I used to derive the angle between
                // lines
                if (x == 16) {
                    g.setColor(Color.RED);
                    g.draw(new Line2D.Double(500, 300+ Y * -sizing, 500 + ADJ * -sizing, 300+ Y * -sizing));
                    g.setColor(Color.MAGENTA);
                    g.draw(new Line2D.Double(500, 300 + Y * -sizing, 500, 300 + Y*-sizing + OPP * sizing));
                }
            }
            stopRender = true;
        }

        g.dispose();

        bufferStrategy.show();
    }

    // moves the blocks and counts collisions. This method was really annoying to write and there is nothing in it
    // to change so I won't comment it to show you what it is doing
    private void moveEverything() {

        if (STOP == false) {
            double[] graphPoints = new double[2];
            graphPoints[0] = Block[0].getVelocity();
            graphPoints[1] = Block[1].getVelocity();
            points.add(graphPoints);
            STOP = true;
        }

        double newVelocity = 0;
        boolean closeToLine = false;
        double toLine = 0;
        boolean noChange = false;
        boolean stop = false;


        if (Block[0].getVelocity() <= 0 && Block[1].getVelocity() <= 0 && Block[1].getVelocity() <= Block[0].getVelocity() && Block[1].getXpos() - Block[0].getXpos() > 200) {
            finished = true;
        }
        if (finished == true) {

        } else {
            if ((Block[1].getXpos() - Block[1].getVelocity()) < (Block[0].getXpos() + 100 - Block[0].getVelocity())) {
                double slowerV1 = 0;
                double slowerV0 = 0;
                boolean slower = false;

                double intersectionStep1 = (Block[0].getXpos() + 100 - Block[1].getXpos()) / (Block[0].getVelocity() - Block[1].getVelocity());
                double xposOfIntersection = Block[1].getVelocity() * intersectionStep1 + Block[1].getXpos();
                double smaller = 0.5;
                if (Math.abs(Block[0].getXpos() + 100 - xposOfIntersection) > smaller) {
                    double sign = (Block[0].getXpos() - xposOfIntersection) / (Math.sqrt(Math.pow(Block[0].getXpos() - xposOfIntersection, 2)));
                    slowerV0 = Block[0].getXpos() - sign * smaller;
                    slowerV1 = Block[1].getXpos() + (smaller * Block[1].getVelocity()) / Block[0].getVelocity();
                    slower = true;
                }
                if (slower == true) {
                    Block[0].new_xpos(slowerV0);
                    Block[0].new_rectangle();
                    Block[1].new_xpos(slowerV1);
                    Block[1].new_rectangle();
                    render();
                    stop = true;
                } else {
                    Block[0].new_xpos(xposOfIntersection - 100);
                    Block[0].new_rectangle();
                    Block[1].new_xpos(xposOfIntersection);
                    Block[1].new_rectangle();
                    nOfCollisions++;
                    calculate_new_velocity();
                    render();

                    double[] graphPoints = new double[2];
                    graphPoints[0] = Block[0].getVelocity();
                    graphPoints[1] = Block[1].getVelocity();
                    points.add(graphPoints);
                }
            } else {
                if (Block[0].getXpos() + 100 - Block[0].getVelocity() == Block[1].getXpos() - Block[1].getVelocity()) {
                    calculate_new_velocity();
                    nOfCollisions++;

                    double[] graphPoints = new double[2];
                    graphPoints[0] = Block[0].getVelocity();
                    graphPoints[1] = Block[1].getVelocity();
                    points.add(graphPoints);
                }
            }

            if (Block[0].getXpos() - Block[0].getVelocity() < 100) {
                if (Block[0].getXpos() - 100 > .5) {
                    toLine = .5;
                    noChange = true;
                } else {
                    toLine = Block[0].getXpos() - 100;
                    nOfCollisions++;
                }
                newVelocity = (toLine * Block[1].getVelocity()) / Block[0].getVelocity();
                closeToLine = true;
            }

            if (stop == false) {
                if (closeToLine == false) {
                    Block[0].new_xpos(Block[0].getXpos() - Block[0].getVelocity());
                    Block[0].new_rectangle();
                    Block[1].new_xpos(Block[1].getXpos() - Block[1].getVelocity());
                    Block[1].new_rectangle();
                } else {
                    Block[0].new_xpos(Block[0].getXpos() - toLine);
                    Block[0].new_rectangle();
                    Block[1].new_xpos(Block[1].getXpos() - newVelocity);
                    Block[1].new_rectangle();
                    if (noChange == false) {
                        Block[0].new_velocity(Block[0].getVelocity() * -1);
                        double[] graphPoints = new double[2];
                        graphPoints[0] = Block[0].getVelocity();
                        graphPoints[1] = Block[1].getVelocity();
                        points.add(graphPoints);
                    }
                    render();
                }
            }
        }
    }

    // calculates the energy of the system
    private void calculate_initial_energy() {
        Block[0].calculate_kinetic_energy();
        Block[1].calculate_kinetic_energy();
        initial_energy = Block[0].getKineticEnergy() + Block[1].getKineticEnergy();
    }

    // using the equations that I derived in algebra_1 in the attached folder, this method calculates the new velocities of the
    // blocks after each collision
    public void calculate_new_velocity() {
        double block0NewVelocity;
        double block1NewVelocity;
        calculate_initial_energy();

        block1NewVelocity = (Block[1].getMass() * Block[1].getVelocity() - Block[0].getMass() * (Block[1].getVelocity() - 2 * Block[0].getVelocity())) / (Block[0].getMass() + Block[1].getMass());
        block0NewVelocity = block1NewVelocity + Block[1].getVelocity() - Block[0].getVelocity();
        Block[0].new_velocity(block0NewVelocity);
        Block[1].new_velocity(block1NewVelocity);
    }
}
