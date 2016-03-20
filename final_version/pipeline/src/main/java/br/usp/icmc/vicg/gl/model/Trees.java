/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import br.usp.icmc.vicg.gl.animator.LeavesAnimator;
import br.usp.icmc.vicg.gl.app.Pipeline;
import br.usp.icmc.vicg.gl.app.Pipeline;
import br.usp.icmc.vicg.gl.app.Scene;
import br.usp.icmc.vicg.gl.app.Scene;
import br.usp.icmc.vicg.gl.model.Terrain;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.jwavefront.JWavefrontModel;
import br.usp.icmc.vicg.gl.model.jwavefront.Maths;
import br.usp.icmc.vicg.gl.util.Loader;
import br.usp.icmc.vicg.gl.util.ManageableCollection;
import br.usp.icmc.vicg.gl.util.ManageableObject;
import br.usp.icmc.vicg.gl.util.Point3D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL3;

/**
 *
 * @author 
 */
public class Trees implements ManageableCollection{
  private GL3 gl;    
  private ArrayList<ArrayList<JWavefrontModel> > trees;
  private final String collectionName;
  private final Pipeline pipeline;
  private ArrayList<Point3D> position;
  private LeavesAnimator la;
    
  public Trees(Pipeline pipeline){
    this.pipeline = pipeline;
    this.trees = new ArrayList<>();
    ArrayList<JWavefrontModel> barkLeaf;
    /// Adding all trees in the this.trees array: 
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/palm_peq_hd/Models/peq_adulta.obj"), "Short Palm A", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/palm_peq_hd/Models/peq_A2.obj"), "Short Palm", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_0.obj"), "Tropical Palm 0", 1));
    // Tree 0:
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_0_bark.obj"), "Tropical Palm bark",3)); 
    barkLeaf.get(0).setNormalMapSource("obj/Tropical_palm/map/palm_bark_norm.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_0_leaf.obj"), "Tropical Palm leaves", 3));
    barkLeaf.get(1).setIsLeaf(true);
    this.trees.add(barkLeaf);

    // Tree 1:
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_1_bark.obj"), "Tropical Palm bark 2", 3)); 
    barkLeaf.get(0).setNormalMapSource("obj/Tropical_palm/map/palm_bark_norm.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_1_leaf.obj"), "Tropical Palm leaves2", 3));
    barkLeaf.get(1).setIsLeaf(true);
    this.trees.add(barkLeaf);

    // Tree 2:
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_3_bark.obj"), "Tropical Palm bark 3", 3)); 
    barkLeaf.get(0).setNormalMapSource("obj/Tropical_palm/map/palm_bark_norm.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_3_leaf.obj"), "Tropical Palm leaves3", 3));
    barkLeaf.get(1).setIsLeaf(true);
    this.trees.add(barkLeaf);

    // Tree 3:
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_4_bark.obj"), "Tropical Palm bark 4", 2)); 
    barkLeaf.get(0).setNormalMapSource("obj/Tropical_palm/map/palm_bark_norm.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_4_leaf.obj"), "Tropical Palm leaves4", 2));
    barkLeaf.get(1).setIsLeaf(true);
    this.trees.add(barkLeaf);

     // Tree 3:
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_5_bark.obj"), "Tropical Palm bark 5", 2)); 
    barkLeaf.get(0).setNormalMapSource("obj/Tropical_palm/map/palm_bark_norm.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_5_leaf.obj"), "Tropical Palm leaves5", 2));
    barkLeaf.get(1).setIsLeaf(true);
    this.trees.add(barkLeaf);


//        // Tree 3:
//        barkLeaf = new ArrayList<>();
//        /* Adding the bark*/
//        barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/tree_medium/tree_bark.obj"),"Medium bark", 2)); 
//        barkLeaf.get(0).setNormalMapSource("obj/tree_medium/map/medium_NRM.png");
//        /* Adding the leaf*/
//        barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/tree_medium/tree_leaves.obj"), "Medium leaves", 2));
//        barkLeaf.get(1).setNormalMapSource("obj/tree_medium/map/medium_NRM.png");
//        barkLeaf.get(1).setIsLeaf(true);
//        this.trees.add(barkLeaf);


    // Tree 6: banana palm
    barkLeaf = new ArrayList<>();
    /* Adding the bark*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/palm_banana/banana_bark.obj"),"Banana bark", 2)); 
    barkLeaf.get(0).setSpecMapSource("obj/palm_banana/map/palm_trunk_s.png");
    /* Adding the leaf*/
    barkLeaf.add(new JWavefrontModel(new File("./src/main/resources/obj/palm_banana/banana_leaves.obj"), "Banana leaves", 2));
    barkLeaf.get(1).setIsLeaf(true);
