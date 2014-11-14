
package mygame;

public interface Subject {
    //methods to register and unregister observers
    public abstract void attach(Observer obj);
    public abstract void detach(Observer obj);

    //method to notify observers of changes
    public abstract void notifyObservers();
}
