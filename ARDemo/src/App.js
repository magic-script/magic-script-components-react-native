import React from 'react';
import { Image, StyleSheet, Switch, Text, View } from 'react-native';
import { mxs, ARKitView } from 'react-native-arkit';

export default class App extends React.Component {
  state = { 
    debugNodesValue: false,
    sampleImage: require('../res/sample_image/sample_image.png'),
  }

  onButton1Press = () => {
    this.setState({ sampleImage: require('../res/sample_image/sample_image.png') });
  }

  onSwitchValueChange = () => {
    const value = this.state.debugNodesValue ? false : true;
    this.setState({ debugNodesValue: value });
  }

  render() {
    const { sampleImage } = this.state;
    return (
      <View style={styles.container}>
        <Text style={styles.headerText}>Sample React Native App</Text>
        <ARKitView style={styles.arkitView}>
            <Text style={styles.sceneTitle}>Scene 3d view</Text>
            {/* <mxs.view position={{ x: 0, y: 0, z: 0 }}>
              <mxs.text position={{ x: 0, y: 1.2, z: 0 }} text={'Main menu'} color='cyan'/>
              <mxs.button position={{ x: 0, y: 0.0, z: 0 }} title={'Button1'} color='yellow' />
              <mxs.button position={{ x: 0, y: -1.1, z: 0 }} title={'Button2'} color='red' />
              <mxs.button position={{ x: 0, y: -2.2, z: 0 }} title={'Button3'} color='orange' />
              <mxs.image position={{ x: 2, y: -1.1, z: 0 }} source={sampleImage} size={{ width: 1, height: 1 }}/>
            </mxs.view> */}
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
