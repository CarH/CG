/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.util;

import br.usp.icmc.vicg.gl.model.jwavefront.JWavefrontModel;
import java.util.ArrayList;

/**
 *
 * @author ch
 */
public interface ManageableObject extends Manageable{
    public String getModelName();
    public ArrayList<String> getNamesOfEntities();
    public void setTx(float value, int entPos);
    public void setTy(float value, int entPos);
    public void setTz(float value, int entPos);
    public void setSx(float value, int entPos);
    public void setSy(float value, int entPos);
    public void setSz(float value, int entPos);
    public void setRx(float value, int entPos);
    public void setRy(float value, int entPos);
    public void setRz(float value, int entPos);
    public void setTheta(float value, int entPos);
    public float getTx(int entPos);
    public float getTy(int entPos);
    public float getTz(int entPos);
    public float getSx(int entPos);
    public float getSy(int entPos);
    public float getSz(int entPos);
    public float getRx(int entPos);
    public float getRy(int entPos);
    public float getRz(int entPos);
    public float getTheta(int entPos);
}
