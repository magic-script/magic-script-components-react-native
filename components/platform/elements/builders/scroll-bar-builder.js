import ElementBuilder from './element-builder';

export default class ScrollBarBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createScrollBarNode(props, id);
    }
}
