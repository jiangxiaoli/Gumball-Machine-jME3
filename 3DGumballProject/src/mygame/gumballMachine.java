package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class gumballMachine extends AbstractControl{
    //private int gBAmount = 25; //gumball price
    private boolean dispense = false; //if gumball was dispensed or not
    private State state;
    private State NoCoinState;
    private State HasCoinState;
    private State HasGPriceState;
    private State SoldOutState;
    
    public gumballMachine() {
        NoCoinState = new NoCoinState(this);
        HasCoinState = new HasCoinState(this);
        HasGPriceState = new HasGPriceState(this);
        SoldOutState = new SoldOutState(this);
        state = NoCoinState;
    }
    
    public void setPayment(int value) {
        spatial.setUserData("payment", value);
    }
    
    public int getPayment() {
        return (Integer)spatial.getUserData("payment");
    }
    
    public void setCount(int count) {
        spatial.setUserData("gCount",count);
    }
    
    public int getCount() {
        return (Integer)spatial.getUserData("gCount");
    }
    
    public void setGBallPrice(int price) {
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
    
    public State SoldOutState() {
        return SoldOutState;
    }
    

    /*Machine Functions*/
    public void acceptCoin(int value) {
        state.acceptCoin(value);
    }
    
    public void turnCrank() {
        state.turnCrank();
    }
    
    public void refill(int gumballs) {
        int amount = getCount();
        amount+=gumballs;
        spatial.setUserData("gCount", amount);
        System.out.println("Refilling gumball machine..");
        System.out.println("There are now " + amount + " gumballs in the machine!");
        state = NoCoinState;
    }
    
    public void resetAmtInSlot() {
        spatial.setUserData("payment", 0);
        //System.out.println(spatial.getUserData("payment"));
        dispense = false;
    }
    
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       //controlRender code here
    }
}