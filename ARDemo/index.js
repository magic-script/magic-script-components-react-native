import { AppRegistry } from 'react-native';
import App from './src/App';

AppRegistry.registerComponent('ARDemo', () => App);


import React from 'react';
import ARKitScript from './src_custom/renderer/arkit-script';
import ARKitApp from './src_custom/ARKitApp';

ARKitScript.render(<ARKitApp />, { name: 'foo' });
