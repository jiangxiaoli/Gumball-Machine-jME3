package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class SoldOutState extends AbstractControl implements State {
    gumballMachine gMach;
    
    public SoldOutState(gumballMachine gMach) {
        this.gMach = gMach;
    }
    
    public void acceptCoin(int value) {
        System.out.println("The machine is sold out!");
        System.out.println("Returning your " + value + " cents");
        gMach.resetAmtInSlot();
    }
    
    public void turnCrank() {
        System.out.println("Crank turned but the machine is sold out!");
    }
    
    
    /*private void dispense() {
        
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

