import ElementBuilder from './element-builder';

export default class ContentBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createContentNode(props, id);
  }
}
