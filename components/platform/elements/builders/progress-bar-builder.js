import ElementBuilder from './element-builder';

export default class ProgressBarBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createProgressBarNode(props, id);
    }
}
