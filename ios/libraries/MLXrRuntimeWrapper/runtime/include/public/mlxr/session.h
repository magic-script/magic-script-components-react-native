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
#include "anchor.h"
#include "frame.h"
#include "localization.h"

/*! Callback function to be invoked when the anchors are updated in the scene. */
typedef void(*MLXrAnchorsUpdatedCallback)(void *context);

MLXR_EXTERN_C_BEGIN

/*!
  \brief Creates a session for shared experience in the real world.

  \param[in] token Token.
  \param[in] out_session The session handle created.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The session handle has been created successfully.
*/
MLXrResult MLXrBackendSessionCreate(MLXrAuthToken *token, MLXrSession **out_session);

/*!
  \brief Connects to the Local Cloud.

  \param[in] session session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] address Address of the Local Cloud.
  \param[in] device_id Device ID.
  \param[in] token Authentication token for the Local Cloud.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok Connected to the Local Cloud.
*/
MLXrResult MLXrBackendSessionConnect(MLXrSession *session, const char *address, const char *device_id, const char *token);

/*!
  \brief Releases the session when the it is no longer needed.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The session has been released successfully.
*/
MLXrResult MLXrBackendSessionDestroy(MLXrSession *session);

/*!
  \brief Creates a Frame. See frame.h for operations.

  Call MLXrBackendFrameRelease() after use to release the resources.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[out] out_frame Frame handle created.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The Frame handle has been released successfully.
*/
MLXrResult MLXrBackendSessionCreateFrame(MLXrSession *session, MLXrFrame **out_frame);

/*!
  \brief Gets all anchors in the scene. See anchor.h for operations.

  Call MLXrBackendAnchorListRelease() after use to release the resources.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[out] out_list Anchor List handle created.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The Anchor List has been retrieved successfully.
*/
MLXrResult MLXrBackendSessionGetAllAnchors(MLXrSession *session, MLXrAnchorList **out_list);

/*!
  \brief Sets the callback function when the anchors are updated in the scene.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] callback Callback function to invoke.
  \param[in] context User data to be passed to the callback function.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The callback function has been set successfully.
*/
MLXrResult MLXrBackendSessionSetAnchorsUpdatedCallback(MLXrSession *session, MLXrAnchorsUpdatedCallback callback, void *context);

/*!
  \brief Gets the localization status.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[out] out_loc_status Location status. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The localization status has been retrieved successfully.
*/
MLXrResult MLXrBackendSessionGetLocalizationStatus(MLXrSession *session, MLXrLocalizationStatus *out_loc_status);

MLXR_EXTERN_C_END
