
package mygame;

public class Quarter extends Coin {
    
    private int totalQuarter;
    private int numQuarter;
    
    public Quarter(int numQuarter){
       this.numQuarter = numQuarter;
       totalQuarter = 25 * numQuarter;
    }
    
    protected int getNumQuarter() { return numQuarter;}
    
    protected int getTotalQuarter() { return totalQuarter;}
    
    public void setNumberQuarter(int numQuarter) {
        this.numQuarter = numQuarter;
    }
}
