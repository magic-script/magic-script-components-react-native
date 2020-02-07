import ARView from "./ARView";
import ReactNativeMagicScript from "./components/react-native-magic-script/react-native-magic-script";
import PlatformInformation from "./components/platform/platform-information";
import NativePlaneDetector from "./components/platform/native-plane-detector";

// backward compatibility, should be removed in the future
const ARKitScript = ReactNativeMagicScript;

export { ARView, ARKitScript, ReactNativeMagicScript, PlatformInformation, NativePlaneDetector };
