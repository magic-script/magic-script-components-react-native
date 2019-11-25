import ElementBuilder from './element-builder';

export default class PageViewBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createPageViewNode(props, id);
  }
}
