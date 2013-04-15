#version 120
#extension GL_EXT_gpu_shader4 : enable

const float thickness = 0.5;
const vec3 light_dir = vec3(0.0, 0.0, -1.0);

// INPUT
varying vec3 normal_world;
varying vec3 normal_eye;
varying vec3 color;
varying float theta;
noperspective varying vec3 dist;

float diffuse(vec3 l, vec3 n)
{
  return max(dot(l,-n), dot(l,n));
}

void main()
{
  bool shading = true;
  float diffuse = shading ? max(min(diffuse(light_dir, normal_eye), 1.0), 0.35) : 1.0;


  float d = min(min(dist[0], dist[1]), dist[2]);
  float dMinusThickness = d - thickness;
  float i = exp2(-dMinusThickness * dMinusThickness);
  
  vec4 wire = vec4(0.0, 0.0, 0.0, 1.0);
  
  if (d <= thickness)
    gl_FragColor = wire;
  else
    gl_FragColor = mix(vec4(color * diffuse, 1.0), wire, i);
}