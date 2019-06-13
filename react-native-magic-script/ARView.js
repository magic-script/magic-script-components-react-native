import React from 'react';
import { requireNativeComponent } from 'react-native';

const RCTARView = requireNativeComponent('RCTARView');

export default class ARView extends React.Component {
    render() {
        return <RCTARView {...this.props} />
    }
}
