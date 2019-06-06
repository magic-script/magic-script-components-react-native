//
//  Copyright © 2019 nomtek. All rights reserved.
//

import { NativeModules } from 'react-native';
import createArComponent from './lib/createArComponent';

const MLView = createArComponent(
  { mount: NativeModules.ARComponentManager.addView, pick: ['id'] },
  { },
  []
);

export default MLView;