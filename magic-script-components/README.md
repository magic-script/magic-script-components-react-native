
# react-native-magic-script-components

## Getting started

`$ npm install react-native-magic-script-components --save`

### Mostly automatic installation

`$ react-native link react-native-magic-script-components`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-magic-script-components` and add `RNMagicScriptComponents.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNMagicScriptComponents.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNMagicScriptComponentsPackage;` to the imports at the top of the file
  - Add `new RNMagicScriptComponentsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-magic-script-components'
  	project(':react-native-magic-script-components').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-magic-script-components/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-magic-script-components')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNMagicScriptComponents.sln` in `node_modules/react-native-magic-script-components/windows/RNMagicScriptComponents.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Magic.Script.Components.RNMagicScriptComponents;` to the usings at the top of the file
  - Add `new RNMagicScriptComponentsPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNMagicScriptComponents from 'react-native-magic-script-components';

// TODO: What to do with the module?
RNMagicScriptComponents;
```
  