#version 430 core

in vec3 textureCoords;
out vec4 fragColor;

uniform samplerCube cubeMap;
uniform sampler2D tex;

void main(void){
  fragColor = texture(cubeMap, textureCoords);
}