import ElementBuilder from './element-builder';

export default class DropdownListBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createDropdownListNode(props, id);
    }
}
