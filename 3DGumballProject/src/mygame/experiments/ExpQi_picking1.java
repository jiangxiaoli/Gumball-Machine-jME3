/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.experiments;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;



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
  boolean hasCoin = false;
  String coinName = null;
  
  /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;
 
  /** Prepare Materials */
  Material floor_mat;  
  
  /** Prepare geometries and physical nodes for cannon balls. */
  private RigidBodyControl    ball_phy;
  private static Sphere sphere;
  private RigidBodyControl    floor_phy;
  private static Box    floor;
  
  Geometry gumball, gumball2;
  Vector3f bounceUp = new Vector3f(0, 8f, -0.3f);  
  Vector3f bounceDown = new Vector3f(0f, 2, -0.3f);  
    static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 0.55f, true, false);
      
    /** Initialize the floor geometry */
    floor = new Box(10f, 0.1f, 5f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
   
  }
 
  
  @Override
  public void simpleInitApp() {
      
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);  
       
    //make mouse pointer  
    flyCam.setDragToRotate(true);
    
    
    
    /** Configure cam to look at scene */
    // cam.setLocation(new Vector3f(-6, 2f, 4f));
    //cam.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);
    
    //flyCam.setMoveSpeed(1);
    //inputManager.setCursorVisible(true);
    
    initCrossHairs(); // a "+" in the middle of the screen to help aiming
    initKeys();       // load custom key mappings
    initMark();       // a red sphere to mark the hit
    
    viewPort.setBackgroundColor(ColorRGBA.LightGray);  //set the backgroud color as Gray 
    //  viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
 
    /** create four sphere as coins, one Cube as gumball machine */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeSphere("coin1", -2f, 0f, -3f));
    shootables.attachChild(makeSphere("coin2", -2f, 1.2f,-3f));
    shootables.attachChild(makeSphere("coin3", -2f, 2.4f,-3f));
    shootables.attachChild(makeSphere("coin4", -2f, -1.2f,-3f));
    shootables.attachChild(makeCube("gumball machine",1f, -2f, -5f));
    shootables.attachChild(makeFloor());    
    

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
              
            coinName = target.getName();            
            target.setLocalTranslation(1f, -2f, -3f);    // move the coin to gumball machine  
            hasCoin = true;                                                       
            System.out.println(target.getName() + " inserted.");
            
            //set text in screen
             showText(coinName + " inserted. You can turn the crank");  
            
          } else if (target.getName().equals("gumball machine") && hasCoin) {  
                // hit gumball machine
            System.out.println(coinName + " has been inserted to the gumball machine");
            shootables.detachChildNamed(coinName);       //when hit the gumball machine, the coin disappear
            hasCoin = false;

             // make a gumball
            if(coinName.equals("coin2") ){
                gumball2 = makeBigSphere("gumball2", 1.5f, -2f, 2f);
                shootables.attachChild(gumball2);    //this gumball after click can bounce
                showText("Here is your gumball.");  
            }else {
                gumball = makeBigSphere("gumball", 1.5f, -2f, 2f);
                shootables.attachChild(gumball);  
                showText("Here is your gumball.");  
            }
             // gumball.move(1.50f, -2f, 2.5f);
            target.rotate(0, intensity, 0);    //gumball machine rotate   
            
          }else if(target.getName().equals("gumball")){                   
                //gumball disppear
                shootables.detachChild(gumball); 
                //show text              
                showText("Now you took your gumball");   
          } else if(target.getName().equals("gumball2")){  
              
                showText("Now your gumball can bounce");   
                ball_phy = new RigidBodyControl(1f);           
                target.addControl(ball_phy);
                bulletAppState.getPhysicsSpace().add(ball_phy);
                ball_phy.clearForces();
                ball_phy.setLinearVelocity(ball_phy.getLinearVelocity().clone().setY(0));
                ball_phy.applyImpulse(new Vector3f(0, 18f, 0), Vector3f.ZERO);
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
  
  
  /** A sphere object for target practice */  
  // this is as coin
  protected Geometry makeSphere(String name, float x, float y, float z) {
    Sphere ball = new Sphere(32,32, 0.5f);
    Geometry sphere = new Geometry(name, ball);
    sphere.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    sphere.setMaterial(mat1);
    return sphere;
  }
  
    // make gumball
    protected Geometry makeBigSphere(String name, float x, float y, float z) {
 
     /** Create a gumball geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry(name, sphere);
    
    /** Position the gumballl  */
    ball_geo.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    ball_geo.setMaterial(mat1);
    //rootNode.attachChild(ball_geo);
         
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(1f);
    
    /** Add physical ball to physics space. */
    
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);

    /** Accelerate the physcial ball to shoot it. */
    //ball_phy.setLinearVelocity(cam.getDirection().mult(50)); 
    
    //=======       
          ball_phy.clearForces();
          ball_phy.setLinearVelocity(ball_phy.getLinearVelocity().clone().setY(0));
          ball_phy.applyImpulse(bounceUp, Vector3f.ZERO); 
          //ball_phy.applyImpulse(bounceDown.mult(1), Vector3f.ZERO); 
                  
     //========
           
    return ball_geo;
  }
    
   /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
   
    /*  
    Box box = new Box(15, .2f, 15);
    Geometry floor = new Geometry("the Floor", box);
    floor.setLocalTranslation(0, -4, -5);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Gray );
    floor.setMaterial(mat1);
    return floor;*/
 
      
     /** Initialize the floor geometry */
    floor = new Box(15, .2f, 15);
   // floor.scaleTextureCoordinates(new Vector2f(3, 6));
    
    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    floor_mat.setTexture("ColorMap", tex3);
         
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setMaterial(floor_mat);
    floor_geo.setLocalTranslation(0, -6f, -5);
 
    /* Make the floor physical with mass 0.0f! */
    floor_phy = new RigidBodyControl(0.0f);
    floor_geo.addControl(floor_phy);
    bulletAppState.getPhysicsSpace().add(floor_phy);      
      
    return floor_geo; 
      
      
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

  /** show text on the screen */
  
  protected void showText(String text){
     
                    
    guiNode.detachAllChildren();  // first clear the previous  text        
    BitmapText showText = new BitmapText(guiFont, false);          
    showText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
    showText.setColor(ColorRGBA.Blue);                             // font color
    showText.setText(text);             // the text
    showText.setLocalTranslation(300, showText.getLineHeight()+20, 0); // position
    guiNode.attachChild(showText);   
      
  }
     
             
  
 
}
