import React, { Component } from 'react';

class ARKitApp extends Component {
  render() {
    return (
      <view id={'id_v1'}>
        <text id={'id_t2'} position={{ x: 0, y: 1.2, z: 0 }} text={'Main menu'} color='cyan' />
      </view>
    );
  }
}

export default ARKitApp;