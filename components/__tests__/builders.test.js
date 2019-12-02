import AudioBuilder from "../platform/elements/builders/audio-builder.js";
import ButtonBuilder from "../platform/elements/builders/button-builder.js";
import CircleConfirmationBuilder from "../platform/elements/builders/circle-confirmation-builder.js";
import ColorPickerBuilder from "../platform/elements/builders/color-picker-builder.js";
import ContentBuilder from "../platform/elements/builders/content-builder.js";
import DatePickerBuilder from "../platform/elements/builders/date-picker-builder.js";
import DialogBuilder from "../platform/elements/builders/dialog-builder.js";
import DropdownListBuilder from "../platform/elements/builders/dropdown-list-builder.js";
import DropdownListItemBuilder from "../platform/elements/builders/dropdown-list-item-builder.js";
import GridLayoutBuilder from "../platform/elements/builders/grid-layout-builder.js";
import GroupBuilder from "../platform/elements/builders/group-builder.js";
import ImageBuilder from "../platform/elements/builders/image-builder.js";
import LinearLayoutBuilder from "../platform/elements/builders/linear-layout-builder.js";
import LineBuilder from "../platform/elements/builders/line-builder.js";
import ListViewBuilder from "../platform/elements/builders/listview-builder.js";
import ListViewItemBuilder from "../platform/elements/builders/listview-item-builder.js";
import ModelBuilder from "../platform/elements/builders/model-builder.js";
import PageViewBuilder from "../platform/elements/builders/page-view-builder.js";
import PanelBuilder from "../platform/elements/builders/panel-builder.js";
import ProgressBarBuilder from "../platform/elements/builders/progress-bar-builder.js";
import RectLayoutBuilder from "../platform/elements/builders/rect-layout-builder.js";
import ScrollBarBuilder from "../platform/elements/builders/scroll-bar-builder.js";
import ScrollViewBuilder from "../platform/elements/builders/scroll-view-builder.js";
import SliderBuilder from "../platform/elements/builders/slider-builder.js";
import SpinnerBuilder from "../platform/elements/builders/spinner-builder.js";
import TabBuilder from "../platform/elements/builders/tab-builder.js";
import TextBuilder from "../platform/elements/builders/text-builder.js";
import TextEditBuilder from "../platform/elements/builders/text-edit-builder.js";
import TimePickerBuilder from "../platform/elements/builders/time-picker-builder.js";
import ToggleBuilder from "../platform/elements/builders/toggle-builder.js";
import ToggleGroupBuilder from "../platform/elements/builders/toggle-group-builder.js";
import VideoBuilder from "../platform/elements/builders/video-builder.js";
import WebViewBuilder from "../platform/elements/builders/webview-builder.js";

const componentManager = class {};
componentManager.createAudioNode = jest.fn();
componentManager.createButtonNode = jest.fn();
componentManager.createCircleConfirmationNode = jest.fn();
componentManager.createColorPickerNode = jest.fn();
componentManager.createContentNode = jest.fn();
componentManager.createDatePickerNode = jest.fn();
componentManager.createDialogNode = jest.fn();
componentManager.createDropdownListNode = jest.fn();
componentManager.createDropdownListItemNode = jest.fn();
componentManager.createGridLayoutNode = jest.fn();
componentManager.createGroupNode = jest.fn();
componentManager.createImageNode = jest.fn();
componentManager.createLineNode = jest.fn();
componentManager.createLinearLayoutNode = jest.fn();
componentManager.createListViewNode = jest.fn();
componentManager.createListViewItemNode = jest.fn();
componentManager.createModelNode = jest.fn();
componentManager.createPageViewNode = jest.fn();
componentManager.createPanelNode = jest.fn();
componentManager.createProgressBarNode = jest.fn();
componentManager.createRectLayoutNode = jest.fn();
componentManager.createScrollBarNode = jest.fn();
componentManager.createScrollViewNode = jest.fn();
componentManager.createSliderNode = jest.fn();
componentManager.createSpinnerNode = jest.fn();
componentManager.createTabNode = jest.fn();
componentManager.createTextNode = jest.fn();
componentManager.createTextEditNode = jest.fn();
componentManager.createTimePickerNode = jest.fn();
componentManager.createToggleNode = jest.fn();
componentManager.createToggleGroupNode = jest.fn();
componentManager.createVideoNode = jest.fn();
componentManager.createWebViewNode = jest.fn();

