#version 150

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelviewMatrix;
uniform mat4 u_normalMatrix;

in vec3 a_position;
in vec3 a_normal;
in vec2 a_texcoord;

out vec3 v_normal;
out vec3 v_eye;
out vec2 v_texcoord;
out vec4 v_position;

void main(void)
{ 
  v_position = u_modelviewMatrix * vec4(a_position, 1.0);
  
  v_eye = -vec3(v_position);
  
  v_normal = mat3(u_normalMatrix) * a_normal;

  v_texcoord = a_texcoord;

  gl_Position = u_projectionMatrix * v_position;
}