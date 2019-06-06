import ARKitRenderer from './arkit-renderer.js';

const ARKitScript = {    
    render: (element, container, callback) => {
        console.log('[MSX] render.element: ', element);
        if (!container.__rootContainer) {
        container.__rootContainer = ARKitRenderer.createContainer(container);
        }

        console.log('[MSX] render.container: ', container);
        
        // Schedule changes on the roots
        // returns expirationTime
        try {
            ARKitRenderer.updateContainer(element, container.__rootContainer, null, callback);
            console.log('[MSX] render.updateContainer: ', container.__rootContainer);
        } catch (error) {
            console.log('[MSX] render.updateContainer.error ', error);
        }

        return container;
    }
};

export default ARKitScript;
