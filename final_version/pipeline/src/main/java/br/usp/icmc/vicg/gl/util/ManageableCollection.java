/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.util;

import java.util.ArrayList;

/**
 *
 * @author pc
 */
public interface ManageableCollection extends Manageable{
    public String getCollectionName();
    public ArrayList<String> getNamesOfModels();
    public ArrayList<String> getNamesOfEntities(int modelIdx);
    public ManageableObject getModel(int idx);
}
