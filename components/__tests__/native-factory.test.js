import { NativeFactory } from "../core/native-factory";

describe("NativeFactory", () => {
  describe("constructor ", () => {
    test("should assign mapping to internals", () => {
      const nativeFactory = new NativeFactory([]);
      expect(nativeFactory._mapping).toEqual([]);
    });
  });

  describe("when created ", () => {
    const nativeFactory = new NativeFactory([]);
    test("createElement should throw error", () => {
      expect(() => {
        nativeFactory.createElement();
      }).toThrowError(Error);
    });

    test("updateElement should throw error", () => {
      expect(() => {
        nativeFactory.updateElement();
      }).toThrowError(Error);
    });

    test("addChildElement should throw error", () => {
      expect(() => {
        nativeFactory.addChildElement();
      }).toThrowError(Error);
    });
  });
});
