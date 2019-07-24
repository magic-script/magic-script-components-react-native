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

#include <stdint.h>
#include "types.h"

#ifdef __cplusplus
#define MLXR_EXTERN_C_BEGIN extern "C" {
#define MLXR_EXTERN_C_END }
#else
#define MLXR_EXTERN_C_BEGIN
#define MLXR_EXTERN_C_END
#endif

/*! MLXrResult type that all functions in the API return. */
typedef int32_t MLXrResult;

/*! Macro to set a the prefix bytes of an API specific MLXrResult code. */
#define MLXRRESULT_PREFIX(val) (val << 16)

enum {
  /*! Defines the prefix for global MLResult codes. */
  MLXrResultAPIPrefix_Global = MLXRRESULT_PREFIX(0),
};

typedef enum MLXrResultGlobal {
  /*! Operation completed successfuly. */
  MLXrResult_Ok = MLXrResultAPIPrefix_Global,
  /*! Operation failed due to an invalid parameter being supplied. */
  MLXrResult_InvalidParam,
  /*! Operation failed due to an unspecified internal error. */
  MLXrResult_UnknownError,
  /*! Operation failed because it is not currently implemented. */
  MLXrResult_NotImplemented,
  /*! Ensure enum is represented as 32 bits. */
  MLXrResult_Ensure32Bits = 0x7FFFFFFF
} MLXrResultGlobal;
