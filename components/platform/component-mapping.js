// Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved

import AudioBuilder from "./elements/builders/audio-builder.js";
import ButtonBuilder from "./elements/builders/button-builder.js";
import ColorPickerBuilder from "./elements/builders/color-picker-builder.js";
import DatePickerBuilder from "./elements/builders/date-picker-builder.js";
import DialogBuilder from './elements/builders/dialog-builder.js';
import DropdownListBuilder from "./elements/builders/dropdown-list-builder.js";
import DropdownListItemBuilder from "./elements/builders/dropdown-list-item-builder.js";
import GridLayoutBuilder from "./elements/builders/grid-layout-builder.js";
import GroupBuilder from "./elements/builders/group-builder.js";
import ImageBuilder from "./elements/builders/image-builder.js";
import LinearLayoutBuilder from "./elements/builders/linear-layout-builder.js";
import LineBuilder from "./elements/builders/line-builder.js";
import ListViewBuilder from './elements/builders/listview-builder.js';
import ListViewItemBuilder from './elements/builders/listviewitem-builder.js';
import ModelBuilder from "./elements/builders/model-builder.js";
import ProgressBarBuilder from "./elements/builders/progress-bar-builder.js";
import RectLayoutBuilder from "./elements/builders/rect-layout-builder.js";
import ScrollBarBuilder from "./elements/builders/scroll-bar-builder.js";
import ScrollViewBuilder from "./elements/builders/scroll-view-builder.js";
import SliderBuilder from "./elements/builders/slider-builder.js";
import SpinnerBuilder from "./elements/builders/spinner-builder.js";
import TextBuilder from "./elements/builders/text-builder.js";
import TextEditBuilder from "./elements/builders/text-edit-builder.js";
import TimePickerBuilder from "./elements/builders/time-picker-builder.js";
import ToggleBuilder from "./elements/builders/toggle-builder.js";
import VideoBuilder from "./elements/builders/video-builder.js";
import WebViewBuilder from "./elements/builders/webview-builder.js";

export default {
  version: "1.0",
  platform: "mobile (ios, android)",
  controllers: {
    // 'scene': () => new ControllerBuilder(),
  },
  elements: {
    // ui nodes
    button: componentManager => new ButtonBuilder(componentManager),
    colorPicker: componentManager => new ColorPickerBuilder(componentManager),
    datePicker: componentManager => new DatePickerBuilder(componentManager),
    dialog: (componentManager) => new DialogBuilder(componentManager),
    dropdownList: componentManager => new DropdownListBuilder(componentManager),
    dropdownListItem: componentManager => new DropdownListItemBuilder(componentManager),
    image: componentManager => new ImageBuilder(componentManager),
    listView: (componentManager) => new ListViewBuilder(componentManager),
    listViewItem: (componentManager) => new ListViewItemBuilder(componentManager),
    progressBar: componentManager => new ProgressBarBuilder(componentManager),
    scrollBar: componentManager => new ScrollBarBuilder(componentManager),
    scrollView: componentManager => new ScrollViewBuilder(componentManager),
    slider: componentManager => new SliderBuilder(componentManager),
    spinner: componentManager => new SpinnerBuilder(componentManager),
    text: componentManager => new TextBuilder(componentManager),
    textEdit: componentManager => new TextEditBuilder(componentManager),
    timePicker: componentManager => new TimePickerBuilder(componentManager),
    toggle: componentManager => new ToggleBuilder(componentManager),
    view: componentManager => new GroupBuilder(componentManager),
    webView: componentManager => new WebViewBuilder(componentManager),

    // layouts
    gridLayout: componentManager => new GridLayoutBuilder(componentManager),
    linearLayout: componentManager => new LinearLayoutBuilder(componentManager),
    rectLayout: componentManager => new RectLayoutBuilder(componentManager),

    // render nodes
    line: componentManager => new LineBuilder(componentManager),
    model: componentManager => new ModelBuilder(componentManager),
    video: componentManager => new VideoBuilder(componentManager),

    // transform nodes
    audio: componentManager => new AudioBuilder(componentManager),
  }
};
