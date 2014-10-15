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
        int curr_amt = getAmtInSlot();
        curr_amt += value;
        spatial.setUserData("payment", curr_amt);
        
    }
    
    public void turnCrank() {
        
    }
    
    public void dispense() {
        
    }
    
    public int getAmtInSlot() {
        return (Integer)spatial.getUserData("payment");
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

