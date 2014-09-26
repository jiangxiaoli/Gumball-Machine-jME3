package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
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
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

 
public class Main extends SimpleApplication {
 
  public static void main(String args[]) {
    Main app = new Main();
    app.start();
    
    /** Disable the default scene graph statistics */
    app.setDisplayStatView(false); 
    app.setDisplayFps(false);
  }
 
  /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;
 
  /** Prepare Materials */
  Material stone_mat;
  Material floor_mat;
  private Node shootables;
  private Geometry gM, gBall;
  //private Spatial testcoin;
  
  /** Prepare geometries and physical nodes for bricks and cannon balls. */
  private RigidBodyControl    ball_phy;
  private static final Sphere sphere;
  private RigidBodyControl    floor_phy;
  private static final Box    floor;
  private RigidBodyControl    cube_phy;
 
  static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    /** Initialize the floor geometry */
    floor = new Box(20f, 0.1f, 15f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
  }
 
  @Override
  public void simpleInitApp() {
    ColorRGBA background_blue = new ColorRGBA(.1f,.5f,1,1);
    viewPort.setBackgroundColor(background_blue);
    
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
 
    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(0, 4f, 18f));
    cam.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);
    
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    
    makeGumballMachine();
    makeCoins();
    
    /** Initialize the scene, materials, and physics space */
    initMaterials();
    initFloor();
    initCrossHairs();
    initKeys();
  }
 
  /**
   * Every time the shoot action is triggered, a new cannon ball is produced.
   * The ball is set up to fly from the camera position in the camera direction.
   */
  /*private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("shoot") && !keyPressed) {
        makeCannonBall();
      }
    }
  };*/
  
   protected Geometry makeSphere(String name, float x, float y, float z) {
    Sphere sphere_ball = new Sphere(40, 150, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry balloon = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    balloon.setMaterial(mat1);
    balloon.setLocalTranslation(x,y,z);
    
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(1f);
    
    /** Add physical ball to physics space. */
    balloon.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    return balloon;
  }
   
  protected Geometry makeGumball(String name, float x, float y, float z, ColorRGBA color) {
    Sphere sphere_ball = new Sphere(40, 150, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry gumball = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", color);
    gumball.setMaterial(mat1);
    gumball.setLocalTranslation(x,y,z);
    
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(1f);
    
    /** Add physical ball to physics space. */
    gumball.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    
    return gumball;
  
  }
  
   /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 4, 2);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    
    cube_phy = new RigidBodyControl(0.0f);
    cube.addControl(cube_phy);
    bulletAppState.getPhysicsSpace().add(cube_phy);
    
    return cube;
  }
  
  public void makeGumballMachine() {
    //make gumball machine
    gM = makeCube("Gumball Machine", 0, 2f, 1f);    
    gM.addControl((Control) new gumballMachine());
    gM.getControl(gumballMachine.class).setCount(5);
    gM.getControl(gumballMachine.class).resetAmtInSlot();
    shootables.attachChild(gM);
  }
  
  //make 20 coins
  public void makeCoins() {
    //making 5x each coin
    int j = 3;
    int k = -3;
    for (int i = 0; i < 5; i++) {
        Spatial Quarter = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Quarter.setName("Quarter");
        Quarter.scale(1f, 1f, 1f);
        Quarter.rotate(2f, -3.0f, 0.0f);
        Quarter.setLocalTranslation(j, 2, 0);
        Quarter.addControl((Control) new Coin());
        Quarter.getControl(Coin.class).setValue(25);
        Quarter.getControl(Coin.class).setCount(1);
        shootables.attachChild(Quarter);
        
        Spatial Nickel = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Nickel.setName("Nickel");
        Nickel.scale(0.8f, 0.8f, 0.8f);
        Nickel.rotate(2f, -3.0f, 0.0f);
        Nickel.setLocalTranslation(k, 2, 0);
        Nickel.addControl((Control) new Coin());
        Nickel.getControl(Coin.class).setValue(5);
        Nickel.getControl(Coin.class).setCount(1);
        shootables.attachChild(Nickel);
        
        Spatial Dime = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Dime.setName("Dime");
        Dime.scale(0.5f, 0.5f, 0.5f);
        Dime.rotate(2f, -3.0f, 0.0f);
        Dime.setLocalTranslation(j, 4, 0);
        Dime.addControl((Control) new Coin());
        Dime.getControl(Coin.class).setValue(10);
        Dime.getControl(Coin.class).setCount(1);
        shootables.attachChild(Dime);
        
        Spatial Penny = assetManager.loadModel("Models/BrownCoin/BrownCoin.mesh.xml");
        Penny.setName("Penny");
        Penny.scale(0.5f, 0.5f, 0.5f);
        Penny.rotate(2f, -3.0f, 0.0f);
        Penny.setLocalTranslation(k, 4, 0);
        Penny.addControl((Control) new Coin());
        Penny.getControl(Coin.class).setValue(1);
        Penny.getControl(Coin.class).setCount(1);
        shootables.attachChild(Penny);   
        j+=2;
        k-=2;
    }
   
    // You must add a light to make the model visible
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    shootables.addLight(sun);

  }
  
  public void makeGumball() {
      ColorRGBA Red = new ColorRGBA(1,0,0,1);//red
      gBall = makeGumball("gumball", 0, 3, 8, Red);
      gBall.addControl((Control) new gumball());
      gBall.getControl(gumball.class).setColor("red");
      gBall.getControl(gumball.class).setValue(5);
      shootables.attachChild(gBall);
      
  }
 
  /** Initialize the materials used in this scene. */
  public void initMaterials() {
    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);
 
    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key3 = new TextureKey("Textures/Terrain/Pond/Pond.jpg");
    key3.setGenerateMips(true);
    Texture tex3 = assetManager.loadTexture(key3);
    tex3.setWrap(WrapMode.Repeat);
    floor_mat.setTexture("ColorMap", tex3);
  }
 
  /** Make a solid floor and add it to the scene. */
  public void initFloor() {
    Geometry floor_geo = new Geometry("Floor", floor);
    floor_geo.setMaterial(floor_mat);
    floor_geo.setLocalTranslation(0, -0.1f, 0);
    this.rootNode.attachChild(floor_geo);
    /* Make the floor physical with mass 0.0f! */
    floor_phy = new RigidBodyControl(0.0f);
    floor_geo.addControl(floor_phy);
    bulletAppState.getPhysicsSpace().add(floor_phy);
  }
 
  /**A plus sign used as crosshairs to help the player with aiming.*/
  protected void initCrossHairs() {
    guiNode.detachAllChildren();
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+");        // fake crosshairs :)
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
      settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
    guiNode.attachChild(ch);
  }
  
  /** Declaring the "Shoot" action and mapping to its triggers. */
  private void initKeys() {
    inputManager.addMapping("Shoot",
      new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Shoot");
    
    inputManager.addMapping("Click",
      new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(actionListener, "Click");
    
    inputManager.addMapping("Refill",
      new KeyTrigger(KeyInput.KEY_R));//R button is trigger for refill action
    inputManager.addListener(actionListener, "Refill");
  }
  
  
  private ActionListener actionListener = new ActionListener() {
      public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("Click") && !keyPressed) {
            // 1. Reset results list.
            CollisionResults results = new CollisionResults();
            // 2. Aim the ray from cam loc to cam direction.
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
            // 3. Collect intersections between Ray and Shootables in results list.
            shootables.collideWith(ray, results);
            
            if (results.size() > 0) {//not missed
                //String hit = results.getCollision(0).getGeometry().getName();
                //System.out.println("  You hit " + hit);
                Spatial s = results.getCollision(0).getGeometry();
                s = s.getParent();
                if ("Gumball Machine".equals(results.getCollision(0).getGeometry().getName())){
                    gM.getControl(gumballMachine.class).turnCrank();
                    if (gM.getControl(gumballMachine.class).makeGumball()){
                        makeGumball();  
                        gM.getControl(gumballMachine.class).resetAmtInSlot();
                    }
                    
                }
                else if ("Quarter".equals(s.getName()) || "Dime".equals(s.getName())
                        || "Nickel".equals(s.getName()) || "Penny".equals(s.getName()) ) {
                    System.out.println(s.getName() + " inserted.");
                    //System.out.print(s.getUserData("value"));
                    //System.out.println(" cent(s)");
                    gM.getControl(gumballMachine.class).acceptCoin((Integer)s.getUserData("value"));
                    //remove coin from scene
                    shootables.detachChild(s);
                    System.out.print("Machine has: ");
                    System.out.print(gM.getControl(gumballMachine.class).getAmtInSlot());
                    System.out.println(" cent(s)");
                }
                else if ("gumball".equals(results.getCollision(0).getGeometry().getName())){
                    System.out.println("Taking gumball...");
                    System.out.print("Gumball has color ");
                    System.out.print(results.getCollision(0).getGeometry().getUserData("color"));
                    System.out.print(" and value of ");
                    System.out.println(results.getCollision(0).getGeometry().getUserData("value"));
                    //remove gumball from scene
                    bulletAppState.getPhysicsSpace().remove(ball_phy);
                    results.getCollision(0).getGeometry().removeFromParent();
                    shootables.detachChild(results.getCollision(0).getGeometry());
                }
            }
            
          }//for "Crank"
          else if (name.equals("Refill") && !keyPressed) {
              gM.getControl(gumballMachine.class).refill(5);
              //default refill by 5 gumballs
          }//for "Refill"
          else if (name.equals("Shoot") && !keyPressed) {
              //do something here for shooting
          }//for "Shoot"
          
          
          
       }
  };//end ActionListener
  

  
}
