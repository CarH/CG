package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.app.Pipeline;
import br.usp.icmc.vicg.gl.util.Loader;
import br.usp.icmc.vicg.gl.util.Point3D;
import br.usp.icmc.vicg.gl.util.Shader;
import com.jogamp.common.nio.Buffers;
import com.jogamp.common.util.IOUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.TextureData;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLContext;
import org.lwjgl.BufferUtils;

public class Skybox {
  
  private static final float SIZE = 500f;
  private static final float[] vertices = {        
    -SIZE,  SIZE, -SIZE,
    -SIZE, -SIZE, -SIZE,
     SIZE, -SIZE, -SIZE,
     SIZE, -SIZE, -SIZE,
     SIZE,  SIZE, -SIZE,
    -SIZE,  SIZE, -SIZE,

    -SIZE, -SIZE,  SIZE,
    -SIZE, -SIZE, -SIZE,
    -SIZE,  SIZE, -SIZE,
    -SIZE,  SIZE, -SIZE,
    -SIZE,  SIZE,  SIZE,
    -SIZE, -SIZE,  SIZE,

     SIZE, -SIZE, -SIZE,
     SIZE, -SIZE,  SIZE,
     SIZE,  SIZE,  SIZE,
     SIZE,  SIZE,  SIZE,
     SIZE,  SIZE, -SIZE,
     SIZE, -SIZE, -SIZE,

    -SIZE, -SIZE,  SIZE,
    -SIZE,  SIZE,  SIZE,
     SIZE,  SIZE,  SIZE,
     SIZE,  SIZE,  SIZE,
     SIZE, -SIZE,  SIZE,
    -SIZE, -SIZE,  SIZE,

    -SIZE,  SIZE, -SIZE,
     SIZE,  SIZE, -SIZE,
     SIZE,  SIZE,  SIZE,
     SIZE,  SIZE,  SIZE,
    -SIZE,  SIZE,  SIZE,
    -SIZE,  SIZE, -SIZE,

    -SIZE, -SIZE, -SIZE,
    -SIZE, -SIZE,  SIZE,
     SIZE, -SIZE, -SIZE,
     SIZE, -SIZE, -SIZE,
    -SIZE, -SIZE,  SIZE,
     SIZE, -SIZE,  SIZE
  };
  
  private static final String folder = "./src/main/resources/skybox/";
//  private static final String folder = "skybox/";
  private static final String[] suffix = { "right", "left", "top",  "top", "back", "front" }; // TODO: check!
  private static final int[] targets = { GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                                         GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                                         GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
  private int cubemap_handle;
  private int vertex_position_handle;
  private GL3 gl;
  private int[] vao;
  private final Loader loader;
  private ArrayList<Point3D> positionList;
  private int texID;
  private float angle = 0; // To rotate the sky
  private float ROT_SPEED = 0.01f;
  /**
   * It uses the default texture names: right, left, top, bottom, front and back.
   * 
   */
  public Skybox(){
    this.loader = Loader.getInstance();
    positionList = new ArrayList<>();
  }
  
  public void init(GL3 gl, Shader shader){
    this.gl = gl;
    
    this.texID = this.loader.loadCubeMap(gl, folder, suffix, "png");

    this.cubemap_handle = shader.getUniformLocation("cubeMap");
    this.vertex_position_handle = shader.getAttribLocation("position");
  }
  
  public void draw() {
    if (vao == null) {
      create_vao();
    } else {    
      gl.glUniform1i(cubemap_handle, 0);
      gl.glActiveTexture(GL3.GL_TEXTURE0);
      gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, texID);

      gl.glBindVertexArray(vao[0]);
      gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertices.length/3);
      gl.glBindVertexArray(0);
    }
  }

  private void create_vao() {
    vao = new int[1];
    gl.glGenVertexArrays(vao.length, vao, 0);
    gl.glBindVertexArray(vao[0]); // bind the vao to add a vbo
    
    int[] vbo = new int[1];     // It represents the vertex array objects
    gl.glGenBuffers(1, vbo, 0); // Generate one Vertex Buffer Object

    
    // Send the vertex positions to the card
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]); // Bind the vertex buffer to add data
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * Buffers.SIZEOF_FLOAT,
            storeDataInFloatBuffer(vertices), GL3.GL_STATIC_DRAW);
    gl.glEnableVertexAttribArray(vertex_position_handle);
    gl.glVertexAttribPointer(vertex_position_handle, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);      // unbind the current vbo
    
    gl.glBindVertexArray(0);  // unbind the current vao
  }
  
  public void addEntityPosition(Point3D position){
      this.positionList.add(position);
  }
  
  public void setModelMatrix(Pipeline pipeline, int entPos){
      pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
      pipeline.getMatrix(Pipeline.MatrixType.MODEL).translate(this.positionList.get(entPos).x, 
              this.positionList.get(entPos).y, this.positionList.get(entPos).z);
      pipeline.getMatrix(Pipeline.MatrixType.MODEL).rotate(angle, 0, 1, 0); 
      pipeline.bind(Pipeline.ShaderName.SKYBOX);
      angle += ROT_SPEED;
  }
  
  public void dispose() {
    this.loader.freeTexture(gl, texID);
    gl.glDeleteVertexArrays(vao.length, vao, 0);
    gl.glBindVertexArray(0);
  }
  
  private FloatBuffer storeDataInFloatBuffer(float[] data){
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
}
