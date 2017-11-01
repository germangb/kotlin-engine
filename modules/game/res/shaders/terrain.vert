#version 450 core
layout(location = 0) in vec2 a_position;
layout(location = 1) in mat4 a_transform;

out vec2 v_uv;
out vec3 v_position;

uniform mat4 u_view;
uniform mat4 u_proj;
uniform float u_size;
uniform sampler2D u_height;

void main() {
    vec4 world = a_transform * vec4(a_position.x, 0.0, a_position.y, 1.0);
    v_uv = world.xz / u_size + 0.5;
    world.y = (texture(u_height, v_uv).r * 2 - 1) * 16;
    vec4 view_pos = u_view * world;
    v_position = view_pos.xyz;
    gl_Position = u_proj * view_pos;
}
