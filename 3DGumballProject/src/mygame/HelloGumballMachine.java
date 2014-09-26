package cmpe202.hellogumballmachine;

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
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

public class HelloGumballMachine extends SimpleApplication{
    
    public static void main(String[] args){
        HelloGumballMachine app = new HelloGumballMachine();
        app.start();
    }
    
    protected Spatial teapot1;
    protected Spatial teapot2;
    protected Spatial teapot3;
    protected Spatial elephant;    
    protected Spatial ninja;
    Boolean isRunning=true;
    private Node shootables;
    private Geometry mark;
    
    @Override
    public void simpleInitApp(){
        
        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initKeys();       // load custom key mappings
        initMark();       // a red sphere to mark the hit
        
        rootNode.attachChild(makeFloor());
        
        /** create a red box at (1, -1, 1) to represent gumball machine */
        Box gumballMachine1 = new Box (1, 3, 1);
        Geometry gm1 = new Geometry("Box", gumballMachine1);
        gm1.setLocalTranslation(new Vector3f(1, 0, 3));
        Material matGm1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matGm1.setColor("Color", ColorRGBA.Magenta);
        gm1.setMaterial(matGm1);
        rootNode.attachChild(gm1);
        

        
        /** Illuminated bumpy rock with shiny effect. 
        *  Uses Texture from jme3-test-data library! Needs light source! */
       Sphere sphereMesh = new Sphere(32,32, 0.2f);
       Geometry shinyGeoGB1 = new Geometry("Shiny rock", sphereMesh);
       Geometry shinyGeoGB2 = new Geometry("Shiny rock", sphereMesh);
       Geometry shinyGeoGB3 = new Geometry("Shiny rock", sphereMesh);
       Geometry shinyGeoGB4 = new Geometry("Shiny rock", sphereMesh);
       sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
       TangentBinormalGenerator.generate(sphereMesh);   // for lighting effect
       Material shinyMatGB1 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
       shinyMatGB1.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
       shinyMatGB1.setTexture("NormalMap",  assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
       //shinyMat.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires glow filter!
       shinyMatGB1.setBoolean("UseMaterialColors",true);  // needed for shininess
       shinyMatGB1.setColor("Specular", ColorRGBA.Red); // needed for shininess
       shinyMatGB1.setColor("Diffuse",  ColorRGBA.Red); // needed for shininess
       shinyMatGB1.setFloat("Shininess", 5f); // shininess from 1-128
       shinyGeoGB1.setLocalTranslation(0.2f, 2, 4);
       shinyGeoGB1.setMaterial(shinyMatGB1);
       Material shinyMatGB2 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
       shinyMatGB2.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
       shinyMatGB2.setTexture("NormalMap",  assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
       //shinyMat.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires glow filter!
       shinyMatGB2.setBoolean("UseMaterialColors",true);  // needed for shininess
       shinyMatGB2.setColor("Specular", ColorRGBA.Green); // needed for shininess
       shinyMatGB2.setColor("Diffuse",  ColorRGBA.Green); // needed for shininess
       shinyMatGB2.setFloat("Shininess", 5f); // shininess from 1-128
       shinyGeoGB2.setLocalTranslation(0.6f, 2, 4);
       shinyGeoGB2.setMaterial(shinyMatGB2);
       Material shinyMatGB3 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
       shinyMatGB3.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
       shinyMatGB3.setTexture("NormalMap",  assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
       //shinyMat.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires glow filter!
       shinyMatGB3.setBoolean("UseMaterialColors",true);  // needed for shininess
       shinyMatGB3.setColor("Specular", ColorRGBA.Yellow); // needed for shininess
       shinyMatGB3.setColor("Diffuse",  ColorRGBA.Yellow); // needed for shininess
       shinyMatGB3.setFloat("Shininess", 5f); // shininess from 1-128
       shinyGeoGB3.setLocalTranslation(1f, 2, 4);
       shinyGeoGB3.setMaterial(shinyMatGB3);
       Material shinyMatGB4 = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md");
       shinyMatGB4.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
       shinyMatGB4.setTexture("NormalMap",  assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
       //shinyMat.setTexture("GlowMap", assetManager.loadTexture("Textures/glowmap.png")); // requires glow filter!
       shinyMatGB4.setBoolean("UseMaterialColors",true);  // needed for shininess
       shinyMatGB4.setColor("Specular", ColorRGBA.Blue); // needed for shininess
       shinyMatGB4.setColor("Diffuse",  ColorRGBA.Blue); // needed for shininess
       shinyMatGB4.setFloat("Shininess", 5f); // shininess from 1-128
       shinyGeoGB4.setLocalTranslation(1.4f, 2, 4);
       shinyGeoGB4.setMaterial(shinyMatGB4);
       rootNode.attachChild(shinyGeoGB1);
       rootNode.attachChild(shinyGeoGB2);
       rootNode.attachChild(shinyGeoGB3);
       rootNode.attachChild(shinyGeoGB4);
           
        /** create three spheres to represent coins */
        Sphere coin1 = new Sphere(32,32, 0.6f);
        Sphere coin2 = new Sphere(32,32, 0.6f);
        Sphere coin3 = new Sphere(32,32, 0.6f);;
        Geometry c1 = new Geometry("Sphere", coin1);
        Geometry c2 = new Geometry("Sphere", coin2);
        Geometry c3 = new Geometry("Sphere", coin3);
        c1.setLocalTranslation(new Vector3f(-5, 3, 0));
        c2.setLocalTranslation(new Vector3f(-5, 0, 0));
        c3.setLocalTranslation(new Vector3f(-5, -3, 0));
        Material matCoin1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCoin1.setColor("Color", ColorRGBA.Yellow);
        Material matCoin2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCoin2.setColor("Color", ColorRGBA.Blue);
        Material matCoin3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCoin3.setColor("Color", ColorRGBA.Green);
        c1.setMaterial(matCoin1);
        c2.setMaterial(matCoin2);
        c3.setMaterial(matCoin3);
        rootNode.attachChild(c1);
        rootNode.attachChild(c2);
        rootNode.attachChild(c3);
        
//        Node pivot = new Node("pivot");
//        rootNode.attachChild(pivot);
//        pivot.attachChild(gm1);
//        pivot.attachChild(c1);
//        pivot.attachChild(c2);
//        pivot.attachChild(c3);
//        pivot.rotate(0, 0, FastMath.HALF_PI);
        
        /** create three teapots on the wall */
        teapot1 = assetManager.loadModel("Models/Teapot/Teapot.obj");
        teapot2 = assetManager.loadModel("Models/Teapot/Teapot.obj");
        teapot3 = assetManager.loadModel("Models/Teapot/Teapot.obj");
        Material mat_default = new Material( 
            assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        teapot1.setMaterial(mat_default);
        teapot2.setMaterial(mat_default);
        teapot3.setMaterial(mat_default);
        teapot1.setLocalTranslation(-2.0f,2.5f,-3.0f);
        teapot2.setLocalTranslation(2f,2.5f,-3.0f);
        teapot3.setLocalTranslation(10.0f,2.5f,-3.0f);
        rootNode.attachChild(teapot1);
        rootNode.attachChild(teapot2);
        rootNode.attachChild(teapot3);
        
        //Create an elephant statue on the wall
        elephant = assetManager.loadModel("Models/Elephant/Elephant.mesh.xml");
        elephant.scale(0.01f, 0.01f, 0.01f);
        elephant.rotate(0.0f, -1.5f, 0.0f);
        elephant.setMaterial(mat_default);
        elephant.setLocalTranslation(6f,2.5f,-3.0f);
        rootNode.attachChild(elephant);
        
        // Create a wall with a simple texture from test_data
        Box box = new Box(10f,2.5f,1.0f);
        Spatial wall = new Geometry("Box", box );
        Material mat_brick = new Material( 
            assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap", 
            assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        wall.setMaterial(mat_brick);
        wall.setLocalTranslation(2.0f,0,-3.0f);
        rootNode.attachChild(wall);
        
        // Display a line of text with a default font
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText helloText = new BitmapText(guiFont, false);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setText("Hello Gumball Machine!");
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);
        
        // Load a model from test_data (OgreXML + material + texture)
        ninja = assetManager.loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.05f, 0.05f, 0.05f);
        ninja.rotate(0.0f, 1.5f, 0.0f);
        ninja.setLocalTranslation(8f, -4f, 3f);
        rootNode.attachChild(ninja);
//        initKeys();// load my custom keybinding
        
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        viewPort.setBackgroundColor(ColorRGBA.DarkGray); 
        
            /** create four colored boxes and a floor to shoot at: */
        shootables = new Node("Shootables");
        rootNode.attachChild(shootables);
        shootables.attachChild(teapot1);
        shootables.attachChild(teapot2);
        shootables.attachChild(teapot3);
        shootables.attachChild(elephant);
        shootables.attachChild(makeFloor());
       
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // make the player rotate:
        teapot1.rotate(0, 1*tpf, 0); 
        teapot2.rotate(0, 2*tpf, 0);
        teapot3.rotate(0, 3*tpf, 0);
        elephant.rotate(3*tpf, 0, 0);
    }
    
      /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    // You can map one or several inputs to one named action
    inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));
    inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));
    inputManager.addMapping("In",   new KeyTrigger(KeyInput.KEY_I));
    inputManager.addMapping("Out",  new KeyTrigger(KeyInput.KEY_M));
    inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_R),
                                      new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    
    inputManager.addMapping("Shoot",
      new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
      new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: right-button click
    
    // Add the names to the action listener.
    inputManager.addListener(actionListener,"Pause", "Shoot");
    inputManager.addListener(analogListener,"Left", "Right", "In", "Out", "Rotate");
  }
 
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Pause") && !keyPressed) {
        isRunning = !isRunning;
      }
    
    /** Defining the "Shoot" action: Determine what was hit and how to respond. */
    if (name.equals("Shoot") && !keyPressed) {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        // 3. Collect intersections between Ray and Shootables in results list.
        shootables.collideWith(ray, results);
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
          CollisionResult closest = results.getClosestCollision();
          // Let's interact - we mark the hit with a red dot.
          mark.setLocalTranslation(closest.getContactPoint());
          rootNode.attachChild(mark);
        } else {
          // No hits? Then remove the red mark.
          rootNode.detachChild(mark);
        }
      }
    }
  };
 
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
      if (isRunning) {
        if (name.equals("Rotate")) {
          ninja.rotate(0, value*speed, 0);
        }
        if (name.equals("Right")) {
          Vector3f v = ninja.getLocalTranslation();
          ninja.setLocalTranslation(v.x + value*speed, v.y, v.z);
        }
        if (name.equals("Left")) {
          Vector3f v = ninja.getLocalTranslation();
          ninja.setLocalTranslation(v.x - value*speed, v.y, v.z);
        }
        if (name.equals("In")) {
          Vector3f v = ninja.getLocalTranslation();
          ninja.setLocalTranslation(v.x, v.y, v.z - value*speed);
        }
        if (name.equals("Out")) {
          Vector3f v = ninja.getLocalTranslation();
          ninja.setLocalTranslation(v.x, v.y, v.z + value*speed);
        }
      } else {
        System.out.println("Press P to unpause.");
      }
    }
  };
  
    /** A floor to show that the "shot" can go through several objects. */
    protected Geometry makeFloor(){
      Box box = new Box(15, .2f, 15);
      Geometry floor = new Geometry("the Floor", box);
      floor.setLocalTranslation(0, -4, -5);
      Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      mat1.setColor("Color", ColorRGBA.Gray);
      floor.setMaterial(mat1);
      return floor;
    }
    
    /** A centred plus sign to help the player aim. */
  protected void initCrossHairs() {
//    setDisplayStatView(false);
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 32);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
    guiNode.attachChild(ch);
  }
    /** A red ball that marks the last spot that was "hit" by the "shot". */
  protected void initMark() {
    Sphere sphere = new Sphere(30, 30, 5f);
    mark = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.Red);
    mark.setMaterial(mark_mat);
  }
}
