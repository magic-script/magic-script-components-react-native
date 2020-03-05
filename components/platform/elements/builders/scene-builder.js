import ElementBuilder from './element-builder';

export default class SceneBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createScene(props, id);
  }
}