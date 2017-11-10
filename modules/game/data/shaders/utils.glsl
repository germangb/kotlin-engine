#ifdef LIGHTING_UTILS
#define SUN_DIR vec3(-1, 4, 2)
#endif

#ifdef FOG_UTILS

vec3 fog(in vec3 color, in vec3 position) {
    vec3 fog_color = vec3(0.2);
    float exp = exp(-max(abs(position.z) * 0.04, 0.0));
    return mix(fog_color, color, clamp(exp, 0.05, 1));
}

#endif
