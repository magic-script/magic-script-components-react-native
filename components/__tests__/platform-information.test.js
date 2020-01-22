import PlatformInformation from "../platform/platform-information.js";
import { Platform } from "react-native";

// mocking Platform consts with idea from: https://stackoverflow.com/questions/43161416/mocking-platform-detection-in-jest-and-react-native
const setPlatform = function(platform, version) {
  Object.defineProperty(Platform, "OS", {
    get: jest.fn(() => platform)
  });

  Object.defineProperty(Platform, "Version", {
    get: jest.fn(() => version)
  });
};

describe("PlatformInformation module", () => {
  test("Should provide correct platform name", () => {
    setPlatform("iOS", "");
    const plarformInformation = new PlatformInformation();
    expect(plarformInformation.platformName()).toEqual("iOS");

    setPlatform("platformName", "");
    expect(plarformInformation.platformName()).toEqual("platformName");
  });

  test("Should provide correct platform version", () => {
    setPlatform("", "13.12.11");
    const plarformInformation = new PlatformInformation();
    expect(plarformInformation.platformVersion()).toEqual("13.12.11");

    setPlatform("", "version.string");
    expect(plarformInformation.platformVersion()).toEqual("version.string");
  });
});
