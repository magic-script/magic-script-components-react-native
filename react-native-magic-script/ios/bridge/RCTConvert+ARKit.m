//
//  RCTConvert+ARKit.m
//  RCTARKit
//
//  Created by Zehao Li on 9/28/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

#import "RCTConvert+ARKit.h"
#import <React/RCTImageSource.h>

@implementation RCTConvert (ARKit)

+ (SCNMaterial *)SCNMaterial:(id)json {
    SCNMaterial *material = [SCNMaterial new];
    [self setMaterialProperties:material properties:json];
    return material;
}

+ (SCNVector3)SCNVector3:(id)json {
    const CGFloat x = [json[@"x"] floatValue];
    const CGFloat y = [json[@"y"] floatValue];
    const CGFloat z = [json[@"z"] floatValue];
    return SCNVector3Make(x, y, z);
}

+ (SCNVector4)SCNVector4:(id)json {
    const CGFloat x = [json[@"x"] floatValue];
    const CGFloat y = [json[@"y"] floatValue];
    const CGFloat z = [json[@"z"] floatValue];
    const CGFloat w = [json[@"w"] floatValue];
    return SCNVector4Make(x, y, z, w);
}

+ (SCNNode *)SCNNode:(id)json {
    SCNNode *node = [SCNNode new];
    
    node.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    [self setNodeProperties:node properties:json];
    
    return node;
}

+ (void)addMaterials:(SCNGeometry *)geometry json:(id)json sides:(int) sides {
    SCNMaterial *material = [self SCNMaterial:json[@"material"]];
    
    NSMutableArray *materials = [NSMutableArray array];
    for (int i = 0; i < sides; i++)
        [materials addObject: material];
    geometry.materials = materials;
}

+ (SCNTextNode *)SCNTextNode:(id)json {
    // init SCNText
    NSString *text = [NSString stringWithFormat:@"%@", json[@"text"]];
    if (!text) {
        text = @"(null)";
    }
    
    NSDictionary* font = json[@"font"];
    CGFloat depth = [font[@"depth"] floatValue];
    if (!depth) {
        depth = 0.0f;
    }
    CGFloat fontSize = [font[@"size"] floatValue];
    CGFloat size = fontSize / 12;
    SCNText *scnText = [SCNText textWithString:text extrusionDepth:depth / size];
    
    scnText.flatness = 0.1;
    
    // font
    NSString *fontName = font[@"name"];
    if (fontName) {
        scnText.font = [UIFont fontWithName:fontName size:12];
    } else {
        scnText.font = [UIFont systemFontOfSize:12];
    }
    
    // chamfer
    CGFloat chamfer = [font[@"chamfer"] floatValue];
    if (!chamfer) {
        chamfer = 0.0f;
    }
    scnText.chamferRadius = chamfer / size;
    
    // material
    //    scnText.materials = @[face, face, border, border, border];
    [self addMaterials:scnText json:json sides:5];
    
    
    // SCNTextNode
    SCNTextNode *textNode = [SCNNode nodeWithGeometry:scnText];
    textNode.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    
    
    textNode.scale = SCNVector3Make(size, size, size);
    
    // position textNode
    SCNVector3 min = SCNVector3Zero;
    SCNVector3 max = SCNVector3Zero;
    [textNode getBoundingBoxMin:&min max:&max];
    
    textNode.position = SCNVector3Make(-(min.x + max.x) / 2 * size,
                                       -(min.y + max.y) / 2 * size,
                                       -(min.z + max.z) / 2 * size);
    
    return textNode;
}

+ (UiGroupNode *)UiGroupNode:(id)json {
    UiGroupNode *groupNode = [UiGroupNode new];
    [self setNodeProperties:groupNode properties:json];
    groupNode.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    return groupNode;
}

+ (UiButtonNode *)UiButtonNode:(id)json {
    NSString *title = [NSString stringWithFormat:@"%@", json[@"title"]];
    if (!title) {
        title = @"(null)";
    }

    NSDictionary *sizeDict = json[@"size"];
    CGSize size = CGSizeMake(2.f, 1.f);
    if (sizeDict) {
        size.width = [sizeDict[@"width"] floatValue];
        size.height = [sizeDict[@"height"] floatValue];
    }

    UIColor *color = json[@"color"] ? [self UIColor:json[@"color"]] : UIColor.whiteColor;

    UiButtonNode *buttonNode = [UiButtonNode new];
    [self setNodeProperties:buttonNode properties:json];
    buttonNode.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    buttonNode.title = title;
    buttonNode.size = size;
    buttonNode.color = color;

    return buttonNode;
}

+ (UiImageNode *)UiImageNode:(id)json {
    UiImageNode *imageNode = [UiImageNode new];
    [self setNodeProperties:imageNode properties:json];
    imageNode.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    imageNode.size = [self CGSize:json[@"size"]];

    RCTImageSource *source = [self RCTImageSource:json[@"source"]];
    imageNode.URL = source.request.URL;

    return imageNode;
}

