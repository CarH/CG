
package br.usp.icmc.vicg.gl.app;

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
import javax.swing.event.MouseInputAdapter;

public class App extends MouseInputAdapter implements GLEventListener, KeyListener {
    private final float ASPECT_RATIO = 1.83f; // width/height
    private final float DNEAR = 0.01f;
    private final float DFAR = 1000f;
    private final float THETA = 60;
    
    private final Pipeline pipeline;
    private final Scene scene;

    private float alpha;
    private float beta;
    private float delta;

    
    
  ///Tests:
    double x = 0;   
    double y = 0;
    double z = 0;
  
  
    private float zoomFactor, dx, dy, tx, ty;
    private Point dragStart, dragEnd;
    private double counter;
    private double angle;
    private float goForward;
    private float goRight;
    
    
    public App() {
        pipeline = new Pipeline();
        scene = new Scene(pipeline);

        alpha = 0;
        beta = 0;
        delta = 5;
        
        dx = dy = 0;
        zoomFactor = 1.0f;
        goForward = -2.0f;
        goRight = 0f;        
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

        //inicializa o pipeline
        pipeline.init(gl);

        //inicializa a cena
        scene.init(gl);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();

        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        pipeline.getMatrix(Pipeline.MatrixType.PROJECTION).loadIdentity();
        pipeline.getMatrix(Pipeline.MatrixType.PROJECTION).perspective(THETA, ASPECT_RATIO, DNEAR, DFAR); 
        
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).loadIdentity();
        
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).lookAt(
                goRight, 0.0f, goForward-2.0f,       // Po
                goRight, alpha, goForward,           // Pref
                0, 1, 0);                            // View up
//        pipeline.getMatrix(Pipeline.MatrixType.VIEW).scale(, 1, 1);
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).rotate(-beta, 0, 1.0f, 0);
        pipeline.getMatrix(Pipeline.MatrixType.VIEW).rotate(-alpha, 1.0f, 0, 0);

        scene.display(gl);

        // Força execução das operações declaradas
        gl.glFlush();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        scene.dispose();
    }

    
   /****************************************************************************
    * Game Controls 
    */ 
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP://faz zoom-in
                delta = delta * 0.809f;
                break;
            case KeyEvent.VK_PAGE_DOWN://faz zoom-out
                delta = delta * 1.1f;
                break;
            case KeyEvent.VK_UP://
                alpha = alpha + 0.01f;
                break;
            case KeyEvent.VK_DOWN://
                alpha = alpha - 0.01f;
                break;
            case KeyEvent.VK_LEFT://gira sobre o eixo-y
                beta = beta - 5;
                break;
            case KeyEvent.VK_RIGHT://gira sobre o eixo-y
                beta = beta + 5;
                break;
            case KeyEvent.VK_W: //avança a camera = puxa o mundo
            {
                goForward += 0.05;
                break;
            }
            case KeyEvent.VK_S: //recua a camera = empurra o mundo
            {
                goForward -= 0.03;
                break;
            }
            case KeyEvent.VK_A: //recua a camera = empurra o mundo
            {
                goRight += 0.03;
                break;
            }
            case KeyEvent.VK_D: //recua a camera = empurra o mundo
            {
                goRight -= 0.03;
                break;
            }
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
        frame.setSize(1920, 1050); // aspect ~ 1.83
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
