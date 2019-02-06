import java.awt.*;
import java.awt.geom.Rectangle2D;
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
    private Rectangle2D.Double rec;

    public block(double pmass, double pvelocity, int pxpos, int pypos){
        mass = pmass;
        velocity = pvelocity;
        xpos = pxpos;
        ypos = pypos;
        width = 100;
        height = 100;
        rec = new Rectangle2D.Double(xpos, ypos, width, height);
    }
    public double getMass(){ return mass; }
    public double getVelocity(){ return velocity; }
    public double getXpos(){ return xpos; }
    public double getMomentum(){ return momentum; }
    public double getKineticEnergy(){ return kineticEnergy; }
    public Rectangle2D.Double getRec(){ return rec; }

    public void calculate_momentum(){
        momentum = mass * velocity;
    }
    public void calculate_kinetic_energy(){
        kineticEnergy = (mass * Math.pow(velocity, 2))/2;
    }
    public void new_velocity(double newVelocity){
        velocity = newVelocity;
    }
    public void new_xpos(double tXpos){ xpos = tXpos; }
    public void new_rectangle() {rec = new Rectangle.Double(xpos, ypos, width, height); }


}
