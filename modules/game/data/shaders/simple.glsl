#ifdef VERTEX_SHADER
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_uv;
layout(location = 3) in mat4 a_transform;

out vec2 v_uv;
out vec3 v_normal;
out vec3 v_position;

uniform mat4 u_proj;
uniform mat4 u_view;

void main() {
    mat4 model_view = u_view * a_transform;
    vec4 model_view_position = model_view * vec4(a_position, 1.0);
    gl_Position = u_proj * model_view_position;
    v_normal = normalize((model_view * vec4(a_normal, 0.0)).xyz);
    v_position = model_view_position.xyz;
    v_uv = a_uv;
}
#endif

#ifdef FRAGMENT_SHADER
in vec2 v_uv;
in vec3 v_normal;
in vec3 v_position;

out vec4 frag_color;

#define FOG_UTILS
#include "shaders/utils.glsl"

void main() {
    float light = clamp(dot(v_normal, vec3(0, 0, 1)), 0.0, 1.0);
    light = clamp(0.75, 1.0, light);
    vec3 color = fog(vec3(light), v_position);
    frag_color = vec4(color, 1.0);
}
#endif