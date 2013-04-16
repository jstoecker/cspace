// C-Space 3D Sub Vertex Shader
// Justin Stoecker

#version 120

const float clip_width = 0.05;
const float clip_smooth_width = 0.025;
const vec3 l = vec3(0.0, 0.0, -1.0);

uniform vec3  color;
uniform int   color_style;   // 0 = UNIFORM, 1 = NORMAL, 2/3 = VERTEX COLOR (per sub/sum)
uniform int   clip_style;    // 0 = NONE, 1 = ABOVE, 2 = AROUND, 3 = BELOW
uniform float robot_theta;
uniform bool  shading;
uniform float alpha;

varying vec3  vertex_color;
varying vec3  normal_world;
varying vec3  normal_eye;
varying float theta;

void main()
{
  // determine the base color of the fragment
  vec4 fragColor = vec4(0.0, 0.0, 0.0, alpha);
  if (color_style == 0)
  	fragColor.rgb = color;
  else if (color_style == 1)
    fragColor.rgb = normal_world * 0.5 + 0.5;
  else
    fragColor.rgb = vertex_color;
  
  // apply shading
  if (shading) {
    vec3 n = normalize(normal_eye);
    fragColor.rgb *= max(min(max(dot(l, -n), dot(l, n)), 1.0), 0.35);
  }
  
  // apply clipping
  if (clip_style == 1) {
    if (theta > robot_theta)
      discard;
  } else if (clip_style == 2) {
    float e1 = robot_theta - clip_width - clip_smooth_width;
    float e2 = robot_theta - clip_width;
    float e3 = robot_theta + clip_width;
    float e4 = robot_theta + clip_width + clip_smooth_width;
    if (theta > e1 && theta < e2) {
      fragColor.a += (1.0 - alpha) * smoothstep(e1, e2, theta);
    } else if (theta >= e2 && theta <= e3) {
      fragColor.a = 1.0;
    } else if (theta > e3 && theta < e4) {
      fragColor.a += (1.0 - alpha) * (1.0 - smoothstep(e3, e4, theta));
    }
  } else if (clip_style == 3) {
    if (theta < robot_theta)
      discard;
  }
  
  gl_FragColor = fragColor;
}