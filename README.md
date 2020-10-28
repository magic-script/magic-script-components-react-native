[![codecov](https://codecov.io/gh/magic-script/magic-script-components-react-native/branch/master/graph/badge.svg?token=gZKfulNJa4)](https://codecov.io/gh/magic-script/magic-script-components-react-native)
[![JSX_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20JavaScript/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![Android_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20Android/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![iOS_CI](https://github.com/magic-script/magic-script-components-react-native/workflows/Unit%20Tests%20iOS/badge.svg)](https://github.com/magic-script/magic-script-components-react-native/actions)
[![npm version](https://badge.fury.io/js/magic-script-components-react-native.svg)](https://badge.fury.io/js/magic-script-components-react-native)
[![npm downloads](https://img.shields.io/npm/dt/magic-script-components-react-native.svg)](https://www.npmjs.com/package/magic-script-components-react-native)
[![License](https://img.shields.io/:license-Apache%202.0-blue.svg)](LICENSE)


# MagicScript Components ReactNative

## Prerequisites

Make sure you have:

- properly set up an environment for **React Native** (see [React Native CLI Quickstart](https://reactnative.dev/docs/environment-setup) for iOS|Android),

- installed either [yarn](https://classic.yarnpkg.com/en/docs/install/) or [npm](https://www.npmjs.com/get-npm),

- installed [MagicScript Command Line Toolkit](https://github.com/magic-script/magic-script-cli):

```
npm install -g magic-script-cli
```

<p>&nbsp;</p>

## Create a react native project

To create a sample project, type the command below and follow instructions in the wizard.
```
magic-script init
````

Remember to select: 
- **Components** app, 
- **Landscape** app type,
- **iOS** or **Android** platform.

<img src="https://github.com/magic-script/magic-script-components-react-native/blob/master/docs/screens/mxs_wizard.gif" height="160">

## Build and run the project

1. Move to the root directory of the project

```
cd AwesomeProject
```

2. To build and run the project, type:
```
magic-script build ios
```
or
```
magic-script build android
```

This will build the app for the specified platform and will try to run in on a local emulator.
