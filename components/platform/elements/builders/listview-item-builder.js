import ElementBuilder from './element-builder';

export default class ListViewItemBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createListViewItemNode(props, id);
  }
}