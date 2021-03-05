package com.luck.picture.lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.luck.picture.lib.immersion.CropImmersiveManage;
import com.luck.picture.lib.util.MimeType;
import com.luck.picture.lib.util.ScreenUtils;
import com.luck.picture.lib.view.GestureCropImageView;
import com.luck.picture.lib.view.OverlayView;
import com.luck.picture.lib.view.UCropView;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.SelectedStateListDrawable;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.widget.AspectRatioTextView;
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.yalantis.ucrop.UCrop.EXTRA_ASPECT_RATIO_X;
import static com.yalantis.ucrop.UCrop.EXTRA_ASPECT_RATIO_Y;
import static com.yalantis.ucrop.UCrop.EXTRA_ERROR;
import static com.yalantis.ucrop.UCrop.EXTRA_MAX_SIZE_X;
import static com.yalantis.ucrop.UCrop.EXTRA_MAX_SIZE_Y;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_IMAGE_WIDTH;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_OFFSET_X;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_OFFSET_Y;
import static com.yalantis.ucrop.UCrop.RESULT_ERROR;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */

@SuppressWarnings("ConstantConditions")
public class UCropActivity extends AppCompatActivity {
    /**
     * 是否使用沉浸式，子类复写该方法来确定是否采用沉浸式
     *
     * @return 是否沉浸式，默认true
     */
    @Override
    public boolean isImmersive() {
        return true;
    }


    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersive() {
        CropImmersiveManage.immersiveAboveAPI23(this
                , mStatusBarColor
                , mToolbarColor
                , isOpenWhiteStatusBar);
    }


    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropActivity";
    private static final long CONTROLS_ANIMATION_DURATION = 50;
    private static final int TABS_COUNT = 3;
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;

    private String mToolbarTitle;
    protected int mScreenWidth;
    // Enables dynamic coloring
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mActiveWidgetColor;
    private int mActiveControlsWidgetColor;
    private int mToolbarWidgetColor;
    @ColorInt
    private int mRootViewBackgroundColor;
    @DrawableRes
    private int mToolbarCancelDrawable;
    @DrawableRes
    private int mToolbarCropDrawable;
    private int mLogoColor;

    protected boolean mShowBottomControls;
    private boolean mShowLoader = true;

