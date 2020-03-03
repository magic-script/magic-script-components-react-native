import ElementBuilder from './element-builder';

export default class PrismBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createPrism(props, id);
  }
}