//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

#import <React/RCTConvert.h>

@class UiAudioNode;
@class UiButtonNode;
@class UiCircleConfirmationNode;
@class UiColorPickerNode;
@class UiContentNode;
@class UiDatePickerNode;
@class UiDialogNode;
@class UiDropdownListNode;
@class UiDropdownListItemNode;
@class UiGridLayoutNode;
@class UiGroupNode;
@class UiImageNode;
@class UiLineNode;
@class UiLinearLayoutNode;
@class UiListViewNode;
@class UiListViewItemNode;
@class UiModelNode;
@class UiPageViewNode;
@class UiPanelNode;
@class UiProgressBarNode;
@class UiRectLayoutNode;
@class UiScrollBarNode;
@class UiScrollViewNode;
@class UiSliderNode;
@class UiSpinnerNode;
@class UiTabNode;
@class UiTextNode;
@class UiTextEditNode;
@class UiTimePickerNode;
@class UiToggleNode;
@class UiToggleGroupNode;
@class UiVideoNode;
@class UiWebViewNode;

@interface RCTConvert (Components)
+ (UiAudioNode *)UiAudioNode:(id)json;
+ (UiButtonNode *)UiButtonNode:(id)json;
+ (UiCircleConfirmationNode *)UiCircleConfirmationNode:(id)json;
+ (UiColorPickerNode *)UiColorPickerNode:(id)json;
+ (UiContentNode *)UiContentNode:(id)json;
+ (UiDatePickerNode *)UiDatePickerNode:(id)json;
+ (UiDialogNode *)UiDialogNode:(id)json;
+ (UiDropdownListNode *)UiDropdownListNode:(id)json;
+ (UiDropdownListItemNode *)UiDropdownListItemNode:(id)json;
+ (UiGridLayoutNode *)UiGridLayoutNode:(id)json;
+ (UiGroupNode *)UiGroupNode:(id)json;
+ (UiImageNode *)UiImageNode:(id)json;
+ (UiLineNode *)UiLineNode:(id)json;
+ (UiLinearLayoutNode *)UiLinearLayoutNode:(id)json;
+ (UiListViewNode *)UiListViewNode:(id)json;
+ (UiListViewItemNode *)UiListViewItemNode:(id)json;
+ (UiModelNode *)UiModelNode:(id)json;
+ (UiPageViewNode *)UiPageViewNode:(id)json;
+ (UiPanelNode *)UiPanelNode:(id)json;
+ (UiProgressBarNode *)UiProgressBarNode:(id)json;
+ (UiRectLayoutNode *)UiRectLayoutNode:(id)json;
+ (UiScrollBarNode *)UiScrollBarNode:(id)json;
+ (UiScrollViewNode *)UiScrollViewNode:(id)json;
+ (UiSliderNode *)UiSliderNode:(id)json;
+ (UiSpinnerNode *)UiSpinnerNode:(id)json;
+ (UiTabNode *)UiTabNode:(id)json;
+ (UiTextNode *)UiTextNode:(id)json;
+ (UiTextEditNode *)UiTextEditNode:(id)json;
+ (UiTimePickerNode *)UiTimePickerNode:(id)json;
+ (UiToggleNode *)UiToggleNode:(id)json;
+ (UiToggleGroupNode *)UiToggleGroupNode:(id)json;
+ (UiVideoNode *)UiVideoNode:(id)json;
+ (UiWebViewNode *)UiWebViewNode:(id)json;
@end
