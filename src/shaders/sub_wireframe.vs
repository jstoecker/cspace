#version 120

// OUTPUT
varying vec3  vert_normal_world;
varying vec3  vert_normal_eye;
varying vec3  vert_color;
varying float vert_theta;

void main()
{
  vert_normal_world = normalize(gl_Normal);
  vert_normal_eye = (gl_ProjectionMatrix * gl_ModelViewMatrix * vec4(vert_normal_world, 0.0)).xyz;
  vert_color = gl_Color.xyz;
  vert_theta = gl_Vertex.z;
	gl_Position = ftransform();
}