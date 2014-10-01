
package demoGame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class gumball extends AbstractControl{
    public gumball() {
        
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