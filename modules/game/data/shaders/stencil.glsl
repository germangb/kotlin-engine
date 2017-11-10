#ifdef VERTEX_SHADER
layout(location = 0) in vec2 a_position;
void main() {
    gl_Position = vec4(a_position * 2 - 1, 0.0, 1.0);
}
#endif

#ifdef FRAGMENT_SHADER
out vec4 frag_color;

void main() {
    frag_color = vec4(0.79, 0.5, 1, 1);
}
#endif