import { AppRegistry } from 'react-native';
import RNApp from './src/RNApp';

AppRegistry.registerComponent('ARDemo', () => RNApp);


import React from 'react';
import { ARKitScript } from 'react-native-magic-script';
import MyApp from './src/app';

ARKitScript.render(<MyApp />, { name: 'root' });
