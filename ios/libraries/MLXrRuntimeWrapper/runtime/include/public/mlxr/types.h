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
#pragma once

#include "common.h"

/*! UUID used for PCF ID. */
typedef struct MLXrPCFId {
  uint64_t data[2];
} MLXrPCFId;

/*! 2D vector of float numbers. */
typedef struct MLXrVec2f {
  float x;
  float y;
} MLXrVec2f;

/*! 3D vector of float numbers. */
typedef struct MLXrVec3f {
  float x;
  float y;
  float z;
} MLXrVec3f;

/*! Quaternion. */
typedef struct MLXrQuaternionf {
  float x;
  float y;
  float z;
  float w;
} MLXrQuaternionf;

/*! The position and rotation. */
typedef struct MLXrPose {
  /*! Row-major 4x4 matrix. */
  float matrix4x4[16];
} MLXrPose;

/*! Opaque data type for Session. */
typedef struct MLXrSession MLXrSession;

/*! Opaque data type for authentication token. */
typedef struct MLXrAuthToken MLXrAuthToken;
