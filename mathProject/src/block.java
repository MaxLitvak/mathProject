import java.util.ArrayList;

public class block {
    private double mass;
    private double velocity;
    private double xpos;
    private int ypos;
    private int width;
    private int height;
    private double momentum;
    private double kineticEnergy;
    public ArrayList<double[]> movement = new ArrayList<>();

    public block(double pmass, double pvelocity, int pxpos, int pypos){
        mass = pmass;
        velocity = pvelocity;
        xpos = pxpos;
        ypos = pypos;
        width = 100;
        height = 100;

    }
    public double getMass(){ return mass; }
    public double getVelocity(){ return velocity; }
    public double getXpos(){ return xpos; }
    public int getYpos(){ return ypos; }
    public double getMomentum(){ return momentum; }
    public double getKineticEnergy(){ return kineticEnergy; }
    public int getWidth(){ return width; }
    public int getHeight(){ return height; }

    public void calculate_momentum(){
        momentum = mass * velocity;
    }
    public void calculate_kinetic_energy(){
        kineticEnergy = (mass * Math.pow(velocity, 2))/2;
    }
    public void new_velocity(double newVelocity){
        velocity = newVelocity;
    }
    public void add_movement(double tXpos){
        double newMovement[] = new double[2];
        newMovement[0] = velocity;
        newMovement[1] = tXpos;
        movement.add(newMovement);
        xpos = tXpos;

    }


}
