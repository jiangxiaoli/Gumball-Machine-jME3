/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/**
 *
 * @author qi
 */
public class makeElephant implements Product{
    //For Factory Method Pattern, Concrete Product
    
    AssetManager assetManager;
    Node node;
    
    makeElephant(AssetManager aM, Node rN){
        this.assetManager = aM;  
        this.node =rN;
    }

    @Override
    public void makeProduct(){
          
      System.out.println("Inside makeElephant::makeProduct() method.");
      
        //Create elephant as the tagert to shoot
        Spatial elephant = assetManager.loadModel("Models/Elephant/Elephant.mesh.xml");   
        elephant.setName("Small Elephant");
        elephant.scale(0.025f, 0.025f, 0.025f);
        elephant.setLocalTranslation(-18f,5f,10f);   
        elephant.rotate(0, -1.3f, 0);
        node.attachChild(elephant);      
       
    }
    
}
