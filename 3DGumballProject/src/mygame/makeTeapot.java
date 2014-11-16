/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author qi
 */
public class makeTeapot implements Product{
    //For Factory Method Pattern, Concrete Product
    
    AssetManager assetManager;
    Node node;
    
    makeTeapot(AssetManager aM, Node rN){
        this.assetManager = aM;  
        this.node =rN;
    }

    @Override
    public void makeProduct(){
          
      System.out.println("Inside makeTeapot::makeProduct() method.");
      
        //Create teapot as the tagert to shoot
        Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material( 
            assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot.setMaterial(mat_default);
        teapot.scale(1.8f, 1.8f, 1.8f);
        teapot.setLocalTranslation(-18f,5f,5f); 
        teapot.rotate(0, -0.2f, 0);
        node.attachChild(teapot);
        teapot.setName("Teapot");
        
    }
    
}
