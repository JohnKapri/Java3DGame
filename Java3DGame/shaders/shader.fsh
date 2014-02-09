#version 420 core

uniform vec3 color;
uniform vec3 diffuse_light = vec3(1);
uniform vec3 specular_light = vec3(0);
uniform float specular_power = 100.0;
uniform vec3 ambient = vec3(0.2);

in VS_OUT {
	smooth vec3 N;
	smooth vec3 L;
	smooth vec3 V;
	smooth float D;
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float intensity;
	float spotAngle;
} fs_in;

out vec4 outColor;

void main()
{
	vec3 N = normalize(fs_in.N);
	vec3 L = normalize(fs_in.L);
	vec3 V = normalize(fs_in.V);

	vec3 R = reflect(-L, N);
	
	vec3 diffuse = max(dot(N, L), 0.0) * diffuse_light;
	vec3 specular = pow(max(dot(R, V), 0.0), specular_power) * specular_light;
	//vec3 specular = pow(max(dot(R, V), 0.0), 100) * fs_in.specular;

    outColor = vec4(color * (ambient + diffuse + specular) * fs_in.D, 1.0);
}