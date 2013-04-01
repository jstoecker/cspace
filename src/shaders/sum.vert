varying vec3 normal_ES;
varying vec3 subColor;

void main()
{
  normal_ES = vec3(gl_ProjectionMatrix * gl_ModelViewMatrix * vec4(gl_Normal, 0.0));
  subColor = gl_Color.xyz;
  gl_Position = ftransform();
}