import ElementBuilder from './element-builder';

export default class TabBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createTabNode(props, id);
    }
}
