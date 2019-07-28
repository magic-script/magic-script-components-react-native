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
#import <simd/simd.h>

NS_ASSUME_NONNULL_BEGIN

/// Contains information about the anchor state.
@interface MLXRAnchorState : NSObject
{
    BOOL tracked;
}
/// @c YES or @c true if tracked. @c NO or @c false otherwise.
@property BOOL tracked;
@end


/// Contains information about the accuracy of the anchor.
@interface MLXRAnchorConfidence : NSObject
{
    NSNumber *confidence;
    NSNumber *validRadiusM;
    NSNumber *rotationErrDeg;
    NSNumber *translationErrM;
}
/// A confidence value (from [0, 1]) representing the confidence in the Persistent Coordinate Frame error within the valid radius.
@property (strong) NSNumber *confidence;
/// The radius (in meters) within which the confidence is valid.
@property (strong) NSNumber *validRadiusM;
/// The rotational error (in degrees).
@property (strong) NSNumber *rotationErrDeg;
/// The translation error (in meters).
@property (strong) NSNumber *translationErrM;
@end


/// Contains data about the anchor pose.
@interface MLXRAnchorPose : NSObject
{
    /// A 4x4 matrix contating the position and rotation of the anchor.
    matrix_float4x4 pose;
}
@property matrix_float4x4 pose;
@end


/// Contains methods to query the anchor information such as state, pose, and more.
@interface MLXRAnchor : NSObject
/// Default @c init is disabled.
- (instancetype)init NS_UNAVAILABLE;

/// Initializes the anchor object with its handle.
/// This instance is created by @c MLXRSession.getAllAnchors() .
///
/// @param apiAnchor Anchor handle.
///
/// @return The anchor instance.
- (instancetype)initWith:(id)apiAnchor;

/// Gets the anchor state.
///
/// @return See @c MLXRAnchorState. Returns @c nil if failed to query the state.
- (MLXRAnchorState * _Nullable)getState;

/// Gets the confidence level of the anchor.
///
/// @return See @c MLXRAnchorConfidence . Returns @c nil if failed to query the confidence.
- (MLXRAnchorConfidence * _Nullable)getConfidence;

/// Gets the pose of the anchor.
///
/// @return See @c MLXRAnchorPose . Returns @c nil if failed to query the pose.
- (MLXRAnchorPose * _Nullable)getPose;

/// Gets the ID of the anchor.
///
/// @return ID of the anchor. Returns @c nil if failed to query the ID.
- (NSUUID * _Nullable)getId;

@end

NS_ASSUME_NONNULL_END
