
package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.model.Terrain;
import br.usp.icmc.vicg.gl.util.GameManager;
import br.usp.icmc.vicg.gl.util.Point3D;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.MouseInputAdapter;

public class App extends MouseInputAdapter implements GLEventListener, KeyListener {
    private final float ASPECT_RATIO = 1.83f; // width/height
    private float HEIGHT_OF_CAMERA = 2.0f;
    private final boolean GAME_MANAGER = true;// Runs the game manager
    private final float DNEAR = 0.01f;
    private final float DFAR = 1000f;
    private final float THETA = 60;
    private static boolean DEBUG = true;
    /**
     *  It's static to allow the GameManager to change this value
     */
    public static float SPEED = 5.0f;
    public static float SPEED_ROT = 2.0f;
    
    private final Pipeline pipeline;
    private final Scene scene;
    private final Terrain terrain;
    private GameManager gameManager;
    
    private float alpha;
    private float beta;
    private float delta;

    
  ///Tests:
  
    private float zoomFactor, dx, dy, tx, ty;
    private Point dragStart, dragEnd;
    private double camAngle;
    private float goForward;
    private float goRight;
    private float deltaX;
    private float deltaZ;
    private float walkingMov;
    private float movCounter;
    
    // Vertical movement
    private boolean up;
    private boolean down;
    private float upLimit;
    
    Point3D Po;
    Point3D Pref;
    Point3D PoCurr;
    
    public App() {
      pipeline = Pipeline.getInstance(); // Pipeline -> Singleton
      scene = new Scene();

      terrain = new Terrain(new File("./src/main/resources/terrain/heightmap3.jpg"));

      if (GAME_MANAGER){
          gameManager = new GameManager();
          gameManager.setVisible(true);
          scene.setGameManager(gameManager);
      }

      Po = new Point3D(68.0f, HEIGHT_OF_CAMERA, 14.0f);   // Posição inicial da câmera:
//        Pref = new Point3D(68.0f, HEIGHT_OF_CAMERA, 15.0f); // Pref inicial:
      Pref = new Point3D(68.0f, HEIGHT_OF_CAMERA-2f, 14.0f); // Pref inicial:

      PoCurr = new Point3D(Po.x, Po.y, Po.z);

      alpha = 0;
      beta = 0;
      delta = 5;

      dx = dy = 0;
      deltaX = deltaZ = 0.0f;
      zoomFactor = 1.0f;
      movCounter = walkingMov = goForward = 0.0f;
      goRight = 0.0f;
      camAngle = 0.0f;
      up=down=false;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glClearDepth(1.0f);

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        
        //ativa antialiasing para linha
//        gl.glEnable(GL.GL_LINE_SMOOTH);
//        gl.glEnable(GL.GL_BLEND);
//        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        //inicializa o pipeline
        pipeline.init(gl);
        
        try {
          terrain.setNormalMapSource("terrain/soilBeach_Normal.jpg");
          terrain.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));

