using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Scene3d.RNScene3d
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNScene3dModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNScene3dModule"/>.
        /// </summary>
        internal RNScene3dModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNScene3d";
            }
        }
    }
}
