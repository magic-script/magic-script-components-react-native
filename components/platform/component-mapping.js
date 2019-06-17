// Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved

import { ButtonBuilder } from './elements/builders/button-builder.js';
import { GroupBuilder } from './elements/builders/group-builder.js';
import { ImageBuilder } from './elements/builders/image-builder.js';
import { TextBuilder } from './elements/builders/text-builder.js';

import { ModelBuilder } from './elements/builders/model-builder.js';


export default {
    version: '1.0',
    platform: 'mobile (ios, android)',
    controllers: {
        // 'scene': () => new ControllerBuilder(),
    },
    elements: {
        'view': (componentManager) => new GroupBuilder(componentManager),
        'text': (componentManager) => new TextBuilder(componentManager),
        // 'textEdit': (componentManager) => new TextEditBuilder(componentManager),
        'button': (componentManager) => new ButtonBuilder(componentManager),
        'image': (componentManager) => new ImageBuilder(componentManager),
        'model': (componentManager) => new ModelBuilder(componentManager),
    }
};
