#version 150

struct LightProperties
{
    vec4 position;
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 specularColor;
};

struct MaterialProperties
{
    vec4 ambientColor;
    vec4 diffuseColor;
    vec4 ks;
    float ns;
    float transparency;
};

uniform	LightProperties u_light;
uniform	MaterialProperties u_material;

uniform sampler2D u_texture;
uniform sampler2D u_normalmap;
uniform sampler2D u_specmap;

uniform bool u_is_texture;
uniform bool u_is_sky;
uniform bool u_has_normal_map;
uniform bool u_has_spec_map;

in vec3 v_normal;
in vec3 v_eye;
in vec2 v_texcoord;
in vec4 v_position;

//////// FOG 
in float visibility;  
uniform vec3 skyColour;
/////////END FOG



out vec4 fragColor;

// WATER
uniform int is_water;
uniform float tempo;
const int ITER_GEOMETRY = 3;
const int ITER_FRAGMENT = 5;
const float SEA_HEIGHT = 0.6;
const float SEA_CHOPPY = 4.0;
const float SEA_SPEED = 0.8;
const float SEA_FREQ = 0.16;
const vec3 SEA_BASE = vec3(0.1,0.19,0.22);
const vec3 SEA_WATER_COLOR = vec3(0.8,0.9,0.6);
float SEA_TIME = tempo * SEA_SPEED;
mat2 octave_m = mat2(1.6,1.2,-1.2,1.6);

const int NUM_STEPS = 8;
const float PI	 	= 3.1415;
const float EPSILON	= 1e-3;
float EPSILON_NRM	= 0.1;


// math
mat3 fromEuler(vec3 ang) {
    vec2 a1 = vec2(sin(ang.x),cos(ang.x));
    vec2 a2 = vec2(sin(ang.y),cos(ang.y));
    vec2 a3 = vec2(sin(ang.z),cos(ang.z));
    mat3 m;
    m[0] = vec3(a1.y*a3.y+a1.x*a2.x*a3.x,a1.y*a2.x*a3.x+a3.y*a1.x,-a2.y*a3.x);
	m[1] = vec3(-a2.y*a1.x,a1.y*a2.y,a2.x);
	m[2] = vec3(a3.y*a1.x*a2.x+a1.y*a3.x,a1.x*a3.x-a1.y*a3.y*a2.x,a2.y*a3.y);
	return m;
}
float hash( vec2 p ) {
	float h = dot(p,vec2(127.1,311.7));	
    return fract(sin(h)*43758.5453123);
}
float noise( in vec2 p ) {
    vec2 i = floor( p );
    vec2 f = fract( p );	
	vec2 u = f*f*(3.0-2.0*f);
    return -1.0+2.0*mix( mix( hash( i + vec2(0.0,0.0) ), 
                     hash( i + vec2(1.0,0.0) ), u.x),
                mix( hash( i + vec2(0.0,1.0) ), 
                     hash( i + vec2(1.0,1.0) ), u.x), u.y);
}

// lighting
float diffuse(vec3 n,vec3 l,float p) {
    return pow(dot(n,l) * 0.4 + 0.6,p);
}
float specular(vec3 n,vec3 l,vec3 e,float s) {    
    float nrm = (s + 8.0) / (3.1415 * 8.0);
    return pow(max(dot(reflect(e,n),l),0.0),s) * nrm;
}



vec3 getSkyColor(vec3 e) {
    e.y = max(e.y,0.0);
    vec3 ret;
    ret.x = pow(1.0-e.y,2.0);
    ret.y = 1.0-e.y;
    ret.z = 0.6+(1.0-e.y)*0.4;
    return ret;
}
// sea
float sea_octave(vec2 uv, float choppy) {
    uv += noise(uv);        
    vec2 wv = 1.0-abs(sin(uv));
    vec2 swv = abs(cos(uv));    
    wv = mix(wv,swv,wv);
    return pow(1.0-pow(wv.x * wv.y,0.65),choppy);
}

