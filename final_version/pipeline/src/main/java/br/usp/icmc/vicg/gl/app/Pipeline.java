package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import javax.media.opengl.GL3;

public class Pipeline {
  private GL3 gl;
  private final Shader shader;    
  private final Shader skyboxShader;

  private final Matrix4 modelMatrix;
  private final Matrix4 projectionMatrix;
  private final Matrix4 viewMatrix;
  private final Matrix4 modelviewMatrix;
  private final Matrix4 normalMatrix;
  private final Light sunLight;
   ///FOG
  private int sky_color_handle;
  private int enable_fog_handle;
  
  private int[] projection_handle;
  private int[] view_handle;
  private int model_handle;
  private int wind_vector_handle;
  private float[] wv;
  private double counter;
  private static Pipeline pipeline;
    public enum LightNumber {
        LIGHT0
    };

    public enum MatrixType {
        MODEL, VIEW, PROJECTION
    };
    
    public enum ShaderName {
      WORLD, SKYBOX
    };

    public static Pipeline getInstance() {
      if (pipeline == null) pipeline = new Pipeline();
      return pipeline;
    }
    
    private Pipeline() {
        /// Loading the shaders
        shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.JWAVEFRONT_SHADER);
        skyboxShader = ShaderFactory.getInstance(ShaderFactory.ShaderType.SKYBOX_SHADER);
        
        /// Transformation Matrices 
        modelMatrix       = new Matrix4();  // 1- Mapeamento de objetos NO mundo
        viewMatrix        = new Matrix4();  // 2- Mapeamento de objetos em view coord
        projectionMatrix  = new Matrix4();  // 3- Projecao de todas as coord.   
        
        modelviewMatrix   = new Matrix4();  // View * Model em CPU (p/ maior eficiencia na GPU
        normalMatrix      = new Matrix4();
        
        /// Light of the scene
        sunLight = new Light();
        
        /// Both shaders will share the same matrix, it's not perfect, but works :)
        projection_handle = new int[ShaderName.values().length];
        view_handle       = new int[ShaderName.values().length];
        
        wv = new float[] { 0.0f, 0.0f, 0.0f };
        counter = 0.0;
    }
   
    public void init(GL3 gl) {
        this.gl = gl;
        //inicializa o shader
        shader.init(gl);
        skyboxShader.init(gl);

        // Get the handles of the ModelView e Projection - WORLD
        this.projection_handle[0] = shader.getUniformLocation("u_projectionMatrix");
        this.view_handle[0]       = shader.getUniformLocation("u_viewMatrix");
        
        // Get the handles of the ModelView e Projection - SKYBOX
        this.projection_handle[1] = skyboxShader.getUniformLocation("projectionMatrix");
        this.view_handle[1]       = skyboxShader.getUniformLocation("viewMatrix");
        this.model_handle         = skyboxShader.getUniformLocation("modelMatrix");
        
        modelMatrix.init(gl);
        viewMatrix.init(gl);
        projectionMatrix.init(gl);
        
        modelviewMatrix.init(gl, shader.getUniformLocation("u_modelviewMatrix"));
        normalMatrix.init(gl, shader.getUniformLocation("u_normalMatrix"));
        
        ///FOG
        sky_color_handle = shader.getUniformLocation("skyColour");
        
        wind_vector_handle = shader.getUniformLocation("windVector");
        
        //inicializa a luz
        sunLight.init(gl, shader);
    }
    
    // FOG
    public void bindSkyColor(float rgb[]){
       this.gl.glUniform3f(sky_color_handle, rgb[0], rgb[1], rgb[2]);
    }
    
    public Matrix4 getMatrix(MatrixType type) {
        if (type == MatrixType.MODEL) {
            return this.modelMatrix;
        } else if (type == MatrixType.VIEW) {
            return this.viewMatrix;
        } else if (type == MatrixType.PROJECTION) {
            return this.projectionMatrix;
        }
        return null;
    }

    public Shader getShader(ShaderName name) {
      if (name == ShaderName.WORLD){
        return this.shader;
      } else if (name == ShaderName.SKYBOX){
        return this.skyboxShader;
      }
      return null;
    }

    public Light getLight(LightNumber number) {
        if (number == LightNumber.LIGHT0) {
            return this.sunLight;
        }
        return null;
    }
    
    public void setWindVector(float[] wv) {
      this.wv = wv;
    }
    
    public float[] getWindVector() {
      return this.wv;
    }

    public void bind(ShaderName name) {
      if (name == ShaderName.WORLD){
        this.skyboxShader.unbind();
        this.shader.bind();
        this.projectionMatrix.bind(this.projection_handle[0]);
        this.modelviewMatrix.copy(viewMatrix).multiply(modelMatrix).bind();
        this.normalMatrix.copy(modelviewMatrix).bind(); // Perguntar !!
        this.viewMatrix.bind(this.view_handle[0]);
        this.sunLight.bind();
        /// LEAVES ANIMATION TEST:
        wv[0] += Math.sin(counter)/99999f;
        wv[1] += Math.cos(counter)/90999f;
        wv[2] += Math.sin(counter)/99999f;
        
        this.counter += 0.001f;
        ///
        gl.glUniform3f(wind_vector_handle, wv[0], wv[1], wv[2]);
      } else if (name == ShaderName.SKYBOX) {
        this.shader.unbind();
        this.skyboxShader.bind();
        this.projectionMatrix.bind(this.projection_handle[1]);
        this.viewMatrix.bind(this.view_handle[1]);
        this.modelMatrix.bind(this.model_handle);
        
      }
    }
    
}
