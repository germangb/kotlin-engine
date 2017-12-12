#version 450 core

layout(location = 0) out vec4 foo;

void main() {
    foo = mat4() * vec4(1,2,3,4);
}
