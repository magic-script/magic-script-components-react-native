[![codecov](https://codecov.io/gh/magic-script/magic-script-components-react-native/branch/master/graph/badge.svg?token=gZKfulNJa4)](https://codecov.io/gh/magic-script/magic-script-components-react-native)
[![JSX_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20JavaScript/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![Android_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20Android/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![iOS_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20iOS/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![npm version](https://badge.fury.io/js/magic-script-components-react-native.svg)](https://badge.fury.io/js/magic-script-components-react-native)
[![npm downloads](https://img.shields.io/npm/dt/magic-script-components-react-native.svg)](https://www.npmjs.com/package/magic-script-components-react-native)
[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](LICENSE)


# MagicScript Components ReactNative


### Create a project

The instruction assumes that you have the following tools installed and you have set environment variables properly:
- npm or yarn 
- react-native-cli

**Steps:**

1. Create a new project: 
`react-native init AwesomeProject --version 0.60.5`
2. Go to the project directory
3. Add Magic Script Components ReactNative library to the project:
	 `yarn add magic-script-components-react-native`
4. Add Magic Script Components library to the project:
	`yarn add magic-script-components`
5. Execute `react-native link` in order to link the libraries,
6. In the main project directory add `proxy_mobile` folder from [https://github.com/magic-script/magic-script-components-catalog](https://github.com/magic-script/magic-script-components-catalog/tree/master/)


### Pre-configuration for Android

The instruction assumes that you have the following tools installed and you have set environment variables properly:
- npm or yarn
- react-native-cli
- Android Studio (or a standalone Android SDK)
- Java JDK

**Steps:**

1. In the `./android/build.gradle` file set `minSdkVersion` to 24
2. Open `android/app/src/main/AndroidManifest.xml` file and add the following **between** the `<application>` tags:


	```
	<meta-data
    android:name="com.google.ar.core"
    android:value="required" />
	```

	Add the permission to use the Camera:
	`<uses-permission android:name="android.permission.CAMERA" />`

	And optionally this **above** the `<application>` tag:
	`<uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />`
        
	*This tag indicates that this application requires ARCore. This results in the application
only being visible in the Google Play Store on devices that support ARCore)

	You can compare your AndroidManifest file with [this one](https://github.com/magic-script/magic-script-components-catalog/blob/master/android/app/src/main/AndroidManifest.xml)

### Pre-configuration for iOS

The instruction assumes that you have the following tools installed and you have set environment variables properly:
- npm or yarn 
- react-native-cli
- XCode version > 10
- CocoaPods version > 1.7.5

1. Create Empty.swift file inside project and `Create bridging header file`
2. In Podfile file update `platform :ios, ‘9.0’` to `platform :ios, ‘12.0’`
3. Declare `Privacy - Camera Usage Description` in Info.plist
4. Declare `Privacy - Location When In Use Usage Description` in Info.plist
5. Open terminal, navigate to `./ios` directory and execute `pod install` in order to install necessary Pods

### General pre-configuration & project building

**Steps**
1. Run `yarn` or `npm install` to install the dependencies
2. Replace App.js content with the following:
	```
	import React from 'react';
	import { View, Text } from 'magic-script-components';

	class MyApp extends React.Component {
	  render() {
	    return (
	      <View>
	          <Text localPosition={[0, 0, 0]} alignment={'center-center'}>Welcome in AR!</Text>
	      </View>
	    );
	  }
	}
	export default MyApp
	
	```
3. Replace `index.js` content with the following:
	```
	import React from 'react';
	import { MagicScript } from './proxy_mobile';
	import MyApp from './App';

	MagicScript.registerApp('AwesomeProject', <MyApp />, false);
	
	```
4. In order to build & install the application on Android or iOS device, execute one of the following in root directory:

	`react-native run-android`
	or
	`react-native run-ios`
