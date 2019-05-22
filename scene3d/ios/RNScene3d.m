
#import "RNScene3d.h"

@implementation RNScene3d

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(createText) {
  NSLog(@"export method: createText");
//   [_bridge.eventDispatcher sendAppEventWithName:@"greeted" body:@{ @"name": name }];
}

@end
  