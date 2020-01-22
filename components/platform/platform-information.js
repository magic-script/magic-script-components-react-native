import { Platform } from "react-native";

export default class PlatformInformation {
    name() {
        return Platform.OS
    }

    version() {
        return Platform.Version
    }
}