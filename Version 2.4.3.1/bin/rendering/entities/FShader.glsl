#version 400 core

in vec3 pass_textureCoordinates;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec3 passSurfaceNormal;
in float useNormalMap;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 out_BrightColor;

uniform sampler2D modelTexture;
uniform sampler2D normalMap;
uniform sampler2D specularMap;

uniform float useSpecularMap;

uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

uniform float useMaterial;


void main(void){

	vec3 unitNormal;
	if(useNormalMap > 0){
		vec4 normalMapValue = 2.0 * texture(normalMap, pass_textureCoordinates.xy) - 1.0;
		unitNormal = normalize(normalMapValue.rgb);
	}else{
		unitNormal = normalize(passSurfaceNormal);
	}
	//out_Color = vec4(passSurfaceNormal,1.0);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i=0;i<4;i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);	
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);
	
	vec4 textureColour;

	if(useMaterial > 0){
		textureColour = vec4(pass_textureCoordinates,1.0);
	}else{
		textureColour = texture(modelTexture,pass_textureCoordinates.xy);
		if(textureColour.a<0.5){
			discard;
		}
	}
	out_BrightColor = vec4(0.0);
	
	if(useSpecularMap > 0 && useMaterial == 0){
		vec4 mapInfo = texture(specularMap, pass_textureCoordinates.xy);
		totalSpecular *= mapInfo.r;
		if(mapInfo.g > 0.5){
			out_BrightColor = textureColour + vec4(totalSpecular,1.0);
			totalDiffuse = vec3(1.0);
		}
	}
	
	out_Color =  vec4(totalDiffuse,1.0) * textureColour + vec4(totalSpecular,1.0);
	out_Color = mix(vec4(skyColour,1.0),out_Color, visibility);
	
}
