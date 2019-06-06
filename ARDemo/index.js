import { AppRegistry } from 'react-native';
import App from './src/App';

AppRegistry.registerComponent('ARDemo', () => App);


import React from 'react';
import { ARKitScript } from 'react-native-arkit';
import ARKitApp from './src/ARKitApp';

ARKitScript.render(<ARKitApp />, { name: 'root' });
