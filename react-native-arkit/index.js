//
//  index.js
//
//  Created by HippoAR on 7/9/17.
//  Copyright © 2017 HippoAR. All rights reserved.
//

import ARKit from './ARKit';
import mxs from './mxs';
import ARKitView from './ARKitView';
import ARKitScript from './arkit/arkit-script/arkit-script';

import startup from './startup';
startup();

export {
  mxs,
  ARKit,
  ARKitView,
  ARKitScript,
};
