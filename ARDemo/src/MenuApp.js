import React, { Component } from 'react';

class MenuApp extends Component {
  state = { sampleImage: undefined, counter: 0 }

  onPlayButtonPress = () => {
    this.setState({ sampleImage: require('../res/sample_image/sample_image.png') });
  }

  onExitButtonPress = () => {
    this.setState({ sampleImage: undefined });
  }

  increaseCounter = () => {
    const { counter } = this.state;
    this.setState({ counter: counter + 1 });
  }

  render() {
    return (
      <view position={{ x: 0, y: 0, z: 0 }}>
        <text text={`Main menu ${this.state.counter}`} position={{ x: 0, y: 0, z: 0 }} color={'yellow'} />
        <button position={{ x: 0, y: -1, z: 0 }} title={'Play'} color='cyan' onPress={() => this.onPlayButtonPress()} />
        <button position={{ x: 0, y: -2, z: 0 }} title={'Exit'} color='cyan' onPress={this.onExitButtonPress} />
        <button position={{ x: 0, y: -3, z: 0 }} title={'Counter'} color='yellow' onPress={this.increaseCounter} />
        <image position={{ x: 2, y: -1.1, z: 0 }} source={this.state.sampleImage} size={{ width: 1, height: 1 }}/>
        
        <view position={{ x: 1, y: -4.5, z: 0 }}>
          <text text={'About'} position={{ x: 0, y: 0, z: 0 }} />
          <text text={'Privacy Policy'} position={{ x: 0, y: -0.7, z: 0 }} />
        </view>
      </view>
    );
  }
}

export default MenuApp;