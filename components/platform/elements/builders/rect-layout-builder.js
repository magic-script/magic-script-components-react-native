import ElementBuilder from './element-builder';

export default class RectLayoutBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createRectLayoutNode(props, id);
  }
}
