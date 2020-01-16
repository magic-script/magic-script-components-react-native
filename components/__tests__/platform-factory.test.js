import nativeComponentMapping from "platform/component-mapping.js";
import { PlatformFactory } from "platform/platform-factory.js";
import generateId from "utils/generateId";

describe("platformFactory", () => {
  describe("createElement", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    platformFactory._createElement = jest.fn(() => "mockCreateElementReturn");

    test("name of created element must be string", () => {
      expect(() => {
        platformFactory.createElement(1, 2, 3);
      }).toThrowError(Error);
    });

    test("only known elements can be created", () => {
      expect(() => {
        platformFactory.createElement("unknown element", 2, 3);
      }).toThrowError(Error);
    });

    test("_createElement should be called", () => {
      const result = platformFactory.createElement("button", null, 3);
      expect(result).toEqual("mockCreateElementReturn");
      expect(platformFactory._createElement).toHaveBeenCalledWith(
        "button",
        null,
        3
      );
    });
  });

  describe("_processCustomProps", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    const processCustomProps = platformFactory._processCustomProps.bind(
      platformFactory
    );

    test('children of type string and number should be returned as "text" prop', () => {
      expect(processCustomProps("", { children: 123 })).toEqual({
        text: "123"
      });
      expect(processCustomProps("", { children: "abc" })).toEqual({
        text: "abc"
      });
      expect(processCustomProps("", { children: 3.14 })).toEqual({
        text: "3.14"
      });
    });

    test('all props except "children" should be returned', () => {
      expect(
        processCustomProps("", { p1: 1, p2: "a", p3: {}, children: {} })
      ).toEqual({ p1: 1, p2: "a", p3: {} });
    });

    test("custom props should be processed and updated value should be returned", () => {
      platformFactory._processColor = jest.fn(x => "ok");
      platformFactory._processAssetSource = jest.fn(x => "ok");
      const customProps = {
        color: "color",
        textColor: "textColor",
        modelPath: "modelPath",
        filePath: "filePath",
        videoPath: "videoPath",
        fileName: "fileName"
      };
      expect(processCustomProps("", customProps)).toEqual({
        color: "ok",
        textColor: "ok",
        modelPath: "ok",
        filePath: "ok",
        videoPath: "ok",
        fileName: "ok"
      });
      expect(platformFactory._processColor).toHaveBeenCalledWith("color");
      expect(platformFactory._processColor).toHaveBeenCalledWith("textColor");
      expect(platformFactory._processAssetSource).toHaveBeenCalledWith(
        "modelPath"
      );
      expect(platformFactory._processAssetSource).toHaveBeenCalledWith(
        "filePath"
      );
      expect(platformFactory._processAssetSource).toHaveBeenCalledWith(
        "videoPath"
      );
      expect(platformFactory._processAssetSource).toHaveBeenCalledWith(
        "fileName"
      );
    });
  });

  describe("registerEvent", () => {
    const platformFactory = new PlatformFactory(nativeComponentMapping);

    test("should return on undefined elementId", () => {
      const run = jest.fn(() =>
        platformFactory.registerEvent(undefined, null, null)
      );
      run();
      expect(run).toHaveReturned();
    });

    test("selected events should be served", () => {
      platformFactory.registerEvent("", "onActivate", null);
      platformFactory.registerEvent("", "onClick", null);
      platformFactory.registerEvent("", "onDialogConfirmed", null);
      platformFactory.registerEvent("", "onDialogCanceled", null);
      platformFactory.registerEvent("", "onDialogTimeExpired", null);
      platformFactory.registerEvent("", "onColorCanceled", null);
      platformFactory.registerEvent("", "onColorChanged", null);
      platformFactory.registerEvent("", "onColorConfirmed", null);
      platformFactory.registerEvent("", "onDateChanged", null);
      platformFactory.registerEvent("", "onDateConfirmed", null);
      platformFactory.registerEvent("", "onScrollChanged", null);
      platformFactory.registerEvent("", "onSelectionChanged", null);
      platformFactory.registerEvent("", "onSliderChanged", null);
      platformFactory.registerEvent("", "onConfirmationCompleted", null);
      platformFactory.registerEvent("", "onConfirmationUpdated", null);
      platformFactory.registerEvent("", "onConfirmationCanceled", null);
      platformFactory.registerEvent("", "onTextChanged", null);
      platformFactory.registerEvent("", "onTimeChanged", null);
      platformFactory.registerEvent("", "onTimeConfirmed", null);
      platformFactory.registerEvent("", "onToggleChanged", null);
      platformFactory.registerEvent("", "onVideoPrepared", null);

      const m = platformFactory.componentManager;
      expect(m.addOnActivateEventHandler).toHaveBeenCalledTimes(2);
      expect(m.addOnDialogConfirmedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnDialogCanceledEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnDialogTimeExpiredEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnColorConfirmedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnColorCanceledEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnColorChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnDateChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnDateConfirmedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnScrollChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnSelectionChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnSliderChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnConfirmationCompletedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnConfirmationUpdatedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnConfirmationCanceledEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnTextChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnTimeChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnTimeConfirmedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnToggleChangedEventHandler).toHaveBeenCalledTimes(1);
      expect(m.addOnVideoPreparedEventHandler).toHaveBeenCalledTimes(1);
    });

    test("events should be added to array", () => {
      platformFactory.eventsByElementId = {};
      platformFactory.registerEvent("mockId", "onClick", "mockHandler");
      expect(platformFactory.eventsByElementId).toEqual({
        mockId: [
          {
            handler: "mockHandler",
            name: "onClick"
          }
        ]
      });
      platformFactory.registerEvent("mockId", "onClick", "mockHandler2");
      expect(platformFactory.eventsByElementId).toEqual({
        mockId: [
          {
            handler: "mockHandler",
            name: "onClick"
          },
          {
            handler: "mockHandler2",
            name: "onClick"
          }
        ]
      });
    });
  });

  describe("_createElement", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    platformFactory._processCustomProps = jest.fn((x, props) => props);
    platformFactory.setComponentEvents = jest.fn();
    platformFactory.elementBuilders["mock"] = new (class {
      create = jest.fn();
    })();

    test("new ID should be generated if it is null", () => {
      expect(platformFactory._createElement("mock", null, {})).toEqual(
        expect.objectContaining({ id: "mockId" })
      );
      expect(generateId).toHaveBeenCalled();
    });

    test("old ID should be returned if it is not null", () => {
      expect(
        platformFactory._createElement("mock", null, { id: "oldId" })
      ).toEqual(expect.objectContaining({ id: "oldId" }));
      expect(generateId).not.toHaveBeenCalled();
    });

    test("element builder should be called", () => {
      platformFactory._createElement("mock", null, { id: "mockId" });
      expect(
        platformFactory.elementBuilders["mock"].create
      ).toHaveBeenCalledWith({ id: "mockId" }, "mockId");
    });

    test("setComponentEvents should be called", () => {
      platformFactory._createElement("mock", null, { id: "mockId" });
      expect(platformFactory.setComponentEvents).toHaveBeenCalledWith(
        "mockId",
        {
          id: "mockId"
        }
      );
    });

    test("should return name, id, props", () => {
      expect(
        platformFactory._createElement("mock", null, { id: "mockId" })
      ).toEqual({ id: "mockId", name: "mock", props: { id: "mockId" } });
    });
  });

  describe("startListeningEvent", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("should correctly prepare eventsManager ", () => {
      platformFactory.setupEventsManager();
      expect(Object.keys(platformFactory.eventsByElementId).length).toEqual(0);
    });
  });

  describe("startListeningEvent", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    platformFactory.setupEventsManager();
    describe("when correct eventName", () => {
      test("should register ", () => {
        platformFactory.startListeningEvent("onClick");
        // expect(platformFactory.eventsManager.addListener).toHaveBeenCalled();
      });
    });
  });

  describe("setComponentEvents", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when multiple handles provided", () => {
      test("should register all", () => {
        platformFactory.registerEvent = jest.fn();
        platformFactory.setComponentEvents("elementId", {
          onClick: () => {},
          onPress: () => {}
        });
        expect(platformFactory.registerEvent).toHaveBeenCalledTimes(2);
      });
    });

    describe("when eventName undefined", () => {
      test("should throw error", () => {
        // expect(() => { platformFactory.setComponentEvents("elementId", { }) }).toThrowError(Error);
      });
    });

    describe("when handle isn't funcion", () => {
      test("should throw error", () => {
        expect(() => {
          platformFactory.setComponentEvents("elementId", { onClick: {} });
        }).toThrowError(Error);
      });
    });
  });

  describe("_processAssetSource", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when arg is number", () => {
      test("should Image asset", () => {
        const assetPath_number = 1234;
        expect(platformFactory._processAssetSource(assetPath_number)).toEqual(
          null
        ); // this asset doesn't exist it's why we receive null
      });
    });

    describe("when arg is string", () => {
      test("should return arg as path", () => {
        const assetPath_string = "pathTOASSET";
        expect(platformFactory._processAssetSource(assetPath_string)).toEqual(
          "pathTOASSET"
        );
      });
    });
  });

  describe("_processColor", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);

    describe("when arg is string", () => {
      test("should return array", () => {
        const textColorString = "white";
        const expectedColor_fromText = [1.0, 1.0, 1.0, 1.0];
        expect(platformFactory._processColor(textColorString)).toEqual(
          expectedColor_fromText
        );
        const hexColorString = "#FFFFFF";
        const expectedColor_fromHex = [1.0, 1.0, 1.0, 1.0];
        expect(platformFactory._processColor(hexColorString)).toEqual(
          expectedColor_fromHex
        );
      });
    });
  });

  describe("updateElement", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when name arg isn't string", () => {
      test("should throw error", () => {
        const elementName = 1234;
        expect(() => {
          platformFactory.updateElement(elementName, "", "");
        }).toThrowError(Error);
      });
    });

    describe("when element isn't managed", () => {
      test("should throw error", () => {
        const elementName = "elementName";
        expect(() => {
          platformFactory.updateElement(elementName, "", "");
        }).toThrowError(Error);
      });
    });

    describe("when element is managed", () => {
      test("should process prop update", () => {
        const elementName = "button";
        const oldProp = { children: 123 };
        const newProp = { children: 456 };

        platformFactory.updateElement(elementName, {}, oldProp, newProp);

        const componentManager = platformFactory.componentManager;
        expect(componentManager.updateNode).toHaveBeenCalledWith(undefined, {
          text: "456"
        });
      });
    });
  });

  describe("resetAfterCommit", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("updateLayout should be called", () => {
      platformFactory.resetAfterCommit();
      const componentManager = platformFactory.componentManager;
      expect(componentManager.updateLayout).toHaveBeenCalled();
    });
  });

  describe("removeChildFromContainer", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("removeChildNodeFromRoot should be called", () => {
      const stubbedContainer = {};
      const stubbedChild = { id: "stubbedChildId" };
      platformFactory.removeChildFromContainer(stubbedContainer, stubbedChild);
      const componentManager = platformFactory.componentManager;
      expect(componentManager.removeChildNodeFromRoot).toHaveBeenCalledWith(
        "stubbedChildId"
      );
    });
  });

  describe("appendChildToContainer", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("addChildNodeToContainer should be called", () => {
      const stubbedContainer = {};
      const stubbedChild = { id: "stubbedChildId" };
      platformFactory.appendChildToContainer(stubbedContainer, stubbedChild);
      const componentManager = platformFactory.componentManager;
      expect(componentManager.addChildNodeToContainer).toHaveBeenCalledWith(
        "stubbedChildId"
      );
    });
  });

  describe("insertBefore", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when child is kind of string", () => {
      test("updateNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_string = "stubbedChildId";
        platformFactory.insertBefore(stubbedParent, stubbedChildId_string);
        const componentManager = platformFactory.componentManager;
        expect(componentManager.updateNode).toHaveBeenCalledWith(
          "stubbedParentId",
          {
            text: stubbedChildId_string.toString()
          }
        );
      });
    });

    describe("when child is kind of number", () => {
      test("updateNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_number = 1313;
        platformFactory.insertBefore(stubbedParent, stubbedChildId_number);
        const componentManager = platformFactory.componentManager;
        expect(componentManager.updateNode).toHaveBeenCalledWith(
          "stubbedParentId",
          {
            text: stubbedChildId_number.toString()
          }
        );
      });
    });

    describe("when child is kind of object", () => {
      test("addChildNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChild = { id: "stubbedChildId" };
        platformFactory.insertBefore(stubbedParent, stubbedChild);
        const componentManager = platformFactory.componentManager;
        expect(componentManager.addChildNode).toHaveBeenCalledWith(
          "stubbedChildId",
          "stubbedParentId"
        );
      });
    });
  });

  describe("addChildElement", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when child is kind of string", () => {
      test("nothing shouuld happen", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_string = "stubbedChildId";
        platformFactory.addChildElement(stubbedParent, stubbedChildId_string);
      });
    });

    describe("when child is kind of number", () => {
      test("nothing shouuld happen", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_number = 1313;
        platformFactory.addChildElement(stubbedParent, stubbedChildId_number);
      });
    });

    describe("when child is kind of object", () => {
      test("addChildNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChild = { id: "stubbedChildId" };
        platformFactory.addChildElement(stubbedParent, stubbedChild);
        const componentManager = platformFactory.componentManager;
        expect(componentManager.addChildNode).toHaveBeenCalledWith(
          "stubbedChildId",
          "stubbedParentId"
        );
      });
    });
  });

  describe("removeChildElement", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    describe("when child is kind of string", () => {
      test("updateNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_string = "stubbedChildId";
        platformFactory.removeChildElement(
          stubbedParent,
          stubbedChildId_string
        );
        const componentManager = platformFactory.componentManager;
        expect(
          componentManager.updateNode
        ).toHaveBeenCalledWith("stubbedParentId", { text: "" });
      });
    });

    describe("when child is kind of number", () => {
      test("updateNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChildId_number = 1313;
        platformFactory.removeChildElement(
          stubbedParent,
          stubbedChildId_number
        );
        const componentManager = platformFactory.componentManager;
        expect(
          componentManager.updateNode
        ).toHaveBeenCalledWith("stubbedParentId", { text: "" });
      });
    });

    describe("when child is kind of object", () => {
      test("removeChildNode should be called", () => {
        const stubbedParent = { id: "stubbedParentId" };
        const stubbedChild = { id: "stubbedChildId" };
        platformFactory.removeChildElement(stubbedParent, stubbedChild);
        const componentManager = platformFactory.componentManager;
        expect(componentManager.removeChildNode).toHaveBeenCalledWith(
          "stubbedChildId",
          "stubbedParentId"
        );
      });
    });
  });

  // Functions with empty implementation

  describe("commitTextUpdate", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("commitTextUpdate - nothing shouuld happen", () => {
      platformFactory.commitTextUpdate({}, {}, {});
    });
  });

  describe("createApp", () => {
    var platformFactory = new PlatformFactory(nativeComponentMapping);
    test("createApp - nothing shouuld happen", () => {
      platformFactory.createApp({});
    });
  });
});
