#define FOG_UTILS
#define LIGHT_UTILS
#include "shaders/utils.glsl"

in vec2 v_uv;
in vec3 v_position;
in vec3 v_normal;
in vec4 v_shadow_position;

out vec4 frag_color;

uniform sampler2D u_texture;
uniform sampler2D u_depth;

void main() {
    vec3 color = texture(u_texture, v_uv * 32).rgb;
    float light = clamp(dot(v_normal, normalize(vec3(0, 1, 0))), 0.0, 1.0);

    // compute shader
    float shadow = shadow_contrib(v_shadow_position.xyz, u_depth);
    light = min(light, shadow);

    light = mix(0.5, 1, light);

    color.rgb = fog(color.rgb * light, v_position);
    frag_color = vec4(color, 1.0);
}
