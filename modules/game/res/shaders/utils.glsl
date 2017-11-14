#ifdef LIGHT_UTILS

float shadow_contrib(in vec3 shadow_position, in sampler2D depth) {
    vec2 uv = shadow_position.xy * 0.5 + 0.5;
    float light = 1;
    if (abs(shadow_position.x) < 1 && abs(shadow_position.y) < 1) {
        float sampled_depth = texture(depth, uv).r * 2 - 1;
        float bias = 0.0001;
        if (sampled_depth < shadow_position.z - bias)
           light = 0;
    }
    return light;
}

#endif

#ifdef FOG_UTILS

vec3 fog(in vec3 color, in vec3 world_position) {
    vec3 fog_color = vec3(0.2);
    return mix(color, fog_color, clamp(abs(world_position.z) / 32 - 0.1, 0, 0.8));
}

#endif

#ifdef TERRAIN_UTILS

vec3 compute_normal(in vec2 v_uv, sampler2D map) {
    return vec3(0, 1, 0);
}

#endif