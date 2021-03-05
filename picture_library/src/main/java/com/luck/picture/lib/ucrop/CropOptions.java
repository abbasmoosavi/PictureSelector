package com.luck.picture.lib.ucrop;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luck.picture.lib.UCropActivity;
import com.luck.picture.lib.model.CutInfo;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.yalantis.ucrop.UCrop.MIN_SIZE;

public class CropOptions implements Parcelable {
    private static final String EXTRA_PREFIX = "com.yalantis.ucrop";

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
    public static final String EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight";
    public static final String EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX";
    public static final String EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY";
    public static final String EXTRA_ERROR = EXTRA_PREFIX + ".Error";

    public static final String EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX";
    public static final String EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY";

    public static final String EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX";
    public static final String EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY";

    protected CropOptions(Parcel in) {
    }

    public static final Creator<CropOptions> CREATOR = new Creator<CropOptions>() {
        @Override
        public CropOptions createFromParcel(Parcel in) {
            return new CropOptions(in);
        }

        @Override
        public CropOptions[] newArray(int size) {
            return new CropOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }


    public static class Options extends UCrop.Options {
        public static final String EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName";
        public static final String EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality";

        public static final String EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures";

        public static final String EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize";
        public static final String EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier";
        public static final String EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration";

        public static final String EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor";
        public static final String EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer";

        public static final String EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame";
        public static final String EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor";
        public static final String EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth";

        public static final String EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid";
        public static final String EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount";
        public static final String EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount";
        public static final String EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor";
        public static final String EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth";

        public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
        public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
        public static final String EXTRA_UCROP_COLOR_WIDGET_ACTIVE = EXTRA_PREFIX + ".UcropColorWidgetActive";
        public static final String EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE = EXTRA_PREFIX + ".UcropColorControlsWidgetActive";

        public static final String EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor";
        public static final String EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText";
        public static final String EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCancelDrawable";
        public static final String EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable";

        public static final String EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor";

        public static final String EXTRA_HIDE_BOTTOM_CONTROLS = EXTRA_PREFIX + ".HideBottomControls";
        public static final String EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop";

        public static final String EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT = EXTRA_PREFIX + ".AspectRatioSelectedByDefault";
        public static final String EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions";

        public static final String EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR = EXTRA_PREFIX + ".UcropRootViewBackgroundColor";

        // more
        public static final String EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR = EXTRA_PREFIX + ".openWhiteStatusBar";
        public static final String EXTRA_DIMMED_LAYER_BORDER_COLOR = EXTRA_PREFIX + ".DimmedLayerBorderColor";
        public static final String EXTRA_CIRCLE_STROKE_WIDTH_LAYER = EXTRA_PREFIX + ".CircleStrokeWidth";
        public static final String EXTRA_DRAG_CROP_FRAME = EXTRA_PREFIX + ".DragCropFrame";
        public static final String EXTRA_SCALE = EXTRA_PREFIX + ".scale";
        public static final String EXTRA_ROTATE = EXTRA_PREFIX + ".rotate";
        public static final String EXTRA_NAV_BAR_COLOR = EXTRA_PREFIX + ".navBarColor";
        public static final String EXTRA_SKIP_MULTIPLE_CROP = EXTRA_PREFIX + ".skip_multiple_crop";
        public static final String EXTRA_RENAME_CROP_FILENAME = EXTRA_PREFIX + ".RenameCropFileName";
        public static final String EXTRA_CAMERA = EXTRA_PREFIX + ".isCamera";
        public static final String EXTRA_MULTIPLE_RECYCLERANIMATION = ".isMultipleAnimation";
        public static final String EXTRA_CUT_CROP = EXTRA_PREFIX + ".cuts";
        public static final String EXTRA_WITH_VIDEO_IMAGE = EXTRA_PREFIX + ".isWithVideoImage";
        public static final String EXTRA_OUTPUT_URI_LIST = EXTRA_PREFIX + ".OutputUriList";
        public static final String EXTRA_WINDOW_EXIT_ANIMATION = EXTRA_PREFIX + ".WindowAnimation";

        private final Bundle mOptionBundle;

        public Options() {
            mOptionBundle = new Bundle();
        }

        @NonNull
        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        /**
         * Set one of {@link android.graphics.Bitmap.CompressFormat} that will be used to save resulting Bitmap.
         */
        public void setCompressionFormat(@NonNull Bitmap.CompressFormat format) {
            mOptionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name());
        }

        /**
         * Set compression quality [0-100] that will be used to save resulting Bitmap.
         */
        public void setCompressionQuality(@IntRange(from = 0) int compressQuality) {
            mOptionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality);
        }

