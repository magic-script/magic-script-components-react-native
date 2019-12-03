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
      addOnDialogConfirmedEventHandler: jest.fn(),
      addOnDialogCanceledEventHandler: jest.fn(),
      addOnDialogTimeExpiredEventHandler: jest.fn(),
      addOnColorConfirmedEventHandler: jest.fn(),
      addOnColorCanceledEventHandler: jest.fn(),
      addOnColorChangedEventHandler: jest.fn(),
      addOnDateChangedEventHandler: jest.fn(),
      addOnDateConfirmedEventHandler: jest.fn(),
      addOnScrollChangedEventHandler: jest.fn(),
      addOnSelectionChangedEventHandler: jest.fn(),
      addOnSliderChangedEventHandler: jest.fn(),
      addOnConfirmationCompletedEventHandler: jest.fn(),
      addOnConfirmationUpdatedEventHandler: jest.fn(),
      addOnConfirmationCanceledEventHandler: jest.fn(),
      addOnTextChangedEventHandler: jest.fn(),
      addOnTimeChangedEventHandler: jest.fn(),
      addOnTimeConfirmedEventHandler: jest.fn(),
      addOnToggleChangedEventHandler: jest.fn(),
      addOnVideoPreparedEventHandler: jest.fn()
    }
  };
});

jest.mock('NativeEventEmitter', () =>
  class {
    addListener = () => { }
  }
);

const generateId = jest.mock("generateId", () => jest.fn(x => "mockId"));