          //inicializa a cena
          scene.init(gl, terrain);
          
        } catch (IOException ex) {
          Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        /// Setting Projection Matrix
        pipeline.getMatrix(Pipeline.MatrixType.PROJECTION).loadIdentity();
        pipeline.getMatrix(Pipeline.MatrixType.PROJECTION).perspective(THETA, ASPECT_RATIO, DNEAR, DFAR); 
        
        /// Do the magic of the person movement
        move();
        
        /// Setting the View Matrix, camera positioning
        Pref.x = PoCurr.x + ((float) Math.sin(Math.toRadians(camAngle)));
        Pref.z = PoCurr.z + ((float) Math.cos(Math.toRadians(camAngle)));
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).loadIdentity();
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).lookAt(
                PoCurr.x, PoCurr.y+walkingMov, PoCurr.z,                // Po
                Pref.x, (Pref.y + alpha)+walkingMov, Pref.z,            // Pref, alpha: olha p/ cima/baixo
                0, 1, 0);                                               // View up

        scene.display(gl);
        
        /* Draw terrain */
        terrain.setModelMatrix(pipeline);
        terrain.draw();
        
        
        
        if (GAME_MANAGER) {
            gameManager.updateCamAngle(camAngle);
            gameManager.updatePlayerPosition( (PoCurr.x), (PoCurr.y-HEIGHT_OF_CAMERA), (PoCurr.z) );
        } 
        // Força execução das operações declaradas
        gl.glFlush();
    }

    
    private void move(){
          
        /// Decrease the height of the character if its necessary
        if(down){ // It's going down
          if (walkingMov > 0.01f){ walkingMov -= 0.05f; }
          if (walkingMov < 0.001f){ 
            walkingMov = 0f; 
            up = false;
            down = false;
          }          
        }
        if(up){ // It's going up 
          walkingMov += 0.02f; 
          
          if(walkingMov > upLimit) {
            up = false;
            down = true;
          }
        }
        
        /**********************************
         * Terrain colision detection
         */
        float height = terrain.getHeightOfTerrain(PoCurr.x, PoCurr.z);
        PoCurr.y = height + HEIGHT_OF_CAMERA;
        Pref.y = height + HEIGHT_OF_CAMERA;
//        System.out.println("HEIGHT["+PoCurr.x+"]["+PoCurr.z+"]: "+height+",  PoCurr.y: "+PoCurr.y);
        
    }
    
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        scene.dispose();
        terrain.dispose();
    }

    
   /****************************************************************************
    * Game Controls 
    */ 
    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_E:
          HEIGHT_OF_CAMERA += 0.1f;
          break;
        case KeyEvent.VK_F:
          HEIGHT_OF_CAMERA -= 0.1f;
          break; 
        case KeyEvent.VK_PAGE_UP://faz zoom-in
             delta = delta * 0.809f * SPEED;
             break;
         case KeyEvent.VK_PAGE_DOWN://faz zoom-out
             delta = delta * 1.1f * SPEED;
             break;
         case KeyEvent.VK_UP:    // Look up
             alpha = alpha + 0.05f * SPEED_ROT;
             break;
         case KeyEvent.VK_DOWN:  // Look down
             alpha = alpha - 0.05f * SPEED_ROT;
             break;
         case KeyEvent.VK_LEFT:  // Turns the camera to the left
             camAngle += 0.5f * SPEED_ROT;
             break;
         case KeyEvent.VK_RIGHT: // Turns the camera to the left
             camAngle -= 0.5f * SPEED_ROT;
             break;
         case KeyEvent.VK_W: // Go forward
         {
             goForward = 0.03f * SPEED; // hipotenusa
             PoCurr.x += ((float) Math.sin(Math.toRadians(camAngle))) * goForward; // correto
             PoCurr.z += ((float) Math.cos(Math.toRadians(camAngle))) * goForward;
//                movCounter += 0.5f;
//                walkingMov = (walkingMov <= 0.01f) ? (float)Math.sin(movCounter)*0.5f : walkingMov;
//                walkingMov = (walkingMov <= 0.01f) ? 1.0f : walkingMov; 
             if (!this.up){
               this.up = true; 
               this.upLimit = 0.4f;
             } 
             break;
         }
         case KeyEvent.VK_S: // Go backward
         {
             goForward = 0.02f * SPEED;
             PoCurr.x -= ((float) Math.sin(Math.toRadians(camAngle))) * goForward; // 
             PoCurr.z -= ((float) Math.cos(Math.toRadians(camAngle))) * goForward; // 
//                movCounter -= 0.1f;
//                walkingMov = (walkingMov <= 0.01f) ? (float)Math.sin(movCounter)*0.5f : walkingMov;
             if (!this.up){ 
//                  System.out.println(" >>>> Vai pra cima !");
               this.up = true; 
               this.upLimit = 0.4f;
             } 
             break;
         }
         case KeyEvent.VK_A: // Go to the left
         {
             /* Vertical moviment */
             movCounter -= 0.2f;
             //walkingMov = (float)Math.sin(movCounter)*0.5f;

             if (!this.up){ 
               this.up = true; 
               this.upLimit = 0.4f;
             } 


             double angle = ( camAngle < 0.0 ) ? (360+camAngle) : camAngle;
             double tempAngle = 0.0;
             goRight = 0.05f * SPEED;
             //System.out.println("angle = "+angle);

             if (angle == 0.0) {
                 PoCurr.x += goRight;
                 return;
             } else if (angle == 90.00){
                 PoCurr.z -= goRight;
                 return;
             }  else if (angle == 180.00){
                 PoCurr.x -= goRight;
                 return;
             } else if (angle == 270.00){
                 PoCurr.z += goRight;
                 return;
             } 

             if (Pref.x > PoCurr.x && Pref.z < PoCurr.z){ // camera is pointing from bottom left to top right
                 //System.out.println(" >> 1 quadrante!");
                 tempAngle = angle-90;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x -= deltaX;
                 PoCurr.z -= deltaZ;
             }
             if (Pref.x < PoCurr.x && Pref.z < PoCurr.z){ // camera is pointing from bottom right to top left
                 //System.out.println(" >> 2 quadrante!");

                 tempAngle = 270 - angle;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x -= deltaX;
                 PoCurr.z += deltaZ;
             }
             if (Pref.x < PoCurr.x && Pref.z > PoCurr.z){ // camera is pointing from top right to bottom left
                 //System.out.println(" >> 3 quadrante!");

                 tempAngle = 360 - angle;
                 deltaX = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x += deltaX;
                 PoCurr.z += deltaZ;
             }
             if (Pref.x > PoCurr.x && Pref.z > PoCurr.z){ // camera is pointing from top left to bottom right
                 //System.out.println(" >> 4 quadrante!");

                 tempAngle = 90 - angle;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x += deltaX;
                 PoCurr.z -= deltaZ;
             }

             break;
         }
         case KeyEvent.VK_D: // Go to the right
         {
             /* Vertical moviment */
             movCounter += 0.2f;
             //walkingMov = (float)Math.sin(movCounter)*0.5f;

             if (!this.up){ 
               this.up = true; 
               this.upLimit = 0.4f;
             } 

             double angle = ( camAngle < 0.0 ) ? (360+camAngle) : camAngle;
             double tempAngle = 0.0;
             goRight = 0.05f * SPEED;
             if (angle == 0.00) {
                 PoCurr.x -= goRight;
                 return;
             } else if (angle == 90.00){
                 PoCurr.z += goRight;
                 return;
             }  else if (angle == 180.00){
                 PoCurr.x += goRight;
                 return;
             } else if (angle == 270.00){
                 PoCurr.z -= goRight;
                 return;
             } 

             if (Pref.x > PoCurr.x && Pref.z < PoCurr.z){ // camera is pointing from bottom left to top right
                 // System.out.println(" >> 1 quadrante!");

                 tempAngle = angle-90;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x += deltaX;
                 PoCurr.z += deltaZ;
             }
             if (Pref.x < PoCurr.x && Pref.z < PoCurr.z){ // camera is pointing from bottom right to top left
                 // System.out.println(" >> 2 quadrante!");

                 tempAngle = 270 - angle;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x += deltaX;
                 PoCurr.z -= deltaZ;
             }
             if (Pref.x < PoCurr.x && Pref.z > PoCurr.z){ // camera is pointing top right to bottom left
                 // System.out.println(" >> 3 quadrante!");

                 tempAngle = 360 - angle;
                 deltaX = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x -= deltaX;
                 PoCurr.z -= deltaZ;
             }
             if (Pref.x > PoCurr.x && Pref.z > PoCurr.z){ // camera is pointing from top left to bottom right
                 // System.out.println(" >> 4 quadrante!");

                 tempAngle = 90 - angle;
                 deltaX = (float)(Math.sin(Math.toRadians(tempAngle)))*goRight;
                 deltaZ = (float)(Math.cos(Math.toRadians(tempAngle)))*goRight;
                 PoCurr.x -= deltaX;
                 PoCurr.z += deltaZ;
             }
             break;
         }
     }
     if (camAngle >= 360 || camAngle <= -360) {
         camAngle = 0.0;
     }
  }

  @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        dragStart = e.getPoint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        tx += dx;
        ty += dy;
        dx = dy = 0;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        dragEnd = e.getPoint();
        
        GLCanvas canvas = (GLCanvas) e.getSource();
        dx = (float) (dragEnd.x - dragStart.x) / canvas.getWidth();
        dy = -(float) (dragEnd.y - dragStart.y) / canvas.getHeight(); 
    }
    
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        if (rotation < 0) {
            zoomFactor *= 1.1f;
        } else {
            zoomFactor *= 0.809f;
        }
    }
    
    
    public static void main(String[] args) {
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);

        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);

        // Create canvas
        GLCanvas glCanvas = new GLCanvas(glcaps);

        // Add listener to panel
        App listener = new App();
        glCanvas.addGLEventListener(listener);
        glCanvas.addKeyListener(listener);

        glCanvas.addKeyListener(listener);
        glCanvas.addMouseMotionListener(listener);
        glCanvas.addMouseWheelListener(listener);
        glCanvas.addMouseListener(listener);
        
        Frame frame = new Frame("The Island");
        
        if (DEBUG) {
            frame.setSize(1000, 546); // aspect ~ 1.83
        }else {
            frame.setSize(1920, 1050); // aspect ~ 1.83
        }
        
        
        
        frame.add(glCanvas);
        
        final AnimatorBase animator = new FPSAnimator(glCanvas, 60);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }

                }).start();
            }

        });
        frame.setVisible(true);
        animator.start();
    }

}