const props = {};
const nodeId = "nodeId";

describe("builders", () => {
  describe("AudioBuilder", () => {
    test("should call native builder", () => {
      new AudioBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createAudioNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ButtonBuilder", () => {
    test("should call native builder", () => {
      new ButtonBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createButtonNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("CircleConfirmationBuilder", () => {
    test("should call native builder", () => {
      new CircleConfirmationBuilder(componentManager).create(props, nodeId);
      expect(
        componentManager.createCircleConfirmationNode
      ).toHaveBeenCalledWith(props, nodeId);
    });
  });

  describe("ColorPickerBuilder", () => {
    test("should call native builder", () => {
      new ColorPickerBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createColorPickerNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ContentBuilder", () => {
    test("should call native builder", () => {
      new ContentBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createContentNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("DatePickerBuilder", () => {
    test("should call native builder", () => {
      new DatePickerBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createDatePickerNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("DialogBuilder", () => {
    test("should call native builder", () => {
      new DialogBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createDialogNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("DropdownListBuilder", () => {
    test("should call native builder", () => {
      new DropdownListBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createDropdownListNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("DropdownListItemBuilder", () => {
    test("should call native builder", () => {
      new DropdownListItemBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createDropdownListItemNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("GridLayoutBuilder", () => {
    test("should call native builder", () => {
      new GridLayoutBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createGridLayoutNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("GroupBuilder", () => {
    test("should call native builder", () => {
      new GroupBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createGroupNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ImageBuilder", () => {
    test("should call native builder", () => {
      new ImageBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createImageNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("LineBuilder", () => {
    test("should call native builder", () => {
      new LineBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createLineNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("LinearLayoutBuilder", () => {
    test("should call native builder", () => {
      new LinearLayoutBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createLinearLayoutNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ListViewBuilder", () => {
    test("should call native builder", () => {
      new ListViewBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createListViewNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ListViewItemBuilder", () => {
    test("should call native builder", () => {
      new ListViewItemBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createListViewItemNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ModelBuilder", () => {
    test("should call native builder", () => {
      new ModelBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createModelNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("PageViewBuilder", () => {
    test("should call native builder", () => {
      new PageViewBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createPageViewNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("PanelBuilder", () => {
    test("should call native builder", () => {
      new PanelBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createPanelNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ProgressBarBuilder", () => {
    test("should call native builder", () => {
      new ProgressBarBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createProgressBarNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("RectLayoutBuilder", () => {
    test("should call native builder", () => {
      new RectLayoutBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createRectLayoutNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ScrollBarBuilder", () => {
    test("should call native builder", () => {
      new ScrollBarBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createScrollBarNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ScrollViewBuilder", () => {
    test("should call native builder", () => {
      new ScrollViewBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createScrollViewNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("SliderBuilder", () => {
    test("should call native builder", () => {
      new SliderBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createSliderNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("SpinnerBuilder", () => {
    test("should call native builder", () => {
      new SpinnerBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createSpinnerNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("TabBuilder", () => {
    test("should call native builder", () => {
      new TabBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createTabNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("TextBuilder", () => {
    test("should call native builder", () => {
      new TextBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createTextNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("TextEditBuilder", () => {
    test("should call native builder", () => {
      new TextEditBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createTextEditNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("TimePickerBuilder", () => {
    test("should call native builder", () => {
      new TimePickerBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createTimePickerNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ToggleBuilder", () => {
    test("should call native builder", () => {
      new ToggleBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createToggleNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("ToggleGroupBuilder", () => {
    test("should call native builder", () => {
      new ToggleGroupBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createToggleGroupNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("VideoBuilder", () => {
    test("should call native builder", () => {
      new VideoBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createVideoNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });

  describe("WebViewBuilder", () => {
    test("should call native builder", () => {
      new WebViewBuilder(componentManager).create(props, nodeId);
      expect(componentManager.createWebViewNode).toHaveBeenCalledWith(
        props,
        nodeId
      );
    });
  });
});
