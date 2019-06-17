import ElementBuilder from './element-builder';

export class ModelBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createModelNode(props, id);
    }
}
