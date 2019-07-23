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

#include <stdbool.h>

/*!
  \defgroup Frame Frame
  \addtogroup Frame
  \brief Information about the image captured during the MLXR session.
 \{
*/

/*! Opaque data type for Frame. */
typedef struct MLXrFrame MLXrFrame;

/*! Image types. */
typedef enum MLXrImageType {
  MLXrImageType_BitMask,
  MLXrImageType_R8,
  MLXrImageType_RGB24,
  MLXrImageType_RGBA32,
  MLXrImageType_YUV,
  MLXrImageType_Ensure32Bits = 0x7FFFFFFF
} MLXrImageType;

/*! Image information. */
typedef struct MLXrImage {
  /*! Width. */
  uint32_t width;
  /*! Height. */
  uint32_t height;
  /*! Row stride. The number of bytes per row. */
  uint32_t stride;
  /*! Image type. */
  MLXrImageType image_type;
  /*! Image data. */
  uint8_t *data;
} MLXrImage;

/*! Camera intrinsics. */
typedef struct MLXrCameraIntrinsics {
  uint32_t width;
  uint32_t height;
  MLXrVec2f focal_length;
  MLXrVec2f principal_point;
  float fov;
} MLXrCameraIntrinsics;

/*! Camera metadata color correction mode. */
typedef enum MLXrCameraMetadataColorCorrectionMode {
  MLXrCameraMetadataColorCorrectionMode_TransformMatrix = 0,
  MLXrCameraMetadataColorCorrectionMode_Fast,
  MLXrCameraMetadataColorCorrectionMode_HighQuality,
  MLXrCameraMetadataColorCorrectionMode_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataColorCorrectionMode;

/*! Camera metadata color correction aberration mode. */
typedef enum MLXrCameraMetadataColorCorrectionAberrationMode {
  MLXrCameraMetadataColorCorrectionAberrationMode_Off = 0,
  MLXrCameraMetadataColorCorrectionAberrationMode_Fast,
  MLXrCameraMetadataColorCorrectionAberrationMode_HighQuality,
  MLXrCameraMetadataColorCorrectionAberrationMode_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataColorCorrectionAberrationMode;

/*! Camera metadata control AE anti banding mode. */
typedef enum MLXrCameraMetadataControlAEAntibandingMode {
  MLXrCameraMetadataControlAEAntibandingMode_Off = 0,
  MLXrCameraMetadataControlAEAntibandingMode_50Hz,
  MLXrCameraMetadataControlAEAntibandingMode_60Hz,
  MLXrCameraMetadataControlAEAntibandingMode_Auto,
  MLXrCameraMetadataControlAEAntibandingMode_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAEAntibandingMode;

/*! Camera metadata control AE lock. */
typedef enum MLXrCameraMetadataControlAELock {
  MLXrCameraMetadataControlAELock_Off = 0,
  MLXrCameraMetadataControlAELock_On,
  MLXrCameraMetadataControlAELock_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAELock;

/*! Camera metadata control AE mode. */
typedef enum MLXrCameraMetadataControlAEMode {
  MLXrCameraMetadataControlAEMode_Off = 0,
  MLXrCameraMetadataControlAEMode_On,
  MLXrCameraMetadataControlAEMode_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAEMode;

/*! Camera metadata control AW block. */
typedef enum MLXrCameraMetadataControlAWBLock {
  MLXrCameraMetadataControlAWBLock_Off = 0,
  MLXrCameraMetadataControlAWBLock_On,
  MLXrCameraMetadataControlAWBLock_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAWBLock;

/*! Camera metadata control AWB mode. */
typedef enum MLXrCameraMetadataControlAWBMode {
  MLXrCameraMetadataControlAWBMode_Off = 0,
  MLXrCameraMetadataControlAWBMode_Auto,
  MLXrCameraMetadataControlAWBMode_Incandescent,
  MLXrCameraMetadataControlAWBMode_Fluorescent,
  MLXrCameraMetadataControlAWBMode_WarmFluorescent,
  MLXrCameraMetadataControlAWBMode_Daylight,
  MLXrCameraMetadataControlAWBMode_CloudyDaylight,
  MLXrCameraMetadataControlAWBMode_Twilight,
  MLXrCameraMetadataControlAWBMode_Shade,
  MLXrCameraMetadataControlAWBMode_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAWBMode;

/*! Camera metadata control AE state. */
typedef enum MLXrCameraMetadataControlAEState {
  MLXrCameraMetadataControlAEState_Inactive = 0,
  MLXrCameraMetadataControlAEState_Searching,
  MLXrCameraMetadataControlAEState_Converged,
  MLXrCameraMetadataControlAEState_Locked,
  MLXrCameraMetadataControlAEState_FlashRequired,
  MLXrCameraMetadataControlAEState_PreCapture,
  MLXrCameraMetadataControlAEState_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAEState;

/*! Camera metadata control AWB state */
typedef enum MLXrCameraMetadataControlAWBState {
  MLXrCameraMetadataControlAWBState_Inactive = 0,
  MLXrCameraMetadataControlAWBState_Searching,
  MLXrCameraMetadataControlAWBState_Converged,
  MLXrCameraMetadataControlAWBState_Locked,
  MLXrCameraMetadataControlAWBState_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataControlAWBState;

/*! Camera metadata scaler available formats. */
typedef enum MLXrCameraMetadataScalerAvailableFormats {
  MLXrCameraMetadataScalerAvailableFormats_RAW16,
  MLXrCameraMetadataScalerAvailableFormats_RAW_OPAQUE,
  MLXrCameraMetadataScalerAvailableFormats_YV12,
  MLXrCameraMetadataScalerAvailableFormats_YCrCb_420_SP,
  MLXrCameraMetadataScalerAvailableFormats_IMPLEMENTATION_DEFINED,
  MLXrCameraMetadataScalerAvailableFormats_YCbCr_420_888,
  MLXrCameraMetadataScalerAvailableFormats_BLOB,
  MLXrCameraMetadataScalerAvailableFormats_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataScalerAvailableFormats;

/*! Camera metadata scaler available stream configurations. */
typedef enum MLXrCameraMetadataScalerAvailableStreamConfigurations {
  MLXrCameraMetadataScalerAvailableStreamConfigurations_OUTPUT = 0,
  MLXrCameraMetadataScalerAvailableStreamConfigurations_INPUT,
  MLXrCameraMetadataScalerAvailableStreamConfigurations_Ensure32Bits = 0x7FFFFFFF
} MLXrCameraMetadataScalerAvailableStreamConfigurations;

/*! Camera image metadata. */
typedef struct MLXrCameraImageMetadata {
  MLXrCameraMetadataColorCorrectionMode color_correction_mode;
  MLXrCameraMetadataColorCorrectionAberrationMode color_correction_aberration_mode;
  MLXrCameraMetadataControlAEAntibandingMode control_ae_antibanding_mode;
  MLXrCameraMetadataControlAELock control_ae_lock;
  MLXrCameraMetadataControlAEMode control_ae_mode;
  MLXrCameraMetadataControlAWBLock control_awb_lock;
  MLXrCameraMetadataControlAWBMode control_awb_mode;
  MLXrCameraMetadataControlAEState control_ae_state;
  MLXrCameraMetadataControlAWBState control_awb_state;
  MLXrCameraMetadataScalerAvailableFormats scaler_available_formats;
  MLXrCameraMetadataScalerAvailableStreamConfigurations scaler_available_stream_configurations;
} MLXrCameraImageMetadata;

/*! Camera frame data. */
typedef struct MLXrCameraFrameData {
  /*! The position and rotation of the camera. */
  MLXrPose pose;
  /*! The image data and information. */
  MLXrImage image;
  /*! Camera intrinsics. */
  MLXrCameraIntrinsics intrinsics;
  /*! Camera metadata. */
  MLXrCameraImageMetadata metadata;
  /*! UNIX timestamp when the frame was captured. */
  uint64_t timestamp;
} MLXrCameraFrameData;

/*! Location data. */
typedef struct MLXrLocationData {
  /*! The latitude of the geographical location. */
  double lat;
  /*! The longitude of the geographical location. */
  double lon;
} MLXrLocationData;

MLXR_EXTERN_C_BEGIN

/*!
  \brief Checks if it is ready to process the image for localization via MLXrBackendFrameUpdate().

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] frame Frame handle acquired by MLXrBackendSessionCreateFrame().
  \param[out] out_ready True if ready, false otherwise. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The result was successfully retrieved. Check out_ready for result.
*/
MLXrResult MLXrBackendFrameIsReadyForImage(MLXrSession *session, MLXrFrame *frame, bool *out_ready);

/*!
  \brief Sets the camera frame data for localization via MLXrBackendFrameUpdate().

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] frame Frame handle acquired by MLXrBackendSessionCreateFrame().
  \param[in] frame_data Frame data to process for localization.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The frame data has been set successfully.
*/
MLXrResult MLXrBackendFrameSetCameraFrameData(MLXrSession *session, MLXrFrame *frame, const MLXrCameraFrameData *frame_data);

/*!
  \brief Sets the camera location for localization via MLXrBackendFrameUpdate().

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] frame Frame handle acquired by MLXrBackendSessionCreateFrame().
  \param[in] location Location to process for localization.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The location has been set successfully.
*/
MLXrResult MLXrBackendFrameSetLocation(MLXrSession *session, MLXrFrame *frame, MLXrLocationData location);

/*!
  \brief Updates the frame data and location for localization.

  The frame data and location can be set via MLXrBackendFrameSetCameraFrameData() and
  MLXrBackendFrameSetLocation().

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] frame Frame handle acquired by MLXrBackendSessionCreateFrame().

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The frame data and location have been updated successfully.
*/
MLXrResult MLXrBackendFrameUpdate(MLXrSession *session, MLXrFrame *frame);

/*!
  \brief Released the frame resources.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] frame Frame handle acquired by MLXrBackendSessionCreateFrame().

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The frame resources have been released successfully.
*/
MLXrResult MLXrBackendFrameRelease(MLXrSession *session, MLXrFrame *frame);

MLXR_EXTERN_C_END
