#ifdef LIGHTING_UTILS
#ifndef LIGHTING_UTILS_GLSL
#define LIGHTING_UTILS_GLSL
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

#endif // LIGHTING_UTILS_GLSL
#endif // LIGHTING_UTILS

/* ------------------------------------------------------------------------------------------------------------------ */

#ifdef FOG_UTILS
#ifndef FOG_UTILS_GLSL
#define FOG_UTILS_GLSL

vec3 fog(in vec3 color, in vec3 position) {
    vec3 fog_color = vec3(0.2);
    float exp = exp(-max(abs(position.z) * 0.03, 0.0));
    return mix(fog_color, color, clamp(exp, 0.05, 1));
}

#endif // FOG_UTILS_GLSL
#endif // FOG_UTILS

/* ------------------------------------------------------------------------------------------------------------------ */

#ifdef SSAO
#ifndef SSAO_GLSL
#define SSAO_GLSL

uniform vec3 u_samples[16] = {
    vec3(0.037177928,0.0044654296,0.029933896),
    vec3(-0.24385965,0.60051495,-0.28419802),
    vec3(0.17040135,0.0470007,0.41519555),
    vec3(-0.28103375,0.40869126,-0.4802908),
    vec3(-0.07622337,0.7991029,0.5932831),
    vec3(0.42802635,0.07755524,-0.06482649),
    vec3(0.3692872,0.6875985,-0.16762063),
    vec3(-0.3168595,0.07392933,0.09266477),
    vec3(-0.23339422,0.100786276,0.26212963),
    vec3(-0.25795275,0.062427703,0.06880221),
    vec3(0.04867975,0.062343445,0.013762419),
    vec3(0.23781206,0.13471504,0.7503907),
    vec3(-0.18128182,0.061228994,-0.054967016),
    vec3(0.7448471,0.012968527,-0.1929226),
    vec3(0.484571,0.041223455,0.21814911),
    vec3(-0.008805875,0.19797431,-0.09444523)
};

float compute_ao(in vec2 uv, in sampler2D depth, in sampler2D normals, in mat4 projection, in mat4 inv_projection) {
    // reconstruct view position
    vec4 clip = vec4(uv * 2 - 1, texture(depth, uv).r * 2 - 1, 1.0);
    vec4 world = inv_projection * clip;

    // get ss normal
    vec3 normal = texture(normals, uv).rgb * 2 - 1;

    // compute ssao
    float ao = 1;
    for (int i = 0; i < 16; ++i) {
        // transform sample
        vec3 tan = vec3(1, 1, 1);
        tan = tan - normal * dot(tan, normal);

        mat3 transf = mat3(normalize(tan), normalize(normal), normalize(cross(normal, tan)));
        vec4 offset = world + vec4(transf * u_samples[i] * 0.005, 0.0);

        // project result
        vec4 result = projection * offset;
        result.xyz /= result.w;

        // sample depth and compare with computed z
        float depth_sample = texture(depth, result.xy * 0.5 + 0.5).r * 2 - 1;
        if (depth_sample < result.z - 0.000025) ao -= 1/16.0;
        //if (depth_sample < result.z - 0.00025) ao -= 1/16.0;
        //if (depth_sample < result.z - 0.0005) ao -= 1/16.0;

    }
    return ao;
}

#endif // SSAO_GLSL
#endif // SSAO
