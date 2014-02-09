#version 150

uniform vec3 color;

in vec3 fragColor;

out vec4 outColor;

void main()
{
    outColor = vec4(color * fragColor, 1.0);
}