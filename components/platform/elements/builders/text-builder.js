import ElementBuilder from './element-builder';

export default class TextBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createTextNode(props, id);
  }
}
