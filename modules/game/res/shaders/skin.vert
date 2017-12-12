#define INPUT(index) layout(location=index) in
#define OUTPUT out
#define float2 vec2
#define float3 vec3
#define float4 vec4
#define int4 ivec4

INPUT(0) float3 a_position;
INPUT(1) float3 a_normal;
INPUT(2) float2 a_uv;
INPUT(3) int4 a_bones;
INPUT(4) float4 a_weights;

out vec2 v_uv;
out vec3 v_normal;
out vec3 v_normal_view;
out vec3 v_position;
out vec4 v_shadow_position;

layout (std140) uniform u_transforms{
    mat4 projection;
    mat4 view;
    mat4 skin[110];
};

uniform mat4 u_projection;
uniform mat4 u_view;
uniform mat4 u_skin[110];
uniform mat4 u_shadow_viewproj;

void main () {
    mat4 u_skin_transform = u_skin[a_bones.x] * a_weights.x +
                            u_skin[a_bones.y] * a_weights.y +
                            u_skin[a_bones.z] * a_weights.z +
                            u_skin[a_bones.w] * a_weights.w;
    mat4 model = u_skin_transform;
    gl_Position = u_projection * u_view * model * float4(a_position, 1.0);
    v_shadow_position = u_shadow_viewproj * model * float4(a_position, 1.0);
    v_normal = normalize((model * float4(a_normal, 0.0)).xyz);
    v_normal_view = normalize((u_view * model * float4(a_normal, 0.0)).xyz);
    v_position = (u_view * model * float4(a_position, 1.0)).xyz;
    v_uv = a_uv;
}
