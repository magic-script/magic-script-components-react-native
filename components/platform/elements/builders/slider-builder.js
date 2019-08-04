import ElementBuilder from './element-builder';

export default class SliderBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createSliderNode(props, id);
    }
}
