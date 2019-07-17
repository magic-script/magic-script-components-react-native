import { NativeModules } from 'react-native';
import ARView from './ARView';
import ReactNativeMagicScript from './components/react-native-magic-script/react-native-magic-script';

// backward compatibility, should be removed in the future
const ARKitScript = ReactNativeMagicScript;

const MLXrClientSession = NativeModules.MLXrClientSession;

export {
  ARView,
  ARKitScript,
  ReactNativeMagicScript,
  MLXrClientSession
};
