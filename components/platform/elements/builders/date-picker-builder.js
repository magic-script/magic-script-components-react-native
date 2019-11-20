import ElementBuilder from './element-builder';

export default class DatePickerBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createDatePickerNode(props, id);
  }
}
