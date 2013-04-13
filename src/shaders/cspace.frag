varying vec3 normal;
varying vec3 color;
varying float theta;
varying vec3 normal_ES;

uniform bool reverse;
uniform int coloring;    // 0 = EDGE COLOR, 1 = UNIQUE, 2 = NORMAL
uniform int clipping;    // 0 = NONE, 1 = ABOVE, 2 = AROUND, 3 = BELOW
uniform float robotTheta;
uniform bool shading;
uniform vec3 edgeColor;
uniform float alpha;

const vec3 lightDir = vec3(0.0, 0.0, -1.0);

float diff(vec3 l, vec3 n)
{
  return max(dot(l,-n), dot(l,n));
}

void main()
{
  float diffuse = shading ? max(min(diff(lightDir,normal_ES), 1.0), 0.35) : 1.0;
  vec3 fcolor;

  if (coloring == 0) {
    fcolor = edgeColor * diffuse;
  } else if (coloring == 1) {
    fcolor = color * diffuse;
  } else {
    fcolor = (normal * 0.5 + 0.5) * diffuse;
  }
  
  if (reverse && alpha < 1.0) {
    fcolor.x = 1.0 - fcolor.x;
    fcolor.y = 1.0 - fcolor.y;
    fcolor.z = 1.0 - fcolor.z;
    fcolor *= alpha;
  }
  
  if (clipping == 0) {
    gl_FragColor = vec4(fcolor, alpha);
  } else if (clipping == 1) {
    if (theta > robotTheta) {
      discard;
    } else {
      gl_FragColor = vec4(fcolor, alpha);
    }
  } else if (clipping == 2) {
    if (theta > robotTheta || theta < robotTheta - 0.1) {
      discard;
    } else {
      gl_FragColor = vec4(fcolor, alpha);
    }
  } else if (clipping == 3) {
    if (theta < robotTheta) {
      discard;
    } else {
      gl_FragColor = vec4(fcolor, alpha);
    }
  }
}