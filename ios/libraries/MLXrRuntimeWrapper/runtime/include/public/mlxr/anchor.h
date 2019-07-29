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

/*!
  \defgroup Anchor Anchor
  \addtogroup Anchor
  \brief Information about the contents that persist at locations in the real world.
 \{
*/

/*! Opaque data type for Anchor. */
typedef struct MLXrAnchor MLXrAnchor;

/*! Opaque data type for Anchor List. */
typedef struct MLXrAnchorList MLXrAnchorList;

/*! Anchor states. */
typedef enum MLXrAnchorState {
  MLXrAnchorState_NotTracked,
  MLXrAnchorState_Tracked,
  MLXrAnchorState_Ensure32Bits = 0x7FFFFFFF
} MLXrAnchorState;

/*! Anchor Confidence. */
typedef struct MLXrAnchorConfidence {
  float confidence;
  float valid_radius_m;
  float rotation_err_deg;
  float translation_err_m;
} MLXrAnchorConfidence;

MLXR_EXTERN_C_BEGIN

/*!
  \brief Gets the anchor state.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] anchor Anchor handle acquired by a MLXrBackendAnchorListGet* function.
  \param[out] out_state Anchor state. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor state was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorGetState(MLXrSession *session, MLXrAnchor *anchor, MLXrAnchorState *out_state);

/*!
  \brief Gets the anchor confidence.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] anchor Anchor handle acquired by a MLXrBackendAnchorListGet* function
  \param[out] out_state Anchor confidence. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor confidence was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorGetConfidence(MLXrSession *session, MLXrAnchor *anchor, MLXrAnchorConfidence *out_state);

/*!
  \brief Gets the anchor pose.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] anchor Anchor handle acquired by a MLXrBackendAnchorListGet* function.
  \param[out] out_pose Anchor pose. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor pose was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorGetPose(MLXrSession *session, MLXrAnchor *anchor, MLXrPose *out_pose);

/*!
  \brief Gets the anchor ID(UUID).

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] anchor Anchor handle acquired by a MLXrBackendAnchorListGet* function.
  \param[out] out_id Anchor ID. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor ID was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorGetPCFId(MLXrSession *session, MLXrAnchor *anchor, MLXrPCFId *out_id);

/*!
  \brief Gets the number of anchors in the anchor list.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] list Anchor List handle acquired by MLXrBackendSessionGetAllAnchors().
  \param[out] out_size The number of anchors. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The number of anchors was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorListGetSize(MLXrSession *session, MLXrAnchorList *list, uint32_t *out_size);

/*!
  \brief Gets an anchor by index from the given anchor list.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] list Anchor List handle acquired by MLXrBackendSessionGetAllAnchors().
  \param[in] index Zero-based index of the anchor from the anchor list.
  \param[out] out_anchor The anchor at the given index. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorListGetAnchor(MLXrSession *session, MLXrAnchorList *list, uint32_t index, MLXrAnchor **out_anchor);

/*!
  \brief Gets an anchor by ID from the given anchor list.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] list Anchor List handle acquired by MLXrBackendSessionGetAllAnchors().
  \param[in] id The anchor ID to search.
  \param[out] out_anchor The anchor with the given ID. Valid only if the function returns #MLXrResult_Ok.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The anchor was retrieved successfully.
*/
MLXrResult MLXrBackendAnchorListGetAnchorByPCFId(MLXrSession *session, MLXrAnchorList *list, const MLXrPCFId *id, MLXrAnchor **out_anchor);

/*!
  \brief Releases the resources allocated for the anchor list by MLXrBackendSessionGetAllAnchors().

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] list Anchor List handle acquired by MLXrBackendSessionGetAllAnchors().

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The resource has been released successfully.
*/
MLXrResult MLXrBackendAnchorListRelease(MLXrSession *session, MLXrAnchorList *anchor_list);

/*!
  \note Currently not in use.
  \brief Releases the resources allocated for the anchor.

  \param[in] session Session handle acquired by MLXrBackendSessionCreate().
  \param[in] list Anchor List handle acquired by MLXrBackendSessionGetAllAnchors().
  \param[in] anchor The anchor to release its allocated resources.

  \retval MLXrResult_InvalidParam Invalid parameter(s).
  \retval MLXrResult_Ok The resource has been released successfully.
*/
MLXrResult MLXrBackendAnchorListReleaseAnchor(MLXrSession *session, MLXrAnchorList *list, MLXrAnchor *anchor);

MLXR_EXTERN_C_END
