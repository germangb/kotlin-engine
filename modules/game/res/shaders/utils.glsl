#ifdef FOG_UTILS

vec3 fog(in vec3 color, in vec3 world_position) {
    vec3 fog_color = vec3(0.2);
    return mix(color, fog_color, clamp(abs(world_position.z) / 32 - 0.1, 0, 0.8));
}

#endif