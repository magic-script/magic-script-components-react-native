import ReactNativeRenderer from './react-native-renderer.js';
import { Log } from '../utils/logger';

const ReactNativeMagicScript = {    
    render: (element, container, callback, debug = false) => {
        Log.setEnabled(debug);
        Log.debug('render.element: ', element);
        if (!container.__rootContainer) {
            container.__rootContainer = ReactNativeRenderer.createContainer(container);
        }

        Log.debug('render.container');
        
        // Schedule changes on the roots
        try {
            ReactNativeRenderer.updateContainer(element, container.__rootContainer, null, callback);
            Log.debug('render.updateContainer');
        } catch (error) {
            Log.debug('render.updateContainer.error ', error);
        }

        return container;
    }
};

export default ReactNativeMagicScript;
