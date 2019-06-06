//
//  Copyright © 2019 nomtek. All rights reserved.
//

import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import { material } from './lib/propTypes';
import createArComponent from './lib/createArComponent';

const MLImage = createArComponent(
  { mount: NativeModules.ARComponentManager.addImage, pick: ['id', 'size', 'source'] },
  {
    size: PropTypes.shape({
      width: PropTypes.number,
      height: PropTypes.number,
    }),
    source: PropTypes.any,
    material
  },
  []
);

export default MLImage;
