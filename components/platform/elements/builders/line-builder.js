import ElementBuilder from './element-builder';

export default class LineBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createLineNode(props, id);
  }
}
