#ifdef VERTEX_SHADER
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_uv;

out vec3 v_normal;

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_model;

void main() {
    mat4 mv = u_view * u_model;
    gl_Position = u_projection * mv * vec4(a_position, 1.0);
    v_normal = normalize((mv * vec4(a_position, 0.0)).xyz);
}
#endif

#ifdef FRAGMENT_SHADER
in vec3 v_normal;

out vec4 frag_color;

void main() {
    vec3 light_dir = vec3(1, 3, 2);

    float light = dot(normalize(light_dir), v_normal) * 0.5 + 0.5;
    float light_top = dot(normalize(vec3(0, -2, -1)), v_normal) * 0.5 + 0.5;

    light = mix(0.2, 1, light);

    vec3 color = vec3(1.0, 1.0, 1.0) * pow(light, 2);

    frag_color = vec4(color, 1);
}
#endif