import ElementBuilder from './element-builder';

export default class ToggleBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createToggleNode(props, id);
    }
}
