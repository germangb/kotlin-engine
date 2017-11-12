#ifdef LIGHTING_UTILS
#define SUN_DIR vec3(-4, 4, -2)

float shadow_contrib(sampler2D shadow_map, vec3 shadow_position) {
    float light = 1;
    vec2 uv = shadow_position.xy * 0.5 + 0.5;
    float bias = 0.001;

    vec2 texel = vec2(0.00125, 0.0);

    if (texture(shadow_map, uv + texel.yy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(shadow_map, uv + texel.xy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(shadow_map, uv - texel.xy).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(shadow_map, uv + texel.yx).r*2-1 < shadow_position.z - bias) light -= 1/5.0;
    if (texture(shadow_map, uv - texel.yx).r*2-1 < shadow_position.z - bias) light -= 1/5.0;

    return light;
}

#endif

#ifdef FOG_UTILS

vec3 fog(in vec3 color, in vec3 position) {
    vec3 fog_color = vec3(0.2);
    float exp = exp(-max(abs(position.z) * 0.04, 0.0));
    return mix(fog_color, color, clamp(exp, 0.05, 1));
}

#endif
