
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class Coin extends AbstractControl{
           
    public Coin(int value, int count){
        spatial.setUserData("value", value);
        spatial.setUserData("cCount",count);
    }
    
    public int getCount() {
        return (Integer)spatial.getUserData("cCount");
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
