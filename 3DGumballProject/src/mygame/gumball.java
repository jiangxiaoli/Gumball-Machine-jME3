
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/* This class is the control class for the gumball spatials generated
 * through the gumballMachine game logic.
 */

public class gumball extends AbstractControl {
    private gumballState state; //state of gumball
    private gumballState DispensedState;
    private gumballState InPocketState;
    private gumballState FiredState;

    public gumball() {
        DispensedState = new DispensedState(this);
        InPocketState = new InPocketState(this);
        FiredState = new FiredState(this);
        state = DispensedState;
    }
    
    public void setColor(String color) {
        System.out.print("in gumball");
        System.out.print(spatial);
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
    
    
    /*For States*/
    public gumballState getState() {
        return state;
    }
   
    public gumballState getDispensedState() {
        return DispensedState;
    }
    
    public gumballState getFiredState() {
        return FiredState;
    }
    
    public gumballState getInPocketState() {
        return InPocketState;
    }
    
    public void setState(gumballState newState) {
        state = newState;
    }
    
    public void setFiredState() {
        state = FiredState;
    }
    
    
    /*For State Functions*/
    public String catchIt() {
        return state.catchIt();
    }

    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
    
}