import ElementBuilder from './element-builder';

export default class ScrollViewBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createScrollViewNode(props, id);
    }
}
