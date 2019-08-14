
# react-native-magic-script

## Getting started

`$ npm install react-native-magic-script --save`

### Mostly automatic installation

`$ react-native link react-native-magic-script`

### Complete steps to add the library and run on Android

The instruction assumes that you have the following tools installed and you have set environment variables properly:
- npm
- yarn
- react-native-cli
- Android Studio (or a standalone Android SDK)
- Java JDK

**Steps:**
1. Create a new project: 
`react-native init AwesomeProject --version 0.59.9`
2. Go to the project directory
3. Add react native magic script library to the project:
	 `yarn add https://github.com/magic-script/react-native-magic-script.git`
4. Execute `react-native link` in order to link the library,
5. In the main project directory add `proxy` folder from https://github.com/magic-script/magic-script-calendar/tree/master/proxy
6. Create `src` folder and add `app.js` file to the `src` folder from https://github.com/magic-script/magic-script-components
7. In order to run sample scenes from our demo app, you need to copy the `src` folder from https://github.com/magic-script/magic-script-components-platform-android/tree/master/ARDemo
8. Replace the `index.js` file with the one from `ARDemo` project and replace the `ARDemo` name with your project name (here `AwesomeProject`): 
`MagicScript.registerApp('AwesomeProject', <BrowserApp />, false)`

9. Delete `App.js` and `app.json` files since they are not required
10. In the `./android/build.gradle` file set `minSdkVersion` to 24
11. Open `android/app/src/main/AndroidManifest.xml` file and add the following **between** the `<application>` tags:

	`<meta-data
    android:name="com.google.ar.core"
    android:value="required" />`

	And optionally this **above** the `<application>` tag:
	`<uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />`
        
	*This tag indicates that this application requires ARCore. This results in the application
only being visible in the Google Play Store on devices that support ARCore)

	You can compare your AndroidManifest file with [this one](https://github.com/magic-script/magic-script-components-platform-android/blob/master/ARDemo/android/app/src/main/AndroidManifest.xml)

12. Execute `react-native run-android` to run the project.

***If you want to run the most recent scenes from the ARDemo app that download content from the web,  you must additionally add dependency to `react-native-fs` and `axios` libraries:**

- `npm install react-native-fs --save`
- `npm install axios`

Then, being in the project directory, execute:
- `yarn add react-native-fs`
- `yarn add axios`
- `react-native link`

***If you have WeakReference.get() error, click on "RELOAD" 
(it must be a bug related to debugging, because it happens only sometimes and never in release builds)**


### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-magic-script` and add `RNMagicScript.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMagicScript.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNMagicScriptPackage;` to the imports at the top of the file
  - Add `new RNMagicScriptPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-magic-script'
  	project(':react-native-magic-script').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-magic-script/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-magic-script')
  	```

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
  