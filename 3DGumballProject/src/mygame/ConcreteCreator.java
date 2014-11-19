/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 *
 * @author qi
 */
public class ConcreteCreator {
   //For Factory Method Pattern  
    
   AssetManager assetManager;
    Node node;
    
   ConcreteCreator(AssetManager aM, Node rN){
        this.assetManager = aM; 
        this.node = rN;
    }
    
   //use getProduct method to get object of type product
   public Product getProduct(String productType){
      if(productType == null){
         return null;
      }		
      if(productType.equalsIgnoreCase("TEAPOT")){
         return new makeTeapot(assetManager,node);
      }
      else if(productType.equalsIgnoreCase("ELEPHANT")){
         return new makeElephant(assetManager,node);
      }  
      else if(productType.equalsIgnoreCase("SPHERE")){
         return new makeSphere(assetManager,node);
      } 
      return null;
   }
    
}
