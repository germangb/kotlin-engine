#ifdef VERTEX_SHADER
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_uv;
layout(location = 3) in mat4 a_transform;

out vec2 v_uv;
out vec3 v_normal;
out vec3 v_position;
out vec4 v_shadow_position;

uniform mat4 u_proj;
uniform mat4 u_view;
uniform mat4 u_shadow_transform;

void main() {
    mat4 model_view = u_view * a_transform;
    vec4 model_view_position = model_view * vec4(a_position, 1.0);
    gl_Position = u_proj * model_view_position;
    v_normal = normalize((a_transform * vec4(a_normal, 0.0)).xyz);
    v_position = model_view_position.xyz;
    v_shadow_position = u_shadow_transform * a_transform * vec4(a_position, 1.0);
    v_uv = a_uv;
}
#endif

#ifdef FRAGMENT_SHADER
in vec2 v_uv;
in vec3 v_normal;
in vec3 v_position;
in vec4 v_shadow_position;

out vec4 frag_color;
out vec4 normal_color;

#define FOG_UTILS
#define LIGHTING_UTILS
#include "shaders/utils.glsl"

uniform sampler2D u_shadow_map;

void main() {
    //float light = clamp(dot(v_normal, vec3(0, 0, 1)), 0.0, 1.0);
    float light = clamp(dot(v_normal, normalize(SUN_DIR)), 0.0, 1.0);

    // compute shadow
    float shadow = shadow_contrib(u_shadow_map, v_shadow_position.xyz);
    light = min(shadow, light);

    light = mix(0.85, 1.0, light);

    vec3 color = fog(vec3(1)*light, v_position);
    frag_color = vec4(color, 1.0);
    normal_color = vec4(v_normal * 0.5 + 0.5, 1.0);
}
#endif