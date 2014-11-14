
package mygame;

import java.util.HashMap;


public class InfoCenter implements Observer{
    protected String observerState;
    protected userInfo subject;
    
    public InfoCenter( userInfo theSubject){
        this.subject = theSubject;
        observerState = "Score: " + 0 + "\n" + "Num of Gumballs: " 
                + 0 + "\n" + "Coin In Pocket: " + 0 + "\n";
    }
    
    public void update() {
        HashMap<String, Integer> map = subject.getState();
        Integer score = map.get("score");
        Integer coinAmt = map.get("coinAmt");
        Integer numOfGumballs = map.get("numOfGumballs");
        observerState = "Score: " + score + "\n" + "Num of Gumballs: " 
                + numOfGumballs + "\n" + "Coin In Pocket: " + coinAmt + "\n"; 
        //System.out.println("update:" + observerState);
    }
   
}
