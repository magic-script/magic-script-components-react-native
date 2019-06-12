import React from 'react';
import { requireNativeComponent } from 'react-native';
const RCTARKit = requireNativeComponent('RCTARKit');

export default class ARView extends React.Component {
    render() {
        return <RCTARKit {...this.props} />
    }
}
