package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class NoCoinState extends AbstractControl implements State {
    gumballMachine gMach;
    
    public NoCoinState(gumballMachine gMach) {
        this.gMach = gMach;
    }
    
    public void acceptCoin(int value) {
        int curr_amt = 0;
        curr_amt += value;
        gMach.setPayment(curr_amt);
        if (curr_amt >= gMach.getGBallPrice()){
            gMach.setState(gMach.getHasGPriceState());
        }
        else {
            gMach.setState(gMach.getHasCoinState());
        }
    }
    
    public void turnCrank() {
        System.out.println("You need to insert money first!");
    }
    
    public boolean makeGumball() {
        return false;
    }
    
    /*private void dispense() {
        System.out.println("Cannot dispense. Insert coins first.");
    }*/
    
    /*public int getAmtInSlot() {
        //System.out.println(gMach.getPayment());
        return gMach.getPayment();
    }
    
    public void resetAmtInSlot() {
        spatial.setUserData("payment", 0);
        dispense = false;
    }*/
    
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

