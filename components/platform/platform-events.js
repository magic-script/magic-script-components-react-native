export const Events = [
  // UiNode events
  {
    name: "onActivate",
    handler: (componentManager, elementId) => componentManager.addOnActivateEventHandler(elementId)
  },
  {
    name: "onClick",
    handler: (componentManager, elementId) => componentManager.addOnActivateEventHandler(elementId)
  },
  /*
  {
    name: "onPress",
    handler: (componentManager, elementId) => componentManager.addOnPressEventHandler(elementId)
  },
  {
    name: "onLongPress",
    handler: (componentManager, elementId) => componentManager.addOnLongPressEventHandler(elementId)
  },
  {
    name: "onRelease",
    handler: (componentManager, elementId) => componentManager.addOnReleaseEventHandler(elementId)
  },
  {
    name: "onEnabled",
    handler: (componentManager, elementId) => componentManager.addOnEnabledEventHandler(elementId)
  },
  {
    name: "onDisabled",
    handler: (componentManager, elementId) => componentManager.addOnDisabledEventHandler(elementId)
  },
  {
    name: "onFocusGained",
    handler: (componentManager, elementId) => componentManager.addOnFocusGainedEventHandler(elementId)
  },
  {
    name: "onFocusLost",
    handler: (componentManager, elementId) => componentManager.addOnFocusLostEventHandler(elementId)
  },
  {
    name: "onUpdate",
    handler: (componentManager, elementId) => componentManager.addOnUpdateEventHandler(elementId)
  },
  {
    name: "onDelete",
    handler: (componentManager, elementId) => componentManager.addOnDeleteEventHandler(elementId)
  },
*/
  // Other events
  {
    name: "onDialogConfirmed",
    handler: (componentManager, elementId) => componentManager.addOnDialogConfirmedEventHandler(elementId)
  },
  {
    name: "onDialogCanceled",
    handler: (componentManager, elementId) => componentManager.addOnDialogCanceledEventHandler(elementId)
  },
  {
    name: "onDialogTimeExpired",
    handler: (componentManager, elementId) => componentManager.addOnDialogTimeExpiredEventHandler(elementId)
  },
  {
    name: "onColorCanceled",
    handler: (componentManager, elementId) => componentManager.addOnColorConfirmedEventHandler(elementId)
  },
  {
    name: "onColorChanged",
    handler: (componentManager, elementId) => componentManager.addOnColorCanceledEventHandler(elementId)
  },
  {
    name: "onColorConfirmed",
    handler: (componentManager, elementId) => componentManager.addOnColorChangedEventHandler(elementId)
  },
  {
    name: "onDateChanged",
    handler: (componentManager, elementId) => componentManager.addOnDateChangedEventHandler(elementId)
  },
  {
    name: "onDateConfirmed",
    handler: (componentManager, elementId) => componentManager.addOnDateConfirmedEventHandler(elementId)
  },
  {
    name: "onScrollChanged",
    handler: (componentManager, elementId) => componentManager.addOnScrollChangedEventHandler(elementId)
  },
  {
    name: "onSelectionChanged",
    handler: (componentManager, elementId) => componentManager.addOnSelectionChangedEventHandler(elementId)
  },
  {
    name: "onSliderChanged",
    handler: (componentManager, elementId) => componentManager.addOnSliderChangedEventHandler(elementId)
  },
  {
    name: "onConfirmationCompleted",
    handler: (componentManager, elementId) => componentManager.addOnConfirmationCompletedEventHandler(elementId)
  },
  {
    name: "onConfirmationUpdated",
    handler: (componentManager, elementId) => componentManager.addOnConfirmationUpdatedEventHandler(elementId)
  },
  {
    name: "onConfirmationCanceled",
    handler: (componentManager, elementId) => componentManager.addOnConfirmationCanceledEventHandler(elementId)
  },
  {
    name: "onTextChanged",
    handler: (componentManager, elementId) => componentManager.addOnTextChangedEventHandler(elementId)
  },
  {
    name: "onTimeChanged",
    handler: (componentManager, elementId) => componentManager.addOnTimeChangedEventHandler(elementId)
  },
  {
    name: "onTimeConfirmed",
    handler: (componentManager, elementId) => componentManager.addOnTimeConfirmedEventHandler(elementId)
  },
  {
    name: "onToggleChanged",
    handler: (componentManager, elementId) => componentManager.addOnToggleChangedEventHandler(elementId)
  },
  {
    name: "onFileSelected",
    handler: (componentManager, elementId) => componentManager.addOnFileSelectedEventHandler(elementId)
  },
  {
    name: "onVideoPrepared",
    handler: (componentManager, elementId) => componentManager.addOnVideoPreparedEventHandler(elementId)
  },
];
