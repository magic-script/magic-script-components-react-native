import ElementBuilder from './element-builder';

export default class SpinnerBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createSpinnerNode(props, id);
  }
}
