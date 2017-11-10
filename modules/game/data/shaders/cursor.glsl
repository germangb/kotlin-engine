#ifdef VERTEX_SHADER
layout(location = 0) in vec2 a_position;
layout(location = 1) in vec2 a_uv;

out vec2 v_uv;

uniform mat4 u_transform;
uniform vec2 u_position;

void main () {
    gl_Position = u_transform * vec4(a_position * 16 + u_position, 0.0, 1.0);
    v_uv = a_uv;
}
#endif

#ifdef FRAGMENT_SHADER
in vec2 v_uv;

out vec4 frag_color;

uniform sampler2D u_texture;

void main() {
    vec3 val = texture(u_texture, v_uv).rgb;
    if (val == vec3(0, 1, 0)) discard;
    frag_color = vec4(val, 1.0);
}
#endif
