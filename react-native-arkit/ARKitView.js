import React from 'react';
import ARKit from './ARKit';

export default class ARKitView extends React.Component {
    render() {
        return <ARKit {...this.props} />
    }
}
