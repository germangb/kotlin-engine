layout(location = 0) in vec2 a_position;
layout(location = 1) in mat4 a_transform;

out vec2 v_uv;
out vec3 v_position;
out vec3 v_normal;

uniform mat4 u_view;
uniform mat4 u_proj;
uniform float u_size;

uniform float u_max_height;
uniform sampler2D u_height;

#define TERRAIN_UTILS
#include "shaders/utils.glsl"

void main() {
    vec4 world = a_transform * vec4(a_position.x, 0.0, a_position.y, 1.0);
    vec2 uv = world.xz / u_size + 0.5;
    v_uv = uv;
    world.y = (texture(u_height, v_uv).r * 2 - 1) * u_max_height;
    vec4 view_pos = u_view * world;
    v_position = view_pos.xyz;
    v_normal = compute_normal(uv, u_height);
    gl_Position = u_proj * view_pos;
}
