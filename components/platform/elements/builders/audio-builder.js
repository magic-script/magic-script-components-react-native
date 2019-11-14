import ElementBuilder from './element-builder';

export default class AudioBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createAudioNode(props, id);
    }
}
