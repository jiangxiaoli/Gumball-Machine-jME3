
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/* This class is the control class for the gumball spatials generated
 * through the gumballMachine game logic.
 */

public class gumball extends AbstractControl{
    
    private long tStart;
    public gumball() {
        tStart = System.currentTimeMillis();
    }
    
    public long getElapse() {
         return System.currentTimeMillis() - tStart  ; 
    }
    
    public void setColor(String color) {
        spatial.setUserData("color", color);
    }
    
    public String getColor() {
        return (String)spatial.getUserData("color");
    }
    
    public void setValue(int value) {
        spatial.setUserData("value", value);
    }
    
    public int getValue() {
        return (Integer)spatial.getUserData("value");
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