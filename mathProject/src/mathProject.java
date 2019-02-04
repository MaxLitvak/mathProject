import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
    private double block_zero_mass;
    private double block_one_mass;
    private int block_zero_velocity;
    private int block_one_velocity;
    private int block_zero_xpos;
    private int block_one_xpos;
    private int block_zero_ypos;
    private int block_one_ypos;
    private double initial_energy;
    private double initial_momentum;
    private boolean finished;

    private Color block_0_color;
    private Color block_1_color;

    private ArrayList<Integer> collisionSites = new ArrayList<>();




    private int nOfCollisions;

    private block[] Block;


    JFrame frame;
    Canvas canvas;
    BufferStrategy bufferStrategy;


    public static void main(String[] args) {
        mathProject ex = new mathProject();
        new Thread(ex).start();
    }

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


    //thread
    public void run() {

        block_0_color = new Color(170, 170, 170); // Color white
        block_1_color = new Color(50, 50, 50);
        block_zero_mass = 1;
        block_one_mass = 1000000;
        block_zero_velocity = 0;
        block_one_velocity = 10;
        block_zero_xpos = 300;
        block_one_xpos = 500;
        block_zero_ypos = 400;
        block_one_ypos = 400;
        Block = new block[2];
        Block[0] = new block(block_zero_mass, block_zero_velocity, block_zero_xpos, block_zero_ypos);
        Block[1] = new block(block_one_mass, block_one_velocity, block_one_xpos, block_one_ypos);

        collision();
        System.out.println("Number of Collisions: " + nOfCollisions);


        while (true) {
            // paint the graphics
            render();



            //sleep
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {

            }
        }
    }

    //paints things on the screen using bufferStrategy
    private void render() {

        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,800,800);
        g.setColor(Color.BLACK);
        g.drawLine(100, 100, 100, 500);
        g.drawLine(100, 500, 700, 500);
        g.setColor(block_0_color);
        g.fillRoundRect(Block[0].getXpos(), Block[0].getYpos(), Block[0].getWidth(), Block[0].getHeight(), 5, 5);
        g.setColor(block_1_color);
        g.fillRoundRect(Block[1].getXpos(), Block[1].getYpos(), Block[1].getWidth(), Block[1].getHeight(), 5, 5);


        g.dispose();

        bufferStrategy.show();
    }

    private void collision(){
        if(finished == false){
            nOfCollisions++;
            double[] Block0Roots = new double[2];
            double block0NewVelocity;
            double block1NewVelocity;
            calculate_initial_energy();
            calculate_initial_momentum();

            if(Block[1].getVelocity() >= 0){
                if(Block[0].getVelocity() >= 0){
                    if(Block[1].getVelocity() > Block[0].getVelocity()){
                        double a_term = (Block[0].getMass()*Block[1].getMass() + Math.pow(Block[0].getMass(), 2));
                        double b_term = -1*(2*initial_momentum*Block[0].getMass());
                        double c_term = -2*initial_energy*Block[1].getMass() + Math.pow(initial_momentum, 2);

                        Block0Roots = quadratic_equation(a_term, b_term, c_term);
                        if(Block0Roots[0] == Block[0].getVelocity()){
                            block0NewVelocity = Block0Roots[1];
                        }
                        else{
                            block0NewVelocity = Block0Roots[0];
                        }

                        block1NewVelocity= (initial_momentum - Block[0].getMass()*block0NewVelocity) / Block[1].getMass();

                        Block[0].new_velocity(block0NewVelocity);
                        Block[0].add_velocity(block0NewVelocity);
                        Block[1].new_velocity(block1NewVelocity);
                        Block[1].add_velocity(block1NewVelocity);
                        new_collision_site(block0NewVelocity,block1NewVelocity);
                    }
                    else{
                        Block[0].new_velocity(Block[0].getVelocity() * -1);
                    }
                }
                else{
                    double a_term = (Block[0].getMass()*Block[1].getMass() + Math.pow(Block[0].getMass(), 2));
                    double b_term = -1*(2*initial_momentum*Block[0].getMass());
                    double c_term = -2*initial_energy*Block[1].getMass() + Math.pow(initial_momentum, 2);

                    Block0Roots = quadratic_equation(a_term, b_term, c_term);
                    if(Block0Roots[0] == Block[0].getVelocity()){
                        block0NewVelocity = Block0Roots[1];
                    }
                    else{
                        block0NewVelocity = Block0Roots[0];
                    }

                    block1NewVelocity= (initial_momentum - Block[0].getMass()*block0NewVelocity) / Block[1].getMass();

                    Block[0].new_velocity(block0NewVelocity);
                    Block[0].add_velocity(block0NewVelocity);
                    Block[1].new_velocity(block1NewVelocity);
                    Block[1].add_velocity(block1NewVelocity);
                    new_collision_site(block0NewVelocity,block1NewVelocity);
                }
            }
            else{
                if(Block[0].getVelocity() >= 0){
                    Block[0].new_velocity(Block[0].getVelocity() * -1);
                }
                else{
                    if(Block[0].getVelocity() < Block[1].getVelocity()){
                        double a_term = (Block[0].getMass()*Block[1].getMass() + Math.pow(Block[0].getMass(), 2));
                        double b_term = -1*(2*initial_momentum*Block[0].getMass());
                        double c_term = -2*initial_energy*Block[1].getMass() + Math.pow(initial_momentum, 2);

                        Block0Roots = quadratic_equation(a_term, b_term, c_term);
                        if(Block0Roots[0] == Block[0].getVelocity()){
                            block0NewVelocity = Block0Roots[1];
                        }
                        else{
                            block0NewVelocity = Block0Roots[0];
                        }

                        block1NewVelocity= (initial_momentum - Block[0].getMass()*block0NewVelocity) / Block[1].getMass();

                        Block[0].new_velocity(block0NewVelocity);
                        Block[0].add_velocity(block0NewVelocity);
                        Block[1].new_velocity(block1NewVelocity);
                        Block[1].add_velocity(block1NewVelocity);
                        new_collision_site(block0NewVelocity,block1NewVelocity);
                    }
                    else{
                        finished = true;
                        nOfCollisions = nOfCollisions-1;
                    }
                }
            }

            System.out.println("Block[0] Velocity: " + Block[0]. getVelocity());
            System.out.println("Block[1] Velocity: " + Block[1].getVelocity());


            collision();
        }

    }

    private void calculate_initial_energy(){
        Block[0].calculate_kinetic_energy();
        Block[1].calculate_kinetic_energy();
        initial_energy = Block[0].getKineticEnergy() + Block[1].getKineticEnergy();

    }
    private void calculate_initial_momentum(){
        Block[0].calculate_momentum();
        Block[1].calculate_momentum();
        initial_momentum = Block[0].getMomentum() + Block[1].getMomentum();

    }

    private double[] quadratic_equation(double a, double b, double c){
        double[] roots = new double[2];
        double d;
        d = b * b - 4 * a * c;
        roots[0] = ( - b + Math.sqrt(d))/(2*a);
        roots[1] = (-b - Math.sqrt(d))/(2*a);
        return roots;
    }

    private void new_collision_site(double velocity1, double velocity2, boolean wall){

    }
}
