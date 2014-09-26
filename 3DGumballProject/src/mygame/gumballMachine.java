
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class gumballMachine extends AbstractControl{
    private int gBAmount = 25; //gumball amount
    private boolean dispense = false; //if gumball was dispensed or not
    
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
            if (hasGBAmount()){
                dispense();
                dispense = true;
                System.out.println(spatial.getUserData("gCount") + " gumball(s) remaining");
            }
            else {
                int put_in = getAmtInSlot();
                int remaining = gBAmount - put_in;
                System.out.println("You still need to put in " + remaining + " cent(s)");
            }
        }
        else {
            System.out.println("Crank turned, but the gumball machine is out of gumballs!");
        }
    }
    
    public boolean makeGumball() {
        return dispense;
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
    
    public void acceptCoin(int coin_amt) {
        int curr_amt = getAmtInSlot();
        curr_amt += coin_amt;
        spatial.setUserData("payment", curr_amt);
    }
    
    public void resetAmtInSlot() {
        spatial.setUserData("payment", 0);
        dispense = false;
    }
    
    public int getAmtInSlot() {
        return (Integer)spatial.getUserData("payment");
    }
    
    public boolean hasGBAmount() {
        if ((Integer)spatial.getUserData("payment") >= gBAmount) {
            return true;
        }
        else {
            return false;
        }
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