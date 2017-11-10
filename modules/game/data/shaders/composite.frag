in vec2 v_uv;

out vec4 frag_color;

uniform sampler2D u_texture;
uniform sampler2D u_normal;

float sample_normal(in vec2 uv) {
    uv.x = clamp(uv.x, 0, 1);
    uv.y = clamp(uv.y, 0, 1);
    vec3 n =  texture(u_normal, uv).rgb;
    return dot(n, vec3(0.2126, 0.7152, 0.0722));
}

float sobel(in vec2 uv) {
    vec3 tex = vec3(1.25 / vec2(720, 480), 0.0);
    float dx = abs(sample_normal(uv + tex.xz) - sample_normal(uv - tex.xz));
    float dy = abs(sample_normal(uv + tex.zy) - sample_normal(uv - tex.zy));
    float edge = (dx + dy) / 2;
    return smoothstep(0, 1, 1 - clamp(edge, 0.0, 1.0));
}

void main() {
    vec3 color = texture(u_texture, v_uv).rgb;
    frag_color = vec4(color*sobel(v_uv)*vec3(0.92, 0.89, 0.75), 1.0);
}
