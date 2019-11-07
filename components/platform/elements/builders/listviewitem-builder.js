import ElementBuilder from './element-builder';

export default class ListViewItemBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createUiListViewItemNode(props, id);
  }
}