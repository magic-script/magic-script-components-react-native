import React from 'react';
import { requireNativeComponent, NativeModules } from 'react-native';

const RCTARView = requireNativeComponent('RCTARView');

export default class ARView extends React.Component {

    constructor(props) {
        super(props);
        NativeModules.ARComponentManager.initAR();
    }

    render() {
        return <RCTARView {...this.props} />
    }
}
