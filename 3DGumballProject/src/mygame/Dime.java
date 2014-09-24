package mygame;

public class Dime extends Coin {
    
    private int totalDime;
    private int numDime;
    
    public Dime(int numDime){
       this.numDime = numDime;
       totalDime = 10 * numDime;
    }
    
    protected int getNumDime() { return numDime;}
    
    protected int getTotalDime() { return totalDime;}
    
    public void setNumberDime(int numDime) {
        this.numDime = numDime;
    }
}