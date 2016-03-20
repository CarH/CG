/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.app.Pipeline;
import br.usp.icmc.vicg.gl.model.jwavefront.Maths;
import br.usp.icmc.vicg.gl.util.Loader;
import br.usp.icmc.vicg.gl.util.ManageableObject;
import br.usp.icmc.vicg.gl.util.Shader;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLProfile;
import java.nio.IntBuffer;
import java.util.ArrayList;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import org.lwjgl.BufferUtils;

public class Terrain{
  private static final float SIZE = 100;
  private static final float MAX_HEIGHT = 5;
  private static final float MAX_PIXEL_COLOUR = 256 * 256 * 256; // RGB 
  
  private int[] vao;
  private GL3 gl;
  private int vertex_positions_handle;
  private int vertex_normals_handle;
  private int vertex_textures_handle;
  private int is_texture_handle;
  private int texture_handle;
  private float x, y, z;
  
  private String modelName;    // To show on the Game Manager interface
  
  
  /// Material of the terrain
  private br.usp.icmc.vicg.gl.core.Material material;
  
  /// Kd map - terrainTexture
  public com.jogamp.opengl.util.texture.Texture bgTexture;
  public com.jogamp.opengl.util.texture.Texture rTexture;
  public com.jogamp.opengl.util.texture.Texture gTexture;
  public com.jogamp.opengl.util.texture.Texture bTexture;
  public com.jogamp.opengl.util.texture.Texture blendTexture;

  private String kdMapSource;
  
  /// Normal map
  private int normalmap_handle;
  private com.jogamp.opengl.util.texture.Texture normalmapdata;
  private boolean has_nomal_map;
  private int has_normal_handle;
  private String normalMapSource;
  private int is_terrain_handle;
  
  /// Height map
  private File heightMap;
  private float[][] heights;
  
  private float[] vertices;
  private float[] normals;
  private float[] textureCoords;
  private int[] indices;          // used to create the indexBuffer
  private int NUM_VERTICES;
  private int has_spec_handle;
  private int is_sky_handle;
  private int blendmap_handle;
  private int bg_texture_handle;
  private int rtexture_handle;
  private int gtexture_handle;
  private int btexture_handle;
  
  public Terrain(File heightMap) {
    this.heightMap = heightMap;
    this.x = this.y = this.z =0.0f;
  }
 

  public void init(GL3 gl, Shader shader) throws IOException {
    this.gl = gl;
    
    // Setando um material default para o terreno
    this.material = new br.usp.icmc.vicg.gl.core.Material();
    this.material.setSpecularColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
    this.material.setDiffuseColor(new float[]{2.0f, 2.0f, 2.0f, 1.0f});
    this.material.setAmbientColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});

    
    this.material.init(gl, shader);

    // Get the variable's handle associated with the vertex position 
    this.vertex_positions_handle = shader.getAttribLocation("a_position");
    this.vertex_normals_handle = shader.getAttribLocation("a_normal");
    this.vertex_textures_handle = shader.getAttribLocation("a_texcoord"); // Kd map
    
    // Normal map
    this.has_normal_handle = shader.getUniformLocation("u_has_normal_map");    
    this.normalmap_handle = shader.getUniformLocation("u_normalmap");
    
    // Kd map - terrainTexture
    this.is_texture_handle = shader.getUniformLocation("u_is_texture");
    this.texture_handle = shader.getUniformLocation("u_texture");
    
    
    this.is_sky_handle = shader.getUniformLocation("u_is_sky");
    this.has_spec_handle = shader.getUniformLocation("u_has_spec_map");
    
    /// Terrain control flag and textures handlers
    this.is_terrain_handle= shader.getUniformLocation("is_terrain");
    this.bg_texture_handle= shader.getUniformLocation("background");
    this.blendmap_handle  = shader.getUniformLocation("blendMap");
    this.rtexture_handle  = shader.getUniformLocation("rtexture");
    this.gtexture_handle  = shader.getUniformLocation("gtexture");
    this.btexture_handle  = shader.getUniformLocation("btexture");
    
    createTerrain(heightMap);
    
    
