package br.usp.icmc.vicg.gl.animator;

import br.usp.icmc.vicg.gl.app.Pipeline;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.jwavefront.JWavefrontModel;
import java.util.ArrayList;

public class LeavesAnimator {
  private static LeavesAnimator leavesAnimator;
  private ArrayList<JWavefrontModel> models;
  
  public static LeavesAnimator getInstance() {
    if (leavesAnimator == null) leavesAnimator = new LeavesAnimator();
    return leavesAnimator;
  }
  private double counter;
  
  private LeavesAnimator() {
    this.models = new ArrayList<>();
    this.counter = 0;
  }
  
  
  public void addModel(JWavefrontModel jwm) {
    this.models.add(jwm);
    
  }
    
  public void animate() {
    
//    for (JWavefrontModel model : models) {
//      for (int i=0; i<model.getNumEntities(); i++) {
//        
//        model.setTx((float) ((float) model.getTx(i) + Math.sin(this.counter)), i);
//        
//        this.counter = counter + 0.01;
//      }
//    }
//    
    
    
  }
  
  
}
