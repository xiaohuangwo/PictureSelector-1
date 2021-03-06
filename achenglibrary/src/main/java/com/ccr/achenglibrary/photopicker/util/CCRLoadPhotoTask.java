/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ccr.achenglibrary.photopicker.util;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.ccr.achenglibrary.R;
import com.ccr.achenglibrary.photopicker.model.CCRImageFolderModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * 在此写用途
 *
 * @Author: Acheng
 * @Email: 345887272@qq.com
 * @Date: 2017-09-22 16:47
 * @Version: V1.0 <描述当前版本功能>
 */
public class CCRLoadPhotoTask extends CCRAsyncTask<Void, ArrayList<CCRImageFolderModel>> {
    private Context mContext;
    private boolean mTakePhotoEnabled;


    public CCRLoadPhotoTask(Callback<ArrayList<CCRImageFolderModel>> callback, Context context, boolean takePhotoEnabled) {
        super(callback);
        mContext = context.getApplicationContext();
        mTakePhotoEnabled = takePhotoEnabled;
    }

    private static boolean isNotImageFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        return !file.exists() || file.length() == 0;

        // 获取图片的宽和高，但不把图片加载到内存中
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//        return options.outMimeType == null;
    }

    @Override
    protected ArrayList<CCRImageFolderModel> doInBackground(Void... voids) {
        ArrayList<CCRImageFolderModel> imageFolderModels = new ArrayList<>();

        CCRImageFolderModel allImageFolderModel = new CCRImageFolderModel(mTakePhotoEnabled);
        allImageFolderModel.name = mContext.getString(R.string.bga_pp_all_image);
        imageFolderModels.add(allImageFolderModel);

        HashMap<String, CCRImageFolderModel> imageFolderModelMap = new HashMap<>();

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media.DATA},
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_ADDED + " DESC"
            );

            CCRImageFolderModel otherImageFolderModel;
            if (cursor != null && cursor.getCount() > 0) {
                boolean firstInto = true;
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (isNotImageFile(imagePath)) {
                        continue;
                    }

                    if (firstInto) {
                        allImageFolderModel.coverPath = imagePath;
                        firstInto = false;
                    }
                    // 所有图片目录每次都添加
                    allImageFolderModel.addLastImage(imagePath);

                    String folderPath = null;
                    // 其他图片目录
                    File folder = new File(imagePath).getParentFile();
                    if (folder != null) {
                        folderPath = folder.getAbsolutePath();
                    }

                    if (TextUtils.isEmpty(folderPath)) {
                        int end = imagePath.lastIndexOf(File.separator);
                        if (end != -1) {
                            folderPath = imagePath.substring(0, end);
                        }
                    }

                    if (!TextUtils.isEmpty(folderPath)) {
                        if (imageFolderModelMap.containsKey(folderPath)) {
                            otherImageFolderModel = imageFolderModelMap.get(folderPath);
                        } else {
                            String folderName = folderPath.substring(folderPath.lastIndexOf(File.separator) + 1);
                            if (TextUtils.isEmpty(folderName)) {
                                folderName = "/";
                            }
                            otherImageFolderModel = new CCRImageFolderModel(folderName, imagePath);
                            imageFolderModelMap.put(folderPath, otherImageFolderModel);
                        }
                        otherImageFolderModel.addLastImage(imagePath);
                    }
                }

                // 添加其他图片目录
                imageFolderModels.addAll(imageFolderModelMap.values());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageFolderModels;
    }

    public CCRLoadPhotoTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
        return this;
    }
}
