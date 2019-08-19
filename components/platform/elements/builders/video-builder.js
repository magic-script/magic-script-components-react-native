import ElementBuilder from './element-builder';

export default class VideoBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createVideoNode(props, id);
    }
}