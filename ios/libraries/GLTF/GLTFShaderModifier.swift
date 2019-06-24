//
//  GLTFShaderModifier.swift
//  RNMagicScript
//
//  Created by Pawel Leszkiewicz on 21/06/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

import Foundation

class GLTFShaderModifier {
    static func alphaCutOff() -> String {
        return "// GLTFShaderModifierFragment_alphaCutOff.shader\n\n" +
            "#pragma arguments\n" +
            "float alphaCutOff;\n" +
            "#pragma body\n" +
            "_output.color.a = _output.color.a >= alphaCutoff ? 1.0 : 0.0"
    }

    static func alphaModeBlend() -> String {
        return "// GLTFShaderModifierSurface.shader\n\n" +
            "#pragma arguments\n" +

            "float baseColorFactorR;\n" +
            "float baseColorFactorG;\n" +
            "float baseColorFactorB;\n" +
            "float baseColorFactorA;\n" +
            "float metallicFactor;\n" +
            "float roughnessFactor;\n" +
            "float emissiveFactorR;\n" +
            "float emissiveFactorG;\n" +
            "float emissiveFactorB;\n" +

            "#pragma body\n" +
            "#pragma transparent\n" +

            "_surface.diffuse *= float4(baseColorFactorR, baseColorFactorG, baseColorFactorB, baseColorFactorA);\n" +
            "_surface.metalness *= metallicFactor;\n" +
            "_surface.roughness *= roughnessFactor;\n" +
            "_surface.emission.rgb *= float3(emissiveFactorR, emissiveFactorG, emissiveFactorB);"
    }

    static func doubleSidedWorkaround() -> String {
        return "// GLTFShaderModifierSurface_doubleSidedWorkaround.shader\n\n" +

        "#pragma arguments\n" +

        "float baseColorFactorR;\n" +
        "float baseColorFactorG;\n" +
        "float baseColorFactorB;\n" +
        "float baseColorFactorA;\n" +
        "float metallicFactor;\n" +
        "float roughnessFactor;\n" +
        "float emissiveFactorR;\n" +
        "float emissiveFactorG;\n" +
        "float emissiveFactorB;\n" +

        "#pragma body\n" +

        "_surface.diffuse *= float4(baseColorFactorR, baseColorFactorG, baseColorFactorB, baseColorFactorA);\n" +
        "_surface.metalness *= metallicFactor;\n" +
        "_surface.roughness *= roughnessFactor;\n" +
        "_surface.emission.rgb *= float3(emissiveFactorR, emissiveFactorG, emissiveFactorB);\n" +

        "if(_surface.normal.z < 0) {\n" +
            "_surface.normal *= -1.0;\n" +
        "}\n"
    }

    static func surface() -> String {
        return "//  GLTFShaderModifierSurface.shader\n\n" +

        "#pragma arguments\n" +

        "float baseColorFactorR;\n" +
        "float baseColorFactorG;\n" +
        "float baseColorFactorB;\n" +
        "float baseColorFactorA;\n" +
        "float metallicFactor;\n" +
        "float roughnessFactor;\n" +
        "float emissiveFactorR;\n" +
        "float emissiveFactorG;\n" +
        "float emissiveFactorB;\n" +

        "#pragma body\n" +

        "_surface.diffuse *= float4(baseColorFactorR, baseColorFactorG, baseColorFactorB, baseColorFactorA);\n" +
        "_surface.metalness *= metallicFactor;\n" +
        "_surface.roughness *= roughnessFactor;\n" +
        "_surface.emission.rgb *= float3(emissiveFactorR, emissiveFactorG, emissiveFactorB);"
    }

