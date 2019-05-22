import React from 'react';
import { StyleSheet, Text } from 'react-native';
import { Scene3dView } from 'react-native-scene3d';

export default class App extends React.Component {
  render() {
    return (
      <Scene3dView showStatistics={true} style={styles.container}>
        <Text>Sample</Text>
      </Scene3dView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#555555',
  },
});