float map(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;
    
    float d, h = 0.0;    
    for(int i = 0; i < ITER_GEOMETRY; i++) {        
    	d = sea_octave((uv+SEA_TIME)*freq,choppy);
    	d += sea_octave((uv-SEA_TIME)*freq,choppy);
        h += d * amp;        
    	uv *= octave_m; freq *= 1.9; amp *= 0.22;
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

float map_detailed(vec3 p) {
    float freq = SEA_FREQ;
    float amp = SEA_HEIGHT;
    float choppy = SEA_CHOPPY;
    vec2 uv = p.xz; uv.x *= 0.75;
    
    float d, h = 0.0;    
    for(int i = 0; i < ITER_FRAGMENT; i++) {        
    	d = sea_octave((uv+SEA_TIME)*freq,choppy);
    	d += sea_octave((uv-SEA_TIME)*freq,choppy);
        h += d * amp;        
    	uv *= octave_m; freq *= 1.9; amp *= 0.22;
        choppy = mix(choppy,1.0,0.2);
    }
    return p.y - h;
}

vec3 getSeaColor(vec3 p, vec3 n, vec3 l, vec3 eye, vec3 dist) {  
    float fresnel = 1.0 - max(dot(n,-eye),0.0);
    fresnel = pow(fresnel,3.0) * 0.65;
        
    vec3 reflected = getSkyColor(reflect(eye,n));    
    vec3 refracted = SEA_BASE + diffuse(n,l,80.0) * SEA_WATER_COLOR * 0.12; 
    
    vec3 color = mix(refracted,reflected,fresnel);
    
    float atten = max(1.0 - dot(dist,dist) * 0.001, 0.0);
    float val = min(max(v_position.y/30.0f,0.099f), 0.18f);
    //float val = 0.18;
    color += SEA_WATER_COLOR * (p.y - SEA_HEIGHT) * atten *val;
    
    color += vec3(specular(n,l,eye,60.0));
    
    return color;
}

// tracing
vec3 getNormal(vec3 p, float eps) {
    vec3 n;
    n.y = map_detailed(p);    
    n.x = map_detailed(vec3(p.x+eps,p.y,p.z)) - n.y;
    n.z = map_detailed(vec3(p.x,p.y,p.z+eps)) - n.y;
    n.y = eps;
    return normalize(n);
}

float heightMapTracing(vec3 ori, vec3 dir, out vec3 p) {  
    float tm = 0.0;
    float tx = 1000.0;    
    float hx = map(ori + dir * tx);
    if(hx > 0.0) return tx;   
    float hm = map(ori + dir * tm);    
    float tmid = 0.0;
    for(int i = 0; i < NUM_STEPS; i++) {
        tmid = mix(tm,tx, hm/(hm-hx));                   
        p = ori + dir * tmid;                   
    	float hmid = map(p);
		if(hmid < 0.0) {
        	tx = tmid;
            hx = hmid;
        } else {
            tm = tmid;
            hm = hmid;
        }
    }
    return tmid;
}
//END WATER

/////// TERRAIN
uniform bool is_terrain;
uniform sampler2D background;
uniform sampler2D rtexture;
uniform sampler2D gtexture;
uniform sampler2D btexture;
uniform sampler2D blendMap;
////////END TERRAIN


void main(void)
{
    vec4 diff_reflection = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 spec_reflection = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 color;
    vec3 N;
    //float dl = 0.0;
    //float att = 0.0;
    //float a0 = 1.0;
    //float a1 = 0.5;
    
    if(u_is_sky){
        color = texture(u_texture, v_texcoord);
    } else if(is_terrain){
        vec4 blendMapColour = texture(blendMap, v_texcoord);
        float bgTexAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
        vec2 tiledCoords  = v_texcoord * 40.0;
        /// Calculating color according to the blendMap
        vec4 bgTexColour  = texture(background, tiledCoords) * bgTexAmount;
        vec4 rTexColour   = texture(rtexture, tiledCoords)   * blendMapColour.r;
        vec4 gTexColour   = texture(gtexture, tiledCoords)   * blendMapColour.g;
        vec4 bTexColour   = texture(btexture, tiledCoords)   * blendMapColour.b;
        
        color = bgTexColour + rTexColour + gTexColour + bTexColour;
    }else {
         // Luz ambiente:
        color = u_light.ambientColor * u_material.ambientColor; // Ia * Ka

        // For each light : 
        if(u_has_normal_map){
          N = v_normal * normalize(texture(u_normalmap, v_texcoord).rgb * 2.0 - 1.0);
        }else {
          N = normalize(v_normal);
        }
   
        // Spotlight - we don't have it 
        //vec3 L = normalize(vec3( v_light_position - v_position ));
        vec3 L = normalize(vec3(0.0, -0.5, -1.0)); // Diretional light (sun)

        // 0.4 -> a cena não ficar muito escura (Ambiente Aberto)
        float nDotL = max(dot(N, L), 0.5); // cos(THETA)

        if (nDotL > 0.0) // Se o cos THETA > 0.0 => Calcular reflexao
        {
            // If it has diffuse map, use it
            if(u_is_texture) {
                diff_reflection = u_light.diffuseColor * texture(u_texture, v_texcoord) * nDotL;
            } else {
                //                       Id            *             Kd          * (N.L)
                diff_reflection = u_light.diffuseColor * u_material.diffuseColor * nDotL;
            }

          //////////////////////////////////////////////////////////////////// 
          // Specular reflection
          ////////////////////////////////////////////////////////////////////
            vec3 V = normalize(v_eye);

            // Incident vector is opposite light L vector.
            vec3 R = normalize(reflect(-L, N));

            float vDotR = max(dot(V, R), 0.0);

            float specularIntensity = 0.0;
            spec_reflection = vec4(0.0, 0.0, 0.0, 0.0);
            if (vDotR > 0.0) {
                specularIntensity = pow(vDotR, u_material.ns);
                
                // If it has specular map, use it
                if (u_has_spec_map){
                  spec_reflection = u_light.specularColor * texture(u_specmap, v_texcoord) * specularIntensity;
                } else {
                  spec_reflection = u_light.specularColor * u_material.ks * specularIntensity;
                }
            }

            // Attenuation factor: We don't use (Sun = directional light without attenuation)
            // dl = max(distance(v_position, v_light_position), 0.0);
            // att = 1/(a0 + a1*dl);
            // color += att * (diff_reflection + spec_reflection);
            color += (diff_reflection + spec_reflection);
        }

        // To avoid black borders around the leaves of the trees
        if(u_is_texture) {
            color.a = texture(u_texture, v_texcoord).a;
            // "Transparency test"
            if(color.a < 0.7){
                discard;
            }
        }else {
            color.a = u_material.transparency;        
        }
    }
    

    // WATER
     if (is_water == 1)
    {
        vec3 ang;
        vec3 ori;
        vec3 dir;
        ori = vec3(v_eye.x, max(v_eye.y, 2.5)/5.0, v_eye.z);//origem do raio
        dir = normalize(vec3(v_position.x, v_position.y, v_position.z));//direcao do raio
        ang = vec3(clamp((dir.y - ori.y)/(dir.x-ori.x), 0.2, 0.8), clamp((dir.z - ori.z)/(dir.y-ori.y), 0.2, 0.8), clamp((dir.x - ori.x)/(dir.z-ori.z), 0.2, 0.8));
        dir = normalize(dir) * fromEuler(ang);

 //       tracing
        vec3 p;
        heightMapTracing(ori,dir,p);
        vec3 dist = p - ori;
        vec3 n = getNormal(p, dot(dist,dist) * EPSILON_NRM);
        vec3 light = normalize(vec3(0.0,1.0,0.8)); 

   //     color
       vec3 color = mix(
           getSkyColor(dir),
           getSeaColor(p,n,light,dir,dist),
           pow(smoothstep(0.0,-0.05,dir.y),0.3));

     //   post
        fragColor = clamp(vec4(pow(color,vec3(0.75)), 1.0), 0.0, 1.0);
    }
    else {
      //if(!u_is_sky)color = mix(vec4(skyColour, 1.0), color, visibility);
      fragColor = clamp(color, 0.0, 1.0);
    }
}