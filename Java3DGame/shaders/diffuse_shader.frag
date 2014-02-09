#version 330

smooth in vec4 vVaryingColor;

out vColor

void main() {
	vColor = vVaryingColor;
}