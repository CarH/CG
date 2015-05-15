/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.model.Square;
import br.usp.icmc.vicg.gl.model.WiredCube;
import br.usp.icmc.vicg.gl.model.jwavefront.JWavefrontModel;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;

/**
 *
 * @author PC
 */
public class Scene {

    private final Pipeline pipeline;
    private final JWavefrontModel sky;
    private final JWavefrontModel palmPeqA; // palm peq adulta
//    private final JWavefrontModel ;
//    private final JWavefrontModel ;
//    private final JWavefrontModel ;
//    private final JWavefrontModel ;
//    private final JWavefrontModel ;
    private final Square floor;
    private final Material mat = new Material();
    private final WiredCube cube;

    public Scene(Pipeline pipeline) {
        this.pipeline = pipeline;

        sky = new JWavefrontModel(new File("./data/sky/sky.obj"));
        palmPeqA = new JWavefrontModel(new File("./data/palm_peq_hd/Models/peq_adulta.obj"));
        floor = new Square();
        cube = new WiredCube();
    }

    public void init(GL3 gl) {
        try {
            sky.init(gl, pipeline.getShader());
            sky.unitize();
            sky.setInitialPosition(0f, 0f, 0f, 0, 2f, 2f, 2f);
            sky.dump();
            
            // Inicialização das árvores:
            palmPeqA.init(gl, pipeline.getShader());
            palmPeqA.unitize();
            palmPeqA.setInitialPosition(0, 0, 0, -90, 0.5f, 0.5f, 0.5f);
            palmPeqA.dump();
            
            
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
        }

        floor.init(gl, pipeline.getShader());
        cube.init(gl, pipeline.getShader());

        //init the light
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setPosition(new float[]{0.0f, 0.0f, 1.0f, 0.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setDiffuseColor(new float[]{0.9f, 0.9f, 0.9f, 1.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});

        mat.init(gl, pipeline.getShader());
        mat.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        mat.setSpecularColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        gl.glEnable(GL3.GL_CULL_FACE); // Escolhe UMA das faces do triangulo para renderizar!
    }
    
    // Draw all objects of scene
    public void display(GL3 gl) {
        
        gl.glDisable(GL3.GL_CULL_FACE); // Teoricamente nao era pra usar isso no ceu
//        pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
//        pipeline.getMatrix(Pipeline.MatrixType.MODEL).scale(10, 10, 10);
//        pipeline.getMatrix(Pipeline.MatrixType.MODEL).translate(0.75f, 0, 0);
//        pipeline.bind();
        sky.setModelMatrix(pipeline);
        sky.draw();
        gl.glEnable(GL3.GL_CULL_FACE);
        
        this.drawPlants(gl);
        
        // Prepara para desenhar o chao: 
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).translate(0, -0.5f, 0);
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).scale(3, 3, 3);
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).rotate(-90, 1, 0, 0);
        pipeline.bind();

        mat.bind();
        floor.bind();
        floor.draw(GL.GL_TRIANGLE_FAN);

        pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).translate(0.0f, 0.0f, 1.0f);
        pipeline.getMatrix(Pipeline.MatrixType.MODEL).scale(0.1f, 0.1f, 0.1f);
        pipeline.bind();

        mat.bind();
        cube.bind();
        cube.draw(GL.GL_LINES);
    }
    
    private void drawPlants(GL3 gl){ 
        gl.glDisable(GL3.GL_CULL_FACE);
        
//        pipeline.getMatrix(Pipeline.MatrixType.MODEL).loadIdentity();
////        pipeline.getMatrix(Pipeline.MatrixType.MODEL).translate(-0.75f, 0, 0);
//        pipeline.getMatrix(Pipeline.MatrixType.MODEL).rotate(-90, 10, 1, 1);
//        pipeline.bind();
        palmPeqA.setModelMatrix(pipeline);
        palmPeqA.draw();
        
        
        
        gl.glEnable(GL3.GL_CULL_FACE);
    }
    
    public void dispose() {
        sky.dispose();
        
        // Trees
        palmPeqA.dispose();
        
        floor.dispose();
    }

}
