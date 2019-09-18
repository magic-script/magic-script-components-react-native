# This repository contains iOS specific implementation of [react-native-magic-script](https://github.com/magic-script/react-native-magic-script) library

[![Unit tests](https://github.com/magic-script/magic-script-components-ios/workflows/Unit%20tests/badge.svg?branch=develop)](https://github.com/magic-script/magic-script-components-ios/actions)

## Running tests

### Introduction
The unit tests for Magic Script Components for iOS use Quick and Nimble frameworks. All tests are stored in the **RNMagicScriptTests** target. You can run them either from Xcode or from a terminal.

### Running tests from Xcode

1. Run `pod install`.
2. Open RNMagicScript.xcworkspace.
3. Make sure the `RNMagicScriptHostApplication` scheme is selected.
4. Press Cmd+U to run tests.

### Running tests from the command line

1. Run `pod install`.
2. Type the following command to build and run tests:
```
xcodebuild \
  -workspace RNMagicScript.xcworkspace \
  -scheme RNMagicScriptHostApplication \
  -sdk iphonesimulator \
  -destination 'platform=iOS Simulator,name=iPhone Xʀ,OS=12.4' \
  test | xcpretty --test --color
```
3. Please note that the command above uses local configuration of iOS simulators. If you don't have such simulator installed, you may change it to other simulator. To see a list of locally installed simulators, you can use the following command:
```
instruments -s devices
```
