import ElementBuilder from './element-builder';

export default class ToggleGroupBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createToggleGroupNode(props, id);
  }
}
