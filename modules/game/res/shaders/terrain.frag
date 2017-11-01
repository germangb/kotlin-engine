#version 450 core

#define FOG_UTILS
#include "shaders/utils.glsl"

in vec2 v_uv;
in vec3 v_position;
in vec3 v_normal;

out vec4 frag_color;

uniform sampler2D u_texture;

void main() {
    vec3 color = texture(u_texture, v_uv * 32).rgb;
    float light = clamp(dot(v_normal, normalize(vec3(0, 1, 0))), 0.0, 1.0);

    color.rgb = fog(color.rgb, v_position);
    frag_color = vec4(color, 1.0);
}
