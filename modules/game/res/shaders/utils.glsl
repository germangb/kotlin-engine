#ifdef LIGHT_UTILS

#define LIGHT_DIR vec3(1, 1, 1)

float shadow_contrib(in vec3 shadow_position, in sampler2D depth) {
    if (abs(shadow_position.x) > .999 || abs(shadow_position.y) > .999) {
        return 1.0;
    }

    float light = 1;
    vec2 uv = shadow_position.xy * 0.5 + 0.5;
    float bias = 0.001;

    vec2 texel = vec2(1/1024., 0.0);
    if (texture(depth, uv + texel.yy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(depth, uv + texel.xy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(depth, uv - texel.xy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(depth, uv + texel.yx).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(depth, uv - texel.yx).r*2-1 < shadow_position.z - bias) light -= 1/5.0;

    return light;
}

float rim(vec3 view_normal) {
    float nor = dot(view_normal, vec3(0, 0, 1));
    return smoothstep(1, -1, nor);
}

#endif

#ifdef FOG_UTILS

vec3 fog(in vec3 color, in vec3 position) {
    vec3 fog_color = vec3(0.2);
    float exp = exp(-max(abs(position.z) * 0.03, 0.0));
    return mix(fog_color, color, clamp(exp, 0.07, 1));
}

#endif

#ifdef TERRAIN_UTILS

vec3 compute_normal(in vec2 v_uv, sampler2D map) {
    return vec3(0, 1, 0);
}

#endif