+ (UiTextNode *)UiTextNode:(id)json {
    NSString *text = [NSString stringWithFormat:@"%@", json[@"text"]];
    if (!text) {
        text = @"(null)";
    }

    NSDictionary *sizeDict = json[@"boundsSize"];
    CGSize size = CGSizeMake(2.f, 1.f);
    if (sizeDict) {
        size.width = [sizeDict[@"x"] floatValue];
        size.height = [sizeDict[@"y"] floatValue];
    }

    UIColor *textColor = json[@"textColor"] ? [self UIColor:json[@"textColor"]] : UIColor.whiteColor;

    UiTextNode *textNode = [UiTextNode new];
    [self setNodeProperties:textNode properties:json];
    textNode.name = [NSString stringWithFormat:@"%@", json[@"id"]];
    textNode.text = text;
//    textNode.font = font;
    textNode.boundsSize = size;
    textNode.textColor = textColor;

    return textNode;
}

+ (void)setMaterialPropertyContents:(id)property material:(SCNMaterialProperty *)material {
    
    if (property[@"path"]) {
        SCNMatrix4 m = SCNMatrix4Identity;
        
        // scenekit has an issue with indexed-colour png's on some devices, so we redraw the image. See for more details: https://stackoverflow.com/questions/40058359/scenekit-some-textures-have-a-red-hue/45824190#45824190
        
        UIImage *correctedImage;
        UIImage *inputImage = [UIImage imageNamed:property[@"path"]];
        CGFloat width  = inputImage.size.width;
        CGFloat height = inputImage.size.height;
        
        UIGraphicsBeginImageContext(inputImage.size);
        [inputImage drawInRect:(CGRectMake(0, 0, width, height))];
        correctedImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        
        material.contents = correctedImage;

        
        if (property[@"wrapS"]) {
            material.wrapS = (SCNWrapMode) [property[@"wrapS"] integerValue];
        }
        
        if (property[@"wrapT"]) {
            material.wrapT = (SCNWrapMode) [property[@"wrapT"] integerValue];
        }
        
        if (property[@"wrap"]) {
            material.wrapT = (SCNWrapMode) [property[@"wrapT"] integerValue];
            material.wrapS = (SCNWrapMode) [property[@"wrapS"] integerValue];
        }
        
        if (property[@"scale"]) {
            float x = [property[@"scale"][@"x"] floatValue];
            float y = [property[@"scale"][@"y"] floatValue];
            float z = [property[@"scale"][@"z"] floatValue];
            
            m = SCNMatrix4Mult(m, SCNMatrix4MakeScale(x, y, z));
        }
        
        if (property[@"rotation"]) {
            float a = [property[@"rotation"][@"angle"] floatValue];
            float x = [property[@"rotation"][@"x"] floatValue];
            float y = [property[@"rotation"][@"y"] floatValue];
            float z = [property[@"rotation"][@"z"] floatValue];
            
            m = SCNMatrix4Mult(m, SCNMatrix4MakeRotation(a, x, y, z));
        }
        
        if (property[@"translation"]) {
            float x = [property[@"translation"][@"x"] floatValue];
            float y = [property[@"translation"][@"y"] floatValue];
            float z = [property[@"translation"][@"z"] floatValue];
            
            m = SCNMatrix4Mult(m, SCNMatrix4MakeTranslation(x, y, z));
        }
        
        material.contentsTransform = m;
        
        
    } else if (property[@"color"]) {
        material.contents = [self UIColor:property[@"color"]];
    }
    if (property[@"intensity"]) {
        material.intensity = [property[@"intensity"] floatValue];
    }
}

+ (void)setMaterialProperties:(SCNMaterial *)material properties:(id)json {
    if (json[@"doubleSided"]) {
        material.doubleSided = [json[@"doubleSided"] boolValue];
    } else {
        material.doubleSided = YES;
    }
    
    if (json[@"blendMode"]) {
        material.blendMode = (SCNBlendMode) [json[@"blendMode"] integerValue];
    }
    
    if (json[@"transparencyMode"]) {
        material.transparencyMode = (SCNTransparencyMode) [json[@"transparencyMode"] integerValue];
    }

    if (json[@"lightingModel"]) {
        material.lightingModelName = json[@"lightingModel"];
    }
    
    if (json[@"diffuse"]) {
        [self setMaterialPropertyContents:json[@"diffuse"] material:material.diffuse];
    }
    
    if (json[@"normal"]) {
        [self setMaterialPropertyContents:json[@"normal"] material:material.normal];
    }
    
    if (json[@"displacement"]) {
        [self setMaterialPropertyContents:json[@"displacement"] material:material.displacement];
    }
    
    if (json[@"specular"]) {
        [self setMaterialPropertyContents:json[@"specular"] material:material.specular];
    }
    
    if (json[@"transparency"]) {
        material.transparency = [json[@"transparency"] floatValue];
    }
    
    if (json[@"metalness"]) {
        material.lightingModelName = SCNLightingModelPhysicallyBased;
        material.metalness.contents = @([json[@"metalness"] floatValue]);
    }
    
    if (json[@"roughness"]) {
        material.lightingModelName = SCNLightingModelPhysicallyBased;
        material.roughness.contents = @([json[@"roughness"] floatValue]);
    }
    
    if(json[@"shaders"] ) {
        material.shaderModifiers = json[@"shaders"];
    }
    
    if(json[@"writesToDepthBuffer"] ) {
        material.writesToDepthBuffer = [json[@"writesToDepthBuffer"] boolValue];
    }
    
    if(json[@"colorBufferWriteMask"] ) {
        material.colorBufferWriteMask = [json[@"colorBufferWriteMask"] integerValue];
    }
    
    if(json[@"fillMode"] ) {
        material.fillMode = [json[@"fillMode"] integerValue];
    }
    
    if(json[@"doubleSided"]) {
        material.doubleSided = [json[@"doubleSided"] boolValue];
    }
    
    if(json[@"litPerPixel"]) {
        material.litPerPixel = [json[@"litPerPixel"] boolValue];
    }
}

