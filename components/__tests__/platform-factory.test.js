import nativeComponentMapping from 'platform/component-mapping.js';
import { PlatformFactory } from 'platform/platform-factory.js';
import generateId from 'utils/generateId';

jest.mock('NativeModules', () => {
  return {
    ARComponentManager: {
      clearScene: () => { },
      addOnPressEventHandler: jest.fn(),
      addOnTextChangedEventHandler: jest.fn(),
      addOnToggleChangedEventHandler: jest.fn()
    },
    AREventsManager: class { }
  };
});

jest.mock('NativeEventEmitter', () =>
  class {
    addListener = () => { }
  }
);

jest.mock('generateId', () =>
  jest.fn(x => 'mockId')
);

describe('platformFactory.createElement', () => {

  var platformFactory = new PlatformFactory(nativeComponentMapping)
  platformFactory._createElement = jest.fn(() => 'mockCreateElementReturn');

  test('name of created element must be string', () => {
    expect(() => {
      platformFactory.createElement(1, 2, 3)
    }).toThrowError('string');
  });

  test('only known elements can be created', () => {
    expect(() => {
      platformFactory.createElement('unknown element', 2, 3)
    }).toThrowError('Unknown');
  });

  test('_createElement should be called', () => {
    expect(platformFactory.createElement('button', null, 3))
      .toEqual('mockCreateElementReturn')
    expect(platformFactory._createElement).toHaveBeenCalledWith('button', null, 3);
  });
});

describe('platformFactory._processCustomProps', () => {

  var platformFactory = new PlatformFactory(nativeComponentMapping)
  const processCustomProps = platformFactory._processCustomProps.bind(platformFactory)

  test('children of type string and number should be returned as "text" prop', () => {
    expect(processCustomProps("", { children: 123 }))
      .toEqual({ text: '123' });
    expect(processCustomProps("", { children: 'abc' }))
      .toEqual({ text: 'abc' });
  });

  test('all props except "children" should be returned', () => {
    expect(processCustomProps("", { p1: 1, p2: "a", p3: {}, children: {} }))
      .toEqual({ p1: 1, p2: "a", p3: {} });
  });

  test('custom props should be processed and updated value should be returned', () => {
    platformFactory._processColor = jest.fn(x => 'ok');
    platformFactory._processAssetSource = jest.fn(x => 'ok');
    const customProps = {
      shadowColor: 'shadowColor',
      color: 'color',
      textColor: 'textColor',
      modelPath: 'modelPath',
      filePath: 'filePath'
    };
    expect(processCustomProps("", customProps)).toEqual({
      shadowColor: 'ok',
      color: 'ok',
      textColor: 'ok',
      modelPath: 'ok',
      filePath: 'ok'
    });
    expect(platformFactory._processColor).toHaveBeenCalledWith('shadowColor');
    expect(platformFactory._processColor).toHaveBeenCalledWith('color');
    expect(platformFactory._processColor).toHaveBeenCalledWith('textColor');
    expect(platformFactory._processAssetSource).toHaveBeenCalledWith('modelPath');
    expect(platformFactory._processAssetSource).toHaveBeenCalledWith('filePath');
  });
});

describe('platformFactory.registerEvent', () => {

  const platformFactory = new PlatformFactory(nativeComponentMapping)

  test('should return on undefined elementId', () => {
    const run = jest.fn(() => platformFactory.registerEvent(undefined, null, null));
    run();
    expect(run).toHaveReturned();
  });

  test('selected events should be served', () => {
    platformFactory.registerEvent('', 'onClick', null);
    platformFactory.registerEvent('', 'onPress', null);
    platformFactory.registerEvent('', 'onTextChanged', null);
    platformFactory.registerEvent('', 'onToggleChanged', null);

    const m = platformFactory.componentManager;
    expect(m.addOnPressEventHandler).toHaveBeenCalledTimes(2);
    expect(m.addOnTextChangedEventHandler).toHaveBeenCalledTimes(1);
    expect(m.addOnToggleChangedEventHandler).toHaveBeenCalledTimes(1);
  });

  test('events should be added to array', () => {
    platformFactory.eventsByElementId = {};
    platformFactory.registerEvent('mockId', 'onClick', 'mockHandler');
    expect(platformFactory.eventsByElementId).toEqual({
      "mockId": [{
        "handler": 'mockHandler',
        "name": "onClick",
      }]
    })
    platformFactory.registerEvent('mockId', 'onClick', 'mockHandler2');
    expect(platformFactory.eventsByElementId).toEqual({
      "mockId": [{
        "handler": 'mockHandler',
        "name": "onClick",
      }, {
        "handler": 'mockHandler2',
        "name": "onClick",
      }]
    })
  });

});

describe('platformFactory._createElement', () => {

  var platformFactory = new PlatformFactory(nativeComponentMapping)
  platformFactory._processCustomProps = jest.fn((x, props) => props);
  platformFactory.setComponentEvents = jest.fn();
  platformFactory.elementBuilders['mock'] = new class {
    create = jest.fn();
  };

  test('new ID should be generated if it is null', () => {
    expect(platformFactory._createElement('mock', null, {}))
      .toEqual(expect.objectContaining({ id: 'mockId' }))
    expect(generateId).toHaveBeenCalled()
  });

  test('old ID should be returned if it is not null', () => {
    expect(platformFactory._createElement('mock', null, { id: 'oldId' }))
      .toEqual(expect.objectContaining({ id: 'oldId' }))
    expect(generateId).not.toHaveBeenCalled()
  });

  test('element builder should be called', () => {
    platformFactory._createElement('mock', null, { id: 'mockId' })
    expect(platformFactory.elementBuilders['mock'].create)
      .toHaveBeenCalledWith({ "id": "mockId" }, 'mockId')
  });

  test('setComponentEvents should be called', () => {
    platformFactory._createElement('mock', null, { id: 'mockId' })
    expect(platformFactory.setComponentEvents)
      .toHaveBeenCalledWith('mockId', { "id": "mockId" })
  });

  test('should return name, id, props', () => {
    expect(platformFactory._createElement('mock', null, { id: 'mockId' }))
      .toEqual({ "id": "mockId", "name": "mock", "props": { "id": "mockId" } })
  });

});
