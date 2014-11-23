
package mygame;

import java.util.HashMap;


public class InfoCenter implements Observer{
    protected String observerState;
    protected userInfo subject;
    
    public InfoCenter( userInfo theSubject){
        this.subject = theSubject;
        observerState = "Score: " + 0 + "\n" + "Total Gumballs Caught: "
                + 0 + "\n" + "Money: " + 0 + "c\n";
    }
    
    public void update() {
        HashMap<String, Integer> map = subject.getState();
        Integer score = map.get("score");
        Integer coinAmt = map.get("coinAmt");
        Integer numOfGumballs = map.get("numOfGumballs");
        observerState = "Score: " + score + "\n" + "Gumballs Caught: "
                + numOfGumballs + "\n" + "Money: " + coinAmt + "c\n";
        //System.out.println("update:" + observerState);
    }
   
}
