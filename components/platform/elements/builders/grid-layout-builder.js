import ElementBuilder from './element-builder';

export default class GridLayoutBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createGridLayout(props, id);
    }
}
