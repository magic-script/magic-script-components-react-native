import { AppRegistry } from 'react-native';
import App from './src/App';

AppRegistry.registerComponent('ARDemo', () => App);


import React from 'react';
import { ARKitScript } from 'react-native-arkit';
import ListApp from './src/ListApp';

ARKitScript.render(<ListApp />, { name: 'root' });
