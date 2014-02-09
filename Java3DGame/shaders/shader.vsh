#version 420 core

layout(std140, binding=2) uniform Light {
	int enabled;
	vec3 position;
	vec3 direction;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float intensity;
	float spotAngle;
} light;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
//uniform Light[10] lights;

layout (location = 0) in vec4 position;
layout (location = 1) in vec3 normal;

//Light and material stuff
uniform vec3 light_pos = vec3(0.0, 50.0, -20.0);

out VS_OUT {
	smooth vec3 N;
	smooth vec3 L;
	smooth vec3 V;
	smooth float D;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float intensity;
	float spotAngle;
} vs_out;

void main()
{	
	vec4 P = model * position;
	vs_out.N = mat3(model) * normal;
	vs_out.L = light.position - P.xyz;
	vs_out.V = mat3(view) * P.xyz;
	vs_out.D = 10 / abs(length(P.xyz - light.position));
	vs_out.ambient = light.ambient;
	vs_out.diffuse = light.diffuse;
	vs_out.specular = light.specular;
	//vs_out.intensity = light.intensity;
	//vs_out.spotAngle = light.spotAngle;
	vs_out.intensity = 100;
	vs_out.spotAngle = 0;	
    gl_Position = proj * view * model * position;
}