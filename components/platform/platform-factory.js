// Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved

import { Image, NativeEventEmitter, NativeModules, processColor } from 'react-native';
import { NativeFactory } from '../core/native-factory';
import generateId from '../utils/generateId';
import { Log } from '../utils/logger';
import omit from 'lodash/omit';
import isEqual from 'lodash/isEqual';

// Events names
const Event = {
    onClick: 'onClick',
    onColorCanceled: 'onColorCanceled',
    onColorChanged: 'onColorChanged',
    onColorConfirmed: 'onColorConfirmed',
    onDateChanged: 'onDateChanged',
    onDateConfirmed: 'onDateConfirmed',
    onPress: 'onPress',
    onScrollChanged: 'onScrollChanged',
    onSelectionChanged: 'onSelectionChanged',
    onSliderChanged: 'onSliderChanged',
    onTextChanged: 'onTextChanged',
    onTimeChanged: 'onTimeChanged',
    onTimeConfirmed: 'onTimeConfirmed',
    onToggleChanged: 'onToggleChanged',
    onVideoPrepared: 'onVideoPrepared',
};

export class PlatformFactory extends NativeFactory {

    constructor(componentMapping) {
        super(componentMapping);

        // { type, builder }
        this.elementBuilders = {};
        this.componentManager = NativeModules.ARComponentManager;
        this.componentManager.clearScene();
        this.setupEventsManager();

        for (const name in this._mapping.elements) {
            const createBuilder = this._mapping.elements[name]
            this.elementBuilders[name] = createBuilder(this.componentManager);
        }
    }

    setupEventsManager() {
        this.eventsByElementId = {};

        this.eventsManager = new NativeEventEmitter(NativeModules.AREventsManager);
        this.startListeningEvent(Event.onClick);
        this.startListeningEvent(Event.onColorCanceled);
        this.startListeningEvent(Event.onColorChanged);
        this.startListeningEvent(Event.onColorConfirmed);
        this.startListeningEvent(Event.onDateChanged);
        this.startListeningEvent(Event.onDateConfirmed);
        this.startListeningEvent(Event.onPress);
        this.startListeningEvent(Event.onScrollChanged);
        this.startListeningEvent(Event.onSelectionChanged);
        this.startListeningEvent(Event.onSliderChanged);
        this.startListeningEvent(Event.onTextChanged);
        this.startListeningEvent(Event.onTimeChanged);
        this.startListeningEvent(Event.onTimeConfirmed);
        this.startListeningEvent(Event.onToggleChanged);
        this.startListeningEvent(Event.onVideoPrepared);
    }

    startListeningEvent(eventName) {
        const subscription = this.eventsManager.addListener(eventName, (sender) => {
            const elementId = sender.nodeId;
            const events = this.eventsByElementId[elementId];
            if (events !== undefined) {
                const filteredEvents = events.filter(item => item.name === eventName);
                filteredEvents.forEach(item => {
                    // Log.debug(`[EVENTS] ${eventName} received: ${elementId}\nitem: `, item);
                    item.handler(sender);
                });
            }
        });
        // Don't forget to unsubscribe, typically in componentWillUnmount
        // subscription.remove();
    }

    registerEvent(elementId, name, handler) {
        if (elementId === undefined) { return; }

        if (name === Event.onClick || name === Event.onPress) {
            this.componentManager.addOnPressEventHandler(elementId);
        } else if (name === Event.onColorConfirmed) {
            this.componentManager.addOnColorConfirmedEventReceivedHandler(elementId);
        } else if (name === Event.onColorCanceled) {
            this.componentManager.addOnColorCanceledEventReceivedHandler(elementId);
        } else if (name === Event.onColorChanged) {
            this.componentManager.addOnColorChangedEventHandler(elementId);
        } else if (name === Event.onDateChanged) {
            this.componentManager.addOnDateChangedEventHandler(elementId);
        } else if (name === Event.onDateConfirmed) {
            this.componentManager.addOnDateConfirmedEventHandler(elementId);
        } else if (name === Event.onScrollChanged) {
            this.componentManager.addOnScrollChangedEventHandler(elementId);
        } else if (name === Event.onSelectionChanged) {
            this.componentManager.addOnSelectionChangedEventHandler(elementId);
        } else if (name === Event.onSliderChanged) {
            this.componentManager.addOnSliderChangedEventHandler(elementId);
        } else if (name === Event.onTextChanged) {
            this.componentManager.addOnTextChangedEventHandler(elementId);
        } else if (name === Event.onTimeChanged) {
            this.componentManager.addOnTimeChangedEventHandler(elementId);
        } else if (name === Event.onTimeConfirmed) {
            this.componentManager.addOnTimeConfirmedEventHandler(elementId);
        } else if (name === Event.onToggleChanged) {
            this.componentManager.addOnToggleChangedEventHandler(elementId);
        } else if (name === Event.onVideoPrepared) {
            this.componentManager.addOnVideoPreparedEventHandler(elementId);
        } 

        const pair = { name, handler };
        var events = this.eventsByElementId[elementId];
        if (events === undefined) {
            events = [pair];
            this.eventsByElementId[elementId] = events;
        } else {
            events.push(pair);
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

            if (eventName === undefined) {
                throw new TypeError(`Event ${pair.name} is not recognized event`);
            }
            if (typeof pair.handler !== 'function') {
                throw new TypeError(`The event handler for ${pair.name} is not a function`);
            }

            this.registerEvent(elementId, pair.name, pair.handler);
        }
    }

