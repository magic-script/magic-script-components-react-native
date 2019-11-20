export default class ElementBuilder {
  constructor(componentManager) {
    this._componentManager = componentManager;
  }

  create(props, id) {
    // Creates native element. This function should be overridden in a derived class.
  }
}