        /**
         * Set the custom clipping output file name
         *
         * @param renameCropFileName
         */
        public void setRenameCropFileName(String renameCropFileName) {
            mOptionBundle.putString(EXTRA_RENAME_CROP_FILENAME, renameCropFileName);
        }

        /**
         * Whether taking pictures
         *
         * @param isCamera
         */
        public void isCamera(boolean isCamera) {
            mOptionBundle.putBoolean(EXTRA_CAMERA, isCamera);
        }


        /**
         * Whether the multi-graph clipping list is animated or not
         *
         * @param isAnimation
         */
        public void isMultipleRecyclerAnimation(boolean isAnimation) {
            mOptionBundle.putBoolean(EXTRA_MULTIPLE_RECYCLERANIMATION, isAnimation);
        }


        /**
         * @param -set cuts path
         */
        public void setCutListData(ArrayList<CutInfo> list) {
            mOptionBundle.putParcelableArrayList(EXTRA_CUT_CROP, list);
        }

        /**
         * @param isWithVideoImage Whether pictures and videos can coexist
         */
        public void isWithVideoImage(boolean isWithVideoImage) {
            mOptionBundle.putBoolean(EXTRA_WITH_VIDEO_IMAGE, isWithVideoImage);
        }

        /**
         * Choose what set of gestures will be enabled on each tab - if any.
         */
        public void setAllowedGestures(@UCropActivity.GestureTypes int tabScale,
                                       @UCropActivity.GestureTypes int tabRotate,
                                       @UCropActivity.GestureTypes int tabAspectRatio) {
            mOptionBundle.putIntArray(EXTRA_ALLOWED_GESTURES, new int[]{tabScale, tabRotate, tabAspectRatio});
        }

