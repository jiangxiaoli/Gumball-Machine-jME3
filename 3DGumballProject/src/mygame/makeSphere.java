/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author qi
 */
public class makeSphere implements Product{
  //For Factory Method Pattern, Concrete Product
    
    AssetManager assetManager;
    Node node;
    
    makeSphere(AssetManager aM, Node rN){
        this.assetManager = aM; 
        this.node = rN;
    }

    @Override
    public void makeProduct(){
          
      System.out.println("Inside makeSphere::makeProduct() method.");
      Node sphereNode = new Node("sphereNode");
    
      
     /** Make A bumpy rock with a shiny light effect.*/
        Sphere sphereMesh = new Sphere(32,32, 1.2f);
        Geometry sphereGeo = new Geometry("Shiny rock", sphereMesh);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
        TangentBinormalGenerator.generate(sphereMesh);           // for lighting effect
        Material sphereMat = new Material(assetManager, 
            "Common/MatDefs/Light/Lighting.j3md");
        sphereMat.setTexture("DiffuseMap", 
            assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        sphereMat.setTexture("NormalMap", 
            assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        sphereMat.setBoolean("UseMaterialColors",true);    
        sphereMat.setColor("Diffuse",ColorRGBA.White);
        sphereMat.setColor("Specular",ColorRGBA.White);
        sphereMat.setFloat("Shininess", 64f);  // [0,128]
        sphereGeo.setMaterial(sphereMat);
        sphereGeo.setLocalTranslation(-18f,6.2f, -1); // Move it a bit
        sphereNode.attachChild(sphereGeo);
        node.attachChild(sphereNode);
        sphereGeo.setName("Rock Ball");
        
        /** Must add a light to make the lit object visible! */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-17f,5.5f, -1f ).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        sphereNode.addLight(sun);       

    }
}

