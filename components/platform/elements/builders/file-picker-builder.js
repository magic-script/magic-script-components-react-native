import ElementBuilder from './element-builder';

export default class FilePickerBuilder extends ElementBuilder {
  create(props, id) {
    this._componentManager.createFilePickerNode(props, id);
  }
}
