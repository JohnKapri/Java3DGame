#version 330

in vec4 vVertex;
in vec3 vNormal;

uniform vec3 lightPos;

smooth out vec4 vVaryingColor;

void main() {
	// Get surface normal in eye coordinates
	vec3 vEyeNormal = normalMatrix * vNormal;
	
	// Get vertex position in eye coordinates
	vec4 vPosition4 = mvMatrix * vVertex;
	vec3 vPosition3 = vPosition4.xyz / vPosition4.w;
	
	// Get vector to light source
	vec3 vLightDir = normalize(vLightPosition - vPosition3);
	
	// Multiply intensity by diffuse color, force alpha to 1.0
	vVaryingColor.xyz = diff * diffuseColor.xyz;
	vVaryingColor.a = 1.0;
	
	// Let’s not forget to transform the geometry
	gl_Position = mvpMatrix * vVertex;
}