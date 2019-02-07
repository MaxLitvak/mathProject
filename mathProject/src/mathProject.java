import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/*
This is a simulation of two blocks of different mass and velocity colliding on a frictionless floor and with purely
elastic collisions, which means that no energy is lost among colliding. The main class is MathBlocks, which runs the
simulation with the help of the Blocks class, which constructs two Blocks, each with its own mass and velocity. For
this simulation, Block[0] is the at first stationary block with the smaller mass and Block[1] is the first moving block
with the bigger mass. This program is completely object oriented, meaning that one must call methods from another class
in order to access its variables. For example, to access a block's mass, one must call the public method "getMass()"
of the Blocks class as opposed to calling the variable itself. This is because the block's variables are all private
and its methods public, meaning that a class only has access to the other class' methods, not its variables. In this
simulation, the velocity vectors are negative to the right and positive to the left, so Block[1] has an initially
positive velocity.
 */

public class mathProject implements Runnable {
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
    private double initial_momentum;
    private int count = 0;
    private Color block_0_color;
    private Color block_1_color;
    private int nOfCollisions;
    private block[] Block;
    private int power;
    private ArrayList<double[]> points = new ArrayList<>();
    private ArrayList<double[]> pointsXpos = new ArrayList<>();
    private boolean finished = false;


    public mathProject() {
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
        mathProject ex = new mathProject();
        new Thread(ex).start();
    }

    //thread
    public void run() {

        block_0_color = new Color(170, 170, 170); // Color white
        block_1_color = new Color(50, 50, 50);
        block_zero_mass = 1;
        power = 2;
        block_one_mass = Math.pow(100, power);
        block_zero_velocity = 0;
        block_one_velocity = .5;
        block_zero_xpos = 300;
        block_one_xpos = 500;
        block_zero_ypos = 400;
        block_one_ypos = 400;
        Block = new block[2];
        Block[0] = new block(block_zero_mass, block_zero_velocity, block_zero_xpos, block_zero_ypos);
        Block[1] = new block(block_one_mass, block_one_velocity, block_one_xpos, block_one_ypos);


        while (true) {
            // paint the graphics
            render();
            // moves blocks
            moveEverything();
            System.out.println(nOfCollisions);


            //sleep
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    //paints things on the screen using bufferStrategy
    private void render() {

        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

        if (finished == false) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 800, 800);
            g.setColor(Color.BLACK);
            g.drawLine(100, 100, 100, 500);
            g.drawLine(100, 500, 700, 500);
            g.setColor(block_0_color);
            g.fill(Block[0].getRec());
            g.setColor(block_1_color);
            g.fill(Block[1].getRec());
        }

        if (finished == true) {
            for (int x = 0; x < points.size(); x++) {
                double X = points.get(x)[0] * 5;
                double Y = points.get(x)[1] * 5;
                Rectangle2D.Double point = new Rectangle2D.Double(500 + X * Math.sqrt(Block[0].getMass()), 300 + Y * Math.sqrt(Block[1].getMass()), 2, 2);
                g.fill(point);
                if (x + 1 < points.size()) {
                    g.draw(new Line2D.Double(500 + X * Math.sqrt(Block[0].getMass()), 300 + Y * Math.sqrt(Block[1].getMass()), 500 + points.get(x + 1)[0] * 5 * Math.sqrt(Block[0].getMass()), 300 + points.get(x + 1)[1] * 5 * Math.sqrt(Block[1].getMass())));
                }
            }
        }

        /*
        if (finished == true) {
            g.clearRect(0, 0, 800, 800);
            g.drawLine(500, 300, 870, 300);
            g.drawLine(500, 50, 500, 300);
            System.out.println("finished");
            double Count = 0;
            for (double[] a : pointsXpos) {
                Count += 0.07;
                Rectangle2D.Double point = new Rectangle2D.Double(500 + Count, 300 + (a[0] / 5), 2, 2);
                g.fill(point);
                Rectangle2D.Double point2 = new Rectangle2D.Double(500 + Count, 300 + (a[1] / 5), 2, 2);
                //g.fill(point2);
            }
        }
        */

        g.dispose();

        bufferStrategy.show();
    }

