
package mygame;

public interface State {
    public void acceptCoin(int value);
    public void turnCrank();
    public void dispense();

    public int getAmtInSlot();
}
