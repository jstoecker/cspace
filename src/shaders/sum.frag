varying vec3 normal_ES;
varying vec3 subColor;

uniform vec3 edgeColor;
uniform int colorMode;

const vec3 lightDir = vec3(0.0, 0.0, -1.0);

float diff(vec3 l, vec3 n)
{
  return max(dot(l,-n), dot(l,n));
}

void main()
{
  float diffuse = max(min(diff(lightDir,normal_ES), 1.0), 0.35);
  gl_FragColor = vec4((colorMode == 0 ? edgeColor : subColor) * diffuse, 0.5);
}