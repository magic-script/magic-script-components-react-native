import ElementBuilder from './element-builder';

export default class DropdownListItemBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createDropdownListItemNode(props, id);
    }
}
