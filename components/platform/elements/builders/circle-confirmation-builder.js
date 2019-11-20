import ElementBuilder from './element-builder';

export default class CircleConfirmationBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createCircleConfirmationNode(props, id);
  }
}
