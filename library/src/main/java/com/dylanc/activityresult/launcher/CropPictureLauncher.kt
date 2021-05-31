/*
 * Copyright (c) 2021. Dylan Cai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.dylanc.activityresult.launcher

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment


inline fun ComponentActivity.CropPictureLauncher() = CropPictureLauncher(this)

inline fun Fragment.CropPictureLauncher() = CropPictureLauncher(this)

data class CropPictureConfig @JvmOverloads constructor(
  val inputUri: Uri,
  var aspectX: Int = 1,
  var aspectY: Int = 1,
  var outputX: Int = 512,
  var outputY: Int = 512,
  var outputContentValues: ContentValues = ContentValues(),
  var onCreateIntent: (Intent) -> Unit = {}
)

class CropPictureLauncher(caller: ActivityResultCaller) :
  BaseActivityResultLauncher<CropPictureConfig, Uri>(caller, CropPictureContract()) {

  @JvmOverloads
  fun launch(
    inputUri: Uri,
    aspectX: Int = 1,
    aspectY: Int = 1,
    outputX: Int = 512,
    outputY: Int = 512,
    outputContentValues: ContentValues = ContentValues(),
    onCreateIntent: (Intent) -> Unit = {},
    block: (Uri?) -> Unit
  ) =
    launch(
      CropPictureConfig(
        inputUri, aspectX, aspectY, outputX,
        outputY, outputContentValues, onCreateIntent
      ), block
    )
}

class CropPictureContract : ActivityResultContract<CropPictureConfig, Uri>() {
  private lateinit var outputUri: Uri

  @CallSuper
  override fun createIntent(context: Context, input: CropPictureConfig): Intent {
    return Intent("com.android.camera.action.CROP")
      .apply {
        outputUri = context.contentResolver.insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
          input.outputContentValues
        )!!
        setDataAndType(input.inputUri, "image/*")
        putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        putExtra("aspectX", input.aspectX)
        putExtra("aspectY", input.aspectY)
        putExtra("outputX", input.outputX)
        putExtra("outputY", input.outputY)
        putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        putExtra("return-data", false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
      }.apply(input.onCreateIntent)
  }

  override fun parseResult(resultCode: Int, intent: Intent?): Uri? =
    if (resultCode == Activity.RESULT_OK) outputUri else null
}