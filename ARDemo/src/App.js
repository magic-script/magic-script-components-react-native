import React from 'react';
import { Image, StyleSheet, Switch, Text, View } from 'react-native';
import { msx } from 'react-native-arkit';
import ARKitView from './ARKitView';

export default class App extends React.Component {
  state = { 
    debugNodesValue: false,
  }

  onSwitchValueChange = () => {
    const value = this.state.debugNodesValue ? false : true;
    this.setState({ debugNodesValue: value });
  }

  render() {
    const sampleImage = require('../res/sample_image/sample_image.png');
    return (
      <View style={styles.container}>
        <Text style={styles.headerText}>Sample React Native App</Text>
        <ARKitView style={styles.arkitView}>
            <Text style={styles.sceneTitle}>Scene 3d view</Text>
            <msx.button position={{ x: 0, y: 0, z: 0 }} title={'Button1'} color='yellow'/>
            <msx.button position={{ x: 0, y: -1.1, z: 0 }} title={'Button2'} color='red'/>
            <msx.button position={{ x: 0, y: -2.2, z: 0 }} title={'Button3'} color='orange'/>
            <msx.text position={{ x: 0, y: -3.3, z: 0 }} text={'Sample text'} color='cyan'/>
            <msx.image position={{ x: 2, y: -1.3, z: 0 }} source={sampleImage} size={{ width: 1, height: 1 }}/>
        </ARKitView>
        {/* <Image source={sampleImage} /> */}
        <View style={styles.footer}>
          <Text style={styles.footerText}>Render debug nodes</Text>
          <Switch
            value={this.state.debugNodesValue}
            style={styles.switch}
            onValueChange={this.onSwitchValueChange}
          />
        </View>
        
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'white',
  },
  headerText: {
    color: 'black',
    fontSize: 20,
    fontWeight: '900',
    fontStyle: 'normal',
    fontFamily: 'System',
    lineHeight: 41,
    marginTop: 44,
  },
  sceneTitle: {
    color: 'white',
    fontSize: 15,
    fontWeight: '700',
    fontStyle: 'normal',
    fontFamily: 'System',
    marginTop: 10,
  },
  arkitView: {
    flex: 1,
    width: '100%',
    justifyContent: 'flex-start',
    alignItems: 'center',
    backgroundColor: '#555555',
  },
  footer: {
    width: '90%',
    height: 60,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
    marginBottom: 30,
  },
  footerText: {
    color: 'black',
    fontSize: 16,
    fontWeight: '400',
    fontStyle: 'normal',
    fontFamily: 'System',
  },
  switch: {
    marginLeft: 10,
  }
});
