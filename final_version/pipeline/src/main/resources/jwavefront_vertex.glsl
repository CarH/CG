#version 150

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelviewMatrix;
uniform mat4 u_normalMatrix;

/////////////////////////
// LEAVES ANIMATION
uniform bool is_leaf;
uniform vec3 windVector;
/////////////////////////

uniform int is_water;
uniform float waves[200];
uniform float tempo;

//////////////////////////////
// DATA IN WORLD COORDINATES!
//////////////////////////////
in vec3 a_position; 
in vec3 a_normal;
in vec2 a_texcoord;
//////////////////////////////

out vec3 v_normal;
out vec3 v_eye;
out vec2 v_texcoord;
out vec4 v_position;
out float visibility;

/// FOG EFFECT
const float density = 0.007;
const float gradient = 1.5;
//////////////////////////

void main(void)
{ 

  //WATER
  vec3 tmp = a_position;
  float g = 9.8; 
  if (is_water == 1)
  {
     float somax = 0;
     float somay = 0;
     for (int i = 0; i < 3; ++i)
     {  
         float k =  (2.0 * 3.1415)/waves[i * 5 + 1];
         float w = sqrt(g * k);
         vec3 dir = vec3(waves[i * 5 + 2], waves[i * 5 + 3], waves[i * 5 + 4]);
          somax += waves[i * 5] * sin(dot(dir, vec3(tmp.x, tmp.y, tmp.z)) - w * tempo);
          somay += waves[i * 5] * cos(dot(dir , vec3(tmp.x, tmp.y, tmp.z)) - w * tempo);
      }
      tmp.x -= somax;
      tmp.y = somay;
  }
  //END WATER
  
  if( is_leaf ){
    vec3 aux =  a_position + windVector; // TODO att factor
    v_position = u_modelviewMatrix * vec4(aux, 1.0);
  } else {
    // Position relative to cam:
    v_position = u_modelviewMatrix * vec4(a_position, 1.0);
  }

  v_eye = -vec3(v_position); // the eye is positioned in the origin(0.0, 0.0, 0.0)
  
  /// Fog
  float dist = length(v_position.xyz);
  visibility = exp(-pow((dist * density), gradient));
  visibility = clamp(visibility, 0.0, 1.0);
  //////////////////////

  
  
  v_normal = normalize(transpose(inverse(mat3(u_modelviewMatrix))) * a_normal); // NOVO
  v_texcoord = a_texcoord;
  
  gl_Position = u_projectionMatrix * v_position;
}