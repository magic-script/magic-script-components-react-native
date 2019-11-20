import ElementBuilder from './element-builder';

export default class TextEditBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createTextEditNode(props, id);
  }
}
