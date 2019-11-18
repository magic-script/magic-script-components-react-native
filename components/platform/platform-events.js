export const Events = [
  {
    name: "onClick",
    handler: (componentManager, elementId) => {
      componentManager.addOnPressEventHandler(elementId);
    }
  },
  {
    name: "onPress",
    handler: (componentManager, elementId) => {
      componentManager.addOnPressEventHandler(elementId);
    }
  },
  {
    name: "onDialogConfirmed",
    handler: (componentManager, elementId) => {
      componentManager.addOnDialogConfirmedEventHandler(elementId);
    }
  },
  {
    name: "onDialogCanceled",
    handler: (componentManager, elementId) => {
      componentManager.addOnDialogCanceledEventHandler(elementId);
    }
  },
  {
    name: "onColorCanceled",
    handler: (componentManager, elementId) => {
      componentManager.addOnColorConfirmedEventHandler(elementId);
    }
  },
  {
    name: "onColorChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnColorCanceledEventHandler(elementId);
    }
  },
  {
    name: "onColorConfirmed",
    handler: (componentManager, elementId) => {
      componentManager.addOnColorChangedEventHandler(elementId);
    }
  },
  {
    name: "onDateChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnDateChangedEventHandler(elementId);
    }
  },
  {
    name: "onDateConfirmed",
    handler: (componentManager, elementId) => {
      componentManager.addOnDateConfirmedEventHandler(elementId);
    }
  },
  {
    name: "onScrollChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnScrollChangedEventHandler(elementId);
    }
  },
  {
    name: "onSelectionChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnSelectionChangedEventHandler(elementId);
    }
  },
  {
    name: "onSliderChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnSliderChangedEventHandler(elementId);
    }
  },
  {
    name: "onTextChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnTextChangedEventHandler(elementId);
    }
  },
  {
    name: "onTimeChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnTimeChangedEventHandler(elementId);
    }
  },
  {
    name: "onTimeConfirmed",
    handler: (componentManager, elementId) => {
      componentManager.addOnTimeConfirmedEventHandler(elementId);
    }
  },
  {
    name: "onToggleChanged",
    handler: (componentManager, elementId) => {
      componentManager.addOnToggleChangedEventHandler(elementId);
    }
  },
  {
    name: "onVideoPrepared",
    handler: (componentManager, elementId) => {
      componentManager.addOnVideoPreparedEventHandler(elementId);
    }
  }
];
