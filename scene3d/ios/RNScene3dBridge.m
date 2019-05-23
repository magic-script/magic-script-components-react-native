
#import "RNScene3dBridge.h"

@implementation RNScene3dBridge

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
  
