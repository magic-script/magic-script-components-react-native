import ElementBuilder from './element-builder';

export class ImageBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createImageNode(props, id);
    }
}
