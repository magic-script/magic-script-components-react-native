import { ColorProperty } from '../platform/elements/properties/color-property';

describe("ColorProperty", () => {
  describe("validate", () => {
    test("should return true", () => {
      const values = [
        "white", "green", "hotpink", "yellow",
        "#ff4455", "ff4455", "#FF4455", "FF4455", "f45", "F45",
        0xaa2211, 0xAA2211,
        [0.8, 0.33, 0.68, 1.0], [0, 0, 0, 1],
        { h:330, s:1, l:0.6 }, 
        { l:80, c:25, h:200 }, 
        { c:1, m:0.5, y:0, k:0.2 }, 
        { r:255, g:51, b:153 }, 
        { r:255, g:51, b:153, a:0.8 },
        { r:1, g:0.25, b:0.75, a:0.8, type: 'RGBAVec4' },
      ];
      values.forEach(value => expect(ColorProperty.validate(value)).toEqual(true));
    });
  });

  describe("parse", () => {
    describe("when arg is string", () => {
      test("should return array", () => {
        const textColorString = "white";
        const expectedColor_fromText = [1.0, 1.0, 1.0, 1.0];
        expect(ColorProperty.parse(textColorString)).toEqual(
          expectedColor_fromText
        );
        const lowercaseHexColorString = "#ff3399";
        const expectedColor_fromLowercaseHex = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(lowercaseHexColorString)).toEqual(
          expectedColor_fromLowercaseHex
        );
        const uppercaseHexColorString = "#FF3399";
        const expectedColor_fromUppercaseHex = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(uppercaseHexColorString)).toEqual(
          expectedColor_fromUppercaseHex
        );
        const shortLowercaseHexColorString = "f39";
        const expectedColor_fromShortLowercaseHex = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(shortLowercaseHexColorString)).toEqual(
          expectedColor_fromShortLowercaseHex
        );
        const shortUppercaseHexColorString = "F39";
        const expectedColor_fromShortUppercaseHex = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(shortUppercaseHexColorString)).toEqual(
          expectedColor_fromShortUppercaseHex
        );
      });
    });

    describe("when arg is number", () => {
      test("should return array", () => {
        const colorNumber = 0xff3399;
        const expectedColor_fromNumber = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(colorNumber)).toEqual(
          expectedColor_fromNumber
        );
      });
    });

    describe("when arg is Array", () => {
      test("should return array", () => {
        const colorArray = [0.8, 0.33, 0.68, 1.0];
        const expectedColor_fromArray = [0.8, 0.33, 0.68, 1.0];
        expect(ColorProperty.parse(colorArray)).toEqual(
          expectedColor_fromArray
        );
      });
    });

    describe("when arg is object", () => {
      test("should return array", () => {
        const colorHsl = { h:330, s:1, l:0.6 };
        const expectedColor_fromHsl = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(colorHsl)).toEqual(
          expectedColor_fromHsl
        );

        const colorLch = { l:80, c:25, h:200 };
        const expectedColor_fromLch = [0.52289, 0.82946, 0.83698, 1.0];
        const result_lch = ColorProperty.parse(colorLch);
        result_lch.every((x, i) => expect(x).toBeCloseTo(expectedColor_fromLch[i]));

        const colorCmyk = { c:1, m:0.5, y:0, k:0.2 };
        const expectedColor_fromCmyk = [0.0, 0.4, 0.8, 1.0];
        expect(ColorProperty.parse(colorCmyk)).toEqual(
          expectedColor_fromCmyk
        );

        const colorRgb = { r:255, g:51, b:153 };
        const expectedColor_fromRgb = [1.0, 0.2, 0.6, 1.0];
        expect(ColorProperty.parse(colorRgb)).toEqual(
          expectedColor_fromRgb
        );

        const colorRgba = { r:255, g:51, b:153, a:0.8 };
        const expectedColor_fromRgba = [1.0, 0.2, 0.6, 0.8];
        expect(ColorProperty.parse(colorRgba)).toEqual(
          expectedColor_fromRgba
        );
      });
    });
  });
});
