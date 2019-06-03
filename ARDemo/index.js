import { AppRegistry } from 'react-native';
import App from './src/App';

AppRegistry.registerComponent('ARDemo', () => App);


import React from 'react';
import ARKitRenderer from './src/ARKitRenderer';
import ARKitApp from './src/ARKitApp';

ARKitRenderer.render(<ARKitApp />, { name: 'foo' });
