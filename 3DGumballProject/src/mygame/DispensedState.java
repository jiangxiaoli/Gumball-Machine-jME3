
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class DispensedState extends AbstractControl implements gumballState {
    gumball Gumball;
    
    public DispensedState(gumball Gumball) {
        this.Gumball = Gumball;
    }
    
    public String catchIt() {
        Gumball.setState(Gumball.getInPocketState());
        return "You have caught the gumball";
    
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
}

