varying vec3 normal;
varying vec3 color;
varying float theta;
varying vec3 normal_ES;

void main()
{
  normal = normalize(gl_Normal);
  normal_ES = vec3(gl_ProjectionMatrix * gl_ModelViewMatrix * vec4(normal, 0.0));
  color = gl_Color.xyz;
  theta = gl_Vertex.z;
  gl_Position = ftransform();
}