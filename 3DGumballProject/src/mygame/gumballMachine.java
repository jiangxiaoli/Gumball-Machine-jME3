
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
        System.out.println("Machine has " + spatial.getUserData("gCount") + " gumballs");
        return spatial.getUserData("gCount");
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null) {
            //System.out.println("Do something here");
           
        }
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }

    
}