import ElementBuilder from './element-builder';

export default class DialogBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createDialogNode(props, id);
    }
}