+ (void)setNodeProperties:(SCNNode *)node properties:(id)json {
    
    if (json[@"categoryBitMask"]) {
        node.categoryBitMask = [json[@"categoryBitMask"] integerValue];
    }
    if (json[@"renderingOrder"]) {
        node.renderingOrder = [json[@"renderingOrder"] integerValue];
    }
    if (json[@"castsShadow"]) {
        node.castsShadow = [json[@"castsShadow"] boolValue];
    }
    if (json[@"constraint"]) {
        SCNBillboardConstraint *constraint = [SCNBillboardConstraint billboardConstraint];
        constraint.freeAxes = [json[@"constraint"] integerValue];
        node.constraints = @[constraint];
    }
    if(json[@"transition"]) {
        NSDictionary * transition =json[@"transition"];
        if(transition[@"duration"]) {
            [SCNTransaction setAnimationDuration:[transition[@"duration"] floatValue]];
        } else {
            [SCNTransaction setAnimationDuration:0.0];
        }
        
    } else {
        [SCNTransaction setAnimationDuration:0.0];
    }
    if (json[@"position"]) {
        node.position = [self SCNVector3:json[@"position"]];
    }
    
    if (json[@"scale"]) {
       
        CGFloat scale = [json[@"scale"] floatValue];
        node.scale = SCNVector3Make(scale, scale, scale);
        
    }
    
    if (json[@"eulerAngles"]) {
        node.eulerAngles = [self SCNVector3:json[@"eulerAngles"]];
    }
    
    if (json[@"orientation"]) {
        node.orientation = [self SCNVector4:json[@"orientation"]];
    }
    
    if (json[@"rotation"]) {
        node.rotation = [self SCNVector4:json[@"rotation"]];
    }
    
    if (json[@"opacity"]) {
        node.opacity = [json[@"opacity"] floatValue];
    }
}

+ (void)setLightProperties:(SCNLight *)light properties:(id)json {
    if (json[@"lightCategoryBitMask"]) {
        light.categoryBitMask = [json[@"lightCategoryBitMask"] integerValue];
    }
    if(json[@"type"]) {
        light.type = json[@"type"];
    }
    if(json[@"color"]) {
        light.color = (__bridge id _Nonnull)([RCTConvert CGColor:json[@"color"]]);
    }
    if(json[@"temperature"]) {
        light.temperature = [json[@"temperature"] floatValue];
    }
    
    if(json[@"intensity"]) {
        light.intensity = [json[@"intensity"] floatValue];
    }
    
    if(json[@"attenuationStartDistance"]) {
        light.attenuationStartDistance = [json[@"attenuationStartDistance"] floatValue];
    }
    
    if(json[@"attenuationEndDistance"]) {
        light.attenuationEndDistance = [json[@"attenuationEndDistance"] floatValue];
    }
    
    if(json[@"spotInnerAngle"]) {
        light.spotInnerAngle = [json[@"spotInnerAngle"] floatValue];
    }
    
    if(json[@"spotOuterAngle"]) {
        light.spotOuterAngle = [json[@"spotOuterAngle"] floatValue];
    }
    
    if(json[@"castsShadow"]) {
        light.castsShadow = [json[@"castsShadow"] boolValue];
    }
    
    if(json[@"shadowRadius"]) {
        light.shadowRadius = [json[@"shadowRadius"] floatValue];
    }
    
    if(json[@"shadowColor"]) {
        light.shadowColor = (__bridge id _Nonnull)([RCTConvert CGColor:json[@"shadowColor"]]);
    }
    
    
    if(json[@"shadowSampleCount"]) {
        light.shadowSampleCount = [json[@"shadowSampleCount"] integerValue];
    }
    
    if(json[@"shadowBias"]) {
        light.shadowBias = [json[@"shadowBias"] floatValue];
    }
    
    if(json[@"shadowMode"]) {
        light.shadowMode = [json[@"shadowMode"] integerValue];
    }
    if(json[@"orthographicScale"]) {
        light.orthographicScale = [json[@"orthographicScale"] floatValue];
    }
    
    if(json[@"zFar"]) {
        light.zFar = [json[@"zFar"] floatValue];
    }
    
    if(json[@"zNear"]) {
        light.zNear = [json[@"zNear"] floatValue];
    }
}

@end