    createElement(name, container, ...args) {
        if (typeof name !== 'string') {
            throw new Error('PlatformFactory.createElement expects "name" to be string');
        }
        if (this._mapping.elements[name] === undefined) {
            throw new Error(`Unknown tag: ${name}`);
        }

        return this._createElement(name, container, ...args)
    }

    _processColor(color) {
        return Array.isArray(color) ? color : processColor(color);
    }

    _processAssetSource(path) {
        
        if (typeof path === 'number') {
            return Image.resolveAssetSource(path);
        }
        return path;
    }

    _processCustomProps(name, props) {
        const properties = omit(props, 'children');
        const child = props.children;
        if (typeof child === 'string' || typeof child === 'number') {
            properties['text'] = child.toString();
        }

        return ({
            ...properties,
            ...(properties.shadowColor ? { shadowColor: this._processColor(properties.shadowColor) } : {}),
            ...(properties.color ? { color: this._processColor(properties.color) } : {}),
            ...(properties.textColor ? { textColor: this._processColor(properties.textColor) } : {}),
            ...(properties.modelPath ? { modelPath: this._processAssetSource(properties.modelPath) } : {}),
            ...(properties.filePath ? { filePath: this._processAssetSource(properties.filePath) } : {}),
            ...(properties.videoPath ? { videoPath: this._processAssetSource(properties.videoPath) } : {}),
        });
    }

    _diffProps(oldProps, newProps) {
        let diffProps = {};
        Object.keys(newProps).forEach((key, index) => {
            const oldValue = oldProps[key];
            const value = newProps[key];
            if (!isEqual(oldValue, value)) {
                diffProps[key] = value;
            }
        });
        return diffProps;
    }

    _createElement(name, container, ...args) {
        const props = this._processCustomProps(name, args[0]);
        const id = props.id || generateId();

        this.elementBuilders[name].create(props, id);
        this.setComponentEvents(id, props);

        return { name, id, props };
    }

    updateElement(name, ...args) {
        if (typeof name !== 'string') {
            throw new Error('PlatformFactory.updateElement expects "name" to be string');
        }
        if (this._mapping.elements[name] === undefined) {
            throw new Error(`Unknown tag: ${name}`);
        }

        const oldProps = this._processCustomProps(name, args[1]);
        const newProps = this._processCustomProps(name, args[2]);
        const diffProps = this._diffProps(oldProps, newProps);
        if (Object.keys(diffProps).length > 0) {
            const element = args[0];
            this.componentManager.updateNode(element.id, diffProps);
        }
    }

    insertBefore(parent, child, beforeChild) {
        if (typeof child === 'string' || typeof child === 'number') {
            this.componentManager.updateNode(parent.id, { text: child.toString() });
        } else {
            this.componentManager.addChildNode(child.id, parent.id);
        }
    }

    addChildElement(parent, child) {
        if (typeof child === 'string' || typeof child === 'number') {
            // Do nothing here. If child is a text or number then it should be included to parent
            // as a text property (see 4th line in _processCustomProps function).
        } else {
            this.componentManager.addChildNode(child.id, parent.id);
        }
    }

    removeChildElement(parent, child) {
        if (typeof child === 'string' || typeof child === 'number') {
            this.componentManager.updateNode(parent.id, { text: '' });
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

    commitTextUpdate(textInstance, oldText, newText) {
        // Nothing to implement here.
    }

    resetAfterCommit(containerInfo) {
        this.componentManager.updateLayout();
    }

    createApp(appComponent) {
    }
    
}
