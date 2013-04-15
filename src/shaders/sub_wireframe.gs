#version 120
#extension GL_EXT_gpu_shader4 : enable
#extension GL_EXT_geometry_shader4 : enable

uniform vec2 viewport;

// INPUT
varying in vec3  vert_normal_world[3];
varying in vec3  vert_normal_eye[3];
varying in vec3  vert_color[3];
varying in float vert_theta[3];

// OUTPUT
varying out vec3 normal_world;
varying out vec3 normal_eye;
varying out vec3 color;
varying out float theta;
noperspective varying vec3 dist;

void main()
{
  // transform vertices from clip to window coordinates
  vec2 vert_0 = viewport * gl_PositionIn[0].xy / gl_PositionIn[0].w;
  vec2 vert_1 = viewport * gl_PositionIn[1].xy / gl_PositionIn[1].w;
  vec2 vert_2 = viewport * gl_PositionIn[2].xy / gl_PositionIn[2].w;
  
  // calculate triangle area
  vec2 edge_0 = vert_2 - vert_1;
  vec2 edge_1 = vert_2 - vert_0;
  vec2 edge_2 = vert_1 - vert_0;
  float area = abs(edge_1.x * edge_2.y - edge_1.y * edge_2.x);
	
	// emit vertex 0
	normal_world = vert_normal_world[0];
	normal_eye = vert_normal_eye[0];
	color = vert_color[0];
	theta = vert_theta[0];
  dist = vec3(area / length(edge_0), 0.0, 0.0);
  gl_Position = gl_PositionIn[0];
  EmitVertex();

  // emit vertex 1
  normal_world = vert_normal_world[1];
  normal_eye = vert_normal_eye[1];
  color = vert_color[1];
  theta = vert_theta[1];
  dist = vec3(0.0, area / length(edge_1), 0.0);
  gl_Position = gl_PositionIn[1];
  EmitVertex();
	
	// emit vertex 2
	normal_world = vert_normal_world[2];
  normal_eye = vert_normal_eye[2];
  color = vert_color[2];
  theta = vert_theta[2];
  dist = vec3(0.0, 0.0, area / length(edge_2));
  gl_Position = gl_PositionIn[2];
  EmitVertex();
	
  EndPrimitive();
}