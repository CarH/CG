/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.util;

/**
 *
 * @author ch
 */
public class Point3D {
    public float x;
    public float y;
    public float z;
    public float theta;
    
    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D(float x, float y, float z, float theta) {
        this(x, y, z);
        this.theta = theta;
    }
    public Point3D() {

    }
}
