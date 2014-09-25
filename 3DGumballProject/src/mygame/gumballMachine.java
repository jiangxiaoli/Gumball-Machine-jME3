
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class gumballMachine extends AbstractControl{
    public gumballMachine() {
        
    }
    
    public void setCount(int count) {
        spatial.setUserData("gCount",count);
    }
    
    public int getCount() {
        return (Integer)spatial.getUserData("gCount");
    }
    
    public void turnCrank() {
        if (getCount() > 0) {
            System.out.println("Crank turned!");
            dispense();
            System.out.println(spatial.getUserData("gCount") + " gumball(s) remaining");
        }
        else {
            System.out.println("Crank turned, but the gumball machine is out of gumballs!");
        }
    }
    
    public void refill(int gumballs) {
        int amount = getCount();
        amount+=gumballs;
        spatial.setUserData("gCount", amount);
        System.out.println("Refilling gumball machine..");
        System.out.println("There are now " + amount + " gumballs in the machine!");
    }
    
    private void dispense() {
        int count = getCount();
        count--;
        setCount(count);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            //System.out.println("Do something here");
           
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
}