//
//  index.js
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

import MLButton from './components/MLButton';
import MLImage from './components/MLImage';
import MLText from './components/MLText';

import ARKit from './ARKit';
import ARKitView from './ARKitView';
import ARKitScript from './arkit/arkit-script/arkit-script';
import { PlatformFactory } from './arkit/platform/platform-factory';

import startup from './startup';
startup();

const mxs = {};
mxs.button = MLButton;
mxs.image = MLImage;
mxs.text = MLText;
mxs._nativeFactory = new PlatformFactory();

export {
  mxs,
  ARKit,
  ARKitView,
  ARKitScript,
};
