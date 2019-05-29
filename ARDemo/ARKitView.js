import React, { Component } from 'react';
import { ARKit } from 'react-native-arkit';

export default class ARKitView extends React.Component {
    render() {
        return <ARKit {...this.props} />
    }
}