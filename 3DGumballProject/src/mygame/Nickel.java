package mygame;

public class Nickel extends Coin {
    
    private int totalNickel;
    private int numNickel;
    
    public Nickel(int numNickel){
       this.numNickel = numNickel;
       totalNickel = 5 * numNickel;
    }
    
    protected int getNumNickel() { return numNickel;}
    
    protected int getTotalNickel() { return totalNickel;}
    
    public void setNumberNickel(int numNickel) {
        this.numNickel = numNickel;
    }
}
