import ElementBuilder from './element-builder';

export class TextBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createTextNode(props, id);
    }
}
