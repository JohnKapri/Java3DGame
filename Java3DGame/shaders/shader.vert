#version 420 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;

//Light and material stuff
uniform vec3 light_pos = vec3(50.0);
uniform vec3 diffuse_light = vec3(0.5);
uniform vec3 specular_light = vec3(0.2);
uniform float specular_power = 100.0;
uniform vec3 ambiant_light = vec3(0.01);

out vec3 fragColor;

void main()
{
	vec4 P = model * vec4(position, 1.0);
	vec3 N = mat3(model) * normal;
	vec3 L = light_pos - P;
	vec3 V = -P.xyz;
	
	N = normalize(N);
	L = normalize(L);
	V = normalize(V);
	
	vec3 R = reflect(-L, N);
	vec3 diffuse = max(dot(N, L), 0.0) * diffuse_light;
	vec3 specular = pow(max(dot(R, V), 0.0), speculat_power) * specular_light;
	
	fragColor = amient + diffuse + specular;
	
    gl_Position = proj * view * model * vec4(position, 1.0);
}