import React from 'react';
import { StyleSheet, Text } from 'react-native';
import { WorldView } from 'react-native-magic-script-components';

export default class App extends React.Component {
  render() {
    return (
      <WorldView showStatistics={true} style={styles.container}>
        <Text>Sample</Text>
      </WorldView>
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
