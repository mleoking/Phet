uniform mat4 g_WorldViewProjectionMatrix;

attribute vec4 inPosition;
attribute vec4 inColor;
attribute vec2 inTexCoord;

varying vec2 texCoord;
varying vec4 color;

void main() {
    vec2 pos = (g_WorldViewProjectionMatrix * inPosition).xy;
    gl_Position = vec4(pos, 0.0, 1.0);

    texCoord = inTexCoord;
    color = inColor;
}