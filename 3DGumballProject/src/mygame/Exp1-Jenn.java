package mygame;
 
import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
 
/*Adapted from example 12*/
public class Main extends SimpleApplication {
 
  public static void main(String args[]) {
    Main app = new Main();
    app.start();
  }
 
  /** Prepare the Physics Application State (jBullet) */
  private BulletAppState bulletAppState;
 
  /** Prepare Materials */
  Material stone_mat;
  Material floor_mat;
  private Node shootables;
  /** Prepare geometries and physical nodes for bricks and cannon balls. */
  private RigidBodyControl    ball_phy, ball_phy2;
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
    /** Set up Physics Game */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
 
    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(0, 2f, 10f));
    cam.lookAt(new Vector3f(2, 2, 0), Vector3f.UNIT_Y);
    /** Add InputManager action: Left click triggers shooting. */
    inputManager.addMapping("shoot", 
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(actionListener, "shoot");
    
    /** create four colored boxes and a floor to shoot at: */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeCube("box1", -3f, 2f, 1f));
    shootables.attachChild(makeCube("box2", 5f, 2f, 1f));
    shootables.attachChild(makeCube("box3", 0f, 2f, 1f));
    //shootables.attachChild(makeCube("the Deputy", 1f, 0f, -4f));
    
    shootables.attachChild(makeSphere("floating1", 2f, 8f, -3f));
    shootables.attachChild(makeSphere("floating2", -5f, 5f, 2f));
    
    /** Initialize the scene, materials, and physics space */
    initMaterials();
    initFloor();
    initCrossHairs();
    
    
  }
 
  /**
   * Every time the shoot action is triggered, a new cannon ball is produced.
   * The ball is set up to fly from the camera position in the camera direction.
   */
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("shoot") && !keyPressed) {
        makeCannonBall();
      }
    }
  };
  
   /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 2, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    
    cube_phy = new RigidBodyControl(2f);
    cube.addControl(cube_phy);
    bulletAppState.getPhysicsSpace().add(cube_phy);
    
    
    return cube;
  }
  
   /** A sphere object for target practice */
  protected Geometry makeSphere(String name, float x, float y, float z) {
    Sphere sphere_ball = new Sphere(40, 150, 0.8f, true, false);
    sphere_ball.setTextureMode(TextureMode.Projected);
    Geometry balloon = new Geometry(name, sphere_ball);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
   
    balloon.setMaterial(mat1);
    balloon.setLocalTranslation(x,y,z);
    
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy2 = new RigidBodyControl(1f);
    //ball_phy2.applyCentralForce(new Vector3f(0,400*10,0));
    /** Add physical ball to physics space. */
    balloon.addControl(ball_phy2);
    bulletAppState.getPhysicsSpace().add(ball_phy2);
    
    return balloon;
      
      
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
 
  
  /** This method creates one individual physical cannon ball.
   * By defaul, the ball is accelerated and flies
   * from the camera position in the camera direction.*/
   public void makeCannonBall() {
    /** Create a cannon ball geometry and attach to scene graph. */
    Geometry ball_geo = new Geometry("cannon ball", sphere);
    ball_geo.setMaterial(stone_mat);
    rootNode.attachChild(ball_geo);
    /** Position the cannon ball  */
    ball_geo.setLocalTranslation(cam.getLocation());
    /** Make the ball physcial with a mass > 0.0f */
    ball_phy = new RigidBodyControl(1f);
    /** Add physical ball to physics space. */
    ball_geo.addControl(ball_phy);
    bulletAppState.getPhysicsSpace().add(ball_phy);
    /** Accelerate the physcial ball to shoot it. */
    ball_phy.setLinearVelocity(cam.getDirection().mult(25));
  }
 
  /** A plus sign used as crosshairs to help the player with aiming.*/
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
}