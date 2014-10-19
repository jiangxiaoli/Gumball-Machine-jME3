package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
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
import com.jme3.util.SkyFactory;
import java.util.Random;

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
  Material wall_mat;
  Material stone_mat;
  Material floor_mat;
  private Node shootables; //node for all objects
  private Node gumballMachine; //node for gumball machine parts
  private Geometry gBall, cBall; //for gumballmachine and gumballs
  
  /** Prepare geometries and physical nodes for gumballs, floor and cube. */
  private RigidBodyControl    ball_phy;
  private static final Sphere sphere;
  private RigidBodyControl    floor_phy;
  private static final Box    floor;
  private RigidBodyControl    cube_phy;
  private RigidBodyControl    coin_phy;
  private RigidBodyControl    brick_phy;
  private RigidBodyControl    gumballm_phy;
  private static final Box    box;
  private static final Box    box2;
 
  /** dimensions used for bricks and wall */
  private static final float brickLength = 5f;
  private static final float brickWidth  = 1f;
  private static final float brickHeight = 2f;
  
  //for audio nodes
  private AudioNode circus_music;
  private AudioNode coin_slot;
  private AudioNode mach_crank;
  private AudioNode ball_rel;
  private AudioNode ball_whoosh;
  
  //for colors
  private ColorRGBA Red = new ColorRGBA(1,0,0,1);//red
  private ColorRGBA Green = new ColorRGBA(0,1,0,1);//green
  private ColorRGBA Blue = new ColorRGBA(0,0,1,1);//blue
  private ColorRGBA Yellow = new ColorRGBA(1,1,0,1);//yellow
  private ColorRGBA Pink = new ColorRGBA(1,0.68f,0.68f,1);//pink
  private ColorRGBA White =  new ColorRGBA(1,1,1,1);//white
  
  static {
    /** Initialize the cannon ball geometry */
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    /** Initialize the brick geometry */
    box = new Box(brickLength, brickHeight, brickWidth);
    box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    box2 = new Box(brickWidth, brickHeight, brickLength);
    box2.scaleTextureCoordinates(new Vector2f(1f, .5f));
    /** Initialize the floor geometry */
    floor = new Box(60f, 0.1f, 40f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
  }
 
  @Override
  public void simpleInitApp() {
    //make background world blue color
    ColorRGBA background_blue = new ColorRGBA(.1f,.5f,1,1);
    viewPort.setBackgroundColor(background_blue);
    
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
 
    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(0, 6f, 18f));
    cam.lookAt(new Vector3f(0, 4f, 0), Vector3f.UNIT_Y);
    flyCam.setMoveSpeed(10); //move camera faster
    
    //add sky model to world
    rootNode.attachChild(SkyFactory.createSky(
            assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
    
    //node for all gumballmachine world objects
    shootables = new Node("Shootables");
    
    //node for gumball machine
    gumballMachine = new Node("GumballMachine");
    rootNode.attachChild(shootables);
    
    makeGumballMachine();
    makeCoins();
    
    /** Initialize the scene, materials, audio and physics space */
    initMaterials();
    initFloor();
    initCrossHairs();
    initKeys();
    initAudio();
    initWall();
  }
  
  
  public void makeBrick(Vector3f loc) {
    /** Create a brick geometry and attach to scene graph for x-axis. */
    Geometry brick_geo = new Geometry("brick", box);
    brick_geo.setMaterial(stone_mat);
    rootNode.attachChild(brick_geo);
    /** Position the brick geometry  */
    brick_geo.setLocalTranslation(loc);
    /** Make brick physical with a mass > 0.0f. */
    brick_phy = new RigidBodyControl(0.0f);
    /** Add physical brick to physics space. */
    brick_geo.addControl(brick_phy);
    bulletAppState.getPhysicsSpace().add(brick_phy);
  }
  
  public void makeBrickSide(Vector3f loc) {
    /** Create a brick geometry and attach to scene graph for z-axis. */
    Geometry brick_geo = new Geometry("brick", box2);
    brick_geo.setMaterial(stone_mat);
    rootNode.attachChild(brick_geo);
    /** Position the brick geometry  */
    brick_geo.setLocalTranslation(loc);
    /** Make brick physical with a mass > 0.0f. */
    brick_phy = new RigidBodyControl(0.0f);
    /** Add physical brick to physics space. */
    brick_geo.addControl(brick_phy);
    bulletAppState.getPhysicsSpace().add(brick_phy);
  }
  
  
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
   
  protected Geometry makeGumball(String name, Vector3f loc, ColorRGBA color) {
    Sphere sphere_ball = new Sphere(35, 35, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry gumball = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", color);
    gumball.setMaterial(mat1);
    gumball.setLocalTranslation(loc);
    gumball.scale(0.7f);
    
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(0.5f);
    
    /** Add physical ball to physics space. */
    gumball.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    
    return gumball;
  
  }
  
  protected Geometry makeGumball(String name, float x, float y, float z, ColorRGBA color) {
    Sphere sphere_ball = new Sphere(35, 35, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry gumball = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", color);
    gumball.setMaterial(mat1);
    gumball.setLocalTranslation(x,y,z);
    gumball.scale(0.7f);
    
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(0.5f);
    
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
  
  protected void makeGumballMachine() {
    //make gumball machine
    Spatial gM_bot = assetManager.loadModel("Models/GM_buttom/GM_buttom.mesh.xml");
    gM_bot.setLocalTranslation(0, 7f, 1f);
    gM_bot.scale(3f,3f,3f);
    gumballMachine.attachChild(gM_bot);
    
    Spatial gM_mid = assetManager.loadModel("Models/GM_middle/GM_middle.mesh.xml");
    gM_mid.setLocalTranslation(0, 7f, 1f);
    gM_mid.scale(3f,3f,3f);
    gumballMachine.attachChild(gM_mid);
    
    Spatial gM_top = assetManager.loadModel("Models/GM_top/GM_top.mesh.xml");
    gM_top.setLocalTranslation(0, 7f, 1f);
    gM_top.scale(3f,3f,3f);
    gumballMachine.attachChild(gM_top);
    gumballMachine.setName("Gumball Machine");
    gumballm_phy = new RigidBodyControl(0.0f);
    gumballMachine.addControl(gumballm_phy);
    bulletAppState.getPhysicsSpace().add(gumballm_phy);
    
    shootables.attachChild(gumballMachine);
    
    //gM = makeCube("Gumball Machine", 0, 4f, 1f);    
    gumballMachine.addControl((Control) new gumballMachine());
    gumballMachine.getControl(gumballMachine.class).setCount(5);
    gumballMachine.getControl(gumballMachine.class).resetAmtInSlot();
    int gBallPrice = 25;
    gumballMachine.getControl(gumballMachine.class).setGBallPrice(25);
    System.out.println("Gumball price is: "+ gBallPrice+ " cents");
    
  }
  
  //make all coins
  protected void makeCoins() {
    //for stacked coins
    System.out.println("There are piles of coins");
    float j = 0.1f;
    float k = j;
    for (int i = 0; i < 10; i++) {
        
        Spatial Quarter = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Quarter.setName("Quarter");
        Quarter.scale(1f, 1f, 1f);
        Quarter.rotate(0f, 0.0f, 0.0f);
        Quarter.setLocalTranslation(5, j, 0);
        Quarter.addControl((Control) new Coin());
        Quarter.getControl(Coin.class).setValue(25);
        Quarter.getControl(Coin.class).setCount(1);
        shootables.attachChild(Quarter);
        /** Make the ball physcial with a mass > 0.0f */
        coin_phy = new RigidBodyControl(0.1f);
        /** Add physical coin to physics space. */
        Quarter.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Nickel = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Nickel.setName("Nickel");
        Nickel.scale(0.8f, 0.8f, 0.8f);
        Nickel.rotate(0f, 0.0f, 0.0f);
        Nickel.setLocalTranslation(8, j, 0);
        Nickel.addControl((Control) new Coin());
        Nickel.getControl(Coin.class).setValue(5);
        Nickel.getControl(Coin.class).setCount(1);
        shootables.attachChild(Nickel);
        /** Make the ball physcial with a mass > 0.0f */
        coin_phy = new RigidBodyControl(0.1f);
        /** Add physical coin to physics space. */
        Nickel.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Dime = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Dime.setName("Dime");
        Dime.scale(0.5f, 0.5f, 0.5f);
        Dime.rotate(0f, 0.0f, 0.0f);
        Dime.setLocalTranslation(-5, k, 0);
        Dime.addControl((Control) new Coin());
        Dime.getControl(Coin.class).setValue(10);
        Dime.getControl(Coin.class).setCount(1);
        shootables.attachChild(Dime);
        /** Make the ball physcial with a mass > 0.0f */
        coin_phy = new RigidBodyControl(0.01f);
        /** Add physical coin to physics space. */
        Dime.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Penny = assetManager.loadModel("Models/BrownCoin/BrownCoin.mesh.xml");
        Penny.setName("Penny");
        Penny.scale(0.5f, 0.5f, 0.5f);
        Penny.rotate(0f, 0.0f, 0.0f);
        Penny.setLocalTranslation(-9, k, 0);
        Penny.addControl((Control) new Coin());
        Penny.getControl(Coin.class).setValue(1);
        Penny.getControl(Coin.class).setCount(1);
        shootables.attachChild(Penny);
        /** Make the ball physcial with a mass > 0.0f */
        coin_phy = new RigidBodyControl(0.1f);
        /** Add physical coin to physics space. */
        Penny.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        j+=0.1;
        k+=0.05;
    }
    
    
    //for falling coins
    /*System.out.println("It's raining coins!");
    int j = 1;
    for (int i = 0; i < 10; i++) {
        
        Spatial Quarter = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Quarter.setName("Quarter");
        Quarter.scale(1f, 1f, 1f);
        Quarter.rotate(2f, -3.0f, 0.0f);
        Quarter.setLocalTranslation(3, j, 0);
        Quarter.addControl((Control) new Coin());
        Quarter.getControl(Coin.class).setValue(25);
        Quarter.getControl(Coin.class).setCount(1);
        shootables.attachChild(Quarter);
        /// Make the ball physcial with a mass > 0.0f
        coin_phy = new RigidBodyControl(0.1f);
        // Add physical coin to physics space. 
        Quarter.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Nickel = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Nickel.setName("Nickel");
        Nickel.scale(0.8f, 0.8f, 0.8f);
        Nickel.rotate(2f, -3.0f, 0.0f);
        Nickel.setLocalTranslation(6, j, 0);
        Nickel.addControl((Control) new Coin());
        Nickel.getControl(Coin.class).setValue(5);
        Nickel.getControl(Coin.class).setCount(1);
        shootables.attachChild(Nickel);
        // Make the ball physcial with a mass > 0.0f 
        coin_phy = new RigidBodyControl(0.1f);
        // Add physical coin to physics space. 
        Nickel.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Dime = assetManager.loadModel("Models/SilverCoin/SilverCoin.mesh.xml");
        Dime.setName("Dime");
        Dime.scale(0.5f, 0.5f, 0.5f);
        Dime.rotate(2f, -3.0f, 0.0f);
        Dime.setLocalTranslation(-3, j, 0);
        Dime.addControl((Control) new Coin());
        Dime.getControl(Coin.class).setValue(10);
        Dime.getControl(Coin.class).setCount(1);
        shootables.attachChild(Dime);
        // Make the ball physcial with a mass > 0.0f 
        coin_phy = new RigidBodyControl(0.01f);
        // Add physical coin to physics space. 
        Dime.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        Spatial Penny = assetManager.loadModel("Models/BrownCoin/BrownCoin.mesh.xml");
        Penny.setName("Penny");
        Penny.scale(0.5f, 0.5f, 0.5f);
        Penny.rotate(2f, -3.0f, 0.0f);
        Penny.setLocalTranslation(-6, j, 0);
        Penny.addControl((Control) new Coin());
        Penny.getControl(Coin.class).setValue(1);
        Penny.getControl(Coin.class).setCount(1);
        shootables.attachChild(Penny);
        // Make the ball physcial with a mass > 0.0f 
        coin_phy = new RigidBodyControl(0.1f);
        // Add physical coin to physics space.
        Penny.addControl(coin_phy);
        bulletAppState.getPhysicsSpace().add(coin_phy);
        
        j+=1;
    }*/
   
    // Add a light to make the model visible
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
    shootables.addLight(sun);

  }
  
  //make individual gumballs w/ random color
  private void makeGumballs(int rand_c) {
      switch(rand_c) {
          case 1:
              //ColorRGBA Red = new ColorRGBA(1,0,0,1);//red
              gBall = makeGumball("gumball", 0, 1.5f, 7, Red);
              gBall.addControl((Control) new gumball());
              gBall.getControl(gumball.class).setColor("red");
              gBall.getControl(gumball.class).setValue(5);
              shootables.attachChild(gBall);
              break;
          case 2:
              //ColorRGBA Green = new ColorRGBA(0,1,0,1);//green
              gBall = makeGumball("gumball", 0, 1.5f, 7, Green);
              gBall.addControl((Control) new gumball());
              gBall.getControl(gumball.class).setColor("green");
              gBall.getControl(gumball.class).setValue(15);
              shootables.attachChild(gBall);
              break;
          case 3:
              //ColorRGBA Blue = new ColorRGBA(0,0,1,1);//blue
              gBall = makeGumball("gumball", 0, 1.5f, 7, Blue);
              gBall.addControl((Control) new gumball());
              gBall.getControl(gumball.class).setColor("blue");
              gBall.getControl(gumball.class).setValue(50);
              shootables.attachChild(gBall);
              break;
          case 4:
              //ColorRGBA Yellow = new ColorRGBA(1,1,0,1);//yellow
              gBall = makeGumball("gumball", 0, 1.5f, 7, Yellow);
              gBall.addControl((Control) new gumball());
              gBall.getControl(gumball.class).setColor("yellow");
              gBall.getControl(gumball.class).setValue(35);
              shootables.attachChild(gBall);
              break;
          case 5:
              //ColorRGBA Pink = new ColorRGBA(1,0.68f,0.68f,1);//pink
              gBall = makeGumball("gumball", 0, 1.5f, 7, Pink);
              gBall.addControl((Control) new gumball());
              gBall.getControl(gumball.class).setColor("pink");
              gBall.getControl(gumball.class).setValue(100);
              shootables.attachChild(gBall);
              break;
      }
      
  }
  
  public static int randInt(int min, int max) {
    //inclusive of min and max
    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
}
  
  /** This method creates one individual physical cannon ball.
   * By defaul, the ball is accelerated and flies
   * from the camera position in the camera direction.*/
   private void makeCannonBall(int g_color) {
    /** Create a cannon ball geometry and attach to scene graph. */
    
    switch(g_color) {
          case 1:
              cBall = makeGumball("cannonball", cam.getLocation(), Red);
              break;
          case 2:
              cBall = makeGumball("cannonball", cam.getLocation(), Green);
              break;
          case 3:
              cBall = makeGumball("cannonball", cam.getLocation(), Blue);
              break;
          case 4:
              cBall = makeGumball("cannonball", cam.getLocation(), Yellow);
              break;
          case 5:
              cBall = makeGumball("cannonball", cam.getLocation(), Pink);
              break;
      }
    /** Add physical ball to physics space. */
    ball_phy = new RigidBodyControl(1f);
    cBall.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physcial ball to shoot it. */
    ball_phy.setLinearVelocity(cam.getDirection().mult(25));
    shootables.attachChild(cBall);
    
  }

   /** This loop builds a wall out of individual bricks. */
  public void initWall() {
    float height = 0;
    for (int j = 0; j < 1; j++) {
      for (int i = -5; i < 6; i++) {
        //back wall
          Vector3f vt =
            new Vector3f(i * brickLength * 2, brickHeight + height, -35);
            makeBrick(vt);
        //front wall
        Vector3f vt2 =
            new Vector3f(i * brickLength * 2, brickHeight + height, 35);
            makeBrick(vt2);
      }
      for (int k = -3; k < 4; k++) {
          //left wall
          Vector3f vt3 =
            new Vector3f(-55, brickHeight, k * brickLength *2);
            makeBrickSide(vt3);
          //right wall
          Vector3f vt4 =
            new Vector3f(55, brickHeight, k * brickLength *2);
            makeBrickSide(vt4); 
      }
      height += 2 * brickHeight;
    }
  }
  
  //initialize audio nodes
  private void initAudio() {
      circus_music = new AudioNode(assetManager, "Sounds/CircusTheme.ogg");
      circus_music.setLooping(true);
      circus_music.setPositional(false);
      circus_music.setVolume(1);
      rootNode.attachChild(circus_music);
      circus_music.play();
      
      coin_slot = new AudioNode(assetManager, "Sounds/CoinSlot.ogg");
      coin_slot.setPositional(false);
      coin_slot.setLooping(false);
      coin_slot.setVolume(2);
      rootNode.attachChild(coin_slot);
      
      mach_crank = new AudioNode(assetManager, "Sounds/Crank.ogg");
      mach_crank.setPositional(false);
      mach_crank.setLooping(false);
      mach_crank.setVolume(2);
      rootNode.attachChild(mach_crank);
      
      ball_rel = new AudioNode(assetManager, "Sounds/Ball.ogg");
      ball_rel.setPositional(false);
      ball_rel.setLooping(false);
      ball_rel.setVolume(2);
      rootNode.attachChild(ball_rel);
      
      ball_whoosh = new AudioNode(assetManager, "Sounds/Whoosh.ogg");
      ball_whoosh.setPositional(false);
      ball_whoosh.setLooping(false);
      ball_whoosh.setVolume(2);
      rootNode.attachChild(ball_whoosh);
  }
   
  /** Initialize the materials used in this scene. */
  public void initMaterials() {  
    wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
    key.setGenerateMips(true);
    Texture tex = assetManager.loadTexture(key);
    wall_mat.setTexture("ColorMap", tex);
    
    
    stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
    key2.setGenerateMips(true);
    Texture tex2 = assetManager.loadTexture(key2);
    stone_mat.setTexture("ColorMap", tex2);
 
    floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //floor_mat.setColor("Color", ColorRGBA.Gray);
    TextureKey key3 = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
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
      int g_color;
      boolean taken_gball = false;
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
                Spatial p = s.getParent();
                s = p.getParent();
                //System.out.println("You hit " + s.getName());
                if ("Gumball Machine".equals(s.getName())){
                    System.out.println(gumballMachine.getControl(gumballMachine.class).getState());
                    gumballMachine.getControl(gumballMachine.class).turnCrank();
                    mach_crank.playInstance();
                    //to delay release of gumball until after audio finishes
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    if (gumballMachine.getControl(gumballMachine.class).makeGumball()){
                        g_color = randInt(1,5);
                        makeGumballs(g_color);//random # btwn 1-5 for color
                        ball_rel.playInstance();
                        gumballMachine.getControl(gumballMachine.class).resetAmtInSlot();
                    }
                }
                else if ("Quarter".equals(p.getName()) || "Dime".equals(p.getName())
                        || "Nickel".equals(p.getName()) || "Penny".equals(p.getName()) ) {
                    System.out.println(p.getName() + " inserted.");
                    //System.out.print(p.getUserData("value"));
                    //System.out.println(" cent(s)");
                    gumballMachine.getControl(gumballMachine.class).acceptCoin((Integer)p.getUserData("value"));
                    //remove coin from scene
                    shootables.detachChild(p);
                    coin_slot.playInstance();
                    System.out.print("Machine has: ");
                    System.out.print(gumballMachine.getControl(gumballMachine.class).getPayment());
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
                    System.out.print("You can now shoot a ");
                    System.out.print(results.getCollision(0).getGeometry().getUserData("color"));
                    System.out.println(" gumball!");
                    taken_gball = true;
                }
            }
            
          }//for "Crank"
          else if (name.equals("Refill") && !keyPressed) {
              gumballMachine.getControl(gumballMachine.class).refill(5);
              //default refill by 5 gumballs
          }//for "Refill"
          else if (name.equals("Shoot") && !keyPressed) {
              if (taken_gball) {
                  makeCannonBall(g_color);
                  ball_whoosh.playInstance();
                  g_color = 0;
                  taken_gball = false;
              }
              else {
                  System.out.println("No cannonball. You must get a gumball first");
              }
              
          }//for "Shoot"
          
          
          
       }
  };//end ActionListener
  

  
}
