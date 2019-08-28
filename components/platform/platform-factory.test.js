import nativeComponentMapping from './component-mapping.js';
import { PlatformFactory } from './platform-factory';

jest.mock('NativeModules', () => {
  return {
    ARComponentManager: {
      clearScene: jest.fn()
    },
    AREventsManager: jest.fn().mockImplementation(() => {
      return {}
    }),
  };
});

jest.mock('NativeEventEmitter', () => {
  return jest.fn().mockImplementation(() => {
    return {
      addListener: jest.fn()
    }
  })
});

describe('platformFactory.createElement', () => {

  var platformFactory = new PlatformFactory(nativeComponentMapping)

  test('name of created element must be string', () => {
    expect(() => {
      platformFactory.createElement(1, 2, 3)
    }).toThrowError('string');
  });

  test('only known elements can be created', () => {
    expect(() => {
      platformFactory.createElement("unknown element", 2, 3)
    }).toThrowError('Unknown');
  });

});