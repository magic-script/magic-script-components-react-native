//
//  Copyright © 2019 nomtek. All rights reserved.
//

import PropTypes from 'prop-types';

import { NativeModules } from 'react-native';

import { material } from './lib/propTypes';
import createArComponent from './lib/createArComponent';

const MLButton = createArComponent(
  { mount: NativeModules.ARComponentManager.mount, pick: ['id', 'title', 'size', 'color'] },
  {
    title: PropTypes.string,
    size: PropTypes.shape({
      width: PropTypes.number,
      height: PropTypes.number,
    }),
    color: PropTypes.string,
    material
  },
  ['title', 'size']
);

export default MLButton;
