// %BANNER_BEGIN%
// ---------------------------------------------------------------------
// %COPYRIGHT_BEGIN%
//
// Copyright (c) 2019 Magic Leap, Inc. (COMPANY) All Rights Reserved.
// Magic Leap, Inc. Confidential and Proprietary
//
// NOTICE: All information contained herein is, and remains the property
// of COMPANY. The intellectual and technical concepts contained herein
// are proprietary to COMPANY and may be covered by U.S. and Foreign
// Patents, patents in process, and are protected by trade secret or
// copyright law. Dissemination of this information or reproduction of
// this material is strictly forbidden unless prior written permission is
// obtained from COMPANY. Access to the source code contained herein is
// hereby forbidden to anyone except current COMPANY employees, managers
// or contractors who have executed Confidentiality and Non-disclosure
// agreements explicitly covering such access.
//
// The copyright notice above does not evidence any actual or intended
// publication or disclosure of this source code, which includes
// information that is confidential and/or proprietary, and is a trade
// secret, of COMPANY. ANY REPRODUCTION, MODIFICATION, DISTRIBUTION,
// PUBLIC PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS
// SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF COMPANY IS
// STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE LAWS AND
// INTERNATIONAL TREATIES. THE RECEIPT OR POSSESSION OF THIS SOURCE
// CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
// TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE,
// USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
// %COPYRIGHT_END%
// ---------------------------------------------------------------------
// %BANNER_END%
#import <Foundation/Foundation.h>
#import <ARKit/ARKit.h>
#import <CoreLocation/CoreLocation.h>
#import <mlxr_ios_client_internal/mlxr_anchor.h>
#import <mlxr_ios_client_internal/mlxr_localization.h>

NS_ASSUME_NONNULL_BEGIN

/// Manages the client session for shared experience in the real world.
@interface MLXRSession : NSObject
/// Default @c init is disabled.
- (instancetype)init NS_UNAVAILABLE;

/// Initializes the client session.
///
/// @param autoToken Authentication token.
/// @param session ARSession object acquired from ARKit.
///
/// @return The client session instance, or @c nil if the parameters are not valid.
- (instancetype)initWith:(id)autoToken :(ARSession *)session;

/// Connects to the local cloud server.
///
/// @param address Address of the server to connect to.
/// @param deviceId Device ID.
/// @param token Authentication token.
///
/// @return @c true if successfully connected, @c false otherwise.
- (BOOL)connect:(NSString *)address :(NSString *)deviceId :(NSString *)token;

/// Updates the camera frame along with the location information to localize into a shared map.
///
/// @param frame ARFrame object captured from the ARSession.
/// @param location CLLocation object containing the geographical location .
///
/// @return @c true if successfully updated, @c false otherwise.
- (BOOL)update:(ARFrame *)frame :(CLLocation *)location;

/// Gets all anchors found in the scene.
///
/// @sa @c MLXRAnchor for the properties of each anchor.
///
/// @return An array of anchors found in the scene.
- (NSArray<MLXRAnchor *> *)getAllAnchors;

/// Gets an anchor by ID.
///
/// @sa @c MLXRAnchor for the properties of the anchor. Call @c getAllAnchors to refresh the anchors in the scene.
///
/// @param anchorId ID of the anchor.
///
/// @return An anchor with the given ID, or @c nil if error occurs.
- (MLXRAnchor * _Nullable)getAnchorByPcfId:(NSUUID *)anchorId;

/// Gets the localization result.
///
/// @sa @c MLXRLocalizationResult for the possible status.
///
/// @return Localiation status, or @c nil if error occurs.
- (MLXRLocalizationResult * _Nullable)getLocalizationStatus;

typedef struct sessionType * sessionType;
@property (readonly) sessionType session_;
@end

NS_ASSUME_NONNULL_END
