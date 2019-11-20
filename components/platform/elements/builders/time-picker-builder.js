import ElementBuilder from './element-builder';

export default class TimePickerBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createTimePickerNode(props, id);
  }
}
