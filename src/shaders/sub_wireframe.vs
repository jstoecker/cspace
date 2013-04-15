// C-Space 3D Sub Vertex Shader (wireframe enabled)
// Justin Stoecker

#version 120

varying out vec3  vert_normal_world;
varying out vec3  vert_normal_eye;
varying out vec3  vert_color;
varying out float vert_theta_world;
varying out float vert_theta_eye;

void main()
{
  vert_normal_world = normalize(gl_Normal);
  vert_normal_eye = (gl_ProjectionMatrix * gl_ModelViewMatrix * vec4(vert_normal_world, 0.0)).xyz;
  vert_color = gl_Color.xyz;
  vert_theta_world = gl_Vertex.z;
  vert_theta_eye = (gl_ModelViewMatrix * gl_Vertex).z;
	gl_Position = ftransform();
}