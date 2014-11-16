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
import com.jme3.renderer.queue.RenderQueue.Bucket;
import java.util.ArrayList;
import java.util.List;

public class Main extends SimpleApplication { 
  public static void main(String args[]) {
    Main app = new Main();
    app.start();
    
    // Disable the default scene graph statistics
    app.setDisplayStatView(false); 
    app.setDisplayFps(false);
  }
 
  // Prepare the Physics Application State (jBullet)
  private BulletAppState bulletAppState;
 
  // Prepare Materials
  Material wall_mat;
  Material stone_mat;
  Material floor_mat;
  private Node shootables; //node for all objects
  private Node gumballMachine; //node for gumball machine parts
  private Geometry gBall, cBall; //for gumballmachine and gumballs
  
  // Prepare HUD text screen
  private BitmapText userInfoScreen; // for showing user's information
  private BitmapText sysStatusInfoScreen; // for showing the system status
  private userInfo userData;
  private InfoCenter infoCenter;
  private String gameStatus;
  
  /// Prepare geometries and physical nodes for gumballs, floor and cube.
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
 
  /// dimensions used for bricks and wall
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
  
  //for cannonball, brick and floor
  static {
    sphere = new Sphere(32, 32, 0.4f, true, false);
    sphere.setTextureMode(TextureMode.Projected);
    
    box = new Box(brickLength, brickHeight, brickWidth);
    box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    box2 = new Box(brickWidth, brickHeight, brickLength);
    box2.scaleTextureCoordinates(new Vector2f(1f, .5f));
    
    floor = new Box(60f, 0.1f, 40f);
    floor.scaleTextureCoordinates(new Vector2f(3, 6));
  }
  
