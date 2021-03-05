package com.luck.picture.lib.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.luck.picture.lib.PictureMultiCuttingActivity;
import com.luck.picture.lib.R;
import com.luck.picture.lib.UCropActivity;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.UCropOptions;
import com.luck.picture.lib.model.CutInfo;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastUtils;
import com.luck.picture.lib.ucrop.CropOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import static com.luck.picture.lib.PictureSelectorActivity.REQUEST_MULTI_CROP;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

/**
 * @author：luck
 * @date：2020/11/19 5:33 PM
 * @describe：UCropManager
 */
public class UCropManager {

    /**
     * 裁剪
     *
     * @param activity
     * @param originalPath
     * @param mimeType
     */
    public static void ofCrop(Activity activity, String originalPath, String mimeType) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (TextUtils.isEmpty(originalPath)) {
            ToastUtils.s(activity.getApplicationContext(), activity.getString(R.string.picture_not_crop_data));
            return;
        }
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        boolean isHttp = PictureMimeType.isHasHttp(originalPath);
        String suffix = mimeType.replace("image/", ".");
        File file = new File(PictureFileUtils.getDiskCacheDir(activity.getApplicationContext()),
                TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_") + suffix : config.renameCropFileName);
        Uri uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        CropOptions.Options options = UCropManager.basicOptions(activity);
        UCrop.of(uri, Uri.fromFile(file))
                .withOptions(options);
        startAnimationActivity(activity, PictureSelectionConfig.windowAnimationStyle.activityCropEnterAnimation);
    }

    /**
     * Send the crop Intent from animation an Activity
     *
     * @param activity Activity to receive result
     */
    public static void startAnimationActivity(@NonNull Activity activity, @AnimRes int activityCropEnterAnimation) {
        if (activityCropEnterAnimation != 0) {
            start(activity, REQUEST_CROP, activityCropEnterAnimation);
        } else {
            start(activity, REQUEST_CROP);
        }
    }

    /**
     * Send the crop Intent from an Activity with a custom request code or animation
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void start(@NonNull Activity activity, int requestCode, @AnimRes int activityCropEnterAnimation) {
        activity.startActivityForResult(getIntent(activity), requestCode);
        activity.overridePendingTransition(activityCropEnterAnimation, R.anim.ucrop_anim_fade_in);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public static void start(@NonNull AppCompatActivity activity) {
        start(activity, REQUEST_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void start(@NonNull AppCompatActivity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public static void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_CROP);
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    public static void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Get Intent to start {@link UCropActivity}
     *
     * @return Intent for {@link UCropActivity}
     */
    public static Intent getIntent(@NonNull Context context) {
        Intent mCropIntent = new Intent();
        Bundle mCropOptionsBundle = new Bundle();
        mCropIntent.setClass(context, UCropActivity.class);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }

