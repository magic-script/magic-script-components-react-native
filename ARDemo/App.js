import React from 'react';
import { StyleSheet, Switch, Text, View } from 'react-native';
import { ARKit, msx } from 'react-native-arkit';
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
    return (
      <View style={styles.container}>
        <Text style={styles.headerText}>Sample React Native App</Text>
        <ARKitView style={styles.arkitView}>
            <Text style={styles.sceneTitle}>Scene 3d view</Text>
            <msx.button position={{ x: 0, y: 0, z: 0 }} title={'Button1'} color='rgb(255,255,0)'/>
            <msx.button position={{ x: 0, y: -1.1, z: 0 }} title={'Button2'} color='rgb(255,0,0)'/>
            <msx.button position={{ x: 0, y: -2.2, z: 0 }} title={'Button3'} color='rgb(0,255,0)'/>
            {/* <ARKit.Box
                position={{ x: 0.2, y: 0, z: 0 }}
                shape={{ width: 0.1, height: 0.1, length: 0.1, chamfer: 0.0 }}
            >
                <ARKit.Cylinder
                    position={{ x: 0.0, y: 0.1, z: 0 }}
                    shape={{ radius: 0.025, height: 0.05 }}
                />
            </ARKit.Box>
            <ARKit.Sphere position={{ x: 0.3, y: 0.0, z: 0 }} shape={{ radius: 0.05 }} >
                <ARKit.Sphere position={{ x: 0.1, y: 0.0, z: 0 }} shape={{ radius: 0.025 }} />
                <ARKit.Sphere position={{ x: -0.1, y: 0.0, z: 0 }} shape={{ radius: 0.025 }} />
                <ARKit.Sphere position={{ x: 0.0, y: 0.1, z: 0 }} shape={{ radius: 0.025 }} />
                <ARKit.Sphere position={{ x: 0.0, y: -0.1, z: 0 }} shape={{ radius: 0.025 }} />
            </ARKit.Sphere> */}
        </ARKitView>
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