        /**
         * This method sets multiplier that is used to calculate max image scale from min image scale.
         *
         * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
         */
        public void setMaxScaleMultiplier(@FloatRange(from = 1.0, fromInclusive = false) float maxScaleMultiplier) {
            mOptionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier);
        }

        /**
         * This method sets animation duration for image to wrap the crop bounds
         *
         * @param durationMillis - duration in milliseconds
         */
        public void setImageToCropBoundsAnimDuration(@IntRange(from = MIN_SIZE) int durationMillis) {
            mOptionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis);
        }

        /**
         * Setter for max size for both width and height of bitmap that will be decoded from an input Uri and used in the view.
         *
         * @param maxBitmapSize - size in pixels
         */
        public void setMaxBitmapSize(@IntRange(from = MIN_SIZE) int maxBitmapSize) {
            mOptionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize);
        }

        /**
         * @param color - desired color of dimmed area around the crop bounds
         */
        public void setDimmedLayerColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color);
        }


        /**
         * @param color - desired border color of dimmed area around the crop bounds
         */
        public void setDimmedLayerBorderColor(@ColorInt int color) {
            if (color != 0) {
                mOptionBundle.putInt(EXTRA_DIMMED_LAYER_BORDER_COLOR, color);
            }
        }

        /**
         * @param width Set the circular clipping border
         */
        public void setCircleStrokeWidth(int width) {
            if (width > 0) {
                mOptionBundle.putInt(EXTRA_CIRCLE_STROKE_WIDTH_LAYER, width);
            }
        }

        /**
         * @param isCircle - set it to true if you want dimmed layer to have an circle inside
         */
        public void setCircleDimmedLayer(boolean isCircle) {
            mOptionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle);
        }

        /**
         * @param show - set to true if you want to see a crop frame rectangle on top of an image
         */
        public void setShowCropFrame(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show);
        }

        /**
         * @param color - desired color of crop frame
         */
        public void setCropFrameColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color);
        }

        /**
         * @param width - desired width of crop frame line in pixels
         */
        public void setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width);
        }

        /**
         * @param show - set to true if you want to see a crop grid/guidelines on top of an image
         */
        public void setShowCropGrid(boolean show) {
            mOptionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show);
        }

        /**
         * @param isDragFrame - 是否可拖动裁剪框
         */
        public void setDragFrameEnabled(boolean isDragFrame) {
            mOptionBundle.putBoolean(EXTRA_DRAG_CROP_FRAME, isDragFrame);
        }


        /**
         * @param count - crop grid rows count.
         */
        public void setCropGridRowCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count);
        }

        public void setScaleEnabled(boolean scaleEnabled) {
            mOptionBundle.putBoolean(EXTRA_SCALE, scaleEnabled);
        }


        public void setRotateEnabled(boolean rotateEnabled) {
            mOptionBundle.putBoolean(EXTRA_ROTATE, rotateEnabled);
        }

        /**
         * @param count - crop grid columns count.
         */
        public void setCropGridColumnCount(@IntRange(from = 0) int count) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count);
        }

        /**
         * @param color - desired color of crop grid/guidelines
         */
        public void setCropGridColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_COLOR, color);
        }

        /**
         * @param width - desired width of crop grid lines in pixels
         */
        public void setCropGridStrokeWidth(@IntRange(from = 0) int width) {
            mOptionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width);
        }

        /**
         * @param color - desired resolved color of the toolbar
         */
        public void setToolbarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color);
        }


        /**
         * @param openWhiteStatusBar - Change the status bar font color
         */
        public void isOpenWhiteStatusBar(boolean openWhiteStatusBar) {
            mOptionBundle.putBoolean(EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR, openWhiteStatusBar);
        }

        /**
         * @param color - desired resolved color of the statusbar
         */
        public void setStatusBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color);
        }

        /**
         * @param color - desired resolved color of the progress wheel middle line (default is violet)
         */
        public void setActiveWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_COLOR_WIDGET_ACTIVE, color);
        }

        /**
         * @param color - desired resolved color of the active and selected widget (default is white)
         */
        public void setActiveControlsWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, color);
        }

        /**
         * @param color - desired resolved color of Toolbar text and buttons (default is darker orange)
         */
        public void setToolbarWidgetColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color);
        }

        /**
         * @param text - desired text for Toolbar title
         */
        public void setToolbarTitle(@Nullable String text) {
            mOptionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text);
        }

        /**
         * @param drawable - desired drawable for the Toolbar left cancel icon
         */
        public void setToolbarCancelDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable);
        }

        /**
         * @param drawable - desired drawable for the Toolbar right crop icon
         */
        public void setToolbarCropDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable);
        }

        /**
         * @param color - desired resolved color of logo fill (default is darker grey)
         */
        public void setLogoColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color);
        }

        /**
         * 多图裁剪时是否可以跳过裁剪
         *
         * @param isMultipleSkipCrop
         */
        public void isMultipleSkipCrop(boolean isMultipleSkipCrop) {
            mOptionBundle.putBoolean(EXTRA_SKIP_MULTIPLE_CROP, isMultipleSkipCrop);
        }

        /**
         * @param hide - set to true to hide the bottom controls (shown by default)
         */
        public void setHideBottomControls(boolean hide) {
            mOptionBundle.putBoolean(EXTRA_HIDE_BOTTOM_CONTROLS, hide);
        }

        /**
         * @param enabled - set to true to let user resize crop bounds (disabled by default)
         */
        public void setFreeStyleCropEnabled(boolean enabled) {
            mOptionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled);
        }

        /**
         * @param activityCropExitAnimation activity exit animation
         */
        public void setCropExitAnimation(@AnimRes int activityCropExitAnimation) {
            mOptionBundle.putInt(EXTRA_WINDOW_EXIT_ANIMATION, activityCropExitAnimation);
        }


        /**
         * @param navBarColor set NavBar Color
         */
        public void setNavBarColor(@ColorInt int navBarColor) {
            if (navBarColor != 0) {
                mOptionBundle.putInt(EXTRA_NAV_BAR_COLOR, navBarColor);
            }
        }

        /**
         * Pass an ordered list of desired aspect ratios that should be available for a user.
         *
         * @param selectedByDefault - index of aspect ratio option that is selected by default (starts with 0).
         * @param aspectRatio       - list of aspect ratio options that are available to user
         */
        public void setAspectRatioOptions(int selectedByDefault, AspectRatio... aspectRatio) {
            if (selectedByDefault > aspectRatio.length) {
                throw new IllegalArgumentException(String.format(Locale.US,
                        "Index [selectedByDefault = %d] cannot be higher than aspect ratio options count [count = %d].",
                        selectedByDefault, aspectRatio.length));
            }
            mOptionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault);
            mOptionBundle.putParcelableArrayList(EXTRA_ASPECT_RATIO_OPTIONS, new ArrayList<Parcelable>(Arrays.asList(aspectRatio)));
        }

        /**
         * @param color - desired background color that should be applied to the root view
         */
        public void setRootViewBackgroundColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color);
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        public void withAspectRatio(float x, float y) {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y);
        }

        /**
         * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
         * User won't see the menu with other ratios options.
         */
        public void useSourceImageAspectRatio() {
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0);
            mOptionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0);
        }

        /**
         * Set maximum size for result cropped image.
         *
         * @param width  max cropped image width
         * @param height max cropped image height
         */
        public void withMaxResultSize(@IntRange(from = MIN_SIZE) int width, @IntRange(from = MIN_SIZE) int height) {
            mOptionBundle.putInt(EXTRA_MAX_SIZE_X, width);
            mOptionBundle.putInt(EXTRA_MAX_SIZE_Y, height);
        }
    }

}
