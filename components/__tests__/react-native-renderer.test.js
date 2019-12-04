import * as FN from "../react-native-magic-script/react-native-renderer.js";

jest.mock("../utils/logger.js");
const mxs = jest.mock("../mxs.js");
jest.mock("../platform/platform-factory.js");

describe("react-native-renderer", () => {
  test("createInstance", () => {
    const rootContainer = 1;
    const result = FN.createInstance("", {}, rootContainer, {}, {});
    // expect(mxs._nativeFactory.createElement).toHaveBeenCalled();
  });

  test("createTextInstance", () => {
    const rootContainer = 1;
    const result = FN.createTextInstance("", rootContainer, {}, {});
  });

  test("appendInitialChild", () => {
    const result = FN.appendInitialChild({}, {});
  });

  test("finalizeInitialChildren", () => {
    const rootContainer = 1;
    const result = FN.finalizeInitialChildren({}, {}, {}, rootContainer, {});
  });

  test("getRootHostContext", () => {
    const result = FN.getRootHostContext({});
  });

  test("getChildHostContext", () => {
    const rootContainer = 1;
    const result = FN.getChildHostContext({}, {}, rootContainer);
  });

  test("getPublicInstance", () => {
    const result = FN.getPublicInstance({});
  });

  test("prepareForCommit", () => {
    const result = FN.prepareForCommit({});
  });

  test("resetAfterCommit", () => {
    const result = FN.resetAfterCommit({});
  });

  test("prepareUpdate", () => {
    const rootContainer = 1;
    const result = FN.prepareUpdate({}, {}, {}, {}, rootContainer, {});
  });

  test("shouldDeprioritizeSubtree", () => {
    const rootContainer = 1;
    const result = FN.shouldDeprioritizeSubtree({}, {});
  });

  test("shouldSetTextContent", () => {
    const rootContainer = 1;
    const result = FN.shouldSetTextContent({}, {});
  });

  test("appendChild", () => {
    const rootContainer = 1;
    const result = FN.appendChild({}, {});
  });

  test("appendChildToContainer", () => {
    const rootContainer = 1;
    const result = FN.appendChildToContainer({}, {});
  });

  test("commitTextUpdate", () => {
    const rootContainer = 1;
    const result = FN.commitTextUpdate({}, {}, {});
  });

  test("commitMount", () => {
    const result = FN.commitMount({}, {}, {}, {});
  });

  test("commitUpdate", () => {
    const result = FN.commitUpdate({}, {}, {}, {}, {}, {});
  });

  test("insertBefore", () => {
    const result = FN.insertBefore({}, {}, {});
  });

  test("insertInContainerBefore", () => {
    const result = FN.insertInContainerBefore({}, {}, {});
  });

  test("removeChild", () => {
    const result = FN.removeChild({}, {});
  });

  test("removeChildFromContainer", () => {
    const result = FN.removeChildFromContainer({}, {});
  });

  test("resetTextContent", () => {
    const result = FN.resetTextContent({});
  });

  test("hideInstance", () => {
    const result = FN.hideInstance({});
  });

  test("hideTextInstance", () => {
    const result = FN.hideTextInstance({});
  });

  test("unhideInstance", () => {
    const result = FN.unhideInstance({});
  });

  test("unhideTextInstance", () => {
    const result = FN.unhideTextInstance({});
  });
});
