#version 400 core

const int MAX_TRANSFORMS = 75;
const int MAX_WEIGHTS = 3;

in vec3 position;
in vec3 textureCoordinates;
in vec3 normal;
in vec3 tangent;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec3 pass_textureCoordinates;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out vec3 passSurfaceNormal;
out float useNormalMap;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];

uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

uniform float density = 0.0007;
uniform float gradient = 1.5;

uniform vec4 plane;

uniform float useAnimation;
uniform mat4 jointTransforms[MAX_TRANSFORMS];

void main(void){

	vec3 actualNormal = normal;
	vec3 actualPosition = position;

	if(useAnimation>0.5){
		vec4 totalPosition = vec4(0.0);
		vec4 totalNormal = vec4(0.0);
		for(int i=0;i<MAX_WEIGHTS;i++){
			mat4 jointTransform = jointTransforms[in_jointIndices[i]];
			vec4 posePosition = jointTransform * vec4(position, 1.0);
			totalPosition += posePosition * in_weights[i];

			vec4 worldNormal = jointTransform * vec4(normal, 0.0);
			totalNormal += worldNormal * in_weights[i];
		}
		actualNormal=totalNormal.xyz;
		actualPosition = totalPosition.xyz;
	}

	vec4 worldPosition = transformationMatrix * vec4(actualPosition, 1.0);


	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	
	pass_textureCoordinates = (textureCoordinates/numberOfRows) + vec3(offset,0);
	
	if(useFakeLighting > 0.5){
		actualNormal = vec3(0.0,1.0,0.0);
	}
	vec3 surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
	
	passSurfaceNormal = surfaceNormal;
	
	for(int i=0;i<4;i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility,0.0,1.0);
	
	useNormalMap=0.0;
	
}
