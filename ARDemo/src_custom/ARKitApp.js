import React, { Component } from 'react';
import ErrorBoundary from './components/ErrorBoundary';
import View from './components/View';
import Text from './components/Text';

class ARKitApp extends Component {
  render() {
    return (
        <ErrorBoundary>
            <View
                style={{
                flex: 1,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: '#8BBDD9',
                height: 400
                }}
            >
                <Text>Hello React Native Custom Renderer</Text>
            </View>
        </ErrorBoundary>
    );
  }
}

export default ARKitApp;