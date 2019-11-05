import ElementBuilder from './element-builder';

export default class ColorPickerBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createColorPickerNode(props, id);
    }
}
