import ElementBuilder from './element-builder';

export default class ListViewBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createListViewNode(props, id);
  }
}