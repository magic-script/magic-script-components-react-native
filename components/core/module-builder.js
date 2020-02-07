import PlaneDetector from '../platform/planes-detector.js';

export default class ModuleBuilder {
    create(componentType, configuration) {
        return new PlaneDetector();
    }
} 