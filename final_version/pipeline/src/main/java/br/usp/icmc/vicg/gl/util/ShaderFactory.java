package br.usp.icmc.vicg.gl.util;

public class ShaderFactory {

  public enum ShaderType {
    JWAVEFRONT_SHADER,
    SKYBOX_SHADER
  };

  public static Shader getInstance(ShaderType type) {
    if (type == ShaderType.JWAVEFRONT_SHADER) {
      return new Shader("jwavefront_vertex.glsl", "jwavefront_fragment.glsl");
    } else if (type == ShaderType.SKYBOX_SHADER) {
      return new Shader("skybox_vertex_shader.glsl", "skybox_fragment_shader.glsl");
    }
    return null;
  }
}