    static func pbrSpecularGlossiness() -> String {
        return "// GLTFShaderModifierSurface_pbrSpecularGlossiness.shader\n\n" +

        "constant float3 dielectricSpecular = float3(0.04, 0.04, 0.04);\n" +
        "constant float invDielect = 1.0 - dielectricSpecular.r;\n" +
        "constant float epsilon = 1e-6;\n" +

        "float calcPerceivedBrightness(float3 color) {\n" +
        "    return sqrt(0.299 * color.r * color.r + 0.587 * color.g * color.g + 0.114 * color.b * color.b);\n" +
        "}\n" +

        "float calcMetalness(float3 diffuse, float3 specular, float invSpecular) {\n" +
        "    float d = calcPerceivedBrightness(diffuse);\n" +
        "    float s = calcPerceivedBrightness(specular);\n" +
        "    if (s < dielectricSpecular.r) {\n" +
        "        return 0.0;\n" +
        "    }\n" +

        "    float a = dielectricSpecular.r;\n" +
        "    float b = d * invSpecular / invDielect + s - 2.0 * dielectricSpecular.r;\n" +
        "    float c = dielectricSpecular.r - s;\n" +
        "    float D = fmax(b * b - 4.0 * a * c, 0.0);\n" +
        "    return clamp((-b + sqrt(D)) / (2.0 * a), 0.0, 1.0);\n" +
        "}\n" +

        "#pragma arguments\n" +

        "float diffuseFactorR;\n" +
        "float diffuseFactorG;\n" +
        "float diffuseFactorB;\n" +
        "float diffuseFactorA;\n" +
        "float specularFactorR;\n" +
        "float specularFactorG;\n" +
        "float specularFactorB;\n" +
        "float emissiveFactorR;\n" +
        "float emissiveFactorG;\n" +
        "float emissiveFactorB;\n" +

        "#pragma body\n" +

        "float4 diffuse = _surface.diffuse * float4(diffuseFactorR, diffuseFactorG, diffuseFactorB, diffuseFactorA);\n" +
        "float3 specular = _surface.specular.r * float3(specularFactorR, specularFactorG, specularFactorB);"
    }

    static func pbrSpecularGlossinessDoubleSidedWorkaround() -> String {
        return "// GLTFShaderModifierSurface_pbrSpecularGlossiness_doubleSidedWorkaround.shader\n\n" +

        "constant float3 dielectricSpecular = float3(0.04, 0.04, 0.04);\n" +
        "constant float invDielect = 1.0 - dielectricSpecular.r;\n" +
        "constant float epsilon = 1e-6;\n" +

        "float calcPerceivedBrightness(float3 color) {\n" +
        "    return sqrt(0.299 * color.r * color.r + 0.587 * color.g * color.g + 0.114 * color.b * color.b);\n" +
        "}\n" +

        "float calcMetalness(float3 diffuse, float3 specular, float invSpecular) {\n" +
        "    float d = calcPerceivedBrightness(diffuse);\n" +
        "    float s = calcPerceivedBrightness(specular);\n" +
        "    if (s < dielectricSpecular.r) {\n" +
        "        return 0.0;\n" +
        "    }\n" +

        "    float a = dielectricSpecular.r;\n" +
        "    float b = d * invSpecular / invDielect + s - 2.0 * dielectricSpecular.r;\n" +
        "    float c = dielectricSpecular.r - s;\n" +
        "    float D = fmax(b * b - 4.0 * a * c, 0.0);\n" +
        "    return clamp((-b + sqrt(D)) / (2.0 * a), 0.0, 1.0);\n" +
        "}\n" +

        "#pragma arguments\n" +

        "float diffuseFactorR;\n" +
        "float diffuseFactorG;\n" +
        "float diffuseFactorB;\n" +
        "float diffuseFactorA;\n" +
        "float specularFactorR;\n" +
        "float specularFactorG;\n" +
        "float specularFactorB;\n" +
        "float emissiveFactorR;\n" +
        "float emissiveFactorG;\n" +
        "float emissiveFactorB;\n" +

        "#pragma body\n" +

        "float4 diffuse = _surface.diffuse * float4(diffuseFactorR, diffuseFactorG, diffuseFactorB, diffuseFactorA);\n" +
        "float3 specular = _surface.specular.rgb * float3(specularFactorR, specularFactorG, specularFactorB);\n" +
        "float invSpecular = 1.0 - fmax(fmax(specular.r, specular.g), specular.b);\n" +
        "float metalness = calcMetalness(diffuse.rgb, specular, invSpecular);\n" +
        "// It seems max number of arguments is 10, so omit glossinessFactor...\n" +
        "// float roughness = 1.0 - _surface.specular.a * glossinessFactor;\n" +
        "float roughness = 1.0 - _surface.specular.a;\n" +

        "float3 baseColorFromDiffuse = diffuse.rgb * (invSpecular / invDielect / fmax(1.0 - metalness, epsilon));\n" +
        "float3 baseColorFromSpecular = specular - dielectricSpecular * (1.0 - metalness) * (1.0 / fmax(metalness, epsilon));\n" +
        "float3 baseColor = clamp(\n" +
        "mix(baseColorFromDiffuse, baseColorFromSpecular, metalness * metalness),\n" +
        "float3(0, 0, 0),\n" +
        "float3(1, 1, 1));\n" +

        "_surface.diffuse = float4(baseColor, diffuse.a);\n" +
        "_surface.metalness = metalness;\n" +
        "_surface.roughness = roughness;\n" +
        "_surface.emission.rgb *= float3(emissiveFactorR, emissiveFactorG, emissiveFactorB);\n" +

        "if(_surface.normal.z < 0){\n" +
        "    _surface.normal *= -1.0;\n" +
        "}"
    }

