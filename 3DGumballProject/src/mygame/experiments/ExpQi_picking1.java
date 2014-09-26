/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.experiments;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.Timer;



/**
 *
 * @author qi
 */

/** Sample 8 - how to let the user pick (select) objects in the scene 
 * using the mouse or key presses. Can be used for shooting, opening doors, etc. */

public class ExpQi_picking1 extends SimpleApplication{
    
 public static void main(String[] args) {
    ExpQi_picking1 app = new ExpQi_picking1();
    app.start();
  }
   
  private Node shootables;
  private Geometry mark;
  
  @Override
  public void simpleInitApp() {
    initCrossHairs(); // a "+" in the middle of the screen to help aiming
    initKeys();       // load custom key mappings
    initMark();       // a red sphere to mark the hit
    
    //make mouse pointer  
    flyCam.setDragToRotate(true);
    //inputManager.setCursorVisible(true);

 
    /** create two colored boxes and a floor to shoot at: */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeSphere("coin1", -2f, 0f, 1f));
    shootables.attachChild(makeSphere("coin2", -2f, 1.2f,1f));
    shootables.attachChild(makeSphere("coin3", -2f, 2.4f,1f));
    shootables.attachChild(makeSphere("coin4", -2f, -1.2f,1f));
    shootables.attachChild(makeCube("gumball machine", 1f, -2f, 0f));

  }
  
  /** Declaring the "Shoot" action and mapping to its triggers. */
  private void initKeys() {
    inputManager.addMapping("pick target",
      new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
      new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
    inputManager.addListener(analogListener, "pick target");
  }
  
  /** Defining the "Shoot" action: Determine what was hit and how to respond. */
  private AnalogListener analogListener = new AnalogListener () {
 
    public void onAnalog(String name, float intensity, float tpf) {
      if (name.equals("pick target") ) {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        /*
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        
        // 3. Collect intersections between Ray and Shootables in results list.
        shootables.collideWith(ray, results);
        */
        
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
        new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(
        new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        shootables.collideWith(ray, results);
        inputManager.setCursorVisible(true);
        
        
        // 4. Print the results
        System.out.println("----- Collisions? " + results.size() + "-----");
        for (int i = 0; i < results.size(); i++) {
          // For each hit, we know distance, impact point, name of geometry.
          float dist = results.getCollision(i).getDistance();
          Vector3f pt = results.getCollision(i).getContactPoint();
          String hit = results.getCollision(i).getGeometry().getName();
          System.out.println("* Collision #" + i);
          System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }
        
        // 5. Use the results (we mark the hit object)
        if (results.size() > 0) {
          // The closest collision point is what was truly hit:
          Geometry target = results.getClosestCollision().getGeometry();
          // =======here is the action for after hitting target  =============//
          // move the coin to the gumball machine and show text "insert coin"
          if (target.getName().equals("coin1") || target.getName().equals("coin2") 
                  ||target.getName().equals("coin3")  ||target.getName().equals("coin4")) {
             
            target.setLocalTranslation(1f, -2f, 1f);           
            System.out.println(target.getName() + " inserted.");
                       
            //set text in screen
            BitmapText hudText = new BitmapText(guiFont, false);          
            hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
            hudText.setColor(ColorRGBA.Blue);                             // font color
            hudText.setText(target.getName() + " inserted. You can turn the crank");             // the text
            hudText.setLocalTranslation(300, hudText.getLineHeight()+20, 0); // position
            guiNode.attachChild(hudText);
                      
           
          } else if (target.getName().equals("gumball machine")) {
            target.rotate(0, intensity, 0);
          }
        } else {
          // No hits? Then remove the red mark.
          rootNode.detachChild(mark);
        }
      }
    }
  };
  
    /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 1, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }
  
  
  /** A cube object for target practice */
  protected Geometry makeSphere(String name, float x, float y, float z) {
    Sphere ball = new Sphere(32,32, 0.5f);
    Geometry sphere = new Geometry(name, ball);
    sphere.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    sphere.setMaterial(mat1);
    return sphere;
  }
  
  /** A red ball that marks the last spot that was "hit" by the "shot". */
  protected void initMark() {
    Sphere sphere = new Sphere(30, 30, 0.2f);
    mark = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.Red);
    mark.setMaterial(mark_mat);
  }
  
  /** A centred plus sign to help the player aim. */
  protected void initCrossHairs() {
    setDisplayStatView(false);
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
    guiNode.attachChild(ch);
  }
  
 
}
