import React, { Component } from 'react';

class ARKitApp extends Component {
  state = { sampleImage: undefined, counter: 0 }

  onPlayButtonPress = () => {
    console.log('[EVENTS] onPlayButtonPress');
    const { counter } = this.state;
    this.setState({ 
      sampleImage: require('../res/sample_image/sample_image.png'),
      counter: counter + 1
    }, () => {
      console.log("[EVENTS] state1: ", this.state);
    });
  }

  onExitButtonPress = () => {
    console.log('[EVENTS] onExitButtonPress');
    const { counter } = this.state;
    this.setState({ sampleImage: undefined, counter: counter + 1 }, () => {
      console.log("[EVENTS] state2: ", this.state);
    });
  }

  render() {
    console.log('[EVENTS] render ', this.state.source);
    return (
      <view position={{ x: 0, y: 0, z: 0 }}>
        <text text={`Main menu ${this.state.counter}`} position={{ x: 0, y: 0, z: 0 }} color={'yellow'} />
        <button position={{ x: 0, y: -1, z: 0 }} title={'Play'} color='cyan' onPress={() => this.onPlayButtonPress()} />
        <button position={{ x: 0, y: -2, z: 0 }} title={'Exit'} color='cyan' onPress={this.onExitButtonPress} />
        <image position={{ x: 2, y: -1.1, z: 0 }} source={this.state.sampleImage} size={{ width: 1, height: 1 }}/>
        
        <view position={{ x: 1, y: -3.5, z: 0 }}>
          <text text={'About'} position={{ x: 0, y: 0, z: 0 }} />
          <text text={'Privacy Policy'} position={{ x: 0, y: -0.7, z: 0 }} />
        </view>
      </view>
    );
  }
}

export default ARKitApp;