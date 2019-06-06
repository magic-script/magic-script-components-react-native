//
//  Copyright © 2019 nomtek. All rights reserved.
//

import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import { material } from './lib/propTypes';
import createArComponent from './lib/createArComponent';

const MLButton = createArComponent(
  { mount: NativeModules.ARComponentManager.addButton, pick: ['id', 'title', 'size', 'color'] },
  {
    title: PropTypes.string,
    size: PropTypes.shape({
      width: PropTypes.number,
      height: PropTypes.number,
    }),
    color: PropTypes.string,
    material
  },
  []
);

export default MLButton;
