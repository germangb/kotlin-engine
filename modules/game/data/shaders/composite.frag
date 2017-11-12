in vec2 v_uv;

out vec4 frag_color;

uniform sampler2D u_texture;
uniform sampler2D u_normal;

// ao
uniform sampler2D u_depth;
uniform mat4 u_inv_projection;
uniform mat4 u_projection;

#define SSAO
#include "shaders/utils.glsl"

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
    vec3 tint = vec3(0.92, 0.89, 0.85);
    tint *= vec3(0.95, 0.89, 0.89);

    float sobel_out = sobel(v_uv);


    float ao = compute_ao(v_uv, u_depth, u_normal, u_projection, u_inv_projection);
    //color = vec3(mix(color, vec3(1), 0.8) * ao);

    //color = vec3(texture(u_depth, v_uv).r, 0, 0);
    color = color * mix(0.25, 1.0, sobel_out * ao) * tint;

    //color = floor(color * 100) / 100;

    frag_color = vec4(color, 1.0);
}
