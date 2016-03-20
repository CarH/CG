/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.usp.icmc.vicg.gl.model.jwavefront;

import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.util.Point3D;
import br.usp.icmc.vicg.gl.util.Shader;
import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public class BoundBox extends SimpleModel {
     private Material mat;
     private Point3D min;
     private Point3D max;
     
     public BoundBox(Point3D min, Point3D max) {
        this.min = min;
        this.max = max;
        vertex_buffer = new float[]{
            // Front face
            min.x, min.y, min.z, 
            max.x, min.y, min.z, 
            max.x, min.y, min.z, 
            max.x, max.y, min.z, 
            max.x, max.y, min.z, 
            min.x, max.y, min.z,
            min.x, max.y, min.z,
            min.x, min.y, min.z,

            // Right face
            max.x, min.y, min.z, 
            max.x, min.y, max.z,
            max.x, min.y, max.z,
            max.x, max.y, max.z,
            max.x, max.y, max.z,
            max.x, max.y, min.z,
            max.x, max.y, min.z,
            max.x, min.y, min.z, 

            // Back face
            max.x, min.y, max.z,            
            min.x, min.y, max.z,
            min.x, min.y, max.z,
            min.x, max.y, max.z,
            min.x, max.y, max.z,
            max.x, max.y, max.z,
            max.x, max.y, max.z,            
            max.x, min.y, max.z, 

            // Left face
            min.x, min.y, min.z,
            min.x, max.y, min.z,
            min.x, max.y, min.z,
            min.x, max.y, max.z,
            min.x, max.y, max.z,
            min.x, min.y, max.z,
            min.x, min.y, max.z,
            min.x, min.y, min.z,

            // Bottom face
            min.x, min.y, min.z,
            min.x, min.y, max.z,
            min.x, min.y, max.z,
            max.x, min.y, max.z,
            max.x, min.y, max.z,
            max.x, min.y, min.z,            
            max.x, min.y, min.z,
            min.x, min.y, min.z,

            // Top face
            min.x, max.y, min.z,
            max.x, max.y, min.z,
            max.x, max.y, min.z,
            max.x, max.y, max.z,
            max.x, max.y, max.z,
            min.x, max.y, max.z,
            min.x, max.y, max.z,
            min.x, max.y, min.z,
        };        
        
        
//         dumb(min, max);
    }

    @Override
    public void draw() {
        this.mat.bind();
        draw(GL3.GL_LINES);
    }

    public Point3D getMin() {
      return min;
    }

    public Point3D getMax() {
      return max;
    }
    public void init(GL3 gl, Shader shader){
        super.init(gl, shader);
        this.mat = new Material();
        this.mat.init(gl, shader);
    }
    
    public void dumb(Point3D min, Point3D max){
        System.out.println(" <> MIN "+min.x+", "+min.y+", "+min.z);
        System.out.println(" <> MAX "+max.x+", "+max.y+", "+max.z);
    }
}
