#version 400 core

in vec3 position;
in vec3 normal;
in float textureColour;
in vec2 textureCoords;

out float[10] textureColours;
out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform vec3 attenuation[4];

uniform mat4 toShadowMapSpace;

uniform float density = 0.0007; //closer/farther
uniform float gradient = 1.5;
const float shadowDistance = 150.0;
const float transitionDistance = 10.0;
const int testAllTex = 3<<20;
const int testSingleTex = 1<<20;

uniform vec4 plane;

void main(void){
	
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	
	shadowCoords = toShadowMapSpace * worldPosition;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	
	int locations = int(textureColour + 0.5);
	textureColours = float[10](0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
	float sum = 0.0;
	
	int testTextures = locations & testAllTex;
	
	if(testTextures == testAllTex){
		for(int i=0; i<10; i++){
			textureColours[i] = 1.0;
			sum+=1.0;
		}
	}else if(testTextures == testSingleTex){
		for(int i =0; i<10; i++){
			int testLoc = 3 << (i*2);
			if((locations & testLoc) > 0){
				textureColours[i] = 1.0;
				break;
			}
		}
		sum+=1.0;
	}else{
		for(int i =0; i<10; i++){
			int testLoc = 3 << (i*2);
			int test = locations & testLoc;
			if(test > 0){
				if(test == testLoc){
					textureColours[i] = 1.0;
					sum += 1.0;
				}else if(test == (2 << (i*2))){
					textureColours[i] = 0.5;
					sum += 0.5;
				}else{
					textureColours[i] = 0.25;
					sum+= 0.25;
				}
			}
		}
	}
	
	float factor = 1./sum;
	for(int i=0; i<10; i++){
		textureColours[i] *= factor;
	}
	
	pass_textureCoords=textureCoords;
	
	surfaceNormal = (transformationMatrix * vec4(normal,0.0)).xyz;
	
	for(int i=0; i<4; i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility,0.0,1.0);
	
	distance = distance - (shadowDistance - transitionDistance);
	distance = distance / transitionDistance;
	shadowCoords.w = clamp(1.0-distance, 0.0, 1.0);

}