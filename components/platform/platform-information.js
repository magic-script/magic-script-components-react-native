import { Platform } from "react-native";

export default class PlatformInformation {
    platformName() {
        return Platform.OS
    }

    platformVersion() {
        return Platform.Version
    }
}