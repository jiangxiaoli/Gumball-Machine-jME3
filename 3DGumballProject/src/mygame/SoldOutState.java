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
        System.out.println("Sorry, cannot return your " + value + " cents");
        //System.out.println("Returning your " + value + " cents");
        gMach.resetAmtInSlot();
    }
    
    public String turnCrank() {
        System.out.println("The machine is sold out! Refill it first.");
        return "The machine is sold out! Refill it first.";
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

