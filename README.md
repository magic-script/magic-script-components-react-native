[![codecov](https://codecov.io/gh/magic-script/magic-script-components-react-native/branch/master/graph/badge.svg?token=gZKfulNJa4)](https://codecov.io/gh/magic-script/magic-script-components-react-native)
[![JSX_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20tests%20JavaScript/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![Android_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20tests%20Android/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![iOS_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20tests%20iOS/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![npm version](https://badge.fury.io/js/magic-script-components-react-native.svg)](https://badge.fury.io/js/magic-script-components-react-native)
[![npm downloads](https://img.shields.io/npm/dt/magic-script-components-react-native.svg)](https://www.npmjs.com/package/magic-script-components-react-native)
[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](LICENSE)


# react-native-magic-script

## Getting started

`$ npm install react-native-magic-script --save`

### Mostly automatic installation

`$ react-native link react-native-magic-script`


### Guide for Android

The instruction assumes that you have the following tools installed and you have set environment variables properly:
- npm
- yarn
- react-native-cli
- Android Studio (or a standalone Android SDK)
- Java JDK

**Steps:**
1. Create a new project: 
`react-native init AwesomeProject --version 0.60.5`
2. Go to the project directory
3. Add react native magic script library to the project:
	 `yarn add https://github.com/magic-script/magic-script-components-react-native`
4. ~~Execute `react-native link` in order to link the library~~ (not required since React 0.60),
5. In the main project directory add `proxy_mobile` folder from [https://github.com/magic-script/magic-script-components-catalog](https://github.com/magic-script/magic-script-components-catalog/tree/master/)
6. Replace App.js content with the following:
	```
	import React from 'react';

	class MyApp extends React.Component {
	  render() {
	    return (
	      <view>
	          <text localPosition={[0, 0, 0]} alignment={'center-center'}>Welcome in AR!</text>
	      </view>
	    );
	  }
	}
	export default MyApp
	
	```

7. Replace `index.js` content with the following:
	```
	import React from 'react';
	import { MagicScript } from './proxy_mobile';
	import MyApp from './App';

	MagicScript.registerApp('AwesomeProject', <MyApp />, false);
	
	```

8. Delete `app.json` file since it's not required
9. In the `./android/build.gradle` file set `minSdkVersion` to 24
10. Open `android/app/src/main/AndroidManifest.xml` file and add the following **between** the `<application>` tags:

	`<meta-data
    android:name="com.google.ar.core"
    android:value="required" />`

	And optionally this **above** the `<application>` tag:
	`<uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />`
        
	*This tag indicates that this application requires ARCore. This results in the application
only being visible in the Google Play Store on devices that support ARCore)

	You can compare your AndroidManifest file with [this one](https://github.com/magic-script/magic-script-components-catalog/blob/master/android/app/src/main/AndroidManifest.xml)

11. Execute `react-native run-android` to run the project.

***If you want to run the most recent scenes from the Catalog app that download content from the web, you must additionally add dependency to `react-native-fs` and `axios` libraries:**

- `npm install react-native-fs --save`
- `npm install axios`

Then, being in the project directory, execute:
- `yarn add react-native-fs`
- `yarn add axios`
- `react-native link`


### Manual installation

#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-magic-script` and add `RNMagicScript.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMagicScript.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<


#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNMagicScript.sln` in `node_modules/react-native-magic-script/windows/RNMagicScript.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Magic.Script.RNMagicScript;` to the usings at the top of the file
  - Add `new RNMagicScriptPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNMagicScript from 'react-native-magic-script';

// TODO: What to do with the module?
RNMagicScript;
```
  
