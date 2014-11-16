
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class InPocketState extends AbstractControl implements gumballState {
    gumball Gumball;
    
    public InPocketState(gumball Gumball) {
        this.Gumball = Gumball;
    }
    
    public String catchIt() {
        return "Gumball is in your pocket.";
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
}

