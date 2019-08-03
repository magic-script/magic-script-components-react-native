import ElementBuilder from './element-builder';

export default class ModelBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createLineNode(props, id);
    }
}