  @Override
  public void simpleInitApp() {
    //make background world blue color
    ColorRGBA background_blue = new ColorRGBA(.1f,.5f,1,1);
    viewPort.setBackgroundColor(background_blue);
    
    // Set up Physics Game
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
 
    // Configure cam to look at scene
    cam.setLocation(new Vector3f(0, 6f, 18f));
    cam.lookAt(new Vector3f(0, 4f, 0), Vector3f.UNIT_Y);
    flyCam.setMoveSpeed(10); //move camera faster
    
    //add sky model to world
    rootNode.attachChild(SkyFactory.createSky(
            assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
    
    //create node for all gumballmachine world objects
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables); //attach all world obj to root note
    
    
    //create node for gumball machine (has 3 parts)
    gumballMachine = new Node("GumballMachine");
    
    //run all initialize functions
    initializeWorld();
    
    //for gumball machine - get input from user?
    int gBallPrice = 25;
    int initialGBall = 5;
    
    makeGumballMachine(initialGBall, gBallPrice);
    makeCoins();
    
    //add wall and target object to shoot --------*/
    addWallandTarget();
  }
  
  // for showing the data of the game
  @Override
  public void simpleUpdate(float tpf) {
    // Display HUD text
    String user_data = infoCenter.observerState;
    userInfoScreen.setText(user_data); 
    sysStatusInfoScreen.setText("Status: " + gameStatus + "\n");
    
    //update gumball elapse time, check each gumball, if > 20s ,detach
    //test shootables.getChildren()
    List<Spatial> sChilren = shootables.getChildren();
    //for gumball collections
    List<Spatial> gumballs = new ArrayList<Spatial>();
    for(Spatial s : sChilren){
        //System.out.println(s.getName());
        if(s.getName().equals("gumball")){
            gumballs.add(s);
        }
    }
    
    for(Spatial s : gumballs){
        //s.addControl((Control) new ElapseGumball());
        int val = s.getControl(ElapseGumball.class).getValue();
        long time = s.getControl(ElapseGumball.class).getElapse();
        //ElapseGumball es = (ElapseGumball) s;
        //long time = es.getElapse();
        System.out.println("gumball elapsed time:" +time );
        
        //gumball disapper with time
        if(time>20000){
            shootables.detachChild(s);
            //maybe add blink effects
        }
    }    
    
  }
  
  public void initializeWorld() {
      /** Initialize the scene, materials, audio and physics space */
      initMaterials();
      initFloor();
      initCrossHairs();
      initKeys();
      initAudio();
      initWall();
      initWelcome();
      userInfoScreen = initPlayerInfo();
      sysStatusInfoScreen = initSysStas();
      intiUserInfo();
  }


  
  public void makeBrick(Vector3f loc) {
    Geometry brick_geo = new Geometry("brick", box);
    brick_geo.setMaterial(stone_mat);
    rootNode.attachChild(brick_geo);
    brick_geo.setLocalTranslation(loc);
    
    // Make brick physical with a mass > 0.0f. */
    brick_phy = new RigidBodyControl(0.0f);
    brick_geo.addControl(brick_phy);
    bulletAppState.getPhysicsSpace().add(brick_phy);
  }
  
  public void makeBrickSide(Vector3f loc) {
    // Create a brick geometry and attach to scene graph for z-axis
    Geometry brick_geo = new Geometry("brick", box2);
    brick_geo.setMaterial(stone_mat);
    rootNode.attachChild(brick_geo);
    brick_geo.setLocalTranslation(loc);
    
    // Make brick physical with a mass > 0.0f. */
    brick_phy = new RigidBodyControl(0.0f);
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
    
    ball_phy = new RigidBodyControl(1f);
    balloon.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    return balloon;
  }
   
  protected Geometry makeGumball(String name, Vector3f loc, ColorRGBA color) {
    /*for cannonballs*/
    Sphere sphere_ball = new Sphere(35, 35, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry gumball = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", color);
    gumball.setMaterial(mat1);
    gumball.setLocalTranslation(loc);
    gumball.scale(0.7f); //scale to make it smaller
    
    ball_phy = new RigidBodyControl(0.5f);
    gumball.addControl(ball_phy);
    //ball_phy.setLinearVelocity(cam.getDirection().mult(40));
    bulletAppState.getPhysicsSpace().add(ball_phy);
    return gumball;
  }
  
  protected Geometry makeGumball(String name, float x, float y, float z, ColorRGBA color) {
    //overloads previous method
    Sphere sphere_ball = new Sphere(35, 35, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry gumball = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", color);
    gumball.setMaterial(mat1);
    gumball.setLocalTranslation(x,y,z);
    gumball.scale(0.7f);
    
    ball_phy = new RigidBodyControl(1f);
    gumball.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    
    
    //Vector3f dir = new Vector3f(0,-2,0);
    //System.out.println(cam.getDirection());
    int g_speed = randInt(40,60);
    ball_phy.setLinearVelocity(cam.getDirection().mult(g_speed));
    //shootables.attachChild(cBall);
    
    //
    //ball_phy.setLinearVelocity(cam.getLocation().mult(g_speed));
    
    
    return gumball;
  }
  
  /*protected Geometry makeCube(String name, float x, float y, float z) {
    // A cube object for target practice
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
  }*/
  
  protected void makeGumballMachine(int initialGBall, int gBallPrice) {
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
    
    //attach gumballMachine to shootables node, which is attached to rootNode
    shootables.attachChild(gumballMachine);
    
    gumballMachine.addControl((Control) new gumballMachine());
    gumballMachine.getControl(gumballMachine.class).setCount(initialGBall);
    gumballMachine.getControl(gumballMachine.class).resetAmtInSlot();
    gumballMachine.getControl(gumballMachine.class).setGBallPrice(gBallPrice);
    System.out.println("Gumball price is: "+ gBallPrice+ " cents");
  }
  
  //make all coins
  protected void makeCoins() {
    //for stacked coins
    System.out.println("There are piles of coins");
    float j = 0.1f;
    float k = j;
    int coinAmt = 0; 
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
        coinAmt += 25;
        
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
        coinAmt += 5;
        
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
        coinAmt += 10;
        
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
        coinAmt += 1;
        
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
    userData.setState("coinAmt", coinAmt); // Initialize the coinAmt in HUD screen

  }
  
  //make individual gumballs w/ random color
  private void makeGumballs(int rand_c) {
      
      
      switch(rand_c) {
          case 1:
              //ColorRGBA Red = new ColorRGBA(1,0,0,1);//red
              gBall = makeGumball("gumball", 0, 1.5f, 7, Red);
              gBall.addControl((Control) new ElapseGumball (new gumball())); //apply decorator
              gBall.getControl(ElapseGumball.class).setColor("red");
              gBall.getControl(ElapseGumball.class).setValue(5);
              //shootables.attachChild(gBall);
              break;
          case 2:
              //ColorRGBA Green = new ColorRGBA(0,1,0,1);//green
              gBall = makeGumball("gumball", 0, 1.5f, 7, Green);
              gBall.addControl((Control) new ElapseGumball (new gumball()));//apply decorator
              gBall.getControl(ElapseGumball.class).setColor("green");
              gBall.getControl(ElapseGumball.class).setValue(15);
              //shootables.attachChild(gBall);
              break;
          case 3:
              //ColorRGBA Blue = new ColorRGBA(0,0,1,1);//blue
              gBall = makeGumball("gumball", 0, 1.5f, 7, Blue);
              gBall.addControl((Control) new ElapseGumball (new gumball()));//apply decorator
              gBall.getControl(ElapseGumball.class).setColor("blue");
              gBall.getControl(ElapseGumball.class).setValue(50);
              //shootables.attachChild(gBall);
              break;
          case 4:
              //ColorRGBA Yellow = new ColorRGBA(1,1,0,1);//yellow
              gBall = makeGumball("gumball", 0, 1.5f, 7, Yellow);
              gBall.addControl((Control) new ElapseGumball (new gumball()));//apply decorator
              gBall.getControl(ElapseGumball.class).setColor("yellow");
              gBall.getControl(ElapseGumball.class).setValue(35);
              //shootables.attachChild(gBall);
              break;
          case 5:
              //ColorRGBA Pink = new ColorRGBA(1,0.68f,0.68f,1);//pink
              gBall = makeGumball("gumball", 0, 1.5f, 7, Pink);
              gBall.addControl((Control) new ElapseGumball (new gumball()));//apply decorator
              gBall.getControl(ElapseGumball.class).setColor("pink");
              gBall.getControl(ElapseGumball.class).setValue(100);
              //shootables.attachChild(gBall);
              break;
      }
      
      shootables.attachChild(gBall);
      
      
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
    
    
    ball_phy.setLinearVelocity(cam.getDirection().mult(40));
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
  
  /** Display a Greeting Title on top of the game screen*/
  protected void initWelcome() {
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText hudText = new BitmapText(guiFont, false);
    hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
    hudText.setColor(ColorRGBA.Orange);  // font color
    hudText.setText("Welcome To Team 6's Gumball World\n"); // the text
    hudText.setLocalTranslation(50, 575, 0); // position
    guiNode.attachChild(hudText);
    guiNode.setQueueBucket(Bucket.Gui);
    
  }
  
  /*Display the game status*/
  protected BitmapText initSysStas() {
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText hudText = new BitmapText(guiFont, false);
    hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
    hudText.setColor(ColorRGBA.Yellow);  // font colo
    hudText.setLocalTranslation(10, 60, 0); // position
    guiNode.attachChild(hudText);
    guiNode.setQueueBucket(Bucket.Gui);
    return hudText;
  }
  
  /*Display the player's information*/
  protected BitmapText initPlayerInfo(){
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText hudText = new BitmapText(guiFont, false);
    hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
    hudText.setColor(ColorRGBA.Yellow);  // font colo
    hudText.setLocalTranslation(600, 575, 0); // position
    guiNode.attachChild(hudText);
    guiNode.setQueueBucket(Bucket.Gui);
    return hudText;
    
  }

  private void intiUserInfo(){
    userData  = new userInfo();
    infoCenter = new InfoCenter(userData);
    userData.attach(infoCenter);
    userData.setState("score", 0);
    userData.setState("numOfGumballs", 0);
    // coinAmt will be initialized in makeCoins function
  }
  
  private ActionListener actionListener = new ActionListener() {
      int g_color;
      boolean taken_gball = false;
      int gBalls = 0;
      public void onAction(String name, boolean keyPressed, float tpf) {
          if (name.equals("Click") && !keyPressed) {
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(cam.getLocation(), cam.getDirection());
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
                    gameStatus = gumballMachine.getControl(gumballMachine.class).turnCrank();
                    System.out.println(gameStatus); 
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
                    // Subtract inserted coin value from user
                    int origCoinAmt = userData.getState().get("coinAmt");
                    userData.setState("coinAmt", origCoinAmt -(Integer)p.getUserData("value"));
                    //System.out.print(p.getUserData("value"));
                    //System.out.println(" cent(s)");
                    gumballMachine.getControl(gumballMachine.class).acceptCoin((Integer)p.getUserData("value"));
                    //remove coin from scene
                    shootables.detachChild(p);
                    coin_slot.playInstance();
                    gameStatus = "Machine has: " 
                                 + gumballMachine.getControl(gumballMachine.class).getPayment()
                                 + " cent(s)";
                    System.out.println(gameStatus); 
                    // System.out.print("Machine has: ");
                    // System.out.print(gumballMachine.getControl(gumballMachine.class).getPayment());
                    // System.out.println(" cent(s)");
                }
                else if ("gumball".equals(results.getCollision(0).getGeometry().getName())){
                    String status = "Taking gumball... \n" + "Gumball has color "
                                    + results.getCollision(0).getGeometry().getUserData("color") 
                                    + " and value of ";
                    int gumball_score = results.getCollision(0).getGeometry().getUserData("value");
                    System.out.println(status + gumball_score);

                    //System.out.println("Taking gumball...");
                    //System.out.print("Gumball has color ");
                    //System.out.print(results.getCollision(0).getGeometry().getUserData("color"));
                    //System.out.print(" and value of ");
                    //System.out.println(results.getCollision(0).getGeometry().getUserData("value"));
                    

                    // change the Hud text in the playing screen
                    gameStatus = status + gumball_score;
                    int origScore = userData.getState().get("score");
                    userData.setState("score", origScore + gumball_score);

                    //remove gumball from scene
                    bulletAppState.getPhysicsSpace().remove(ball_phy);
                    results.getCollision(0).getGeometry().removeFromParent();
                    shootables.detachChild(results.getCollision(0).getGeometry());
                    System.out.print("You can now shoot a ");
                    System.out.print(results.getCollision(0).getGeometry().getUserData("color"));
                    System.out.println(" gumball!");
                    taken_gball = true;
                    gBalls++;
                    System.out.println("You have " + gBalls + " gumball(s)!");
                    // Show number of gumballs in HUD screen
                    int origGumballs = userData.getState().get("numOfGumballs");
                    userData.setState("numOfGumballs", origGumballs+1);
                }
                /*else if ("cannonball".equals(results.getCollision(0).getGeometry().getName())){
                    System.out.println("Retrieving the gumball!");
                    gBalls++;
                    //taken_gball = true;
                    //g_color = results.getCollision(0).getGeometry().getUserData("color");
                    //System.out.println("You have " + gBalls + " gumball(s) now!");
                    gameStatus = "You have " + gBalls + " gumball(s) now!";
                    System.out.println(gameStatus);
                    //remove cannonball from scene
                    bulletAppState.getPhysicsSpace().remove(ball_phy);
                    results.getCollision(0).getGeometry().removeFromParent();
                    shootables.detachChild(results.getCollision(0).getGeometry());
                }*/
            }//end hit
          }//end Click
          else if (name.equals("Refill") && !keyPressed) {
              gumballMachine.getControl(gumballMachine.class).refill(5);
              //default refill by 5 gumballs
          }//end Refill
          else if (name.equals("Shoot") && !keyPressed) {
              if (taken_gball) {
                  makeCannonBall(g_color);
                  ball_whoosh.playInstance();
                  g_color = 0;
                  gBalls--;
                  //System.out.println("You have " + gBalls + " gumball(s) left!");
                  gameStatus = "You have " + gBalls + " gumball(s) left!";
                  System.out.println(gameStatus);
                                                                    
                  /*---------- Adding for gumball shoot the target-----------------*/
                  CollisionResults shootResults = new CollisionResults();
                  Ray shootRay = new Ray(cam.getLocation(), cam.getDirection());
                  shootables.collideWith(shootRay, shootResults);
                  if(shootResults.size()>1)
                  {
                      int origScore = userData.getState().get("score");
                      if ("Teapot".equals(shootResults.getCollision(1).getGeometry().getName())){
                           //Teapot score +100                          
                           userData.setState("score", origScore + 100);
                           gameStatus = "You hit Teapot, and you get 100 points";
                           System.out.println(gameStatus);
                      }else if("Rock Ball".equals(shootResults.getCollision(1).getGeometry().getName())){
                            //Teapot score +200
                           userData.setState("score", origScore + 200);
                           gameStatus = "You hit Rock Ball, and you get 200 points ";
                           System.out.println(gameStatus);
                      }else if("Elephant-geom-1".equals(shootResults.getCollision(1).getGeometry().getName())){
                           userData.setState("score", origScore + 150); 
                           gameStatus = "You hit Elephant, and you get 150 points ";
                           System.out.println(gameStatus);
                      }
                  }
                                    
                  taken_gball = false;
              }
              else {
                  //System.out.println("No cannonball. You must get a gumball first");
                  gameStatus = "No cannonball. You must get a gumball first";
                  System.out.println(gameStatus);
              }
              
          }//end Shoot
          
          
          
       }//end onAction
  };//end ActionListener
  
  /******* add the wall and target for shooting **********/
  protected void addWallandTarget() {

      //For Factory Method Pattern 
      ConcreteCreator concreteCreator = new ConcreteCreator(assetManager,shootables);

      //get an object of Teapot and call its makeProduct method.
      Product teapot = concreteCreator.getProduct("TEAPOT");
      //call makeProduct method of Teapot
      teapot.makeProduct();

      //get an object of Sphere and call its makeProduct method.
      Product sphere = concreteCreator.getProduct("SPHERE");
      //call makeProduct method of Sphere
      sphere.makeProduct();
      
      //get an object of Elephant and call its makeProduct method.
      Product elephant = concreteCreator.getProduct("ELEPHANT");
      //call makeProduct method of Elephant
      elephant.makeProduct();

      // Create a wall with a simple texture from test_data
        Box box = new Box(0.8f,5f,8f);
        Spatial wall = new Geometry("Box", box );
        Material mat_brick = new Material( 
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap", 
            assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg"));
        mat_brick.setColor("Color", ColorRGBA.LightGray);
        wall.setMaterial(mat_brick);
        wall.setLocalTranslation(-18f,0f,4f);
        
        
        // Make brick physical with a mass > 0.0f. */
        brick_phy = new RigidBodyControl(0.0f);
        wall.addControl(brick_phy);
        bulletAppState.getPhysicsSpace().add(brick_phy);
        
        shootables.attachChild(wall);      
  }
  
  
}//end Main class