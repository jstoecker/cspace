// C-Space 3D Sub Vertex Shader
// Justin Stoecker

#version 120

uniform float zOffset;

varying vec3  normal_world;
varying vec3  normal_eye;
varying vec3  vertex_color;
varying float theta;
varying float theta_world;

void main()
{
  normal_world = normalize(gl_Normal);
  normal_eye = vec3(gl_ModelViewMatrix * vec4(normal_world, 0.0));
  vertex_color = gl_Color.xyz;
  theta = gl_Vertex.z; 
  theta_world = theta + zOffset;
  gl_Position = ftransform();
}