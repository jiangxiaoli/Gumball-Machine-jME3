
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class Coin extends AbstractControl{
           
    public void setCount(int count) {
        spatial.setUserData("gCount",count);
    }
    
    public int getCount() {
        return (Integer)spatial.getUserData("gCount");
    }
    
    public void turnCrank() {
        if (getCount() > 0) {
            System.out.println("Crank turned!");
            dispense();
            System.out.println("Machine has " + spatial.getUserData("gCount") + " gumballs");
        }
        else {
            System.out.println("Sorry, Gumball machine is out of gumballs!");
        }
        
    }
    
    public void dispense() {
        int count = getCount();
        count--;
        setCount(count);
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
