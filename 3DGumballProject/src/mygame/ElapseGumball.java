/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Corn
 */
public class ElapseGumball extends GumballDecorator {

    private long tStart;
    gumball gBall;
    
    public ElapseGumball(gumball gBall){
        this.gBall = gBall;
        tStart = System.currentTimeMillis();
    }

    
    @Override
    public long getElapse() {
         return System.currentTimeMillis() - tStart  ; 
    }
    
    public void setColor(String color) {
        gBall.setSpatial(spatial);
        gBall.setColor(color);
    }
    
    public String getColor() {
        gBall.setSpatial(spatial);
        return gBall.getColor();
    }
    
    public void setValue(int value) {
        gBall.setSpatial(spatial);
        gBall.setValue(value);
    }
    
    public int getValue() {
        gBall.setSpatial(spatial);
        return gBall.getValue();
    }

    protected void controlUpdate(float tpf) {
        gBall.setSpatial(spatial);
        gBall.controlUpdate(tpf);    
    }

    protected void controlRender(RenderManager rm, ViewPort vp) {
        gBall.setSpatial(spatial);
        gBall.controlRender(rm, vp);
    }

    
}
