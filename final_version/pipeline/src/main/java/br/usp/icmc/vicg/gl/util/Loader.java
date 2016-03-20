package br.usp.icmc.vicg.gl.util;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import de.matthiasmann.twl.utils.PNGDecoder;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLProfile;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;


public class Loader {
  /// Singleton pattern:
  private static Loader loader = null;
  public static Loader getInstance(){
    if (loader == null){
      loader = new Loader();
    }
    return loader;
  }
  
  private static final int[] targets = { GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
                                         GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
                                         GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
                                         GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
  
  /**
   * Loads a texture cube map to the GC
   * @param baseName path to the textures folder
   * @param textureNames names of the files
   * @param ext extension of the texture file (png, jpg)
   * @return 
   */
  @Deprecated
  public int loadCubeMap(String baseName, String[] textureNames, String ext) {
    int texId = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);
    
    for (int i=0; i<textureNames.length; i++) {
      TextureData data = decodeTexture(baseName+textureNames[i]+"."+ext);
      GL11.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
    }
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    return texId;
  }
  
    /**
   * Loads a texture cube map to the GC
   * @param gl
   * @param baseName path to the textures folder
   * @param textureNames names of the files
   * @param ext extension of the texture file (png, jpg)
   * @return 
   */
  public int loadCubeMap(GL3 gl, String baseName, String[] textureNames, String ext) {
    final IntBuffer buffer = BufferUtils.createIntBuffer(1);
    gl.glGenTextures(buffer.limit(), buffer);// TODO
    final int texId = buffer.get(0);
    System.out.println(">>>> texID: "+texId);
    gl.glActiveTexture(GL3.GL_TEXTURE0);
    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, texId);
    
    for (int i=0; i<textureNames.length; i++) {
      System.err.println(">>> TRYING TO DECODE: "+baseName+textureNames[i]+"."+ext);
      TextureData data = decodeTexture(baseName+textureNames[i]+"."+ext);
      gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL.GL_RGBA, 
              data.getWidth(), data.getHeight(), 0, GL.GL_RGBA, 
              GL.GL_UNSIGNED_BYTE, data.getBuffer());
    }
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

    return texId;
  }
  
  /**
   * Receives a filename of the texture (KdMap/KsMap/NormalMap) and loads the texture
   * into a com.jogamp.opengl.util.texture.Texture object
   * 
   * @param filename
   * @return com.jogamp.opengl.util.texture.Texture object
   */
  public com.jogamp.opengl.util.texture.Texture loadTexture(String filename) {
    System.out.println(" >>> FILENAME: "+filename);
    try {
      com.jogamp.opengl.util.texture.Texture mapTexture;
      BufferedImage image = ImageIO.read(getClass().getClassLoader().getResourceAsStream(filename));
      ImageUtil.flipImageVertically(image); //vertically flip the image
      mapTexture = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL3), image, true);
      
      return mapTexture;
    }catch(IOException e){
      System.err.println("[ERROR] It was not possible to load the "+filename+" texture.");
      e.printStackTrace();
    }
    return null;
  }
  
  public void enableTiling(GL3 gl, com.jogamp.opengl.util.texture.Texture texture) {
    texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    texture.setTexParameteri(gl, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
  }
  
  /**
   * Loads a texture file into a TextureData object
   * @param textureName name of the texture file
   * @return 
   */
  private TextureData decodeTexture(String textureName) {
    int width = 0;
    int height = 0;
    ByteBuffer buffer = null;
    PNGDecoder decoder = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(textureName);
      decoder = new PNGDecoder(fis);
      width = decoder.getWidth();
      height = decoder.getHeight();
      buffer = ByteBuffer.allocateDirect(4 * width * height);
      decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
      buffer.flip();
      fis.close();
    } catch (Exception ex) {
      Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
      System.err.println("Could not load the texture: "+textureName);
    } 
    return new TextureData(buffer, width, height);
  }
  
  // Yes, that is important
  public void freeTexture(GL3 gl, int texID){
    if (gl == null) { return; }
    int[] tmp = new int[1];
    tmp[0] = texID;
    gl.glDeleteTextures(1, tmp, 0);
  }
}