//        barkLeaf.get(0).setNormalMapSource("obj/palm_banana/map/medium_NRM.png");
    this.trees.add(barkLeaf);




//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_1.obj"), "Tropical Palm 1", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_2.obj"), "Tropical Palm 2", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_3.obj"), "Tropical Palm 3", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_4.obj"), "Tropical Palm 4", 1));
//        this.trees.add(new JWavefrontModel(new File("./src/main/resources/obj/Tropical_palm/trop_5.obj"), "Tropical Palm 5", 1));


    // setando as posicoes de cada arvore
    this.position = new ArrayList<>();
//        this.position.add(new Point3D(62.5f, -0.5f, 33.0f)); // Tropical Palm
    this.position.add(new Point3D(62.5f, -0.5f, 33.0f)); // Tropical Palm

    this.position.add(new Point3D(81.0f, -2.8f, 73f));
    this.position.add(new Point3D(20.8f, -2.99f, 46.57f));

    this.position.add(new Point3D(91.819f, -0.5f, 9.7925f)); // Tropical Palm 2
    this.position.add(new Point3D(65.33f, 0.57f, 39.73f));
    this.position.add(new Point3D(27.54f, -3.10f, 49.46f));


    this.position.add(new Point3D(51.0f, -4.0f, 44.99f)); // Tropical Palm 3
    this.position.add(new Point3D(17.8f, -2.21f, 81.59f));
    this.position.add(new Point3D(34.84f, -4.15f, 35.75f));


    this.position.add(new Point3D(59.7f, -1.71f, 24f)); // Tree medium
    this.position.add(new Point3D(71.02f, -1.0f, 19.0f));
    this.position.add(new Point3D(41.1f, -1.6f, 45.85f));

    this.position.add(new Point3D(29.0f, -6.0f, 74.0f)); // Banana Palm
    this.position.add(new Point3D(23.33f, -1.85f, 68.15f));
    this.position.add(new Point3D(23.29f, -2.11f, 19.76f));

    this.collectionName = "Trees";
  }
    
     public void init(GL3 gl, Terrain terrain) {
        this.gl = gl;
        this.la = LeavesAnimator.getInstance();
        try {
            float x, y, z;
            int count = 0;
            // Inicialização das árvores:
            /*********************************************************************************************
            *
            * TODO: Se nao ler as posicoes das arvores de um arquivo terá que adicionar todas hardcoded 
            *       => good luck!
            * 
            * ********************************************************************************************/
            System.out.println("----- Number of trees: "+this.trees.size());

            for (ArrayList<JWavefrontModel> tree : this.trees) {  // For each Model
//                this.initialPosTree.add( new ArrayList<Point3D>() );
                
                /* MODEL 0: bark | Send the bark to the graphic card */
                tree.get(0).init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
//                tree.get(0).dump();
                
                /* MODEL 1: leaves | Send the leaves to the graphic card */
                tree.get(1).init(gl, pipeline.getShader(Pipeline.ShaderName.WORLD));
//                tree.get(1).dump();
                tree.get(1).setIsLeaf(true);
//                la.addModel(tree.get(1)); // To animate the leaves
                /****************************************************************
                 *
                 * How many entities of this model do I have in the world?
                 * For each of them, set its position.
                 * Remember a tree has 2 models associated with it: bark and leaves.
                 *
                 ****************************************************************/
                for (int i = 0; i < tree.get(0).getNumEntities(); i++) {
//                    x = Maths.getRandom(0f, terrain.getSize());
//                    z = Maths.getRandom(0f, terrain.getSize());
//                    y = terrain.getHeightOfTerrain(x, z);
//                    Point3D point = new Point3D(x, y, z);
                    Point3D point = this.position.get(count);
//                    point.x = this.position.get(count).x;
//                    point.z = this.position.get(count).z;
//                    point.y = terrain.getHeightOfTerrain(point.x, point.z);
                    for (JWavefrontModel partOfTree : tree) {
                        // Set the same position for both, bark and leaves of the tree
                        System.out.println("Setando posicao inicial para "+partOfTree.getModelName());
                        partOfTree.addEntityPosition(point);
                        partOfTree.addEntityScale(new Point3D(1f, 1f, 1f));
                        partOfTree.addEntityRotate(new Point3D(0.f, 1.f, 0.f, 0.f));
                    }
                    count++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Scene.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

    public void draw() {
        Matrix4 trans = new Matrix4();
        for (ArrayList<JWavefrontModel> tree: this.trees ){
            
            for(JWavefrontModel partOfTree : tree){ // bark and leaves
              
                //// TO CHECK COLISION - AND FRUSTUM CULLING :
                for (int j = 0; j < partOfTree.getNumEntities(); j++) { 
                  
                  if(partOfTree.isLeaf()){
                    this.gl.glDisable(GL3.GL_CULL_FACE);
                  }
                  
                  // Set the model matrix parameters according to the j entity
                  partOfTree.setModelMatrix(pipeline, j);
                  
                  // Checar se o boundbox da arvore ta dentro ou fora do cubo normalizado:
//                  trans.loadIdentity();
//                  trans.multiply(pipeline.getMatrix(Pipeline.MatrixType.PROJECTION));
//                  trans.multiply(pipeline.getMatrix(Pipeline.MatrixType.VIEW));
//                  trans.multiply(partOfTree.getModelMatrix(pipeline, j));
//
//                  Point3D maxObj = partOfTree.getBoundBox().getMax();
//                  Point3D minObj = partOfTree.getBoundBox().getMin();
//                  
//                  System.out.println(">> MODEL: "+partOfTree.getModelName()+", ENTITY: "+j);
//                  System.out.println("  [ANTES]Pto MIN: ["+minObj.x+", "+minObj.y+", "+minObj.z+"]");
//                  System.out.println("  [ANTES]Pto MAX: ["+maxObj.x+", "+maxObj.y+", "+maxObj.z+"]");
//                  
//                  // Aplicar a trans
//                  float[] maxObjTrans = trans.multiplyByVector(new float[]{maxObj.x, maxObj.y, maxObj.z});
//                  float[] minObjTrans = trans.multiplyByVector(new float[]{minObj.x, minObj.y, minObj.z});
//                  
//                  System.out.println("  [DEPOIS]Pto MIN: ["+minObjTrans[0]+", "+minObjTrans[1]+", "+minObjTrans[2]+"]");
//                  System.out.println("  [DEPOIS]Pto MAX: ["+maxObjTrans[0]+", "+maxObjTrans[1]+", "+maxObjTrans[2]+"]");
//                  
//                  if (minObjTrans[0] < -1 || minObjTrans[1] < -1 || minObjTrans[2] > 1 ||
//                      maxObjTrans[0] > 1 || maxObjTrans[1] > 1 || maxObjTrans[2] < -1 ){ 
//                    System.out.println(" --- Cortou: "+partOfTree.getModelName());
//                    continue;
//                  }
                  
                  partOfTree.draw();
                  if(partOfTree.isLeaf()){
                    this.gl.glEnable(GL3.GL_CULL_FACE);
                  }
                }
            }
        }
        this.gl.glFlush();
    }

    public void dispose() {
      for ( ArrayList<JWavefrontModel> tree : this.trees ) {
        for (JWavefrontModel partOfTree : tree) {
          partOfTree.dispose();
        }
      }
    }

    @Override
    public String getCollectionName() {
      return this.collectionName;
    }

    @Override
    public ArrayList<String> getNamesOfModels() {
      ArrayList<String> res = new ArrayList<>();
      for (ArrayList<JWavefrontModel> tree : this.trees) {
        for (JWavefrontModel partOfTree : tree) {
            res.add(partOfTree.getModelName());
        }
      }
      return res;
    }

    @Override
    public ArrayList<String> getNamesOfEntities(int modelIdx) {
        ArrayList<String> res = new ArrayList<>();
        // modelIdx/2   : Select the tree model
        // modelIdx & 1 : to select the bark (0) or the leaves(1) of the tree
        for (int i = 0; i < this.trees.get(modelIdx/2).get(modelIdx & 1).getNumEntities(); i++) {
            res.add("Entity "+String.valueOf(i));
        }
        return res;
    }

    @Override
    public ManageableObject getModel(int idx) {
//        if(DEBUG)System.err.println("(int)idx/2): "+((int)idx/2)+", (idx & 1):"+(idx & 1));
        return this.trees.get((int)idx/2).get(idx & 1);
    }
   
}
