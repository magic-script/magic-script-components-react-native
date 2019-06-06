import React, { Component } from 'react';

class ARKitApp extends Component {
  render() {
    return (
      <view position={{ x: 0, y: 0, z: 0 }}>
        <text text={'Main menu 1'} position={{ x: 0, y: 0, z: 0 }} />
        <button position={{ x: 0, y: -1, z: 0 }} title={'Play'} />
        <button position={{ x: 0, y: -2, z: 0 }} title={'Exit'} />
        
        <view position={{ x: 1, y: 1, z: 0 }}>
          <text text={'label'} position={{ x: 0, y: 0, z: 0 }}></text>
        </view>
      </view>
    );
  }
}

export default ARKitApp;