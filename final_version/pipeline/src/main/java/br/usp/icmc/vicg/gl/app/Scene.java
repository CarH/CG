/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.model.Skybox;
import br.usp.icmc.vicg.gl.model.Trees;
import br.usp.icmc.vicg.gl.model.Terrain;
import br.usp.icmc.vicg.gl.model.jwavefront.JWavefrontModel;
import br.usp.icmc.vicg.gl.model.jwavefront.Maths;
import br.usp.icmc.vicg.gl.util.GameManager;
import br.usp.icmc.vicg.gl.util.Loader;
import br.usp.icmc.vicg.gl.util.Point3D;
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
    GL3 gl;
    private final Pipeline pipeline;
//    private final JWavefrontModel sky;
//    private final JWavefrontModel terrain; //ANTIGO
    private Trees trees;
    private final JWavefrontModel firePlace;
    private final JWavefrontModel treasure;
    private final JWavefrontModel stone;
    private final JWavefrontModel boat;
    private final JWavefrontModel barrel;
    private final JWavefrontModel justLeaves;
    private final JWavefrontModel water;
    private Skybox skybox;
    private Loader loader;
    
    private static final float RED = 0.5f;
    private static final float GREEN = 0.5f;
    private static final float BLUE = 0.5f;
 
    private GameManager gameManager;
    private boolean DONT_DRAW = false;
    
    public Scene() {
        this.pipeline = Pipeline.getInstance();
        this.loader = Loader.getInstance();
        // Old version of the sky:
//        sky = new JWavefrontModel(new File("./src/main/resources/obj/sky/sky.obj"), "Sky", 1, loader);
//        sky.setIsSky(true);
        
        skybox = new Skybox();
        firePlace = new JWavefrontModel(new File("./src/main/resources/obj/fireplace/fireplace.obj"), "Fire place", 1);
        treasure = new JWavefrontModel(new File("./src/main/resources/obj/treasure_chest/treasure_chest.obj"), "Treasure", 1);
        stone = new JWavefrontModel(new File("./src/main/resources/obj/stone/stone.obj"), "Stone", 1);
        boat = new JWavefrontModel(new File("./src/main/resources/obj/boat/OldBoat.obj"), "Boat", 1);
        barrel = new JWavefrontModel(new File("./src/main/resources/obj/barrel/wb.obj"), "Barrel", 5);
        trees = new Trees(pipeline);
        justLeaves = new JWavefrontModel(new File("./src/main/resources/obj/palm_banana/just_leaves.obj"),"Just Leaves", 3);
        water = new JWavefrontModel(new File("./src/main/resources/obj/waterMaisVertices/water.obj"), "Water", 1);
        water.setIsWater(true);
    }

    public void init(GL3 gl, Terrain terrain) {
        this.gl = gl;
        try {
  
          // float x, z, y;
          skybox.init(gl, pipeline.getShader(Pipeline.ShaderName.SKYBOX));
          skybox.addEntityPosition(new Point3D(69f, 0.5f, 15f));
          
          // Another version of the sky:
//            sky.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
//            sky.unitize();
//            sky.addEntityPosition(new Point3D(1f, -3.0f, 1f));
//            sky.addEntityScale(new Point3D(5.0f, 5.0f, 5.0f));
//            sky.dump();
            water.setNormalMapSource("obj/waterMaisVertices/waterNormal.jpg");
            water.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            water.addEntityPosition(new Point3D(0f, -9.0f, 0f));
            water.addEntityScale(new Point3D(0.5f, 0.5f, 0.5f));
            water.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            
            if (!DONT_DRAW){   

            firePlace.setNormalMapSource("obj/fireplace/map/fireplace_NRM.png");
            firePlace.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            firePlace.addEntityPosition(new Point3D(50.6f, 4.8f, 46.6f));
            firePlace.addEntityScale(new Point3D(0.5f, 0.5f, 0.5f));
            firePlace.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            
//            x = 27f;
//            z = -1.6f;
//            y = terrain.getHeightOfTerrain(x, z);
            treasure.setNormalMapSource("obj/treasure_chest/map/treasure_chest_NRM.png");
            treasure.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            treasure.addEntityPosition(new Point3D(69.7f, 1.1f, 63.5f));
            treasure.addEntityScale(new Point3D(1.5f, 1.5f, 1.5f));
            treasure.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 75.f));
            
//            x = Maths.getRandom(0f, terrain.getSize());
//            z = Maths.getRandom(0f, terrain.getSize());
//            y = terrain.getHeightOfTerrain(x, z);
            stone.setNormalMapSource("obj/stone/map/shan03_N.png");
            stone.setSpecMapSource("obj/stone/map/shan03_S.png");
            stone.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            stone.addEntityPosition(new Point3D(260f, -33.5f, 745f));
            stone.addEntityScale(new Point3D(0.1f, 0.1f, 0.1f));
            stone.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            
//            x = Maths.getRandom(5f, terrain.getSize()-5f);
//            z = Maths.getRandom(5f, terrain.getSize()-5f);
//            y = terrain.getHeightOfTerrain(x, z);
            boat.setNormalMapSource("obj/boat/map/boattexnm.jpg");
            boat.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
//            boat.addEntityPosition(new Point3D(x, y, z));
            boat.addEntityPosition(new Point3D(30.3f, -1.7f, 19.1f));
            boat.addEntityScale(new Point3D(0.5f, 0.5f, 0.5f));
            boat.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
//            boat.generateBoundBox(pipeline.getShader());
            
//            x = Maths.getRandom(0f, terrain.getSize());
//            z = Maths.getRandom(0f, terrain.getSize());
//            y = terrain.getHeightOfTerrain(x, z);
            barrel.setNormalMapSource("obj/barrel/map/wood_NRM.png");
            barrel.setSpecMapSource("obj/barrel/map/wood_SPC.png");
            barrel.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            barrel.addEntityPosition(new Point3D(46.6f, -2.2f, 22.5f));
            barrel.addEntityPosition(new Point3D(50.f, -2.0f, 20.5f));
            barrel.addEntityPosition(new Point3D(46.f, -2.8f, 20.0f));
            barrel.addEntityPosition(new Point3D(44.f, -2.8f, 22.0f));
            barrel.addEntityPosition(new Point3D(41.f, -1.1f, 21.9f));
            barrel.addEntityScale(new Point3D(0.05f, 0.05f, 0.05f));
            barrel.addEntityScale(new Point3D(0.05f, 0.05f, 0.05f));
            barrel.addEntityScale(new Point3D(0.05f, 0.05f, 0.05f));
            barrel.addEntityScale(new Point3D(0.05f, 0.05f, 0.05f));
            barrel.addEntityScale(new Point3D(0.05f, 0.05f, 0.05f));
            barrel.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            barrel.addEntityRotate(new Point3D(0.f, 1.f, 1.f, 51.f));
            barrel.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            barrel.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
            barrel.addEntityRotate(new Point3D(0.f, 0.f, 1.f, 89.f));
            
            }
            
            justLeaves.init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
            justLeaves.addEntityPosition(new Point3D(50.6f, 5.1f, 46.6f));
            justLeaves.addEntityPosition(new Point3D(70.8f, 2.2f, 64.6f));
            justLeaves.addEntityPosition(new Point3D(23.0f, -2.7f, 68.0f));
            justLeaves.addEntityScale(new Point3D(0.5f, 0.5f, 0.5f));
            justLeaves.addEntityScale(new Point3D(0.6f, 0.6f, 0.6f));
            justLeaves.addEntityScale(new Point3D(0.8f, 0.8f, 0.8f));
            justLeaves.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 90.f));
            justLeaves.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 187.f));
            justLeaves.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.0f));
            
            // Inicializa as árvores:
            trees.init(gl, terrain);
            if (this.gameManager != null) {
//                this.gameManager.addObject(terrain);
//                this.gameManager.addObject(skybox);
                this.gameManager.addObject(trees);
                this.gameManager.addObject(water);
                this.gameManager.addObject(firePlace);
                this.gameManager.addObject(treasure);
                this.gameManager.addObject(stone);
                this.gameManager.addObject(boat);
                this.gameManager.addObject(barrel);
                this.gameManager.addObject(justLeaves);

            }
            
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
        }

        // The light of the sun. Position = Direction of the light source
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setPosition(new float[]{0.1f, 1.0f, 1.0f, 1.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setDiffuseColor(new float[]{2.0f, 2.0f, 2.0f, 1.0f});
        pipeline.getLight(Pipeline.LightNumber.LIGHT0).setSpecularColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});

        gl.glEnable(GL3.GL_CULL_FACE); // Ativa cullface - corta uma face/ define uma visivel
    }
    
    // Draw all objects of scene
    public void display(GL3 gl) {
        pipeline.bindSkyColor(new float[]{RED, GREEN, BLUE});
        
        /* Draw sky */
//        sky.setModelMatrix(pipeline, 0);
//        sky.draw();
        skybox.setModelMatrix(pipeline, 0);
        skybox.draw();
        
               
        water.setModelMatrix(pipeline, 0);
        water.draw();
        if(!DONT_DRAW) {
        
        /* Draw fireplace */
        firePlace.setModelMatrix(pipeline, 0);
        firePlace.draw();
        
        /* Draw treasure */
        treasure.setModelMatrix(pipeline, 0);
        treasure.draw();
        
        stone.setModelMatrix(pipeline, 0);
        stone.draw();
        
        boat.setModelMatrix(pipeline, 0);
        boat.draw();
        
        for(int i=0; i<barrel.getNumEntities(); i++){
          barrel.setModelMatrix(pipeline, i);
          barrel.draw();
        }
        
        gl.glDisable(GL.GL_CULL_FACE);
        for(int i=0; i<justLeaves.getNumEntities(); i++){
          justLeaves.setModelMatrix(pipeline, i);
          justLeaves.draw();
        }
        gl.glEnable(GL.GL_CULL_FACE);
        }
        /* Draw trees */
        trees.draw();
    }
    
    public void dispose() {
        skybox.dispose();
        trees.dispose();
        firePlace.dispose();
        treasure.dispose();
        boat.dispose();
        barrel.dispose();
        water.dispose();
        stone.dispose();
        justLeaves.dispose();
    }

    void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
  
  
}
