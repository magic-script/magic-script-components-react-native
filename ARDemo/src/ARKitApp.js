import React, { Component } from 'react';

class ARKitApp extends Component {
  render() {
    return (
      <view id={'view_id1'}>
        <text id={'text_id1'} position={{ x: 0, y: 1.2, z: 0 }} text={'Main menu'} color='cyan'>Hello React Native Custom Renderer</text>
      </view>
    );
  }
}

export default ARKitApp;