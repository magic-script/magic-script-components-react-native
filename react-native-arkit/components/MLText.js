//
//  Copyright © 2019 nomtek. All rights reserved.
//

import PropTypes from 'prop-types';
import { NativeModules } from 'react-native';
import { material } from './lib/propTypes';
import createArComponent from './lib/createArComponent';

const MLText = createArComponent(
  { mount: NativeModules.ARComponentManager.addText, pick: ['id', 'text', 'size', 'color'] },
  {
    text: PropTypes.string,
    size: PropTypes.shape({
      width: PropTypes.number,
      height: PropTypes.number,
    }),
    color: PropTypes.string,
    material
  },
  []
);

export default MLText;