    /**
     * 裁剪
     *
     * @param activity
     * @param list
     */
    public static void ofCrop(Activity activity, ArrayList<CutInfo> list) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (list == null || list.size() == 0) {
            ToastUtils.s(activity.getApplicationContext(), activity.getString(R.string.picture_not_crop_data));
            return;
        }
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        CropOptions.Options options = UCropManager.basicOptions(activity);
        options.setCutListData(list);
        int size = list.size();
        int index = 0;
        if (config.chooseMode == PictureMimeType.ofAll() && config.isWithVideoImage) {
            String mimeType = size > 0 ? list.get(index).getMimeType() : "";
            boolean isHasVideo = PictureMimeType.isHasVideo(mimeType);
            if (isHasVideo) {
                for (int i = 0; i < size; i++) {
                    CutInfo cutInfo = list.get(i);
                    if (cutInfo != null && PictureMimeType.isHasImage(cutInfo.getMimeType())) {
                        index = i;
                        break;
                    }
                }
            }
        }
        if (index < size) {
            CutInfo info = list.get(index);
            boolean isHttp = PictureMimeType.isHasHttp(info.getPath());
            Uri uri;
            if (TextUtils.isEmpty(info.getAndroidQToPath())) {
                uri = isHttp || SdkVersionUtils.checkedAndroid_Q() ? Uri.parse(info.getPath()) : Uri.fromFile(new File(info.getPath()));
            } else {
                uri = Uri.fromFile(new File(info.getAndroidQToPath()));
            }
            String suffix = info.getMimeType().replace("image/", ".");
            File file = new File(PictureFileUtils.getDiskCacheDir(activity),
                    TextUtils.isEmpty(config.renameCropFileName) ? DateUtils.getCreateFileName("IMG_CROP_")
                            + suffix : config.camera || size == 1 ? config.renameCropFileName : StringUtils.rename(config.renameCropFileName));
            UCrop.of(uri, Uri.fromFile(file)).withOptions(options);
            startAnimationMultipleCropActivity(activity, PictureSelectionConfig.windowAnimationStyle.activityCropEnterAnimation);
        }
    }

    /**
     * 多图裁剪
     */
    /**
     * Send the crop Intent from animation an Multiple Activity
     *
     * @param activity Activity to receive result
     */
    public static void startAnimationMultipleCropActivity(@NonNull Activity activity,
                                                   @AnimRes int activityCropEnterAnimation) {
        if (activityCropEnterAnimation != 0) {
            startMultiple(activity, REQUEST_MULTI_CROP, activityCropEnterAnimation);
        } else {
            startMultiple(activity, REQUEST_MULTI_CROP);
        }
    }

    /**
     * Send the crop Intent from an Activity with a custom request code or animation
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void startMultiple(@NonNull Activity activity, int requestCode, @AnimRes int activityCropEnterAnimation) {
        activity.startActivityForResult(getMultipleIntent(activity), requestCode);
        activity.overridePendingTransition(activityCropEnterAnimation, R.anim.ucrop_anim_fade_in);
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public static void startMultiple(@NonNull Activity activity) {
        start(activity, REQUEST_MULTI_CROP);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public static void startMultiple(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getMultipleIntent(activity), requestCode);
    }

    /**
     * Get Intent to start {@link PictureMultiCuttingActivity}
     *
     * @return Intent for {@link PictureMultiCuttingActivity}
     */
    public static Intent getMultipleIntent(@NonNull Context context) {
        Intent mCropIntent = new Intent();
        Bundle mCropOptionsBundle = new Bundle();
        mCropIntent.setClass(context, PictureMultiCuttingActivity.class);
        mCropIntent.putExtras(mCropOptionsBundle);
        return mCropIntent;
    }



    /**
     * basicOptions
     *
     * @param context
     * @return
     */
    public static CropOptions.Options basicOptions(Context context) {
        PictureSelectionConfig config = PictureSelectionConfig.getInstance();
        int toolbarColor = 0, statusColor = 0, titleColor = 0, cropNavBarColor = 0;
        boolean isChangeStatusBarFontColor;
        if (PictureSelectionConfig.uiStyle != null) {
            cropNavBarColor = PictureSelectionConfig.uiStyle.picture_navBarColor;
            isChangeStatusBarFontColor = PictureSelectionConfig.uiStyle.picture_statusBarChangeTextColor;
            if (PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor != 0) {
                toolbarColor = PictureSelectionConfig.uiStyle.picture_top_titleBarBackgroundColor;
            }
            if (PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor != 0) {
                statusColor = PictureSelectionConfig.uiStyle.picture_statusBarBackgroundColor;
            }
            if (PictureSelectionConfig.uiStyle.picture_top_titleTextColor != 0) {
                titleColor = PictureSelectionConfig.uiStyle.picture_top_titleTextColor;
            }
        } else if (PictureSelectionConfig.cropStyle != null) {
            cropNavBarColor = PictureSelectionConfig.cropStyle.cropNavBarColor;
            isChangeStatusBarFontColor = PictureSelectionConfig.cropStyle.isChangeStatusBarFontColor;
            if (PictureSelectionConfig.cropStyle.cropTitleBarBackgroundColor != 0) {
                toolbarColor = PictureSelectionConfig.cropStyle.cropTitleBarBackgroundColor;
            }
            if (PictureSelectionConfig.cropStyle.cropStatusBarColorPrimaryDark != 0) {
                statusColor = PictureSelectionConfig.cropStyle.cropStatusBarColorPrimaryDark;
            }
            if (PictureSelectionConfig.cropStyle.cropTitleColor != 0) {
                titleColor = PictureSelectionConfig.cropStyle.cropTitleColor;
            }
        } else {
            isChangeStatusBarFontColor = config.isChangeStatusBarFontColor;
            if (!isChangeStatusBarFontColor) {
                isChangeStatusBarFontColor = AttrsUtils.getTypeValueBoolean(context, R.attr.picture_statusFontColor);
            }
            if (config.cropTitleBarBackgroundColor != 0) {
                toolbarColor = config.cropTitleBarBackgroundColor;
            } else {
                toolbarColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_toolbar_bg);
            }
            if (config.cropStatusBarColorPrimaryDark != 0) {
                statusColor = config.cropStatusBarColorPrimaryDark;
            } else {
                statusColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_status_color);
            }
            if (config.cropTitleColor != 0) {
                titleColor = config.cropTitleColor;
            } else {
                titleColor = AttrsUtils.getTypeValueColor(context, R.attr.picture_crop_title_color);
            }
        }
        CropOptions.Options options =  new CropOptions.Options();
        options.isOpenWhiteStatusBar(isChangeStatusBarFontColor);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(config.circleDimmedLayer);
        options.setDimmedLayerColor(config.circleDimmedColor);
        options.setDimmedLayerBorderColor(config.circleDimmedBorderColor);
        options.setCircleStrokeWidth(config.circleStrokeWidth);
        options.setShowCropFrame(config.showCropFrame);
        options.setDragFrameEnabled(config.isDragFrame);
        options.setShowCropGrid(config.showCropGrid);
        options.setScaleEnabled(config.scaleEnabled);
        options.setRotateEnabled(config.rotateEnabled);
        options.isMultipleSkipCrop(config.isMultipleSkipCrop);
        options.setHideBottomControls(config.hideBottomControls);
        options.setCompressionQuality(config.cropCompressQuality);
        options.setRenameCropFileName(config.renameCropFileName);
        options.isCamera(config.camera);
        options.setNavBarColor(cropNavBarColor);
        options.isWithVideoImage(config.isWithVideoImage);
        options.setFreeStyleCropEnabled(config.freeStyleCropEnabled);
        options.setCropExitAnimation(PictureSelectionConfig.windowAnimationStyle.activityCropExitAnimation);
        options.withAspectRatio(config.aspect_ratio_x, config.aspect_ratio_y);
        options.isMultipleRecyclerAnimation(config.isMultipleRecyclerAnimation);
        if (config.cropWidth > 0 && config.cropHeight > 0) {
            options.withMaxResultSize(config.cropWidth, config.cropHeight);
        }

        return options;
    }

}