    static func pbrSpecularGlossinessTextureDoubleSidedWorkaround() -> String {
        return "// GLTFShaderModifierSurface_pbrSpecularGlossiness_texture_doubleSidedWorkaround.shader\n\n" +

        "constant float3 dielectricSpecular = float3(0.04, 0.04, 0.04);\n" +
        "constant float invDielect = 1.0 - dielectricSpecular.r;\n" +
        "constant float epsilon = 1e-6;\n" +

        "float calcPerceivedBrightness(float3 color) {\n" +
        "    return sqrt(0.299 * color.r * color.r + 0.587 * color.g * color.g + 0.114 * color.b * color.b);\n" +
        "}\n" +

        "float calcMetalness(float3 diffuse, float3 specular, float invSpecular) {\n" +
        "    float d = calcPerceivedBrightness(diffuse);\n" +
        "    float s = calcPerceivedBrightness(specular);\n" +
        "    if (s < dielectricSpecular.r) {\n" +
        "        return 0.0;\n" +
        "    }\n" +

        "    float a = dielectricSpecular.r;\n" +
        "    float b = d * invSpecular / invDielect + s - 2.0 * dielectricSpecular.r;\n" +
        "    float c = dielectricSpecular.r - s;\n" +
        "    float D = fmax(b * b - 4.0 * a * c, 0.0);\n" +
        "    return clamp((-b + sqrt(D)) / (2.0 * a), 0.0, 1.0);\n" +
        "}\n" +

        "#pragma arguments\n" +

        "float diffuseFactorR;\n" +
        "float diffuseFactorG;\n" +
        "float diffuseFactorB;\n" +
        "float diffuseFactorA;\n" +
        "float specularFactorR;\n" +
        "float specularFactorG;\n" +
        "float specularFactorB;\n" +
        "float emissiveFactorR;\n" +
        "float emissiveFactorG;\n" +
        "float emissiveFactorB;\n" +

        "#pragma body\n" +

        "// Use a multiply texture as a specular texture\n" +
        "// because a specular texture is overwritten by a metalness texture for PBR.\n" +
        "_surface.specular = _surface.multiply;\n" +
        "_surface.multiply= float4(1, 1, 1, 1);\n" +

        "float4 diffuse = _surface.diffuse * float4(diffuseFactorR, diffuseFactorG, diffuseFactorB, diffuseFactorA);\n" +
        "float3 specular = _surface.specular.rgb * float3(specularFactorR, specularFactorG, specularFactorB);\n" +
        "float invSpecular = 1.0 - fmax(fmax(specular.r, specular.g), specular.b);\n" +
        "float metalness = calcMetalness(diffuse.rgb, specular, invSpecular);\n" +
        "// It seems max number of arguments is 10, so omit glossinessFactor...\n" +
        "// float roughness = 1.0 - _surface.specular.a * glossinessFactor;\n" +
        "float roughness = 1.0 - _surface.specular.a;\n" +

        "float3 baseColorFromDiffuse = diffuse.rgb * (invSpecular / invDielect / fmax(1.0 - metalness, epsilon));\n" +
        "float3 baseColorFromSpecular = specular - dielectricSpecular * (1.0 - metalness) * (1.0 / fmax(metalness, epsilon));\n" +
        "float3 baseColor = clamp(\n" +
        "mix(baseColorFromDiffuse, baseColorFromSpecular, metalness * metalness),\n" +
        "float3(0, 0, 0),\n" +
        "float3(1, 1, 1));\n" +

        "_surface.diffuse = float4(baseColor, diffuse.a);\n" +
        "_surface.metalness = metalness;\n" +
        "_surface.roughness = roughness;\n" +
        "_surface.emission.rgb *= float3(emissiveFactorR, emissiveFactorG, emissiveFactorB);\n" +

        "if(_surface.normal.z < 0){\n" +
        "    _surface.normal *= -1.0;\n" +
        "}"
    }
}