    // moves the blocks and counts collisions
    private void moveEverything() {

        double newVelocity = 0;
        boolean closeToLine = false;
        double toLine = 0;
        boolean noChange = false;
        boolean stop = false;


        if (Block[0].getVelocity() <= 0 && Block[1].getVelocity() <= 0 && Block[1].getVelocity() <= Block[0].getVelocity()) {
            finished = true;
            if (count != 5) {
                count++;
            }
        }
        if (count == 5) {

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
                    double[] graphPoints2 = new double[2];
                    graphPoints2[0] = Block[0].getXpos() + 100;
                    graphPoints2[1] = Block[1].getXpos();
                    pointsXpos.add(graphPoints2);
                }
            } else {
                if (Block[0].getXpos() + 100 - Block[0].getVelocity() == Block[1].getXpos() - Block[1].getVelocity()) {
                    calculate_new_velocity();
                    nOfCollisions++;

                    double[] graphPoints = new double[2];
                    graphPoints[0] = Block[0].getVelocity();
                    graphPoints[1] = Block[1].getVelocity();
                    points.add(graphPoints);
                    double[] graphPoints2 = new double[2];
                    graphPoints2[0] = Block[0].getXpos() + 100;
                    graphPoints2[1] = Block[1].getXpos();
                    pointsXpos.add(graphPoints2);
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
                    double[] graphPoints2 = new double[2];
                    graphPoints2[0] = Block[0].getXpos() + 100;
                    graphPoints2[1] = Block[1].getXpos();
                    pointsXpos.add(graphPoints2);
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

                    double[] graphPoints2 = new double[2];
                    graphPoints2[0] = Block[0].getXpos() + 100;
                    graphPoints2[1] = Block[1].getXpos();
                    pointsXpos.add(graphPoints2);
                }
            }
        }
    }

    private void calculate_initial_energy() {
        Block[0].calculate_kinetic_energy();
        Block[1].calculate_kinetic_energy();
        initial_energy = Block[0].getKineticEnergy() + Block[1].getKineticEnergy();

    }

    private void calculate_initial_momentum() {
        Block[0].calculate_momentum();
        Block[1].calculate_momentum();
        initial_momentum = Block[0].getMomentum() + Block[1].getMomentum();

    }

    private double[] quadratic_equation(double a, double b, double c) {
        double[] roots = new double[2];
        double d;
        d = b * b - 4 * a * c;
        roots[0] = (-b + Math.sqrt(d)) / (2 * a);
        roots[1] = (-b - Math.sqrt(d)) / (2 * a);
        return roots;
    }

    public void calculate_new_velocity() {
        double[] Block0Roots = new double[2];
        double block0NewVelocity;
        double block1NewVelocity;
        calculate_initial_energy();
        calculate_initial_momentum();

        double a_term = (Block[0].getMass() * Block[1].getMass() + Math.pow(Block[0].getMass(), 2));
        double b_term = -1 * (2 * initial_momentum * Block[0].getMass());
        double c_term = -2 * initial_energy * Block[1].getMass() + Math.pow(initial_momentum, 2);

        Block0Roots = quadratic_equation(a_term, b_term, c_term);
        if (Block0Roots[0] == Block[0].getVelocity()) {
            block0NewVelocity = Block0Roots[1];
        } else {
            block0NewVelocity = Block0Roots[0];
        }
        block1NewVelocity = (initial_momentum - Block[0].getMass() * block0NewVelocity) / Block[1].getMass();
        Block[0].new_velocity(block0NewVelocity);
        Block[1].new_velocity(block1NewVelocity);
    }
}
