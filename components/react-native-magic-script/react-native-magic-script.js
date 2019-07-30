import ReactNativeRenderer from './react-native-renderer.js';

const ReactNativeMagicScript = {    
    render: (element, container, callback) => {
        console.log('[MSX] render.element: ', element);
        if (!container.__rootContainer) {
            container.__rootContainer = ReactNativeRenderer.createContainer(container);
        }

        console.log('[MSX] render.container');
        
        // Schedule changes on the roots
        try {
            ReactNativeRenderer.updateContainer(element, container.__rootContainer, null, callback);
            console.log('[MSX] render.updateContainer');
        } catch (error) {
            console.log('[MSX] render.updateContainer.error ', error);
        }

        return container;
    }
};

export default ReactNativeMagicScript;
