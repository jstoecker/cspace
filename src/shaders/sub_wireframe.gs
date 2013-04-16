// C-Space 3D Sub Geometry Shader (wireframed)
// Justin Stoecker

#version 120
#extension GL_ARB_geometry_shader4 : enable

uniform vec2 viewport;

varying in vec3 normal_world[3];
varying in vec3 normal_eye[3];
varying in vec3 vertex_color[3];
varying in float theta[3];

varying out vec3 frag_normal_world;
varying out vec3 frag_normal_eye;
varying out vec3 frag_color;
varying out float frag_theta;
noperspective varying out vec3 frag_dist;

void main()
{
  vec2 vert_0 = viewport * gl_PositionIn[0].xy / gl_PositionIn[0].w;
  vec2 vert_1 = viewport * gl_PositionIn[1].xy / gl_PositionIn[1].w;
  vec2 vert_2 = viewport * gl_PositionIn[2].xy / gl_PositionIn[2].w;
  vec2 edge_0 = vert_2 - vert_1;
  vec2 edge_1 = vert_2 - vert_0;
  vec2 edge_2 = vert_1 - vert_0;
  float area = abs(edge_1.x * edge_2.y - edge_1.y * edge_2.x);

  frag_normal_world = normal_world[0];
  frag_normal_eye = normal_eye[0];
  frag_color = vertex_color[0];
  frag_theta = theta[0];
  frag_dist = vec3(area / length(edge_0), 0.0, 0.0);
  gl_Position = gl_PositionIn[0];
  EmitVertex();

  frag_normal_world = normal_world[1];
  frag_normal_eye = normal_eye[1];
  frag_color = vertex_color[1];
  frag_theta = theta[1];
  frag_dist = vec3(0.0, area / length(edge_1), 0.0);
  gl_Position = gl_PositionIn[1];
  EmitVertex();

  frag_normal_world = normal_world[2];
  frag_normal_eye = normal_eye[2];
  frag_color = vertex_color[2];
  frag_theta = theta[2];
  frag_dist = vec3(0.0, 0.0, area / length(edge_2));
  gl_Position = gl_PositionIn[2];
  EmitVertex();
}