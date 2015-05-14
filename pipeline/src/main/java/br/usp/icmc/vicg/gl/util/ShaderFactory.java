/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.util;

/**
 *
 * @author paulovich
 */
public class ShaderFactory {

  public enum ShaderType {

    SIMPLE_SHADER,
    TRANSFORM_SHADER,
    MODEL_MATRIX_SHADER,
    MODEL_PROJECTION_MATRIX_SHADER,
    VIEW_MODEL_PROJECTION_MATRIX_SHADER,
    JWAVEFRONT_SHADER
  };

  public static Shader getInstance(ShaderType type) {
    if (type == ShaderType.SIMPLE_SHADER) {
      return new Shader("simple_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.TRANSFORM_SHADER) {
      return new Shader("transform_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.MODEL_MATRIX_SHADER) {
      return new Shader("model_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.MODEL_PROJECTION_MATRIX_SHADER) {
      return new Shader("model_projection_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.VIEW_MODEL_PROJECTION_MATRIX_SHADER) {
      return new Shader("view_model_projection_vertex.glsl", "simple_fragment.glsl");
    } else if (type == ShaderType.JWAVEFRONT_SHADER) {
      return new Shader("jwavefront_vertex.glsl", "jwavefront_fragment.glsl");
    }
    return null;
  }
}
