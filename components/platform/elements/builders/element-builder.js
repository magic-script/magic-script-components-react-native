export class ElementBuilder {
    constructor(componentManager) {
        this._componentManager = componentManager;
    }

    create(props, id) {
        // Creates native element. This function should be overridden in a derived class.
    }

    update(element, oldProperties, newProperties) {
        // Updates native element
        this._componentManager.update(newProperties, id);
    }
}