    protected RelativeLayout uCropPhotoBox;
    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private ViewGroup mWrapperStateAspectRatio, mWrapperStateRotate, mWrapperStateScale;
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutScale;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();
    private List<AspectRatioTextView> mAspectRatioTextViews = new ArrayList<>();
    private TextView mTextViewRotateAngle, mTextViewScalePercent;
    protected View mBlockingView;
    private Transition mControlsTransition;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};
    private static final String EXTRA_PREFIX = "com.yalantis.ucrop";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";
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

    public static final String EXTRA_DIMMED_LAYER_BORDER_COLOR = EXTRA_PREFIX + ".DimmedLayerBorderColor";
    public static final String EXTRA_CIRCLE_STROKE_WIDTH_LAYER = EXTRA_PREFIX + ".CircleStrokeWidth";
    public static final String EXTRA_DRAG_CROP_FRAME = EXTRA_PREFIX + ".DragCropFrame";
    public static final String EXTRA_SCALE = EXTRA_PREFIX + ".scale";
    public static final String EXTRA_ROTATE = EXTRA_PREFIX + ".rotate";
    public static final String EXTRA_NAV_BAR_COLOR = EXTRA_PREFIX + ".navBarColor";
    public static final String EXTRA_WINDOW_EXIT_ANIMATION = EXTRA_PREFIX + ".WindowAnimation";
    public static final String EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR = EXTRA_PREFIX + ".openWhiteStatusBar";
    public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor";
    public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor";
    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";

    /**
     * 是否可拖动裁剪框
     */
    private boolean isDragFrame;

    /**
     * 图片是否可拖动或旋转
     */
    private boolean isScaleEnabled, isRotateEnabled;
    /**
     * 是否改变状态栏字体颜色
     */
    private boolean isOpenWhiteStatusBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        getCustomOptionsData(intent);
        if (isImmersive()) {
            immersive();
        }
        setContentView(R.layout.ucrop_activity_photobox);
        mScreenWidth = ScreenUtils.getScreenWidth(this);
        setupViews(intent);
        setNavBar();
        setImageData(intent);
        setInitialState();
        addBlockingView();
    }

    /**
     * 获取自定义配制参数
     *
     * @param intent
     */
    private void getCustomOptionsData(@NonNull Intent intent) {
        isOpenWhiteStatusBar = intent.getBooleanExtra(EXTRA_UCROP_WIDGET_CROP_OPEN_WHITE_STATUSBAR, false);
        mStatusBarColor = intent.getIntExtra(EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar));
        if (mToolbarColor == 0) {
            mToolbarColor = ContextCompat.getColor(this, R.color.ucrop_color_toolbar);
        }
        if (mStatusBarColor == 0) {
            mStatusBarColor = ContextCompat.getColor(this, R.color.ucrop_color_statusbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.ucrop_menu_activity, menu);

        // Change crop & loader menu icons color to match the rest of the UI colors

        MenuItem menuItemLoader = menu.findItem(R.id.menu_loader);
        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate();
                menuItemLoaderIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
                menuItemLoader.setIcon(menuItemLoaderIcon);
            } catch (IllegalStateException e) {
                Log.i(TAG, String.format("%s - %s", e.getMessage(), getString(R.string.ucrop_mutate_exception_hint)));
            }
            ((Animatable) menuItemLoader.getIcon()).start();
        }

        MenuItem menuItemCrop = menu.findItem(R.id.menu_crop);
        Drawable menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable);
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate();
            menuItemCropIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
            menuItemCrop.setIcon(menuItemCropIcon);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader);
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_crop) {
            cropAndSaveImage();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    protected void setImageData(@NonNull Intent intent) {
        Uri inputUri = intent.getParcelableExtra(EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(EXTRA_OUTPUT_URI);
        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                boolean isOnTouch = isOnTouch(inputUri);
                mGestureCropImageView.setRotateEnabled(isOnTouch ? isRotateEnabled : isOnTouch);
                mGestureCropImageView.setScaleEnabled(isOnTouch ? isScaleEnabled : isOnTouch);
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                onBackPressed();
            }
        } else {
            setResultError(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            onBackPressed();
        }
    }

    /**
     * 是否可以触摸
     *
     * @return
     */
    private boolean isOnTouch() {
        Uri inputUri = getIntent().getParcelableExtra(EXTRA_INPUT_URI);
        if (inputUri == null) {
            return true;
        }
        return isOnTouch(inputUri);
    }

    /**
     * 是否可以触摸
     *
     * @param inputUri
     * @return
     */
    private boolean isOnTouch(Uri inputUri) {
        if (inputUri == null) {
            return true;
        }
        boolean isHttp = MimeType.isHttp(inputUri.toString());
        if (isHttp) {
            // 网络图片
            String lastImgType = MimeType.getLastImgType(inputUri.toString());
            return !MimeType.isGifForSuffix(lastImgType);
        } else {
            String mimeType = MimeType.getMimeTypeFromMediaContentUri(this, inputUri);
            if (mimeType.endsWith("image/*")) {
                String path = FileUtils.getPath(this, inputUri);
                mimeType = MimeType.getImageMimeType(path);
            }
            return !MimeType.isGif(mimeType);
        }
    }

    /**
     * This method extracts {@link UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = intent.getIntExtra(EXTRA_COMPRESSION_QUALITY, UCropActivity.DEFAULT_COMPRESS_QUALITY);

        // custom options
        mOverlayView.setDimmedBorderColor(intent.getIntExtra(EXTRA_DIMMED_LAYER_BORDER_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        isDragFrame = intent.getBooleanExtra(EXTRA_DRAG_CROP_FRAME, true);

        mOverlayView.setDimmedStrokeWidth(intent.getIntExtra(EXTRA_CIRCLE_STROKE_WIDTH_LAYER, 1));
        isScaleEnabled = intent.getBooleanExtra(EXTRA_SCALE, true);
        isRotateEnabled = intent.getBooleanExtra(EXTRA_ROTATE, true);


        // Gestures options
        int[] allowedGestures = intent.getIntArrayExtra(EXTRA_ALLOWED_GESTURES);
        if (allowedGestures != null && allowedGestures.length == TABS_COUNT) {
            mAllowedGestures = allowedGestures;
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));
        mOverlayView.setDragFrame(isDragFrame);
        mOverlayView.setDimmedColor(intent.getIntExtra(EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        float aspectRatioX = intent.getFloatExtra(EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = intent.getFloatExtra(EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = intent.getIntExtra(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio.setVisibility(View.GONE);
            }
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected void setupViews(@NonNull Intent intent) {
        mStatusBarColor = intent.getIntExtra(EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar));
        mActiveWidgetColor = intent.getIntExtra(EXTRA_UCROP_COLOR_WIDGET_ACTIVE, ContextCompat.getColor(this, R.color.ucrop_color_widget_background));
        mActiveControlsWidgetColor = intent.getIntExtra(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, ContextCompat.getColor(this, R.color.ucrop_color_active_controls_color));

        mToolbarWidgetColor = intent.getIntExtra(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, R.color.ucrop_color_toolbar_widget));
        mToolbarCancelDrawable = intent.getIntExtra(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, R.drawable.ucrop_ic_cross);
        mToolbarCropDrawable = intent.getIntExtra(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, R.drawable.ucrop_ic_done);
        mToolbarTitle = intent.getStringExtra(EXTRA_UCROP_TITLE_TEXT_TOOLBAR);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : getResources().getString(R.string.ucrop_label_edit_photo);
        mLogoColor = intent.getIntExtra(EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_default_logo));
        mShowBottomControls = !intent.getBooleanExtra(EXTRA_HIDE_BOTTOM_CONTROLS, false);
        mRootViewBackgroundColor = intent.getIntExtra(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, ContextCompat.getColor(this, R.color.ucrop_color_crop_background));

        setupAppBar();
        initiateRootViews();

        if (mShowBottomControls) {

            ViewGroup viewGroup = findViewById(R.id.ucrop_photobox);
            ViewGroup wrapper = viewGroup.findViewById(R.id.controls_wrapper);
            wrapper.setVisibility(View.VISIBLE);
            wrapper.setBackgroundColor(mRootViewBackgroundColor);
            LayoutInflater.from(this).inflate(R.layout.ucrop_controls, wrapper, true);

            mControlsTransition = new AutoTransition();
            mControlsTransition.setDuration(CONTROLS_ANIMATION_DURATION);

            mWrapperStateAspectRatio = findViewById(R.id.state_aspect_ratio);
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener);
            mWrapperStateRotate = findViewById(R.id.state_rotate);
            mWrapperStateRotate.setOnClickListener(mStateClickListener);
            mWrapperStateScale = findViewById(R.id.state_scale);
            mWrapperStateScale.setOnClickListener(mStateClickListener);

            mLayoutAspectRatio = findViewById(R.id.layout_aspect_ratio);
            mLayoutRotate = findViewById(R.id.layout_rotate_wheel);
            mLayoutScale = findViewById(R.id.layout_scale_wheel);

            setupAspectRatioWidget(intent);
            setupRotateWidget();
            setupScaleWidget();
            setupStatesWrapper();
        }
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private void setupAppBar() {
        setStatusBarColor(mStatusBarColor);

        final Toolbar toolbar = findViewById(R.id.toolbar);

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.setTitleTextColor(mToolbarWidgetColor);

        final TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTextColor(mToolbarWidgetColor);
        toolbarTitle.setText(mToolbarTitle);

        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = AppCompatResources.getDrawable(this, mToolbarCancelDrawable).mutate();
        stateButtonDrawable.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(stateButtonDrawable);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initiateRootViews() {
        uCropPhotoBox = findViewById(R.id.ucrop_photobox);
        mUCropView = findViewById(R.id.ucrop);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener((com.luck.picture.lib.view.TransformImageView.TransformImageListener) mImageListener);

        ((ImageView) findViewById(R.id.image_view_logo)).setColorFilter(mLogoColor, PorterDuff.Mode.SRC_ATOP);

        findViewById(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor);
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(!isOnTouch());
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            onBackPressed();
        }

    };

    /**
     * Use {@link #mActiveWidgetColor} for color filter
     */
    private void setupStatesWrapper() {
        ImageView stateScaleImageView = findViewById(R.id.image_view_state_scale);
        ImageView stateRotateImageView = findViewById(R.id.image_view_state_rotate);
        ImageView stateAspectRatioImageView = findViewById(R.id.image_view_state_aspect_ratio);

        stateScaleImageView.setImageDrawable(new SelectedStateListDrawable(stateScaleImageView.getDrawable(), mActiveControlsWidgetColor));
        stateRotateImageView.setImageDrawable(new SelectedStateListDrawable(stateRotateImageView.getDrawable(), mActiveControlsWidgetColor));
        stateAspectRatioImageView.setImageDrawable(new SelectedStateListDrawable(stateAspectRatioImageView.getDrawable(), mActiveControlsWidgetColor));
    }

    /**
     * set NavBar Color
     */
    private void setNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navBarColor = getIntent().getIntExtra(EXTRA_NAV_BAR_COLOR, 0);
            if (navBarColor != 0) {
                getWindow().setNavigationBarColor(navBarColor);
            }
        }
    }

    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    private void setupAspectRatioWidget(@NonNull Intent intent) {

        int aspectRationSelectedByDefault = intent.getIntExtra(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioList == null || aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 2;

            aspectRatioList = new ArrayList<>();
            aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 3, 4));
            aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_label_original).toUpperCase(),
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO));
            aspectRatioList.add(new AspectRatio(null, 3, 2));
            aspectRatioList.add(new AspectRatio(null, 16, 9));
        }

        LinearLayout wrapperAspectRatioList = findViewById(R.id.layout_aspect_ratio);

        FrameLayout wrapperAspectRatio;
        AspectRatioTextView aspectRatioTextView;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        if (getCurrentActivity() instanceof PictureMultiCuttingActivity) {
            mAspectRatioTextViews = new ArrayList<>();
            mCropAspectRatioViews = new ArrayList<>();
        }
        for (AspectRatio aspectRatio : aspectRatioList) {
            wrapperAspectRatio = (FrameLayout) getLayoutInflater().inflate(R.layout.ucrop_aspect_ratio, null);
            wrapperAspectRatio.setLayoutParams(lp);
            aspectRatioTextView = ((AspectRatioTextView) wrapperAspectRatio.getChildAt(0));
            aspectRatioTextView.setActiveColor(mActiveControlsWidgetColor);
            aspectRatioTextView.setAspectRatio(aspectRatio);
            mAspectRatioTextViews.add(aspectRatioTextView);
            wrapperAspectRatioList.addView(wrapperAspectRatio);
            mCropAspectRatioViews.add(wrapperAspectRatio);
        }
        mCropAspectRatioViews.get(aspectRationSelectedByDefault).setSelected(true);
        int index = -1;
        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            index++;
            cropAspectRatioView.setTag(index);
            cropAspectRatioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestureCropImageView.setTargetAspectRatio(
                            ((AspectRatioTextView) ((ViewGroup) v).getChildAt(0)).getAspectRatio(v.isSelected()));
                    mGestureCropImageView.setImageToWrapCropBounds();
                    if (!v.isSelected()) {
                        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
                        }
                    }
                }
            });
        }
    }

    private void setupRotateWidget() {
        mTextViewRotateAngle = findViewById(R.id.text_view_rotate);
        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        mGestureCropImageView.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
                    }

                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });

        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel)).setMiddleLineColor(mActiveWidgetColor);


        findViewById(R.id.wrapper_reset_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });
        findViewById(R.id.wrapper_rotate_by_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(90);
            }
        });
    }

    private void setupScaleWidget() {
        mTextViewScalePercent = findViewById(R.id.text_view_scale);
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        if (delta > 0) {
                            mGestureCropImageView.zoomInImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        } else {
                            mGestureCropImageView.zoomOutImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        }
                    }

                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel)).setMiddleLineColor(mActiveWidgetColor);
    }

    private void setAngleText(float angle) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        }
    }

    private void setScaleText(float scale) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent.setText(String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        }
    }

    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private final View.OnClickListener mStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                setWidgetState(v.getId());
            }
        }
    };

    protected void setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio.getVisibility() == View.VISIBLE) {
                setWidgetState(R.id.state_aspect_ratio);
            } else {
                setWidgetState(R.id.state_scale);
            }
        } else {
            setAllowedGestures(0);
        }
    }

    private void setWidgetState(@IdRes int stateViewId) {
        if (!mShowBottomControls) return;

        mWrapperStateAspectRatio.setSelected(stateViewId == R.id.state_aspect_ratio);
        mWrapperStateRotate.setSelected(stateViewId == R.id.state_rotate);
        mWrapperStateScale.setSelected(stateViewId == R.id.state_scale);

        mLayoutAspectRatio.setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
        mLayoutRotate.setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);
        mLayoutScale.setVisibility(stateViewId == R.id.state_scale ? View.VISIBLE : View.GONE);

        changeSelectedTab(stateViewId);

        if (stateViewId == R.id.state_scale) {
            setAllowedGestures(0);
        } else if (stateViewId == R.id.state_rotate) {
            setAllowedGestures(1);
        } else {
            setAllowedGestures(2);
        }
    }

    private void changeSelectedTab(int stateViewId) {
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.ucrop_photobox), mControlsTransition);

        mWrapperStateScale.findViewById(R.id.text_view_scale).setVisibility(stateViewId == R.id.state_scale ? View.VISIBLE : View.GONE);
        mWrapperStateAspectRatio.findViewById(R.id.text_view_crop).setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
        mWrapperStateRotate.findViewById(R.id.text_view_rotate).setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);

    }

    private void setAllowedGestures(int tab) {
        if (isOnTouch()) {
            mGestureCropImageView.setScaleEnabled(isScaleEnabled && mShowBottomControls ? mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE : isScaleEnabled);
            mGestureCropImageView.setRotateEnabled(isRotateEnabled && mShowBottomControls ? mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE : isRotateEnabled);
        }
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    protected void addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) findViewById(R.id.ucrop_photobox)).addView(mBlockingView);
    }

    protected void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        mShowLoader = true;
        supportInvalidateOptionsMenu();

        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);
                Activity currentActivity = getCurrentActivity();
                if (currentActivity instanceof PictureMultiCuttingActivity) {
                    // 如果是多图裁剪则返回逻辑交给PictureMultiCuttingActivity.java去处理
                } else {
                    onBackPressed();
                }
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                setResultError(t);
                onBackPressed();
            }
        });
    }

    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        setResult(RESULT_OK, new Intent()
                .putExtra(EXTRA_OUTPUT_URI, uri)
                .putExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(EXTRA_OUTPUT_OFFSET_Y, offsetY)
        );
    }

    protected void setResultError(Throwable throwable) {
        setResult(RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR, throwable));
    }

    protected Activity getCurrentActivity() {
        return this;
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    /**
     * exit activity
     */
    protected void closeActivity() {
        finish();
        exitAnimation();
    }

    protected void exitAnimation() {
        int exitAnimation = getIntent().getIntExtra(EXTRA_WINDOW_EXIT_ANIMATION, 0);
        overridePendingTransition(R.anim.ucrop_anim_fade_in, exitAnimation != 0 ? exitAnimation : R.anim.ucrop_close);
    }
}
