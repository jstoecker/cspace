// C-Space 3D Sub Fragment Shader (wireframed)
// Justin Stoecker

#version 120

const float wire_thickness = 0.5;
const float clip_width = 0.05;
const float clip_smooth_width = 0.025;
const vec3 l = vec3(0.0, 0.0, -1.0);

uniform vec3  color;
uniform int   color_style;   // 0 = UNIFORM, 1 = NORMAL, 2/3 = VERTEX COLOR (per sub/sum)
uniform int   clip_style;    // 0 = NONE, 1 = ABOVE, 2 = AROUND, 3 = BELOW
uniform float robot_theta;
uniform bool  shading;
uniform float alpha;
uniform vec4  wire_color;

varying vec3  frag_color;
varying vec3  frag_normal_world;
varying vec3  frag_normal_eye;
varying float frag_theta;
noperspective varying in vec3 frag_dist;

void main()
{
  float d = min(min(frag_dist[0], frag_dist[1]), frag_dist[2]);
  
  if (d <= wire_thickness) {
    gl_FragColor = wire_color;
  } else {

    // determine the base color of the fragment
    vec4 fragColor = vec4(0.0, 0.0, 0.0, alpha);
    if (color_style == 0)
  	  fragColor.rgb = color;
    else if (color_style == 1)
      fragColor.rgb = frag_normal_world * 0.5 + 0.5;
    else
      fragColor.rgb = frag_color;
  
    // apply shading
    if (shading) {
      vec3 n = normalize(frag_normal_eye);
      fragColor.rgb *= max(min(max(dot(l, -n), dot(l, n)), 1.0), 0.35);
    }
  
    // apply clipping
    if (clip_style == 1) {
      if (frag_theta > robot_theta)
        discard;
    } else if (clip_style == 2) {
      float e1 = robot_theta - clip_width - clip_smooth_width;
      float e2 = robot_theta - clip_width;
      float e3 = robot_theta + clip_width;
      float e4 = robot_theta + clip_width + clip_smooth_width;
      if (frag_theta > e1 && frag_theta < e2) {
        fragColor.a += (1.0 - alpha) * smoothstep(e1, e2, frag_theta);
      } else if (frag_theta >= e2 && frag_theta <= e3) {
        fragColor.a = 1.0;
      } else if (frag_theta > e3 && frag_theta < e4) {
        fragColor.a += (1.0 - alpha) * (1.0 - smoothstep(e3, e4, frag_theta));
      }
    } else if (clip_style == 3) {
      if (frag_theta < robot_theta)
        discard;
    }
  
    float d2 = d - wire_thickness;
    gl_FragColor = mix(fragColor, wire_color, exp2(-2.0 * d2 * d2));
  }
}