in vec2 v_uv;

out vec4 frag_color;

uniform sampler2D u_texture;

void main() {
    vec3 color = texture(u_texture, v_uv).rgb;
    frag_color = vec4(color*vec3(0.92, 0.89, 0.75), 1.0);
}
