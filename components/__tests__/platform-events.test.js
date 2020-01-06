import { Events } from "../platform/platform-events";

describe("Events", () => {
  test("should contain defined structure", () => {
    expect(Events.length).toEqual(22);
  });

  test("event should be defined as object", () => {
    Events.forEach(event => {
      expect(typeof event).toBe("object");
    });
  });

  test("event contain name (string)", () => {
    Events.forEach(event => {
      expect(typeof event.name).toBe("string");
    });
  });

  test("event contain handler (function)", () => {
    Events.forEach(event => {
      expect(typeof event.handler).toBe("function");
    });
  });
});
