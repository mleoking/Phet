uniform vec4 m_Color;

#define minDensityToShow 2500.0
#define maxDensityToShow 3500.0

varying float density;

void main(){
    vec4 color = vec4( 1.0 );

    color *= m_Color;

    float x = 100.0 + ( 1.0 - ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow ) ) * ( 155.0 );
    x = clamp( x / 255.0, 0.0, 1.0 ); // clamp it in the normal range

    gl_FragColor = vec4( x, x, x, 1.0 );
}