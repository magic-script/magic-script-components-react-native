import ARKitRenderer from './arkit-renderer.js';

const ARKitScript = {
  render(element, container, callback) {
    console.log('[MSX] render.element: ', element);
    if (!container.__rootContainer) {
      container.__rootContainer = ARKitRenderer.createContainer(container);
    }

    console.log('[MSX] render.container: ', container);
    
    // Schedule changes on the roots
    // returns expirationTime
    ARKitRenderer.updateContainer(element, container.__rootContainer, null, callback);
    console.log('[MSX] render.updateContainer: ', container.__rootContainer);

    return container;
  }
};

export default ARKitScript;
