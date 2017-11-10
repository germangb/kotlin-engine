layout(location = 0) in vec2 a_position;
layout(location = 1) in mat4 a_transform;

out vec2 v_uv;
out vec3 v_normal;
out vec3 v_position;

// camera matrices
uniform mat4 u_proj;
uniform mat4 u_view;

// heightfield texture (1 channel, unsigned)
uniform sampler2D u_height;

// size of the heightmap
uniform float u_size;
uniform float u_max_height;

float sample_height(vec2 uv) {
    return (texture(u_height, uv) * 2 - 1) * u_max_height;
}

vec3 compute_normal(in vec2 uv) {
    vec2 dd = vec2(2. / u_size, 0.0);
    float sample0 = sample_height(uv);
    float sampley = sample_height(uv + dd.xy);
    float samplex = sample_height(uv + dd.yx);
    vec3 a = vec3(uv.x * u_size, sample0, uv.y * u_size * u_size);
    vec3 b = vec3(uv.x * u_size + dd.x * u_size, samplex, uv.y * u_size);
    vec3 c = vec3(uv.x * u_size, sampley, uv.y * u_size + dd.x * u_size);
    return normalize(cross(b-a, c-a));
}

void main() {
    vec4 model = a_transform * vec4(a_position.x, 0.0, a_position.y, 1);

    // sample height
    vec2 uv = model.xz / u_size + 0.5;
    float height = sample_height(uv);
    model.y = height;

    vec4 view_pos = u_view * model;
    gl_Position = u_proj * view_pos;

    v_uv = uv;
    v_normal = compute_normal(uv);
    v_position = view_pos.xyz;
}