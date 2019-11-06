import ElementBuilder from './element-builder';

export default class WebViewBuilder extends ElementBuilder {
    create(props, id) {
        this._componentManager.createWebViewNode(props, id);
    }
}