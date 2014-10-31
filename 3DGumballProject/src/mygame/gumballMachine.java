package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/* This class is the control class for the gumball machine spatial node, which
 * is composed of 3 model parts in the jME world. 
 * 
 * The gumballMachine is implemented using the STATE MACHINE design pattern
 * using 4 states: NoCoinState, HasCoinState, HasGPriceState and SoldOutState.
 */

public class gumballMachine extends AbstractControl{
    private boolean dispense = false; //if gumball should be dispensed or not
    private State state; //state of gumballMachine
    private State NoCoinState;
    private State HasCoinState;
    private State HasGPriceState;
    private State SoldOutState;
    
    public gumballMachine() {
        NoCoinState = new NoCoinState(this);
        HasCoinState = new HasCoinState(this);
        HasGPriceState = new HasGPriceState(this);
        SoldOutState = new SoldOutState(this);
        state = NoCoinState; //initialize to NoCoinState
    }
    
    protected void setPayment(int value) {
        spatial.setUserData("payment", value);
    }
    
    public int getPayment() {
        return (Integer)spatial.getUserData("payment");
    }
    
    protected void setCount(int count) {
        spatial.setUserData("gCount",count);
    }
    
    public int getCount() {
        return (Integer)spatial.getUserData("gCount");
    }
    
    public void setGBallPrice(int price) {
        //set gumball price
        spatial.setUserData("price",price);
    }
    
    public int getGBallPrice() {
        return (Integer)spatial.getUserData("price");
    }
    
    protected void setDispense() {
        dispense = true;
    }
    
    public boolean makeGumball() {
        return dispense;
    }
    

    /*For States*/
    public State getState() {
        return state;
    }
    
    public void setState(State newState) {
        state = newState;
    }
    
    public State getNoCoinState() {
        return NoCoinState;
    }
    
    public State getHasCoinState() {
        return HasCoinState;
    }
    
    public State getHasGPriceState() {
        return HasGPriceState;
    }
    
    public State getSoldOutState() {
        return SoldOutState;
    }
    

    /*State Machine Functions*/
    public void acceptCoin(int value) {
        state.acceptCoin(value);
    }
    
    public void turnCrank() {
        state.turnCrank();
    }
    
    /*Gumball Machine Functions*/
    public void refill(int gumballs) {
        int amount = getCount();
        amount+=gumballs;
        spatial.setUserData("gCount", amount);
        System.out.println("Refilling gumball machine...");
        System.out.println("There are now " + amount + " gumballs in the machine!");
        System.out.println("Sorry, cannot return your " + getPayment() + " cents");
        state = NoCoinState; //resets state
    }
    
    public void resetAmtInSlot() {
        spatial.setUserData("payment", 0);
        dispense = false; //reset flag for releasing gumball to false
    }
    
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
}