package mygame;

/* This interface represents the contract that all State classes must
 * implement. The interface requires two methods: acceptCoin and turnCrank.
 */

public interface State {
    public void acceptCoin(int value);
    public String turnCrank();
}
