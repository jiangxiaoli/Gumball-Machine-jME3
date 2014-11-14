package mygame;

import java.util.ArrayList;
import java.util.HashMap;

public class userInfo implements Subject{
   
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    
    public HashMap<String, Integer> getState(){
        return map;
    }
    
    public void setState(String key, Integer val){
        map.put(key, val);
        notifyObservers();
    }
    
    //methods to register and unregister observers
    public void attach(Observer obj){
        if(obj == null) throw new NullPointerException("Null Observer");
        if(!observers.contains(obj)) observers.add(obj);

    }
   
    public void detach(Observer obj){
        observers.remove(obj);
    }

    //method to notify observers of changes
    public void notifyObservers(){
        for (Observer obj  : observers)
        {
            obj.update();
        }   
    }
    
    //method to get updates from subject
    public Object getUpdate(Object obj){
        return obj;
    }
    
}