//    loadKdMap(kdMapSource);
    String path = "terrain/";
    Loader loader = Loader.getInstance();
    this.blendTexture = loader.loadTexture(path+"blendMap.jpg");
    this.bgTexture = loader.loadTexture(path+"soilBeach.jpg");
    this.rTexture  = loader.loadTexture(path+"soil.jpg");
    this.gTexture  = loader.loadTexture(path+"grass.jpg");
    this.bTexture  = loader.loadTexture(path+"soilBeach.jpg");
    loader.enableTiling(gl, bgTexture);
    loader.enableTiling(gl, rTexture);
    loader.enableTiling(gl, gTexture);
    loader.enableTiling(gl, bTexture);

    // Because I'm using multitexturing this normal map won't work, so it's commented
    //if(this.normalMapSource != null){
    //   loadNormalMap(this.normalMapSource);
    //}
    
  }

  public void setModelMatrix(Pipeline pipeline){
      pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
      pipeline.bind(Pipeline.ShaderName.WORLD);
  }
  
  public void draw() {
    
    ///NormalMap
    if (this.has_nomal_map){
      gl.glUniform1i(this.has_normal_handle, 1);
      gl.glUniform1i(normalmap_handle, 1);
      gl.glActiveTexture(GL3.GL_TEXTURE1);
      normalmapdata.bind(gl);         // Send the texture to the G board
    } else {
      gl.glUniform1i(this.has_normal_handle, 0);
    }
    // Disable spec and sky texture - just to ensure
    gl.glUniform1i(this.has_spec_handle, 0);
    gl.glUniform1i(this.is_sky_handle, 0);  

    if (vao == null) {
      create_vao();
    } else {
      
      // Send the soil texture(kd map) to the board
      // REMEMBER: I take it off from group.material.texture (= kdmapdata)
      if (bgTexture != null) {
        gl.glUniform1i(is_texture_handle, 1);
        gl.glUniform1i(texture_handle, 0);  // u_texture
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        bgTexture.bind(gl);    // Ativa a textura
        
        // Set in the vertex shader that it is a terrain:
        gl.glUniform1i(is_terrain_handle, 1);
        
      } else {
        System.err.println("  [ERROR]: The terrain doesn't have a texture associated with it.");
        gl.glUniform1i(is_texture_handle, 0);
      }

      if (material != null) {
        material.bind();
      }else{
        System.err.println("  [ERROR]: The terrain doesn't have a material.");
      }

      if(rTexture != null){
        gl.glUniform1i(rtexture_handle, 1);
        gl.glActiveTexture(GL3.GL_TEXTURE1);
        rTexture.bind(gl);
      }
      if(gTexture != null){
        gl.glUniform1i(gtexture_handle, 2);
        gl.glActiveTexture(GL3.GL_TEXTURE2);
        gTexture.bind(gl);
      }
      if(bTexture != null){
        gl.glUniform1i(btexture_handle, 3);
        gl.glActiveTexture(GL3.GL_TEXTURE3);
        bTexture.bind(gl);
      }
      if(blendTexture != null){
        gl.glUniform1i(blendmap_handle, 4);
        gl.glActiveTexture(GL3.GL_TEXTURE4);
        blendTexture.bind(gl);
      }
      
      
      gl.glBindVertexArray(vao[0]);
      gl.glEnableVertexAttribArray(0); // thin

      /**
       * [NEW : USING INDICES BUFFER]
       * Mater important thing:
       * Number of vertices to be rendered : NUM_VERTICES*6 (Number of INDICES!!)
       */
      gl.glDrawElements(GL.GL_TRIANGLES, NUM_VERTICES*6, GL.GL_UNSIGNED_INT, 0);

      gl.glDisableVertexAttribArray(0); // thin

      gl.glBindVertexArray(0);  // unbind the current vao
      gl.glUniform1i(is_texture_handle, 0);
      gl.glUniform1i(is_terrain_handle, 0);
    }
  }
  
  public void dispose() {
    blendTexture.destroy(gl);
    rTexture.destroy(gl);
    gTexture.destroy(gl);
    bTexture.destroy(gl);
    bgTexture.destroy(gl);
    gl.glDeleteVertexArrays(vao.length, vao, 0);
    gl.glBindVertexArray(0);
  }

  private void create_vao() {
    /* create one vao per group of vertex */
    vao = new int[1];

    gl.glGenVertexArrays(vao.length, vao, 0); // Create a VAO
    gl.glBindVertexArray(vao[0]); // Bind our Vertex Array Object so we can use it            
    
    /**
     * IDEA: Put the data inside the VBO, then put the VBO inside the
     * VAO list, whose index is represented by vbo[i].
     * vbo[0] : vertices positions
     * vbo[1] : vertices normals
     * vbo[2] : vertices texture coordinates
     * vbo[3] : indices to draw the triangles 
     */
    int[] vbo = new int[4];     // It represents the vertex array objects
    gl.glGenBuffers(4, vbo, 0); // Generate three Vertex Buffer Object

    //the positions buffer
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]); // Bind the vertex buffer 
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertices.length * Buffers.SIZEOF_FLOAT,
            storeDataInFloatBuffer(vertices), GL3.GL_STATIC_DRAW);

    gl.glEnableVertexAttribArray(vertex_positions_handle); // a_position
    gl.glVertexAttribPointer(vertex_positions_handle, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);      // unbind the current vbo

    //the normals buffer
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[1]); // Bind normals buffer
    gl.glBufferData(GL3.GL_ARRAY_BUFFER, normals.length * Buffers.SIZEOF_FLOAT,
            storeDataInFloatBuffer(normals), GL3.GL_STATIC_DRAW);
    gl.glEnableVertexAttribArray(vertex_normals_handle);
    gl.glVertexAttribPointer(vertex_normals_handle, 3, GL3.GL_FLOAT, false, 0, 0);
    gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

    //the texture positions buffer
    if (bgTexture != null) {
      gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
      gl.glBufferData(GL3.GL_ARRAY_BUFFER, textureCoords.length * Buffers.SIZEOF_FLOAT,
              storeDataInFloatBuffer(textureCoords), GL3.GL_STATIC_DRAW);
      gl.glEnableVertexAttribArray(vertex_textures_handle);
      gl.glVertexAttribPointer(vertex_textures_handle, 2, GL3.GL_FLOAT, false, 0, 0);
      gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
    }

    // INDICES BUFFER
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, vbo[3]); // CREATE A ELEM BUFFER
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indices.length * Buffers.SIZEOF_INT,
              storeDataInIntBuffer(indices), GL3.GL_STATIC_DRAW);
    gl.glBindVertexArray(0); // Disable our Vertex Buffer Object 
  }
  
  /// KdMap - Texture
  public void loadKdMap(String filename) {
    System.out.println(" >>> LOADING TEXTURE: "+filename);
    try {
      BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
      ImageUtil.flipImageVertically(image); //vertically flip the image
      bgTexture = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
      bgTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
      bgTexture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
      
    }catch(IOException e){
      e.printStackTrace();
    }
  }
  
   /// NormalMap
  public void loadNormalMap(String filename) {
    System.out.println(" >>> LOADING TEXTURE: "+filename);
    try {
      BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
      ImageUtil.flipImageVertically(image); //vertically flip the image

      normalmapdata = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
      normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
      normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
      normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
      normalmapdata.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
      this.has_nomal_map = true;    
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void setNormalMapSource(String src) {
//    this.normalMapSource = src;
  }

  private void createTerrain(File heightMap) {
    try {
      // THE IMAGE NEEDS TO BE A POWER 2 SQUARE SIZE. EX.: 128x128, 256x256...
      BufferedImage image = ImageIO.read(heightMap);
      int VERTEX_COUNT = image.getHeight();
      this.heights = new float[VERTEX_COUNT][VERTEX_COUNT];
      System.err.println(" >> CARREGOU HEIGHT MAP. HEIGHT: "+image.getHeight()+", WIDTH: "+image.getWidth());
      // Calculate the total number of vertices of the squared heightmap image
      int count = VERTEX_COUNT * VERTEX_COUNT; 
      this.NUM_VERTICES = count;

      this.vertices      = new float[count*3];
      this.normals       = new float[count*3];
      this.textureCoords = new float[count*2];
      
      // indices buffer:
      indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)]; // TODO: -1 => *
      int vertexPointer = 0;

      for (int i = 0; i < VERTEX_COUNT; i++){
        for (int j = 0; j < VERTEX_COUNT; j++){
          vertices[vertexPointer * 3] = (float) j / ((float) (VERTEX_COUNT - 1.0f)) * SIZE;
          float height = getHeight(j, i, image);
          heights[j][i] = height;
          vertices[vertexPointer * 3 + 1] = height; // y coord
          vertices[vertexPointer * 3 + 2] = (float) i / ((float) (VERTEX_COUNT - 1.0f)) * SIZE;
          
          Vector3f normal = calculateNormal(j, i, image);
          normals[vertexPointer * 3] = normal.x;
          normals[vertexPointer * 3 + 1] = normal.y;
          normals[vertexPointer * 3 + 2] = normal.z;
          
          textureCoords[vertexPointer * 2] = (float) (float) j / ((float) VERTEX_COUNT - 1.0f);
          textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1.0f);
          vertexPointer++; // Verify
        }
      }
      
      /// Set up the indices to the indexBuffer
      int pointer = 0;
      for(int gz = 0; gz < VERTEX_COUNT - 1; gz++){
        for(int gx = 0; gx < VERTEX_COUNT - 1; gx++){
          int topLeft = (gz*VERTEX_COUNT) + gx;
          int topRight = topLeft + 1;
          int bottomLeft = ((gz+1) * VERTEX_COUNT) + gx;
          int bottomRight = bottomLeft + 1;
          indices[pointer++] = topLeft;
          indices[pointer++] = bottomLeft;
          indices[pointer++] = topRight;
          indices[pointer++] = topRight;
          indices[pointer++] = bottomLeft;
          indices[pointer++] = bottomRight;
        }
      }
      
    } catch (IOException ex) {
      Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
 
  /**
   * Converts a int array into a int buffer to store into a VAO []
   * @param data
   * @return 
   */
  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);  
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  private FloatBuffer storeDataInFloatBuffer(float[] data){
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
  
  private float getHeight(int x, int z, BufferedImage image) {
    if (x<0 || x>=image.getHeight() || z<0 || z>=image.getHeight()){
      return 0;
    }
    float height = image.getRGB(x, z);
    height += MAX_PIXEL_COLOUR / 2f;
    height /= MAX_PIXEL_COLOUR / 2f;
    height *= MAX_HEIGHT;
    return height;
  }
  
  
  private Vector3f calculateNormal(int x, int z, BufferedImage image){
    float heightL = getHeight(x-1, z, image);
    float heightR = getHeight(x+1, z, image);
    float heightD = getHeight(x, z-1, image);
    float heightU = getHeight(x, z+1, image);
    Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
    normal.normalize();
    return normal;
  }
  
  public float getHeightOfTerrain(float worldX, float worldZ) {
    float terrainX = worldX - this.x;
    float terrainZ = worldZ - this.z;
    float gridSquareSize = SIZE / ((float)heights.length-1);
    int gridX = (int) Math.floor(terrainX / gridSquareSize);
    int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
    if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0){
//      System.err.println(" >> RETORNOU HEIGHT: 0 !");
      return 0;
    }
    
    float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
    float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
    float answer;
    
    if (xCoord <= (1-zCoord)) {
      answer = Maths.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
                                      heights[gridX + 1][gridZ], 0), new Vector3f(0,
                                      heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
    } else {
      answer = Maths.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
                                      heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                                      heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
    }
//     System.err.println(" >> RETORNOU HEIGHT: "+answer);
    return answer;
  }

  public float getSize() {
    return this.SIZE;
  }

 
}
