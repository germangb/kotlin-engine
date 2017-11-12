in vec2 v_uv;
in vec3 v_normal;
in vec3 v_position;
in vec4 v_shadow_position;

out vec4 frag_color;
out vec4 normal_color;

uniform float u_size;
uniform sampler2D u_texture;
uniform sampler2D u_shadow_map;

#define FOG_UTILS
#define LIGHTING_UTILS
#include "shaders/utils.glsl"

float grid(float size, in vec2 uv) {
    float gridX = smoothstep(0, 0.1, abs(mod(uv.x * u_size - size/2, size) - size/2));
    float gridZ = smoothstep(0, 0.1, abs(mod(uv.y * u_size - size/2, size) - size/2));
    return min(gridX, gridZ);
}

void main() {
    vec3 color = v_normal * 0.5 + 0.5;
    color = vec3(1.0);

    color = mix(color, texture(u_texture, v_uv*128).rgb, 0.25);

    //color *= clamp(dot(v_normal, normalize(vec3(1, 4, 2))), 0.0, 1.0);

    float light = clamp(dot(v_normal, normalize(SUN_DIR)), 0.0, 1.0);

    // compute shadow
    float shadow = shadow_contrib(u_shadow_map, v_shadow_position.xyz);
    light = min(shadow, light);

    light = smoothstep(0.2, 0.8, light);
    light = mix(0.85, 1.0, light);
    color *= light;

    color = mix(color*0.85, color, grid(8, v_uv));


    color = fog(color, v_position);
    frag_color = vec4(color, 1.0);
    normal_color = vec4(v_normal * 0.5 + 0.5, 1.0);
}
