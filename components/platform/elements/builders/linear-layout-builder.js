import ElementBuilder from './element-builder';

export default class LinearLayoutBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createLinearLayoutNode(props, id);
  }
}
