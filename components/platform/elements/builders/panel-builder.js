import ElementBuilder from './element-builder';

export default class PanelBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createPanelNode(props, id);
  }
}
