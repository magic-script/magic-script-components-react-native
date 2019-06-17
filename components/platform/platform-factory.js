// Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved

import { Image, NativeEventEmitter, NativeModules, processColor } from 'react-native';
import { NativeFactory } from '../core/native-factory';
import generateId from '../lib/generateId';
import omit from 'lodash/omit';
import isEqual from 'lodash/isEqual';

export class PlatformFactory extends NativeFactory {
    constructor(componentMapping) {
        super(componentMapping);

        // { type, builder }
        this.elementBuilders = {};
        this.controllerBuilders = {};
        this.controllers = new WeakMap();
        this.componentManager = NativeModules.ARComponentManager;
        this.componentManager.clearScene();
        this.setupEventsManager();
    }

    setupEventsManager() {
        this.eventsByElementId = {};

        this.eventsManager = new NativeEventEmitter(NativeModules.AREventsManager);
        this.startListeningEvent('onPress');
        this.startListeningEvent('onClick');
    }

    startListeningEvent(eventName) {
        const subscription = this.eventsManager.addListener(eventName, (sender) => {
            const elementId = sender.nodeId;
            const events = this.eventsByElementId[elementId];
            if (events !== undefined) {
                const onPressEvents = events.filter(item => item.name === eventName);
                onPressEvents.forEach(item => {
                    console.log(`[EVENTS] ${eventName} received: ${elementId}\nitem: `, item);
                    item.handler();
                });
            }
        });
        // Don't forget to unsubscribe, typically in componentWillUnmount
        // subscription.remove();
    }

    registerEvent(elementId, name, handler) {
        if (elementId === undefined) { return; }

        this.componentManager.addOnPressEventHandler(elementId);

        const pair = { name, handler };
        var events = this.eventsByElementId[elementId];
        if (events === undefined) {
            events = [pair];
            this.eventsByElementId[elementId] = events;
            console.log(`[EVENTS] "${elementId}" register first ${name} event (${this.eventsByElementId[elementId].length}).`);
        } else {
            events.push(pair);
            console.log(`[EVENTS] "${elementId}" register another ${name} event (${this.eventsByElementId[elementId].length}).`);
        }
    }

    isController(element) {
        return this.controllers[element] !== undefined;
    }

    setComponentEvents(elementId, properties) {
        const eventHandlers = Object.keys(properties)
            .filter(key => key.length > 2 && key.startsWith('on'))
            .map(key => ({ name: key, handler: properties[key] }));

        for (const pair of eventHandlers) {
            const eventName = pair.name;//UiNodeEvents[pair.name];

            if (eventName !== undefined) {
                if (typeof pair.handler === 'function') {
                    this.registerEvent(elementId, pair.name, pair.handler);
                    // element[eventName](pair.handler);
                } else {
                    throw new TypeError(`The event handler for ${pair.name} is not a function`);
                }
            } else {
                throw new TypeError(`Event ${pair.name} is not recognized event`);
            }
        }
    }

    createElement(name, container, ...args) {
        if (typeof name !== 'string') {
            throw new Error('PlatformFactory.createElement expects "name" to be string');
        }

        return this._createElement(name, container, ...args);
        // if (this._mapping.elements[name] !== undefined) {
        //     return this._createElement(name, container, ...args)
        // } else if (this._mapping.controllers[name] !== undefined) {
        //     return this._createController(name, container, ...args);
        // } else {
        //     throw new Error(`Unknown tag: ${name}`);
        // }
    }

    _parseCustomProps = (props) => ({
        ...props,
        ...(props.shadowColor ? { shadowColor: processColor(props.shadowColor) } : {}),
        ...(props.color ? { color: processColor(props.color) } : {}),
        ...(props.textColor ? { textColor: processColor(props.textColor) } : {}),
        // ...(props.material ? { material: processMaterial(props.material) } : {}),
        ...(props.source ? { source: Image.resolveAssetSource(props.source) } : {}),
    });

    _createElement(name, container, ...args) {
        const props = this._parseCustomProps(omit(args[0], 'children'));
        const id = props.id || generateId();
        const type = name;
        
        if (name === 'text') {
            this.componentManager.createTextNode(props, id);
        } else if (name === 'button') {
            this.componentManager.createButtonNode(props, id);
            this.setComponentEvents(id, props);
        } else if (name === 'view') {
            this.componentManager.createViewNode(props, id);
        } else if (name === 'image') {
            this.componentManager.createImageNode(props, id);
        }

        return { type, id, props };

        // if (this.elementBuilders[name] === undefined) {
        //     const createBuilder = this._mapping.elements[name];
        //     this.elementBuilders[name] = createBuilder();
        // }

        // const prism = container.controller.getPrism();
        // const element = this.elementBuilders[name].create(prism, ...args);

        // // TODO: Move setComponentEvents to the builders !!!
        // this.setComponentEvents(element, args[0]); // args = [props]

        // return element;
    }

    updateElement(name, ...args) {
        if (typeof name !== 'string') {
            throw new Error('PlatformFactory.updateElement expects "name" to be string');
        }

        const oldProps = this._parseCustomProps(omit(args[1], 'children'));
        const newProps = this._parseCustomProps(omit(args[2], 'children'));
        if (!isEqual(oldProps, newProps)) {
            console.log('[FACTORY] updateElement.newProps: ', newProps);
            const element = args[0];
            this.componentManager.updateNode(element.id, newProps);
        }
        
        // if (this._mapping.elements[name] !== undefined) {
        //     this.elementBuilders[name].update(...args);
        // } else if (this._mapping.controllers[name] !== undefined) {
        //     this.controllerBuilders[name].update(...args);
        // } else {
        //     throw new Error(`Unknown tag: ${name}`);
        // }
    }

    insertBefore(parent, child, beforeChild) {
        if (typeof child === 'string' || typeof child === 'number') {
            const props = (parent.type === 'button') ? { title: child.toString() } : { text: child.toString() };
            this.componentManager.updateNode(parent.id, props);
        } else {
            this.componentManager.addChildNode(child.id, parent.id);
        }
    }

    addChildElement(parent, child) {
        if (typeof child === 'string' || typeof child === 'number') {
            const props = (parent.type === 'button') ? { title: child.toString() } : { text: child.toString() };
            this.componentManager.updateNode(parent.id, props);
        } else {
            this.componentManager.addChildNode(child.id, parent.id);
        }
    }

    removeChildElement(parent, child) {
        if (typeof child === 'string' || typeof child === 'number') {
            const props = (parent.type === 'button') ? { title: '' } : { text: '' };
            this.componentManager.updateNode(parent.id, props);
        } else {
            this.componentManager.removeChildNode(child.id, parent.id);
        }
    }

    appendChildToContainer(container, child) {
        this.componentManager.addChildNodeToContainer(child.id);
    }

    removeChildFromContainer(container, child) {
        this.componentManager.removeChildNodeFromRoot(child.id);
    }

    createApp(appComponent) {
    }
}
