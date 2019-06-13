import { AppRegistry } from 'react-native';
import App from './src/App';

AppRegistry.registerComponent('ARDemo', () => App);


import React from 'react';
import { ARKitScript } from 'react-native-magic-script';
import Demo1App from './src/Demo1App';

ARKitScript.render(<Demo1App />, { name: 'root' });
