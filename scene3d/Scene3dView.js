import PropTypes from 'prop-types';
import React from 'react';
import { requireNativeComponent } from 'react-native';

// requireNativeComponent automatically resolves this to "RNScene3dViewManager"
const RNScene3dView = requireNativeComponent('RNScene3dView', null);

export default class Scene3dView extends React.Component {
  render() {
    return <RNScene3dView {...this.props} />;
  }
}

Scene3dView.propTypes = {
  showStatistics: PropTypes.bool,
};
