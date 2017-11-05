layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_uv;
layout(location = 3) in ivec4 a_bones;
layout(location = 4) in vec4 a_weights;

out vec2 v_uv;
out vec3 v_normal;

layout (std140) uniform u_transforms{
    mat4 projection;
    mat4 view;
    mat4 skin[110];
};

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_skin[110];

void main () {
    mat4 u_skin_transform = u_skin[a_bones.x] * a_weights.x +
                            u_skin[a_bones.y] * a_weights.y +
                            u_skin[a_bones.z] * a_weights.z +
                            u_skin[a_bones.w] * a_weights.w;
    mat4 model = u_skin_transform;
    gl_Position = u_projection * u_view * model * vec4(a_position, 1.0);
    v_normal = normalize((model * vec4(a_normal, 0.0)).xyz);
    v_uv = a_uv;
}
