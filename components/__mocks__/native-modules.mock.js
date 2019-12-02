const NativeModules = jest.mock("NativeModules", () => {
  return {
    ARComponentManager: {
      clearScene: jest.fn(),
      updateLayout: jest.fn(),
      removeChildNodeFromRoot: jest.fn(),
      addChildNode: jest.fn(),
      updateNode: jest.fn(),
      removeChildNode: jest.fn(),
      addChildNodeToContainer: jest.fn(),
      addOnActivateEventHandler: jest.fn(),
      addOnTextChangedEventHandler: jest.fn(),
      addOnToggleChangedEventHandler: jest.fn(),
    }
  };
});

jest.mock('NativeEventEmitter', () =>
  class {
    addListener = () => { }
  }
);

const generateId = jest.mock("generateId", () => jest.fn(x => "mockId"));
