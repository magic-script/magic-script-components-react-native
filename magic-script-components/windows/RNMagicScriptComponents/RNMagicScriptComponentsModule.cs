using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Magic.Script.Components.RNMagicScriptComponents
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNMagicScriptComponentsModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNMagicScriptComponentsModule"/>.
        /// </summary>
        internal RNMagicScriptComponentsModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNMagicScriptComponents";
            }
        }
    }
}
