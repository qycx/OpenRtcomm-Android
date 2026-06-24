package com.qycx;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Path;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.ViewTreeObserver;
import android.provider.Settings;
import android.graphics.PointF;
import android.text.TextUtils;
import android.annotation.SuppressLint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.android.geometry.VisibleRegion;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapLibreMapOptions;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.LineLayer;
import org.maplibre.android.style.layers.Property;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.Feature;
import org.maplibre.geojson.FeatureCollection;
import org.maplibre.geojson.LineString;
import org.maplibre.geojson.Point;

import static org.maplibre.android.style.expressions.Expression.get;
import static org.maplibre.android.style.layers.PropertyFactory.lineColor;
import static org.maplibre.android.style.layers.PropertyFactory.lineDasharray;
import static org.maplibre.android.style.layers.PropertyFactory.lineOpacity;
import static org.maplibre.android.style.layers.PropertyFactory.lineWidth;
import static org.maplibre.android.style.layers.PropertyFactory.lineCap;
import static org.maplibre.android.style.layers.PropertyFactory.lineJoin;
import static org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap;
import static org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement;
import static org.maplibre.android.style.layers.PropertyFactory.iconImage;
import static org.maplibre.android.style.layers.PropertyFactory.iconSize;
import static org.maplibre.android.style.layers.PropertyFactory.iconRotate;
import static org.maplibre.android.style.layers.PropertyFactory.iconAnchor;
import static org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap;
import static org.maplibre.android.style.layers.PropertyFactory.textAnchor;
import static org.maplibre.android.style.layers.PropertyFactory.textColor;
import static org.maplibre.android.style.layers.PropertyFactory.textField;
import static org.maplibre.android.style.layers.PropertyFactory.textHaloColor;
import static org.maplibre.android.style.layers.PropertyFactory.textHaloWidth;
import static org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement;
import static org.maplibre.android.style.layers.PropertyFactory.textOffset;
import static org.maplibre.android.style.layers.PropertyFactory.textSize;


import com.qycx.mapnav.DlgTalkAv2Bridge;
import com.qycx.mapnav.MapFloatingVideoPanel;
import com.qycx.mapnav.AmapOfflineMapHelper;
import com.qycx.mapnav.SystemOverlayTestWindow;

import com.qycx.MainActivity;
import com.qysd.base.MapLocKeyword;
import com.qysd.base.MapLocSettings;
import com.qycx.anc.Consts;
import com.qycx.base.avCallProc;
import com.qycx.base.audioRecorder_android;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;

import static com.qycx.base.avCallProc.anc_hasAllPermission;
import static com.qycx.base.avCallProc.anc_isPermissionOk;
import static com.qycx.base.avCallProc.talk_doAvCall;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;

import static com.qycx.base.confReq.qyFuncs_Droid.sendConfReq;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qmcProc.isGroup;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_applyForConf;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.isCliHelpPublic;
import com.qycx.base.genericQueue;
import com.qycx.base.myDb;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyDefs_open;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyWmComm;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.TableItem;
import com.qycx.base.CIsTalker;
import static com.qycx.base.qmcProc.doChkTalkDraw;
import com.qycx.base.TalkAvStarter;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.graphics.drawable.GradientDrawable;
import android.view.TextureView;

import com.qycx.Activity_dlgTalk_av;


public class Fragment_map extends Fragment {

    public TextView tv_title;
    public TextView tv_content;
    private static final String ARG_EMBEDDED_CHILD = "embedded_child_map";
    private static final Object MARK_LINE_FILTER_LOCK = new Object();
    private static final List<WeakReference<Fragment_map>> MARK_LINE_FILTER_INSTANCES = new ArrayList<>();
    private static boolean sShowOnlySelfMarkLines;
    private static final int REQUEST_LOCATION_PERMISSION = 2001;
    private static final long LOCATION_TIMEOUT_MS = 45_000L;
    /** 持续定位回调间隔：越小坐标越“实时”，耗电略增。 */
    private static final long LOCATION_UPDATE_MIN_TIME_MS = 500L;
    /** 0 表示不按位移过滤，与 minTime 配合即可较密更新坐标。 */
    private static final float LOCATION_UPDATE_MIN_DISTANCE_M = 0f;
    private static final long LOCATION_SMOOTH_DURATION_MS = 700L;
    private static final long LOCATION_SMOOTH_FRAME_MS = 16L;
    private static final float LOCATION_SMOOTH_SNAP_DISTANCE_M = 1000f;
    /** 回调乱序保护：明显早于上次已采纳点的定位直接丢弃，防止“回拽”。 */
    private static final long LOCATION_OUT_OF_ORDER_TOLERANCE_MS = 1200L;
    /** 网络定位精度过差（米）时直接丢弃，减少高速移动时的大抖动。 */
    private static final float NETWORK_ACCURACY_REJECT_M = 80f;
    /** 速度较高时优先 GPS，达到该速度后直接忽略 network。 */
    private static final float NETWORK_DROP_SPEED_THRESHOLD_MPS = 6.0f;
    /**
     * 自上次 GPS 实时回调起超过该时间则允许 {@link LocationManager#NETWORK_PROVIDER} 更新（室内/长时间丢星）。
     * 此前尽量只用 GPS，避免网络点把轨迹“拉偏”。
     */
    private static final long PREFER_GPS_STALE_AFTER_MS = 90_000L;
    /** 地图交互最大缩放级别限制。 */
    private static final double MAP_MAX_ZOOM = 18.0;
    private static final long LOCATION_SEND_INTERVAL_MS = 2000L;
    /** 临时屏蔽向活动组广播自己的实时定位；本机定位显示和标注同步不受影响。 */
    private static final boolean GROUP_LOCATION_PUSH_ENABLED = false;
    private static final long REMOTE_STALE_MS = 10_000L;
    /** 他人定位仅关心活动组成员；成员缓存过期时间。 */
    private static final long ACTIVE_GROUP_MEMBER_CACHE_TTL_MS = 5_000L;
    /** 定位消息高频时，在线统计最多每 2 秒刷新一次，避免主线程被重复统计打满。 */
    private static final long GROUP_STATS_REFRESH_MIN_INTERVAL_MS = 2_000L;
    private static final long ACTIVE_GROUP_MEETING_REFRESH_MS = 2_000L;
    private static final long ACTIVE_GROUP_MEETING_RECOVERY_THROTTLE_MS = 10_000L;
    private static final String LOC_PREFIX = "__QY_LOC_V1__";
    private static final String MARK_PREFIX = "__QY_MARK_V1__";
    private static final String MARK_LINK_PREFIX = "__QY_MARK_LINK_V1__";
    /** 作战连线颜色：仅本地 / 本人已同步 / 他人已同步 */
    private static final String MARK_LINK_COLOR_LOCAL = "#FF9800";
    private static final String MARK_LINK_COLOR_SELF_SYNCED = "#00B35A";
    private static final String MARK_LINK_COLOR_OTHER_SYNCED = "#42A5F5";
    private static final String MARK_SYNC_PREFIX = "__QY_MARK_SYNC_V1__";
    private static final String MARK_OP_ADD = "add";
    private static final String MARK_OP_DEL = "del";
    private static final String MARK_SYNC_OP_REQ = "req";
    private static final String MARK_SYNC_OP_SNAPSHOT = "snapshot";
    private static final String MARK_SYNC_OP_SNAPSHOT_PART = "snapshot_part";
    private static final long MARK_SYNC_REQ_MIN_INTERVAL_MS = 30_000L;
    private static final long MARK_SYNC_REQ_RETRY_DELAY_MS = 3_000L;
    private static final int MARK_SYNC_REQ_MAX_RETRIES = 2;
    private static final int MARK_SYNC_REQ_MAX_PEERS = 3;
    private static final long MARK_SYNC_RESP_SLOT_DELAY_MS = 2_000L;
    private static final int MARK_SYNC_SNAPSHOT_PART_TARGET_CHARS = 700;
    private static final long MARK_SYNC_SNAPSHOT_PART_SEND_INTERVAL_MS = 200L;
    private static final String TAG_MAP_SYNC_FLOW = "MAP_SYNC_FLOW";
    private static final String TAG_MAP_SYNC_SIZE = "MAP_SYNC_SIZE";
    private static final String TAG_OFFLINE = "Fragment_map.offline";
    /** 地图+视频卡顿排查：{@code adb logcat -s QY_MAP_AV_PERF} */
    private static final String TAG_MAP_AV_PERF = "QY_MAP_AV_PERF";
    /** 相机拖动时作战连线重算合并间隔（低缩放）。 */
    private static final long MAP_LINES_DEBOUNCE_MS = 50L;
    /** 高缩放下瓦片与叠加更吃性能，连线防抖略加长。 */
    private static final long MAP_LINES_DEBOUNCE_HIGH_ZOOM_MS = 80L;
    private static final float MAP_LINES_HIGH_ZOOM_THRESHOLD = 14.5f;
    private static final String MARK_LINKS_SOURCE_ID = "qy-mark-links-source";
    private static final String MARK_LINKS_LAYER_ID = "qy-mark-links-layer";
    private static final FeatureCollection EMPTY_MARK_LINKS = FeatureCollection.fromFeatures(new ArrayList<>());
    private static final String MARK_LINK_LABELS_SOURCE_ID = "qy-mark-link-labels-source";
    private static final String MARK_LINK_LABELS_LAYER_ID = "qy-mark-link-labels-layer";
    private static final FeatureCollection EMPTY_MARK_LINK_LABELS = FeatureCollection.fromFeatures(new ArrayList<>());
    private static final long MARK_LINK_LABELS_UPDATE_INTERVAL_MS = 3000L;
    private static final String BATTLE_MARKS_SOURCE_ID = "qy-battle-marks-source";
    private static final String BATTLE_MARKS_LAYER_ID = "qy-battle-marks-layer";
    private static final String BATTLE_MARKS_OWNER_SOURCE_ID = "qy-battle-marks-owner-source";
    private static final String BATTLE_MARKS_OWNER_LAYER_ID = "qy-battle-marks-owner-layer";
    private static final String BATTLE_MARK_ICON_PREFIX = "qy-battle-mark-";
    private static final float BATTLE_MARK_MAPLIBRE_ICON_SIZE = 0.29f;
    private static final FeatureCollection EMPTY_BATTLE_MARKS = FeatureCollection.fromFeatures(new ArrayList<>());
    /** 某些机型偶发收不到 idle 回调；超时后自动认为停止拖动，避免连线长期不更新。 */
    private static final long CAMERA_MOVE_STALE_TIMEOUT_MS = 260L;
    /** 压测：模拟在线动点数量。 */
    private static final int SYNTHETIC_REMOTE_COUNT = 50;
    /** 压测：每个模拟点在中心周围 100m 范围内活动。 */
    private static final float SYNTHETIC_REMOTE_RADIUS_M = 100f;
    private static final long SYNTHETIC_REMOTE_UPDATE_MS = 300L;
    private static final long SYNTHETIC_REMOTE_ID_BASE = 8_800_000_000L;

    private static final String PREF_BASEMAP_LAYERS = "basemap_layers_v1";
    private static final String PREF_KEY_CB_VECTOR = "cb_vector";
    private static final String PREF_KEY_CB_IMAGERY = "cb_imagery";
    private static final String PREF_KEY_CB_ANNOTATION = "cb_annotation";
    private static final String PREF_KEY_CB_OFFLINE = "cb_offline";
    private static final String PREF_KEY_CB_OFFLINE_IMAGERY = "cb_offline_imagery";
    private static final String PREF_KEY_CB_OFFLINE_ANNOTATION = "cb_offline_annotation";

    /** 仅本地作战连线（synced=false）持久化 */
    private static final String PREF_MAP_MARK_LOCAL = "map_mark_local_links_v1";
    private static final String PREF_KEY_LOCAL_LINKS_PREFIX = "links_u";
    private static final String PREF_KEY_LOCAL_LINKS_GROUP_PART = "_g";

    private static final String MSG_TDT_KEY_INVALID = "天地图 key 无效/权限不足";
    private static final String MSG_LOCATION_ENABLE = "请打开手机定位（GPS/网络定位）";
    private static final String MSG_LOCATION_TIMEOUT = "暂未获取到位置，请到室外或检查定位开关";
    /** 在线导航服务地址（按你本地部署修改为可达地址；127.0.0.1 仅在同机调试可达）。 */
    private static final String DEFAULT_GRAPHHOPPER_ROUTE_HOST = "103.85.174.150";
    private static final String GRAPHHOPPER_WEB_ROUTE_API_SUFFIX = ":8991/route";

    /** Replace with your Tianditu key (tk). */
    private static final String TIANDITU_TOKEN = "41f1322f71eb9739cd41d9479b9dc6d0";

    private static final String STYLE_OSM = "{"
            + "\"version\":8,"
            + "\"sources\":{"
            + "\"osm\":{\"type\":\"raster\",\"tiles\":[\"https://tile.openstreetmap.org/{z}/{x}/{y}.png\"],\"tileSize\":256}"
            + "},"
            + "\"layers\":[{\"id\":\"osm-layer\",\"type\":\"raster\",\"source\":\"osm\"}]"
            + "}";

    /** 离线瓦片总目录：没有该目录时禁用所有离线底图入口。 */
    private static final String LOCAL_OFFLINE_TILE_ROOT = "/storage/emulated/0/mapTitles";
    private static final String LOCAL_OFFLINE_TILE_ZIP = "/storage/emulated/0/mapTitles.zip";
    private static final String LOCAL_OFFLINE_IMAGERY_MBTILES_IN_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/imagery.mbtiles";
    private static final String LOCAL_OFFLINE_VECTOR_MBTILES_IN_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/sl.mbtiles";
    private static final String LOCAL_OFFLINE_ANNOTATION_MBTILES_IN_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/zj.mbtiles";
    /** 离线矢量 XYZ 根目录：{@code /storage/emulated/0/mapTitles/sl/{z}/{x}/{y}.png}。 */
    private static final String LOCAL_OFFLINE_VECTOR_TILE_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/sl";
    /** 离线影像 XYZ 根目录：{@code /storage/emulated/0/mapTitles/xyz/{z}/{x}/{y}.png}。 */
    private static final String LOCAL_OFFLINE_IMAGERY_TILE_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/xyz";
    /** 离线注记 XYZ 根目录：{@code /storage/emulated/0/mapTitles/zj/{z}/{x}/{y}.png}。 */
    private static final String LOCAL_OFFLINE_ANNOTATION_TILE_ROOT = LOCAL_OFFLINE_TILE_ROOT + "/zj";
    private static final String LOCAL_ROADS_HB_ROOT = "/storage/emulated/0/roads/hb";
    private static final int LOCAL_XYZ_MIN_ZOOM = 0;
    private static final int LOCAL_XYZ_MAX_ZOOM = 18;
    /** 偏航自动重算阈值（米）。 */
    private static final float NAV_REROUTE_DEVIATION_METERS = 40f;
    /** 连续命中偏航次数，避免 GPS 抖动误触发。 */
    private static final int NAV_REROUTE_DEVIATION_HITS = 2;
    /** 自动重算冷却时间（毫秒）。 */
    private static final long NAV_REROUTE_COOLDOWN_MS = 2_000L;
    private static final String[] OFFLINE_ROADS_CORE_FILES = new String[]{
            "edges",
            "nodes",
            "geometry",
            "properties",
            "location_index",
            "edgekv_keys",
            "edgekv_vals"
    };

    @NonNull
    private static File getLocalOfflineTileRootDir() {
        return new File(LOCAL_OFFLINE_TILE_ROOT);
    }

    @NonNull
    private static File getLocalOfflineTileZipFile() {
        return new File(LOCAL_OFFLINE_TILE_ZIP);
    }

    @NonNull
    private static File getLocalOfflineImageryMbtilesFile() {
        return new File(LOCAL_OFFLINE_IMAGERY_MBTILES_IN_ROOT);
    }

    @NonNull
    private static File getLocalOfflineVectorMbtilesFile() {
        return new File(LOCAL_OFFLINE_VECTOR_MBTILES_IN_ROOT);
    }

    @NonNull
    private static File getLocalOfflineAnnotationMbtilesFile() {
        return new File(LOCAL_OFFLINE_ANNOTATION_MBTILES_IN_ROOT);
    }

    @NonNull
    private static File getLocalOfflineVectorTileRootDir() {
        return new File(LOCAL_OFFLINE_VECTOR_TILE_ROOT);
    }

    @NonNull
    private static File getLocalOfflineImageryTileRootDir() {
        return new File(LOCAL_OFFLINE_IMAGERY_TILE_ROOT);
    }

    @NonNull
    private static File getLocalOfflineAnnotationTileRootDir() {
        return new File(LOCAL_OFFLINE_ANNOTATION_TILE_ROOT);
    }

    private static boolean hasOfflineTileRootDir() {
        return getLocalOfflineTileRootDir().isDirectory();
    }

    private static boolean hasOfflineVectorTiles() {
        return getLocalOfflineVectorMbtilesFile().isFile()
                || (hasOfflineTileRootDir() && getLocalOfflineVectorTileRootDir().isDirectory());
    }

    private static boolean hasOfflineImageryTiles() {
        return getLocalOfflineImageryMbtilesFile().isFile()
                || (hasOfflineTileRootDir() && getLocalOfflineImageryTileRootDir().isDirectory());
    }

    private static boolean hasOfflineAnnotationTiles() {
        return getLocalOfflineAnnotationMbtilesFile().isFile()
                || (hasOfflineTileRootDir() && getLocalOfflineAnnotationTileRootDir().isDirectory());
    }

    @NonNull
    private static File getLocalRoadsHbDir() {
        return new File(LOCAL_ROADS_HB_ROOT);
    }

    private MapView mapView;
    private ViewTreeObserver.OnGlobalLayoutListener mapLayoutListener;
    private int lastMapContainerW = -1;
    private int lastMapContainerH = -1;

    public void onHostConfigurationChanged(@NonNull Configuration newConfig) {
        if (getView() != null) {
            captureMapOverlayPortraitSnapshotIfNeeded(getView());
            applyMapOverlayOrientationUi();
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && mapView != null) {
            forceMapViewFillParent();
            mapView.postDelayed(this::forceMapViewFillParent, 120);
            mapView.postDelayed(this::forceMapViewFillParent, 280);
            mapView.postDelayed(this::restartMapRenderPipelineIfNeeded, 360);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onHostConfigurationChanged(newConfig);
    }

    /** 竖屏默认 overlay 参数，用于横屏切换后恢复。 */
    private static final class MapOverlayPortraitSnapshot {
        int groupStatsGravity;
        int groupStatsTopMargin;
        int groupStatsMarginStart;
        int groupStatsMarginEnd;
        int compassWidth;
        int compassHeight;
        int compassGravity;
        int compassTopMargin;
        int compassMarginEnd;
        int layerBarGravity;
        int layerBarTopMargin;
        int layerBarBottomMargin;
        int layerBarMarginEnd;
        int layerPanelGravity;
        int layerPanelMarginEnd;
        int rightToolsGravity;
        int rightToolsTopMargin;
        int rightToolsMarginEnd;
    }

    private void captureMapOverlayPortraitSnapshotIfNeeded(@NonNull View root) {
        if (mapOverlayPortraitSnapshot != null) {
            return;
        }
        if (layoutGroupStatsBar == null) {
            layoutGroupStatsBar = root.findViewById(R.id.layoutGroupStatsBar);
        }
        if (layoutLayerBarRoot == null) {
            layoutLayerBarRoot = root.findViewById(R.id.layoutLayerBar);
        }
        MapOverlayPortraitSnapshot s = new MapOverlayPortraitSnapshot();
        if (layoutGroupStatsBar != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutGroupStatsBar.getLayoutParams();
            s.groupStatsGravity = lp.gravity;
            s.groupStatsTopMargin = lp.topMargin;
            s.groupStatsMarginStart = lp.getMarginStart();
            s.groupStatsMarginEnd = lp.getMarginEnd();
        }
        if (ivCompass != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ivCompass.getLayoutParams();
            s.compassWidth = lp.width;
            s.compassHeight = lp.height;
            s.compassGravity = lp.gravity;
            s.compassTopMargin = lp.topMargin;
            s.compassMarginEnd = lp.getMarginEnd();
        }
        if (layoutLayerBarRoot != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutLayerBarRoot.getLayoutParams();
            s.layerBarGravity = lp.gravity;
            s.layerBarTopMargin = lp.topMargin;
            s.layerBarBottomMargin = lp.bottomMargin;
            s.layerBarMarginEnd = lp.getMarginEnd();
        }
        if (layoutLayerPanel != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutLayerPanel.getLayoutParams();
            s.layerPanelGravity = lp.gravity;
            s.layerPanelMarginEnd = lp.getMarginEnd();
        }
        if (layoutRightToolsRoot != null) {
            ViewGroup.LayoutParams raw = layoutRightToolsRoot.getLayoutParams();
            if (raw instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) raw;
                s.rightToolsGravity = lp.gravity;
                s.rightToolsTopMargin = lp.topMargin;
                s.rightToolsMarginEnd = lp.getMarginEnd();
            }
        }
        mapOverlayPortraitSnapshot = s;   
    }

    /** 与导航/定位等右侧圆钮一致：42dp 圆盘 + 8dp 内边距。 */
    private void normalizeMapToolImageButton(@NonNull ImageView btn) {
        int size = dp(42);
        int pad = dp(8);
        if (ivCompassBtn != null) {
            ViewGroup.LayoutParams compassLp = ivCompassBtn.getLayoutParams();
            if (compassLp != null && compassLp.width > 0 && compassLp.height > 0) {
                size = Math.min(compassLp.width, compassLp.height);
            }
            pad = ivCompassBtn.getPaddingLeft();
        }
        btn.setPadding(pad, pad, pad, pad);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btn.setAdjustViewBounds(true);
        ViewGroup.LayoutParams p = btn.getLayoutParams();
        if (p != null) {
            p.width = size;
            p.height = size;
            btn.setLayoutParams(p);
        }
    }

    private void applyLandscapeRightToolsPosition() {
        if (layoutRightToolsRoot == null) {
            return;
        }
        ViewGroup.LayoutParams raw = layoutRightToolsRoot.getLayoutParams();
        if (!(raw instanceof FrameLayout.LayoutParams)) {
            return;
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) raw;
        lp.gravity = Gravity.TOP | Gravity.END;
        lp.topMargin = dp(8) + dp(72) + dp(12);
        lp.bottomMargin = 0;
        lp.setMarginEnd(dp(14));
        layoutRightToolsRoot.setLayoutParams(lp);
    }

    private boolean isMapLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void applyMapOverlayTextSizePx(@Nullable TextView tv, float textSizePx) {
        if (tv == null) {
            return;
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
    }

    /** 由 MainActivity 在分屏布局应用后调用，避免 Fragment 自行猜测分屏状态失败。 */
    public void applyMapOverlayTextCompact(boolean compact) {
        if (!isAdded()) {
            return;
        }
        View root = getView();
        if (root == null) {
            return;
        }
        int compactDimen = R.dimen.map_overlay_text_sp_compact;
        if (compact && getActivity() instanceof MainActivity) {
            compactDimen = ((MainActivity) getActivity()).getMapOverlayTextCompactDimenResId();
        }
        float px = getResources().getDimension(compact ? compactDimen : R.dimen.map_overlay_text_sp);
        applyMapOverlayTextSizePx(root.findViewById(R.id.tvGroupStats), px);
        applyMapOverlayTextSizePx(root.findViewById(R.id.tvActiveGrpMeetingEntry), px);
        applyMapOverlayTextSizePx(root.findViewById(R.id.tvCurrentCoord), px);
    }

    public void refreshMapOverlayTextScale() {
        if (!isAdded()) {
            return;
        }
        android.app.Activity act = getActivity();
        boolean compact = act instanceof MainActivity && ((MainActivity) act).isMapOverlayTextCompactEnabled();
        applyMapOverlayTextCompact(compact);
    }

    private void applyMapOverlayTextSizes() {
        refreshMapOverlayTextScale();
    }

    private boolean isMapSplitOverlayHost() {
        android.app.Activity act = getActivity();
        return act instanceof MainActivity && ((MainActivity) act).isMapFloatingAvSplitEmbedded();
    }

    private void applyMapOverlayOrientationUi() {
        View root = getView();
        if (!isAdded() || root == null) {
            return;
        }
        captureMapOverlayPortraitSnapshotIfNeeded(root);
        if (isMapSplitOverlayHost()) {
            applySplitMapOverlayLayout(root);
        } else if (isMapLandscape()) {
            applyLandscapeMapOverlayLayout(root);
        } else {
            restorePortraitMapOverlayLayout();
        }
        applyMapOverlayTextSizes();
    }

    /** 横屏分屏：活动组条顶中、罗盘 72dp。 */
    private void applyLandscapeSplitMapOverlayStyle(@NonNull View root) {
        if (layoutGroupStatsBar == null) {
            layoutGroupStatsBar = root.findViewById(R.id.layoutGroupStatsBar);
        }
        if (layoutGroupStatsBar != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutGroupStatsBar.getLayoutParams();
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp.setMarginStart(0);
            lp.setMarginEnd(0);
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            lp.topMargin = dp(10);
            layoutGroupStatsBar.setLayoutParams(lp);
        }
        applySplitMapCompassSize(root, (int) getResources().getDimension(R.dimen.map_split_compass_size_landscape));
    }

    /** 竖屏分屏：活动组条保持与未分屏相同位置；罗盘 80dp。 */
    private void applyPortraitSplitMapOverlayStyle(@NonNull View root) {
        restoreSplitMapGroupStatsBarPosition(root);
        applySplitMapCompassSize(root, (int) getResources().getDimension(R.dimen.map_split_compass_size_portrait));
    }

    private void restoreSplitMapGroupStatsBarPosition(@NonNull View root) {
        if (layoutGroupStatsBar == null) {
            layoutGroupStatsBar = root.findViewById(R.id.layoutGroupStatsBar);
        }
        if (layoutGroupStatsBar == null) {
            return;
        }
        MapOverlayPortraitSnapshot s = mapOverlayPortraitSnapshot;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutGroupStatsBar.getLayoutParams();
        if (s != null) {
            lp.gravity = s.groupStatsGravity;
            lp.topMargin = s.groupStatsTopMargin;
            lp.setMarginStart(s.groupStatsMarginStart);
            lp.setMarginEnd(s.groupStatsMarginEnd);
        } else {
            lp.gravity = Gravity.TOP | Gravity.START;
            lp.topMargin = dp(16);
            lp.setMarginStart(dp(12));
            lp.setMarginEnd(0);
        }
        layoutGroupStatsBar.setLayoutParams(lp);
    }

    private void applySplitMapCompassSize(@NonNull View root, int sizePx) {
        if (ivCompass == null) {
            ivCompass = root.findViewById(R.id.ivCompass);
        }
        if (ivCompass != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ivCompass.getLayoutParams();
            lp.width = sizePx;
            lp.height = sizePx;
            lp.gravity = Gravity.TOP | Gravity.END;
            lp.topMargin = dp(8);
            lp.setMarginEnd(dp(14));
            ivCompass.setLayoutParams(lp);
        }
    }

    private void applySplitMapOverlayLayout(@NonNull View root) {
        if (isMapLandscape()) {
            applyLandscapeSplitMapOverlayStyle(root);
            applyLandscapeRightToolsPosition();
            attachLayerBtnToRightToolsColumn();
            updateLandscapeLayerPanelLayout(false);
        } else {
            applyPortraitSplitMapOverlayStyle(root);
            attachLayerBtnToRightToolsColumn();
            updateSplitLayerBarHostVisibility(mapLayerPanelVisible);
            resetLayerPanelBodyScrollForPortrait();
        }
    }

    /** 横屏非分屏：图层钮仍在右侧竖排；仅隐藏无按钮的 layoutLayerBar 宿主。 */
    private void applyLandscapeMapOverlayLayout(@NonNull View root) {
        applyLandscapeSplitMapOverlayStyle(root);
        applyLandscapeRightToolsPosition();
        attachLayerBtnToRightToolsColumn();
        updateLandscapeLayerPanelLayout(false);
    }

    /** 横屏：图层钮并入右侧竖排（与导航/定位/横竖屏对齐），面板在按钮列左侧向上展开。 */
    private void attachLayerBtnToRightToolsColumn() {
        if (ivLayerBtn == null || layoutRightToolsRoot == null) {
            return;
        }
        if (!(layoutRightToolsRoot instanceof ConstraintLayout)) {
            return;
        }
        if (layerBtnAttachedToRightTools && ivLayerBtn.getParent() == layoutRightToolsRoot) {
            normalizeMapToolImageButton(ivLayerBtn);
            return;
        }
        if (ivLayerBtn.getParent() == layoutLayerBarRoot) {
            ViewGroup layerBar = (ViewGroup) layoutLayerBarRoot;
            if (layerBtnPortraitFrameLp == null) {
                ViewGroup.LayoutParams raw = ivLayerBtn.getLayoutParams();
                if (raw instanceof FrameLayout.LayoutParams) {
                    layerBtnPortraitFrameLp = (FrameLayout.LayoutParams) raw;
                } else {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(42), dp(42));
                    lp.gravity = Gravity.BOTTOM | Gravity.END;
                    layerBtnPortraitFrameLp = lp;
                }
            }
            layerBar.removeView(ivLayerBtn);
        } else if (ivLayerBtn.getParent() instanceof ViewGroup) {
            ((ViewGroup) ivLayerBtn.getParent()).removeView(ivLayerBtn);
        }
        ConstraintLayout tools = (ConstraintLayout) layoutRightToolsRoot;
        int size = dp(42);
        if (ivCompassBtn != null) {
            ViewGroup.LayoutParams compassLp = ivCompassBtn.getLayoutParams();
            if (compassLp != null && compassLp.width > 0 && compassLp.height > 0) {
                size = Math.min(compassLp.width, compassLp.height);
            }
        }
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(size, size);
        lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        lp.topToBottom = R.id.ivCompassBtn;
        lp.topMargin = dp(10);
        tools.addView(ivLayerBtn, lp);
        normalizeMapToolImageButton(ivLayerBtn);
        layerBtnAttachedToRightTools = true;
    }

    /** 竖屏分屏：图层钮在右侧竖排时仅控制图层栏宿主显隐，不改动右侧工具列 Frame 位置。 */
    private void updateSplitLayerBarHostVisibility(boolean panelVisible) {
        if (!isAdded() || layoutLayerBarRoot == null || !layerBtnAttachedToRightTools || isMapLandscape()) {
            return;
        }
        if (!isMapSplitOverlayHost()) {
            return;
        }
        layoutLayerBarRoot.setVisibility(panelVisible ? View.VISIBLE : View.GONE);
    }

    private void detachLayerBtnToLayerBar() {
        if (ivLayerBtn == null || layoutLayerBarRoot == null || !layerBtnAttachedToRightTools) {
            return;
        }
        if (ivLayerBtn.getParent() instanceof ViewGroup) {
            ((ViewGroup) ivLayerBtn.getParent()).removeView(ivLayerBtn);
        }
        FrameLayout.LayoutParams lp = layerBtnPortraitFrameLp;
        if (lp == null) {
            lp = new FrameLayout.LayoutParams(dp(42), dp(42));
            lp.gravity = Gravity.BOTTOM | Gravity.END;
        }
        ((ViewGroup) layoutLayerBarRoot).addView(ivLayerBtn, lp);
        layerBtnAttachedToRightTools = false;
    }

    private void updateLandscapeLayerPanelLayout(boolean panelVisible) {
        if (!isAdded() || layoutLayerBarRoot == null || layoutLayerPanel == null) {
            return;
        }
        if (!isMapLandscape()) {
            layoutLayerBarRoot.setVisibility(View.VISIBLE);
            return;
        }
        if (!panelVisible) {
            layoutLayerBarRoot.setVisibility(View.GONE);
            FrameLayout.LayoutParams panelLp = (FrameLayout.LayoutParams) layoutLayerPanel.getLayoutParams();
            if (panelLp != null) {
                panelLp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                layoutLayerPanel.setLayoutParams(panelLp);
            }
            resetLayerPanelBodyScrollForPortrait();
            return;
        }
        layoutLayerBarRoot.setVisibility(View.VISIBLE);
        int toolsMarginEnd = dp(14);
        int toolsWidth = dp(42);
        FrameLayout.LayoutParams barLp = (FrameLayout.LayoutParams) layoutLayerBarRoot.getLayoutParams();
        if (barLp != null) {
            barLp.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            barLp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            barLp.gravity = Gravity.BOTTOM | Gravity.END;
            barLp.topMargin = 0;
            barLp.setMarginEnd(toolsMarginEnd + toolsWidth);
            barLp.bottomMargin = dp(72);
            layoutLayerBarRoot.setLayoutParams(barLp);
        }
        FrameLayout.LayoutParams panelLp = (FrameLayout.LayoutParams) layoutLayerPanel.getLayoutParams();
        if (panelLp != null) {
            panelLp.width = dp(280);
            panelLp.gravity = Gravity.BOTTOM | Gravity.END;
            panelLp.setMarginEnd(0);
            panelLp.bottomMargin = 0;
            layoutLayerPanel.setLayoutParams(panelLp);
        }
        View root = getView();
        if (root != null) {
            layoutLayerBarRoot.post(() -> applyLandscapeLayerPanelMaxHeight(root));
        }
    }

    private void applyLandscapeLayerPanelMaxHeight(@NonNull View root) {
        if (!isAdded() || !isMapLandscape() || layoutLayerPanel == null || layoutRightToolsRoot == null) {
            return;
        }
        int[] toolsLoc = new int[2];
        int[] rootLoc = new int[2];
        layoutRightToolsRoot.getLocationOnScreen(toolsLoc);
        root.getLocationOnScreen(rootLoc);
        int toolsTop = toolsLoc[1] - rootLoc[1];
        int headerH = dp(48);
        int maxH = Math.max(dp(140), root.getHeight() - toolsTop - dp(8));
        FrameLayout.LayoutParams panelLp = (FrameLayout.LayoutParams) layoutLayerPanel.getLayoutParams();
        if (panelLp == null) {
            return;
        }
        panelLp.height = maxH;
        layoutLayerPanel.setLayoutParams(panelLp);
        View body = layoutLayerPanel.findViewById(R.id.layoutLayerPanelBody);
        if (body != null) {
            ViewGroup.LayoutParams bodyLp = body.getLayoutParams();
            bodyLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            body.setLayoutParams(bodyLp);
        }
        View scroll = layoutLayerPanel.findViewById(R.id.layerPanelScroll);
        if (scroll != null) {
            ViewGroup.LayoutParams scrollLp = scroll.getLayoutParams();
            scrollLp.height = Math.max(dp(80), maxH - headerH);
            scroll.setLayoutParams(scrollLp);
        }
    }

    private void resetLayerPanelBodyScrollForPortrait() {
        if (layoutLayerPanel == null) {
            return;
        }
        View body = layoutLayerPanel.findViewById(R.id.layoutLayerPanelBody);
        if (body != null) {
            ViewGroup.LayoutParams bodyLp = body.getLayoutParams();
            bodyLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            body.setLayoutParams(bodyLp);
        }
        View scroll = layoutLayerPanel.findViewById(R.id.layerPanelScroll);
        if (scroll != null) {
            ViewGroup.LayoutParams scrollLp = scroll.getLayoutParams();
            scrollLp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            scroll.setLayoutParams(scrollLp);
        }
    }

    private void restorePortraitMapOverlayLayout() {
        detachLayerBtnToLayerBar();
        updateLandscapeLayerPanelLayout(false);
        if (layoutLayerBarRoot != null) {
            layoutLayerBarRoot.setVisibility(View.VISIBLE);
        }
        MapOverlayPortraitSnapshot s = mapOverlayPortraitSnapshot;
        if (s == null) {
            return;
        }
        if (layoutGroupStatsBar != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutGroupStatsBar.getLayoutParams();
            lp.gravity = s.groupStatsGravity;
            lp.topMargin = s.groupStatsTopMargin;
            lp.setMarginStart(s.groupStatsMarginStart);
            lp.setMarginEnd(s.groupStatsMarginEnd);
            layoutGroupStatsBar.setLayoutParams(lp);
        }
        if (ivCompass != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ivCompass.getLayoutParams();
            lp.width = s.compassWidth;
            lp.height = s.compassHeight;
            lp.gravity = s.compassGravity;
            lp.topMargin = s.compassTopMargin;
            lp.setMarginEnd(s.compassMarginEnd);
            ivCompass.setLayoutParams(lp);
        }
        if (layoutLayerBarRoot != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutLayerBarRoot.getLayoutParams();
            lp.gravity = s.layerBarGravity;
            lp.topMargin = s.layerBarTopMargin;
            lp.bottomMargin = s.layerBarBottomMargin;
            lp.setMarginEnd(s.layerBarMarginEnd);
            layoutLayerBarRoot.setLayoutParams(lp);
        }
        if (layoutLayerPanel != null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutLayerPanel.getLayoutParams();
            lp.gravity = s.layerPanelGravity;
            lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            lp.setMarginEnd(s.layerPanelMarginEnd);
            layoutLayerPanel.setLayoutParams(lp);
            resetLayerPanelBodyScrollForPortrait();
        }
        if (layoutRightToolsRoot != null) {
            ViewGroup.LayoutParams raw = layoutRightToolsRoot.getLayoutParams();
            if (raw instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) raw;
                lp.gravity = s.rightToolsGravity;
                lp.topMargin = s.rightToolsTopMargin;
                lp.setMarginEnd(s.rightToolsMarginEnd);
                layoutRightToolsRoot.setLayoutParams(lp);
            }
        }
    }

    /**
     * 宿主（如通话页分屏/浮窗切换）改动了地图容器尺寸后，主动触发一次尺寸与视口刷新。
     * 否则可能出现“先显示半屏，手势后才完整重绘”。
     */
    public void onHostEmbeddedLayoutChanged() {
        if (mapView == null) return;
        if (getView() != null) {
            applyMapOverlayOrientationUi();
        } else {
            refreshMapOverlayTextScale();
        }
        forceMapViewFillParent();
        mapView.postDelayed(this::forceMapViewFillParent, 60);
        mapView.postDelayed(() -> {
            triggerMapViewportRefreshIfNeeded();
            refreshSelfLocationAfterHostLayoutChange();
            refreshMapOverlayTextScale();
        }, 120);
        mapView.postDelayed(() -> {
            refreshSelfLocationAfterHostLayoutChange();
            refreshMapOverlayTextScale();
        }, 280);
    }

    private void refreshSelfLocationAfterHostLayoutChange() {
        if (!isAdded() || mapLibreMap == null) return;
        if (displayedLatLng != null) {
            // 分屏后即使未收到新定位，也要按最新坐标重算一次屏幕投影。
            renderDisplayedLocation();
            updateRemoteOverlays(true);
            updateBattleMarkOverlays();
            return;
        }
        if (currentLatLng != null) {
            displayedLatLng = currentLatLng;
            renderDisplayedLocation();
            updateRemoteOverlays(true);
            updateBattleMarkOverlays();
        }
        if (pendingLocationListener == null) {
            tryLocateAndMark();
        }
    }

    private void forceMapViewFillParent() {
        if (mapView == null) return;
        ViewParent p = mapView.getParent();
        if (!(p instanceof ViewGroup)) {
            mapView.requestLayout();
            triggerMapViewportRefreshIfNeeded();
            return;
        }
        ViewGroup vg = (ViewGroup) p;
        int pw = vg.getWidth();
        int ph = vg.getHeight();
        if (pw <= 0 || ph <= 0) return;
        ViewGroup.LayoutParams lp = mapView.getLayoutParams();
        if (lp != null && (lp.width != ViewGroup.LayoutParams.MATCH_PARENT || lp.height != ViewGroup.LayoutParams.MATCH_PARENT)) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mapView.setLayoutParams(lp);
        }
        int wSpec = View.MeasureSpec.makeMeasureSpec(pw, View.MeasureSpec.EXACTLY);
        int hSpec = View.MeasureSpec.makeMeasureSpec(ph, View.MeasureSpec.EXACTLY);
        mapView.measure(wSpec, hSpec);
        mapView.layout(0, 0, pw, ph);
        mapView.invalidate();
        triggerMapViewportRefreshIfNeeded();
    }

    private void triggerMapViewportRefreshIfNeeded() {
        if (mapView == null || mapLibreMap == null) return;
        int w = mapView.getWidth();
        int h = mapView.getHeight();
        if (w <= 0 || h <= 0) return;
        mapView.post(() -> {
            if (mapView == null || mapLibreMap == null) return;
            CameraPosition cp = mapLibreMap.getCameraPosition();
            if (cp == null) return;
            mapLibreMap.setCameraPosition(new CameraPosition.Builder(cp).build());
        });
    }

    private void restartMapRenderPipelineIfNeeded() {
        if (mapView == null || !isAdded()) return;
        int w = mapView.getWidth();
        int h = mapView.getHeight();
        if (w <= 0 || h <= 0) return;
        try {
            // 仅重启 MapView 渲染管线，避免重建通话 Activity 导致视频中断。
            mapView.onStop();
            mapView.onStart();
            mapView.onResume();
        } catch (Throwable ignored) {
        }
        forceMapViewFillParent();
        triggerMapViewportRefreshIfNeeded();
    }

    private void addMapLayoutChangeListener() {
        if (mapView == null || mapLayoutListener != null) return;
        mapLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mapView == null) return;
                int w = mapView.getWidth();
                int h = mapView.getHeight();
                if (w > 0 && h > 0 && (w != lastMapContainerW || h != lastMapContainerH)) {
                    lastMapContainerW = w;
                    lastMapContainerH = h;
                    forceMapViewFillParent();
                    triggerMapViewportRefreshIfNeeded();
                }
            }
        };
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(mapLayoutListener);
    }

    private void removeMapLayoutChangeListener() {
        if (mapView == null || mapLayoutListener == null) return;
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeOnGlobalLayoutListener(mapLayoutListener);
        }
        mapLayoutListener = null;
    }

    private View layoutGroupStatsBar;
    private FrameLayout ivCompass;
    private ImageView ivLocateBtn;
    /** 含定位/导航/罗盘竖排与 {@link #layoutLocatePanel}，需在连线层等全屏子 View 之上才能点到菜单。 */
    private View layoutRightToolsRoot;
    private CardView layoutLocatePanel;
    private TextView tvLocateMenuFollow;
    private ImageView ivCompassBtn;
    private ImageView ivZoomInBtn;
    private ImageView ivZoomOutBtn;
    private ImageView ivMarkBtn;
    private ImageView ivMarkLineFilterBtn;
    private ImageView ivMarkNoteListBtn;
    private boolean showOnlySelfMarkLines;
    private ImageView ivHeading;
    private ImageView ivCompassNeedleLarge;
    private FrameLayout layoutCompassPanel;
    private FrameLayout layoutCompassRotatingLayer;
    private FrameLayout compassTopRotatingLayer;
    private FrameLayout layoutFloatingVideoLegacy;
    private FrameLayout layoutFloatingFragmentHost;
    private TextView tvGroupStats;
    private View viewActiveGrpMeetingLamp;
    private TextView tvActiveGrpMeetingEntry;
    private TextView remoteMarkNoticeView;
    private TextView tvCurrentCoord;
    private MapFloatingVideoPanel floatingVideoPanel;
    private HorizontalScrollView remoteCameraStripScroll;
    private LinearLayout remoteCameraStrip;
    private final Map<Long, FrameLayout> remotePreviewByUser = new HashMap<>();
    private long expandedPreviewUserId = -1;
    private long floatingVideoTargetId;
    private long activeGroupMeetingId;
    private boolean activeGroupMeetingRunning;
    private long activeGroupMeetingRecoveryGroupId;
    private long activeGroupMeetingRecoveryStartedMs;
    /** 本会话已对 notify 做过静默 accept 的组 id */
    private long mapGroupNotifyAcceptedGroupId;
    private SystemOverlayTestWindow systemOverlayTestWindow;
    /** 从会议页 TextureView 镜像到地图小窗（多窗显示同一路合成画面） */
    private Bitmap lastMirrorBitmap;
    /** {@link #videoMirrorRunnable} 中 TextureView 为空的采样日志，避免刷屏 */
    private int videoMirrorNullTvCounter;

    /** 无活跃 {@link Activity_dlgTalk_av} 时不应跑镜像循环；否则 TextureView 永久为空会每 300ms post 一次占满主线程，从通话回到地图后拖动卡顿。 */
    private boolean shouldRunVideoMirror() {
        boolean stripVisible = remoteCameraStrip != null && remoteCameraStrip.getChildCount() > 0;
        boolean floatingVisible = floatingVideoPanel != null && floatingVideoPanel.isVisible();
        if (!stripVisible && !floatingVisible) {
            return false;
        }
        return DlgTalkAv2Bridge.getActiveTextureView() != null;
    }

    private final Runnable videoMirrorRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || !shouldRunVideoMirror()) {
                stopVideoMirrorLoop();
                return;
            }
            long tAll = SystemClock.elapsedRealtime();
            long getBitmapMs = -1L;
            int bmpW = 0;
            int bmpH = 0;
            String phase = "ok";
            try {
                TextureView tv = DlgTalkAv2Bridge.getActiveTextureView();
                if (tv == null) {
                    phase = "tv_null";
                    if ((++videoMirrorNullTvCounter % 40) == 0) {
                        Log.d(TAG_MAP_AV_PERF, "videoMirror: TextureView null x" + videoMirrorNullTvCounter
                                + " resumed=" + isResumed() + " embedAv=" + (DlgTalkAv2Bridge.getActiveTextureView() != null));
                    }
                    locationHandler.postDelayed(this, 300);
                    return;
                }
                if (!tv.isAvailable() || tv.getWidth() <= 0 || tv.getHeight() <= 0) {
                    phase = "tv_not_ready";
                    if ((videoMirrorNullTvCounter % 40) == 0) {
                        Log.d(TAG_MAP_AV_PERF, "videoMirror: tv not ready avail=" + tv.isAvailable()
                                + " size=" + tv.getWidth() + "x" + tv.getHeight());
                    }
                    videoMirrorNullTvCounter++;
                    locationHandler.postDelayed(this, 300);
                    return;
                }
                long tGb = SystemClock.elapsedRealtime();
                Bitmap b = tv.getBitmap();
                getBitmapMs = SystemClock.elapsedRealtime() - tGb;
                if (b != null && !b.isRecycled()) {
                    bmpW = b.getWidth();
                    bmpH = b.getHeight();
                    if (remoteCameraStrip != null) {
                        for (int i = 0; i < remoteCameraStrip.getChildCount(); i++) {
                            View card = remoteCameraStrip.getChildAt(i);
                            if (!(card instanceof ViewGroup)) continue;
                            ImageView iv = card.findViewWithTag("mirror");
                            if (iv != null) {
                                iv.setImageBitmap(null);
                            }
                        }
                    }
                    if (floatingVideoPanel != null) floatingVideoPanel.clearMirrorBitmap();
                    if (lastMirrorBitmap != null && !lastMirrorBitmap.isRecycled() && lastMirrorBitmap != b) {
                        lastMirrorBitmap.recycle();
                    }
                    lastMirrorBitmap = b;
                    if (remoteCameraStrip != null) {
                        for (int i = 0; i < remoteCameraStrip.getChildCount(); i++) {
                            View card = remoteCameraStrip.getChildAt(i);
                            if (!(card instanceof ViewGroup)) continue;
                            ImageView iv = card.findViewWithTag("mirror");
                            if (iv != null) {
                                iv.setImageBitmap(b);
                            }
                        }
                    }
                    if (floatingVideoPanel != null && floatingVideoPanel.isVisible()) {
                        floatingVideoPanel.setMirrorBitmap(b);
                    }
                } else {
                    phase = "bitmap_null";
                }
            } catch (Exception e) {
                phase = "ex:" + e.getClass().getSimpleName();
                Log.w(TAG_MAP_AV_PERF, "videoMirror: " + phase, e);
            }
            long totalMs = SystemClock.elapsedRealtime() - tAll;
            if (totalMs >= 25 || getBitmapMs >= 16) {
                Log.w(TAG_MAP_AV_PERF, "videoMirror SLOW totalMs=" + totalMs + " getBitmapMs=" + getBitmapMs
                        + " bmp=" + bmpW + "x" + bmpH + " phase=" + phase
                        + " stripChildren=" + (remoteCameraStrip == null ? -1 : remoteCameraStrip.getChildCount()));
            } else if (bmpW > 0) {
                Log.d(TAG_MAP_AV_PERF, "videoMirror totalMs=" + totalMs + " getBitmapMs=" + getBitmapMs
                        + " bmp=" + bmpW + "x" + bmpH);
            }
            locationHandler.postDelayed(this, 200);
        }
    };
    private MapLibreMap mapLibreMap;
    private LineOverlayView lineOverlayView;
    private LatLng currentLatLng;
    /** 最近一次定位的椭球高（米），{@link Location#hasAltitude()} 为 false 时不展示数值。 */
    private boolean hasCurrentAltitude;
    private double currentAltitudeMeters;
    /** 最近一次采纳定位的 provider（gps/network/passive/...），用于底部坐标栏展示。 */
    @NonNull
    private String currentLocationProviderLabel = "--";
    private LatLng displayedLatLng;
    private LatLng locationSmoothFrom;
    private LatLng locationSmoothTarget;
    private long locationSmoothStartMs;
    private MapLibreMap.OnCameraMoveListener onCameraMoveListener;
    private MapLibreMap.OnCameraIdleListener onCameraIdleListener;
    /** 拖动中隐藏作战连线（线与标签），idle 后恢复。 */
    private boolean isCameraMovingForMarkLines;
    private boolean pendingMarkLinesAfterCameraIdle;
    private long lastCameraMoveRealtimeMs;
    private final Runnable debouncedMarkLinesRunnable = () -> {
        if (!isAdded() || mapLibreMap == null) return;
        ensureCameraMoveStateNotStale();
        if (isCameraMovingForMarkLines) {
            pendingMarkLinesAfterCameraIdle = true;
            return;
        }
        updateMarkConnectionLines(true);
    };
    private final Handler locationHandler = new Handler(Looper.getMainLooper());
    private Runnable locationTimeoutRunnable;
    private LocationListener pendingLocationListener;
    private boolean requestingFreshLocation;
    private boolean hasFreshLocationFix;
    /**
     * {@link SystemClock#elapsedRealtime()}：最近一次接受的 GPS（{@link LocationManager#GPS_PROVIDER}）实时回调；-1 表示尚无。
     */
    private long lastLiveGpsElapsedRealtime = -1L;
    /** 最近一次被采纳定位的系统时间戳（ms）。 */
    private long lastAcceptedLocationTimeMs = -1L;
    /** 最近一次被采纳定位的 elapsedRealtimeNanos（ns）。 */
    private long lastAcceptedElapsedRealtimeNanos = -1L;
    /** 最近一次被采纳定位估算速度（m/s），用于高速时过滤 network。 */
    private float lastAcceptedSpeedMps;
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private SensorEventListener headingListener;
    private boolean hasCenteredOnFirstFix;
    /** 当前 mapView 生命周期内 onResume 次数；用于避免每次回到地图都自动「定位全员」拉镜头。 */
    private int mapViewResumeCount;
    private final Map<Long, RemoteLoc> remoteLocs = new HashMap<>();
    private final Set<Long> syntheticRemoteIds = new HashSet<>();
    private final Map<Long, LatLng> syntheticRemoteCenters = new HashMap<>();
    private final Map<Long, Float> syntheticRemoteAnglesDeg = new HashMap<>();
    private final Map<Long, Float> syntheticRemoteRadiusM = new HashMap<>();
    private final Random syntheticRemoteRandom = new Random();
    private boolean syntheticRemoteTestRunning;
    private final Runnable syntheticRemoteStepRunnable = new Runnable() {
        @Override
        public void run() {
            if (!syntheticRemoteTestRunning || !isAdded() || mapLibreMap == null) {
                return;
            }
            long now = System.currentTimeMillis();
            for (Long id : syntheticRemoteIds) {
                if (id == null) continue;
                LatLng center = syntheticRemoteCenters.get(id);
                if (center == null) continue;
                float angle = syntheticRemoteAnglesDeg.containsKey(id) ? syntheticRemoteAnglesDeg.get(id) : 0f;
                // 每帧给一点角速度抖动，模拟真实人员在小范围内移动。
                angle += (syntheticRemoteRandom.nextFloat() - 0.5f) * 26f;
                syntheticRemoteAnglesDeg.put(id, angle);
                float radius = syntheticRemoteRadiusM.containsKey(id) ? syntheticRemoteRadiusM.get(id) : 0f;
                radius += (syntheticRemoteRandom.nextFloat() - 0.5f) * 10f;
                radius = Math.max(8f, Math.min(SYNTHETIC_REMOTE_RADIUS_M, radius));
                syntheticRemoteRadiusM.put(id, radius);
                double rad = Math.toRadians(angle);
                float eastM = (float) (Math.cos(rad) * radius);
                float northM = (float) (Math.sin(rad) * radius);
                LatLng next = offsetLatLngMeters(center, eastM, northM);
                RemoteLoc r = ensureRemoteLocEntry(id, String.format(Locale.getDefault(), "压测%02d", (int) (id - SYNTHETIC_REMOTE_ID_BASE + 1)));
                r.latLng = next;
                updateRemoteDisplayTarget(r, next);
                updateRemoteHeadingTarget(r, angle);
                r.ts = now;
            }
            updateRemoteOverlays(false);
            scheduleDebouncedMarkConnectionLines();
            locationHandler.postDelayed(this, SYNTHETIC_REMOTE_UPDATE_MS);
        }
    };
    /** 连线标签用，避免相机每帧对同一用户重复查库 */
    private final Map<Long, String> remoteUserDisplayNameCache = new HashMap<>();
    /** 活动组成员缓存：避免每条定位都查库遍历全部群成员。 */
    private final Set<Long> activeGroupMemberIdsCache = new HashSet<>();
    /** 需要显示连线的作战标注 id（默认空：同步后不显示连线，需用户点标注确认后加入）。 */
    private final Map<String, MarkLink> markLinks = new HashMap<>();
    private boolean suppressLocalMarkLinkPersistence;
    private boolean useMapLibreMarkConnectionLines = true;
    private boolean useMapLibreSelfMarkLinkLabels = true;
    private long lastMapLibreMarkLinkLabelsUpdateMs;
    private boolean pendingMapLibreMarkLinkLabelsUpdate;
    private boolean useMapLibreBattleMarks = true;
    private boolean markSyncCacheEnabled = true;
    private final Map<String, CachedMark> syncedMarkCache = new HashMap<>();
    private final Map<String, CachedLink> syncedLinkCache = new HashMap<>();
    private final Map<String, DeleteTombstone> deletedMarkCache = new HashMap<>();
    private final Map<String, DeleteTombstone> deletedLinkCache = new HashMap<>();
    private final Map<Long, Long> lastMarkSyncReqTsByGroup = new HashMap<>();
    private final Map<Long, Integer> markSyncReqAttemptByGroup = new HashMap<>();
    private final Set<Long> markSyncGroupsWithSnapshot = new HashSet<>();
    private long activeGroupMemberCacheTs;
    private long lastGroupStatsRefreshTs;
    private long selfUserId;
    private boolean activeGroupPickPromptShown;
    private float selfHeadingDeg;
    private boolean compassTrackingEnabled = true;
    private long lastLocSendMs;
    private long lastPeerReloadMs;
    private final Runnable locationSmoothRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || mapLibreMap == null || locationSmoothFrom == null || locationSmoothTarget == null) return;
            long elapsed = Math.max(0L, System.currentTimeMillis() - locationSmoothStartMs);
            float t = Math.min(1f, elapsed / (float) LOCATION_SMOOTH_DURATION_MS);
            float eased = t * t * (3f - 2f * t);
            displayedLatLng = interpolateLatLng(locationSmoothFrom, locationSmoothTarget, eased);
            renderDisplayedLocation();
            if (t < 1f) {
                locationHandler.postDelayed(this, LOCATION_SMOOTH_FRAME_MS);
            } else {
                displayedLatLng = locationSmoothTarget;
                locationSmoothFrom = null;
                locationSmoothTarget = null;
                renderDisplayedLocation();
            }
        }
    };
    private final Runnable mapLibreMarkLinkLabelsUpdateRunnable = () -> {
        pendingMapLibreMarkLinkLabelsUpdate = false;
        updateMapLibreSelfMarkLinkLabelsNow();
    };
    private final Set<Long> cachedPeers = new HashSet<>();
    private final Map<Long, Long> recentActiveOnlineTs = new HashMap<>();
    private final Map<String, BattleMark> battleMarksOnMap = new HashMap<>();
    private final Runnable remoteLocationSmoothRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded() || mapLibreMap == null) return;
            boolean needNextFrame = false;
            long now = System.currentTimeMillis();
            for (RemoteLoc r : remoteLocs.values()) {
                if (r == null) continue;
                boolean animating = false;
                if (r.smoothFrom != null && r.smoothTarget != null) {
                    long elapsed = Math.max(0L, now - r.smoothStartMs);
                    float t = Math.min(1f, elapsed / (float) LOCATION_SMOOTH_DURATION_MS);
                    float eased = t * t * (3f - 2f * t);
                    r.displayLatLng = interpolateLatLng(r.smoothFrom, r.smoothTarget, eased);
                    if (t < 1f) {
                        animating = true;
                    } else {
                        r.displayLatLng = r.smoothTarget;
                        r.smoothFrom = null;
                        r.smoothTarget = null;
                    }
                }
                if (r.smoothHeadingFrom != null && r.smoothHeadingTarget != null) {
                    long elapsed = Math.max(0L, now - r.smoothHeadingStartMs);
                    float t = Math.min(1f, elapsed / (float) LOCATION_SMOOTH_DURATION_MS);
                    float eased = t * t * (3f - 2f * t);
                    r.displayHeading = lerpAngleDeg(r.smoothHeadingFrom, r.smoothHeadingTarget, eased);
                    if (t < 1f) {
                        animating = true;
                    } else {
                        r.displayHeading = r.smoothHeadingTarget;
                        r.smoothHeadingFrom = null;
                        r.smoothHeadingTarget = null;
                    }
                }
                if (animating) {
                    needNextFrame = true;
                }
            }
            updateRemoteOverlays(false);
            scheduleDebouncedMarkConnectionLines();
            if (needNextFrame) {
                locationHandler.postDelayed(this, LOCATION_SMOOTH_FRAME_MS);
            }
        }
    };
    /**
     * 远端位置箭头尚未 layout（宽高为 0）时勿对每个箭头 {@code arrow.post(updateRemoteOverlays)}，
     * 否则与相机移动叠加后主线程消息暴增（从通话返回地图后拖动卡顿）。
     */
    private static final int MAX_REMOTE_OVERLAY_LAYOUT_RETRIES = 40;
    private int remoteOverlayLayoutRetrySeq;
    private final Runnable remoteOverlayRetryRunnable = () -> {
        if (!isAdded() || mapLibreMap == null) return;
        if (remoteOverlayLayoutRetrySeq >= MAX_REMOTE_OVERLAY_LAYOUT_RETRIES) return;
        remoteOverlayLayoutRetrySeq++;
        updateRemoteOverlays(true);
    };
    /**
     * 作战标注悬浮 View 尚未完成 layout（宽高为 0）时需要延后刷新位置。
     * 旧实现对每个 {@code mark.root.post(updateBattleMarkOverlays)} 无限递归排队，嵌入地图/弹窗后易占满主线程导致卡死。
     */
    private static final int MAX_BATTLE_OVERLAY_LAYOUT_RETRIES = 40;
    private int battleOverlayLayoutRetrySeq;
    private final Runnable battleOverlayLayoutRetryRunnable = () -> {
        if (!isAdded() || mapLibreMap == null) return;
        if (battleOverlayLayoutRetrySeq >= MAX_BATTLE_OVERLAY_LAYOUT_RETRIES) return;
        battleOverlayLayoutRetrySeq++;
        updateBattleMarkOverlays();
    };

    /** 朝向标尚未 layout 时勿 {@code ivHeading.post} 链式排队（同上）。 */
    private static final int MAX_HEADING_LAYOUT_RETRIES = 40;
    private int headingLayoutRetrySeq;
    private final Runnable headingLayoutRetryRunnable = () -> {
        if (!isAdded() || mapLibreMap == null) return;
        if (headingLayoutRetrySeq >= MAX_HEADING_LAYOUT_RETRIES) return;
        headingLayoutRetrySeq++;
        updateHeadingViewPosition();
    };

    private boolean followSelfCamera = false;
    private static final long NAV_FOLLOW_RESUME_DELAY_MS = 10_000L;
    private boolean navFollowSuspendedByGesture;
    private final Runnable resumeNavFollowRunnable = () -> {
        if (!isAdded() || mapLibreMap == null) return;
        if (!isInAppNavigationActive() || !followSelfCamera) {
            navFollowSuspendedByGesture = false;
            return;
        }
        navFollowSuspendedByGesture = false;
        LatLng target = displayedLatLng != null ? displayedLatLng : currentLatLng;
        if (target != null) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, 16.0));
        }
    };
    /** onResume 延迟全员定位；用户点「定位自己」时需取消，否则 3 秒后相机被 bounds 拉走像「无效」。 */
    private final Runnable scheduledLocateAllMembersRunnable = this::locateToAllMembers;
    private boolean waitingForMarkPlacement = false;
    private String editingBattleMarkId;
    private int selectedBattleMarkIndex = -1;
    private ImageView ivNavBtn;
    private LinearLayout layoutNavBar;
    private TextView tvNavInstruction;
    private Button btnNavStop;
    private boolean waitingForNavDestPick;
    private LatLng pendingNavDestination;
    private String pendingNavDestLabel;
    private String pendingNavCityName;
    /** 强引用高德 {@code OfflineMapManager}，避免离线下载被回收；类型用 Object 避免 Fragment 直接依赖 maps 包名。 */
    private Object amapOfflineMapMgrKeepalive;
    /** 用于作废异步 GraphHopper 算路回调（停止导航或销毁视图时递增）。 */
    private int graphHopperRouteGeneration;    private final List<LatLng> navigationRouteWgs84 = new ArrayList<>();
    private final List<NavInstructionLite> navigationInstructions = new ArrayList<>();
    private int navigationRouteTotalDistanceMeters;
    private int navigationRouteTotalDurationSeconds;
    private Runnable navHudRunnable;
    private boolean navModeDriving;
    private String navInstructionBanner;
    private String navRouteSummaryLine = "";
    private AlertDialog offlineDownloadDialog;
    private boolean navRerouteInProgress;
    private int navDeviationHitCount;
    private long navLastRerouteTs;

    /** 与天地图/本地 XYZ 底图类型对应，用于多选；叠放顺序固定为 离线矢量→离线影像→影像→矢量→离线注记→在线注记（底→顶） */
    private enum MapLayerType {
        VECTOR,
        IMAGERY,
        ANNOTATION,
        OFFLINE_VECTOR,
        OFFLINE_IMAGERY,
        OFFLINE_ANNOTATION
    }

    private ImageView ivLayerBtn;
    private TextView tvLayerOfflineDataAdd;
    private View layoutLayerBarRoot;
    @Nullable
    private MapOverlayPortraitSnapshot mapOverlayPortraitSnapshot;
    @Nullable
    private FrameLayout.LayoutParams layerBtnPortraitFrameLp;
    private boolean layerBtnAttachedToRightTools;
    private CardView layoutLayerPanel;
    private CheckBox cbLayerVector;
    private CheckBox cbLayerImagery;
    private CheckBox cbLayerAnnotation;
    private CheckBox cbLayerOffline;
    private CheckBox cbLayerOfflineImagery;
    private CheckBox cbLayerOfflineAnnotation;
    private AlertDialog offlineDataProgressDialog;
    private ProgressBar offlineDataProgressBar;
    private TextView offlineDataProgressText;
    private final Object offlineDataProgressLock = new Object();
    private boolean offlineDataProgressPostPending;
    private String offlineDataProgressPendingMessage;
    private int offlineDataProgressPendingValue;
    private MbtilesTileServer vectorMbtilesServer;
    private MbtilesTileServer imageryMbtilesServer;
    private MbtilesTileServer annotationMbtilesServer;
    private boolean mapLayerPanelVisible;
    /** 程序化勾选图层时避免触发 {@link #onBasemapLayerCheckboxChanged} 递归 */
    private boolean suppressBasemapLayerCallback;
    /** 天地图在线可用时，矢量/影像/注记才可用天地图源；否则矢量退 OSM，影像/注记不可用 */
    private boolean tiandituOnlineOk;

    /** 在线 GraphHopper 指令的轻量映射（避免依赖 mapnav 包内构造器可见性）。 */
    private static final class NavInstructionLite {
        final int fromIndex;
        final int toIndex;
        final int sign;
        @NonNull
        final String text;
        final int distanceMeters;
        final int durationSeconds;

        NavInstructionLite(int fromIndex, int toIndex, int sign, @NonNull String text, int distanceMeters, int durationSeconds) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.sign = sign;
            this.text = text;
            this.distanceMeters = distanceMeters;
            this.durationSeconds = durationSeconds;
        }
    }

    /** 作战标注类型数；图标为 drawable 下 {@code a1}、{@code a1b}、{@code a2}、{@code a2b} … {@code a22}、{@code a22b} */
    private static final int BATTLE_MARK_TYPE_COUNT = 44;
    private static final String[] BATTLE_MARK_DRAWABLE_NAMES = buildBattleMarkDrawableNames();
    private final String[] battleMarks = buildBattleMarkLabels();

    private static String[] buildBattleMarkDrawableNames() {
        String[] names = new String[BATTLE_MARK_TYPE_COUNT];
        for (int i = 0; i < 22; i++) {
            int n = i + 1;
            names[i * 2] = "a" + n;
            names[i * 2 + 1] = "a" + n + "b";
        }
        return names;
    }

    private static String[] buildBattleMarkLabels() {
        String[] a = new String[BATTLE_MARK_TYPE_COUNT];
        for (int i = 0; i < BATTLE_MARK_TYPE_COUNT; i++) {
            a[i] = "作战标注-" + BATTLE_MARK_DRAWABLE_NAMES[i];
        }
        return a;
    }
    private final Runnable onlineCleanupRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded()) return;
            long t0 = SystemClock.elapsedRealtime();
            pruneExpiredOnlineMembersAndRefreshStats();
            long dt = SystemClock.elapsedRealtime() - t0;
            if (dt >= 10) {
                Log.w(TAG_MAP_AV_PERF, "onlineCleanup pruneExpiredOnlineMembersAndRefreshStats ms=" + dt);
            }
            locationHandler.postDelayed(this, 2000L);
        }
    };
    private final Runnable activeGroupMeetingStatusRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isAdded()) return;
            refreshActiveGroupMeetingStatus();
            locationHandler.postDelayed(this, ACTIVE_GROUP_MEETING_REFRESH_MS);
        }
    };
    private final qyDefs.PF_commonHandler tmpHandler_collectGrpMemberIds = (Object p0, Object p1, Object p2) -> {
        long grpIdInfo = (long) p0;
        @SuppressWarnings("unchecked")
        Set<Long> out = (Set<Long>) p1;
        qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;
        if (pQMem != null && pQMem.idInfo_grp.ui64Id == grpIdInfo) {
            out.add(pQMem.idInfo_mem.ui64Id);
        }
        return 0;
    };

    private static class RemoteLoc {
        long userId;
        LatLng latLng;
        LatLng displayLatLng;
        LatLng smoothFrom;
        LatLng smoothTarget;
        long smoothStartMs;
        float heading;
        /** 当前显示在箭头上的朝向（平滑插值结果）。 */
        float displayHeading;
        boolean headingInitialized;
        Float smoothHeadingFrom;
        Float smoothHeadingTarget;
        long smoothHeadingStartMs;
        long ts;
        ImageView arrow;
        TextView name;
        /** 与 {@link #name} 当前文案一致时跳过 {@link TextView#setText}，减轻相机移动时主线程负担。 */
        @Nullable String cachedNameLabel;
    }

    private static class BattleMark {
        String id;
        long ownerId;
        int typeIndex;
        LatLng latLng;
        long ts;
        float rotationDeg;
        FrameLayout root;
        /** 编辑控件容器：上确定、左旋转、中图钉、右删除 */
        FrameLayout editHub;
        ImageView pin;
        TextView ownerName;
        View rotateHandle;
        LinearLayout actions;
        TextView btnOk;
        TextView btnDel;
        boolean pending;
        /** 正在拖动旋转钮时为 true，用于父布局拦截后续 MOVE/UP。 */
        boolean rotationTouchActive;
    }

    /** 作战标注悬浮根布局：在子 View 之上拦截旋转钮区域的触摸，避免被图钉/按钮抢走事件。 */
    private final class BattleMarkRootLayout extends FrameLayout {
        @NonNull
        private final BattleMark mark;

        BattleMarkRootLayout(@NonNull Context context, @NonNull BattleMark mark) {
            super(context);
            this.mark = mark;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!isBattleMarkRotationEditing(mark)) {
                mark.rotationTouchActive = false;
                return super.onInterceptTouchEvent(ev);
            }
            // 确定/删除必须先于父布局拦截，否则 onIntercept 返回 true 时子按钮收不到 DOWN。
            if (isTouchOnBattleMarkActionButton(mark, ev)) {
                mark.rotationTouchActive = false;
                return false;
            }
            int action = ev.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                if (isTouchInRotateHandle(mark, ev)) {
                    mark.rotationTouchActive = true;
                    requestDisallowInterceptTouchEventForBattleMark(mark);
                    return true;
                }
                if (isTouchOnBattleMarkPin(mark, ev)) {
                    mark.rotationTouchActive = true;
                    requestDisallowInterceptTouchEventForBattleMark(mark);
                    return true;
                }
                mark.rotationTouchActive = false;
            } else if (mark.rotationTouchActive) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (mark.rotationTouchActive) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP
                        || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    mark.rotationTouchActive = false;
                }
                return onBattleMarkRotationTouch(mark, event);
            }
            return super.onTouchEvent(event);
        }
    }

    private static class MarkLink {
        String markId;
        long userId;
        long ts;
        boolean synced;
    }

    private static class CachedMark {
        String id;
        long ownerId;
        int typeIndex;
        double lat;
        double lng;
        long ts;
        float rotationDeg;
    }

    private static class CachedLink {
        String markId;
        long userId;
        long ts;
    }

    private static class DeleteTombstone {
        long ts;
    }

    private static final class NavRouteProgress {
        final int segmentIndex;
        final LatLng point;

        NavRouteProgress(int segmentIndex, @NonNull LatLng point) {
            this.segmentIndex = segmentIndex;
            this.point = point;
        }
    }

    private static class LineItem {
        PointF from;
        PointF to;
        String label;
        int color;
    }

    private static class LineOverlayView extends View {
        private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navRouteCasingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navRouteTraveledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navRoutePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navRouteHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navRouteArrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navEndShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navEndOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navEndPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint navEndTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final List<LineItem> lines = new ArrayList<>();
        private MapLibreMap navRouteMapRef;
        private final List<LatLng> navRoutePoints = new ArrayList<>();
        private int navRouteProgressSegmentIndex = -1;
        private LatLng navRouteProgressPoint;

        LineOverlayView(Context context) {
            super(context);
            linePaint.setColor(Color.parseColor("#E53935"));
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(3f);
            PathEffect effect = new DashPathEffect(new float[]{12f, 8f}, 0f);
            linePaint.setPathEffect(effect);
            textBgPaint.setColor(Color.parseColor("#88000000"));
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(26f);
            navRouteCasingPaint.setColor(Color.WHITE);
            navRouteCasingPaint.setStyle(Paint.Style.STROKE);
            navRouteCasingPaint.setStrokeWidth(26f);
            navRouteCasingPaint.setStrokeJoin(Paint.Join.ROUND);
            navRouteCasingPaint.setStrokeCap(Paint.Cap.ROUND);
            navRouteTraveledPaint.setColor(Color.parseColor("#A9B2B8"));
            navRouteTraveledPaint.setStyle(Paint.Style.STROKE);
            navRouteTraveledPaint.setStrokeWidth(20f);
            navRouteTraveledPaint.setStrokeJoin(Paint.Join.ROUND);
            navRouteTraveledPaint.setStrokeCap(Paint.Cap.ROUND);
            navRoutePaint.setColor(Color.parseColor("#00B35A"));
            navRoutePaint.setStyle(Paint.Style.STROKE);
            navRoutePaint.setStrokeWidth(20f);
            navRoutePaint.setStrokeJoin(Paint.Join.ROUND);
            navRoutePaint.setStrokeCap(Paint.Cap.ROUND);
            navRouteHighlightPaint.setColor(Color.parseColor("#23D36B"));
            navRouteHighlightPaint.setStyle(Paint.Style.STROKE);
            navRouteHighlightPaint.setStrokeWidth(8f);
            navRouteHighlightPaint.setStrokeJoin(Paint.Join.ROUND);
            navRouteHighlightPaint.setStrokeCap(Paint.Cap.ROUND);
            navRouteArrowPaint.setColor(Color.WHITE);
            navRouteArrowPaint.setStyle(Paint.Style.STROKE);
            navRouteArrowPaint.setStrokeWidth(4f);
            navRouteArrowPaint.setStrokeCap(Paint.Cap.ROUND);
            navRouteArrowPaint.setStrokeJoin(Paint.Join.ROUND);
            navEndShadowPaint.setColor(Color.parseColor("#33000000"));
            navEndShadowPaint.setStyle(Paint.Style.FILL);
            navEndOuterPaint.setColor(Color.WHITE);
            navEndOuterPaint.setStyle(Paint.Style.FILL);
            navEndPaint.setColor(Color.parseColor("#F23B45"));
            navEndPaint.setStyle(Paint.Style.FILL);
            navEndTextPaint.setColor(Color.WHITE);
            navEndTextPaint.setTextAlign(Paint.Align.CENTER);
            navEndTextPaint.setFakeBoldText(true);
        }

        void setLines(List<LineItem> items) {
            lines.clear();
            if (items != null) lines.addAll(items);
            invalidate();
        }

        void setNavigationRoute(MapLibreMap map, List<LatLng> wgs84Points) {
            navRouteMapRef = map;
            navRoutePoints.clear();
            if (wgs84Points != null) navRoutePoints.addAll(wgs84Points);
            navRouteProgressSegmentIndex = -1;
            navRouteProgressPoint = null;
            invalidate();
        }

        void setNavigationProgress(int segmentIndex, @Nullable LatLng progressPoint) {
            if (progressPoint == null || segmentIndex < 0 || segmentIndex >= navRoutePoints.size() - 1) {
                navRouteProgressSegmentIndex = -1;
                navRouteProgressPoint = null;
            } else {
                navRouteProgressSegmentIndex = segmentIndex;
                navRouteProgressPoint = progressPoint;
            }
            invalidate();
        }

        void clearNavigationRoute() {
            navRouteMapRef = null;
            navRoutePoints.clear();
            navRouteProgressSegmentIndex = -1;
            navRouteProgressPoint = null;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (navRouteMapRef != null && navRoutePoints.size() >= 2) {
                Path path = new Path();
                List<PointF> screenPoints = new ArrayList<>(navRoutePoints.size());
                boolean first = true;
                for (LatLng ll : navRoutePoints) {
                    PointF pt = navRouteMapRef.getProjection().toScreenLocation(ll);
                    if (pt == null) continue;
                    screenPoints.add(pt);
                    if (first) {
                        path.moveTo(pt.x, pt.y);
                        first = false;
                    } else {
                        path.lineTo(pt.x, pt.y);
                    }
                }
                if (!first) {
                    canvas.drawPath(path, navRouteCasingPaint);
                    List<PointF> remainingScreenPoints = screenPoints;
                    if (navRouteProgressPoint != null
                            && navRouteProgressSegmentIndex >= 0
                            && navRouteProgressSegmentIndex < navRoutePoints.size() - 1) {
                        PointF progressScreen = navRouteMapRef.getProjection().toScreenLocation(navRouteProgressPoint);
                        if (progressScreen != null) {
                            Path traveledPath = new Path();
                            traveledPath.moveTo(screenPoints.get(0).x, screenPoints.get(0).y);
                            for (int i = 1; i <= navRouteProgressSegmentIndex && i < screenPoints.size(); i++) {
                                PointF pt = screenPoints.get(i);
                                traveledPath.lineTo(pt.x, pt.y);
                            }
                            traveledPath.lineTo(progressScreen.x, progressScreen.y);
                            canvas.drawPath(traveledPath, navRouteTraveledPaint);

                            Path remainingPath = new Path();
                            remainingPath.moveTo(progressScreen.x, progressScreen.y);
                            remainingScreenPoints = new ArrayList<>();
                            remainingScreenPoints.add(progressScreen);
                            for (int i = navRouteProgressSegmentIndex + 1; i < screenPoints.size(); i++) {
                                PointF pt = screenPoints.get(i);
                                remainingPath.lineTo(pt.x, pt.y);
                                remainingScreenPoints.add(pt);
                            }
                            canvas.drawPath(remainingPath, navRoutePaint);
                            canvas.drawPath(remainingPath, navRouteHighlightPaint);
                        } else {
                            canvas.drawPath(path, navRoutePaint);
                            canvas.drawPath(path, navRouteHighlightPaint);
                        }
                    } else {
                        canvas.drawPath(path, navRoutePaint);
                        canvas.drawPath(path, navRouteHighlightPaint);
                    }
                    drawRouteDirectionArrows(canvas, remainingScreenPoints);
                    drawRouteEndMarker(canvas, screenPoints.get(screenPoints.size() - 1));
                }
            }
            for (LineItem item : lines) {
                if (item == null || item.from == null || item.to == null) continue;
                linePaint.setColor(item.color != 0 ? item.color : Color.parseColor("#E53935"));
                canvas.drawLine(item.from.x, item.from.y, item.to.x, item.to.y, linePaint);
                if (TextUtils.isEmpty(item.label)) continue;
                float mx = (item.from.x + item.to.x) / 2f;
                float my = (item.from.y + item.to.y) / 2f;
                float angle = (float) Math.toDegrees(Math.atan2(item.to.y - item.from.y, item.to.x - item.from.x));
                // 保持文本大致正向，避免倒着看
                if (angle > 90f) angle -= 180f;
                if (angle < -90f) angle += 180f;
                float tw = textPaint.measureText(item.label);
                float pad = 10f;
                canvas.save();
                canvas.rotate(angle, mx, my);
                canvas.drawRoundRect(mx - tw / 2f - pad, my - 30f, mx + tw / 2f + pad, my + 12f, 8f, 8f, textBgPaint);
                canvas.drawText(item.label, mx - tw / 2f, my, textPaint);
                canvas.restore();
            }
        }

        private void drawRouteDirectionArrows(@NonNull Canvas canvas, @NonNull List<PointF> pts) {
            if (pts.size() < 2) return;
            final float density = getResources().getDisplayMetrics().density;
            navRouteArrowPaint.setStrokeWidth(Math.max(2.4f, density * 2.4f));
            final float spacing = Math.max(74f, density * 74f);
            final float arrowLength = Math.max(10f, density * 10f);
            final float arrowWidth = Math.max(4.4f, density * 4.4f);
            float nextAt = spacing;
            float walked = 0f;
            for (int i = 0; i < pts.size() - 1; i++) {
                PointF a = pts.get(i);
                PointF b = pts.get(i + 1);
                float dx = b.x - a.x;
                float dy = b.y - a.y;
                float len = (float) Math.hypot(dx, dy);
                if (len < 1f) continue;
                while (walked + len >= nextAt) {
                    float t = (nextAt - walked) / len;
                    float x = a.x + dx * t;
                    float y = a.y + dy * t;
                    drawRouteArrow(canvas, x, y, dx / len, dy / len, arrowLength, arrowWidth);
                    nextAt += spacing;
                }
                walked += len;
            }
        }

        private void drawRouteArrow(@NonNull Canvas canvas,
                                    float x,
                                    float y,
                                    float ux,
                                    float uy,
                                    float length,
                                    float width) {
            float bx = x - ux * length;
            float by = y - uy * length;
            float px = -uy;
            float py = ux;
            Path arrow = new Path();
            arrow.moveTo(bx + px * width, by + py * width);
            arrow.lineTo(x, y);
            arrow.lineTo(bx - px * width, by - py * width);
            canvas.drawPath(arrow, navRouteArrowPaint);
        }

        private void drawRouteEndMarker(@NonNull Canvas canvas, @NonNull PointF end) {
            float density = getResources().getDisplayMetrics().density;
            float outerRadius = Math.max(19f, density * 19f);
            float innerRadius = Math.max(15f, density * 15f);
            float centerY = end.y - outerRadius - Math.max(4f, density * 4f);
            Path pointer = new Path();
            pointer.moveTo(end.x, end.y + Math.max(1f, density));
            pointer.lineTo(end.x - density * 6f, centerY + outerRadius * 0.68f);
            pointer.lineTo(end.x + density * 6f, centerY + outerRadius * 0.68f);
            pointer.close();
            canvas.drawCircle(end.x + density * 1.5f, centerY + density * 2f, outerRadius, navEndShadowPaint);
            canvas.drawPath(pointer, navEndOuterPaint);
            canvas.drawCircle(end.x, centerY, outerRadius, navEndOuterPaint);
            canvas.drawCircle(end.x, centerY, innerRadius, navEndPaint);
            navEndTextPaint.setTextSize(Math.max(17f, density * 17f));
            Paint.FontMetrics fm = navEndTextPaint.getFontMetrics();
            float baseline = centerY - (fm.ascent + fm.descent) / 2f;
            canvas.drawText("终", end.x, baseline, navEndTextPaint);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registerMarkLineFilterInstance();
        showOnlySelfMarkLines = getSharedShowOnlySelfMarkLines();
        if (getContext() != null) {
            MapLibre.getInstance(getContext());
        }
        View view = inflater.inflate(R.layout.layout_fg_map, container, false);
        View vHide;
        if ((vHide = view.findViewById(R.id.layoutFloatingVideo)) != null) vHide.setVisibility(View.GONE);
        if ((vHide = view.findViewById(R.id.remoteCameraStripScroll)) != null) vHide.setVisibility(View.GONE);
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            ViewParent parent = mapView.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) parent;
                int idx = vg.indexOfChild(mapView);
                ViewGroup.LayoutParams lp = mapView.getLayoutParams();
                vg.removeView(mapView);
                MapView textureMapView = new MapView(
                        requireContext(),
                        new MapLibreMapOptions().textureMode(true)
                );
                textureMapView.setId(R.id.mapView);
                if (lp != null) {
                    vg.addView(textureMapView, idx, lp);
                } else {
                    vg.addView(textureMapView, idx);
                }
                mapView = textureMapView;
            }
        }
        layoutGroupStatsBar = view.findViewById(R.id.layoutGroupStatsBar);
        ivCompass = view.findViewById(R.id.ivCompass);
        ivLocateBtn = view.findViewById(R.id.ivLocateBtn);
        layoutRightToolsRoot = view.findViewById(R.id.layoutRightTools);
        ivNavBtn = view.findViewById(R.id.ivNavBtn);
        layoutNavBar = view.findViewById(R.id.layoutNavBar);
        tvNavInstruction = view.findViewById(R.id.tvNavInstruction);
        btnNavStop = view.findViewById(R.id.btnNavStop);
        ivCompassBtn = view.findViewById(R.id.ivCompassBtn);
        ivZoomInBtn = view.findViewById(R.id.ivZoomInBtn);
        ivZoomOutBtn = view.findViewById(R.id.ivZoomOutBtn);
        ivMarkBtn = view.findViewById(R.id.ivMarkBtn);
        ivMarkLineFilterBtn = view.findViewById(R.id.ivMarkLineFilterBtn);
        ivMarkNoteListBtn = view.findViewById(R.id.ivMarkNoteListBtn);
        ivHeading = view.findViewById(R.id.ivHeading);
        ivCompassNeedleLarge = view.findViewById(R.id.ivCompassNeedleLarge);
        layoutCompassPanel = view.findViewById(R.id.layoutCompassPanel);
        compassTopRotatingLayer = view.findViewById(R.id.compassTopRotatingLayer);
        layoutCompassRotatingLayer = view.findViewById(R.id.layoutCompassRotatingLayer);
        layoutFloatingVideoLegacy = view.findViewById(R.id.layoutFloatingVideo);
        tvGroupStats = view.findViewById(R.id.tvGroupStats);
        viewActiveGrpMeetingLamp = view.findViewById(R.id.viewActiveGrpMeetingLamp);
        tvActiveGrpMeetingEntry = view.findViewById(R.id.tvActiveGrpMeetingEntry);
        tvCurrentCoord = view.findViewById(R.id.tvCurrentCoord);
        layoutFloatingFragmentHost = view.findViewById(R.id.layoutFloatingFragmentHost);
        // 停用页面内旧悬浮窗，统一改走系统级悬浮窗。
        if (layoutFloatingVideoLegacy != null) {
            layoutFloatingVideoLegacy.setVisibility(View.GONE);
        }
        floatingVideoPanel = null;
        remoteCameraStripScroll = view.findViewById(R.id.remoteCameraStripScroll);
        remoteCameraStrip = view.findViewById(R.id.remoteCameraStrip);
        if (tvGroupStats != null) {
            tvGroupStats.setClickable(true);
            tvGroupStats.setOnClickListener(v -> showActiveGroupMembersDialog());
            tvGroupStats.setOnLongClickListener(v -> {
                showActiveGroupPickDialog(false);
                return true;
            });
        }
        if (isMapEmbeddedInAvPage()) {
            hideActiveGroupMeetingEntryInAvPage();
        } else {
            if (tvActiveGrpMeetingEntry != null) {
                tvActiveGrpMeetingEntry.setClickable(true);
                tvActiveGrpMeetingEntry.setOnClickListener(v -> enterActiveGroupMeetingIfAny());
            }
            if (viewActiveGrpMeetingLamp != null) {
                viewActiveGrpMeetingLamp.setOnClickListener(v -> enterActiveGroupMeetingIfAny());
            }
        }
        setupLocatePanel(view);
        if (ivNavBtn != null) {
            ivNavBtn.setOnClickListener(v -> onNavigationButtonClicked());
        }
        setupMapLayerBar(view);
        if (btnNavStop != null) {
            btnNavStop.setOnClickListener(v -> stopInAppNavigation());
        }
        if (ivMarkBtn != null) {
            ivMarkBtn.setOnClickListener(v -> showBattleMarkPicker());
        }
        if (ivMarkLineFilterBtn != null) {
            ivMarkLineFilterBtn.setOnClickListener(v -> {
                setSharedShowOnlySelfMarkLines(!showOnlySelfMarkLines);
            });
            updateMarkLineFilterButtonUi();
        }
        if (ivMarkNoteListBtn != null) {
            ivMarkNoteListBtn.setOnClickListener(v -> showSyncedMarkNotesDialog());
            ivMarkNoteListBtn.setOnLongClickListener(v -> {
                showManualMarkSyncConfirmDialog();
                return true;
            });
        }
        if (ivCompassBtn != null) {
            // 红框针状按钮改为横竖屏切换。
            ivCompassBtn.setOnClickListener(v -> toggleHostOrientation());
        }
        if (ivCompass != null) {
            // 顶部罗盘承接原红框按钮功能。
            ivCompass.setOnClickListener(v -> toggleCompassPanel());
        }
        if (ivZoomInBtn != null) {
            ivZoomInBtn.setOnClickListener(v -> zoomMapBy(1.0));
        }
        if (ivZoomOutBtn != null) {
            ivZoomOutBtn.setOnClickListener(v -> zoomMapBy(-1.0));
        }
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        mapView.setOnTouchListener((v, event) -> {
            ViewParent parent = v.getParent();
            if (parent == null) return false;
            int action = event.getActionMasked();
            handleNavigationMapTouch(action);
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                parent.requestDisallowInterceptTouchEvent(true);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                parent.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
        mapViewResumeCount = 0;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            mapLibreMap = map;
            mapLibreMap.setMaxZoomPreference(MAP_MAX_ZOOM);
            onCameraMoveListener = () -> {
                isCameraMovingForMarkLines = true;
                pendingMarkLinesAfterCameraIdle = true;
                lastCameraMoveRealtimeMs = SystemClock.elapsedRealtime();
                updateHeadingViewPosition();
                updateRemoteOverlays(false);
                repositionBattleMarkRootsOnly();
                if (!useMapLibreMarkConnectionLines) {
                    clearMarkConnectionLines();
                }
                if (lineOverlayView != null && !navigationRouteWgs84.isEmpty()) {
                    lineOverlayView.invalidate();
                }
            };
            mapLibreMap.addOnCameraMoveListener(onCameraMoveListener);
            onCameraIdleListener = () -> {
                isCameraMovingForMarkLines = false;
                if (pendingMarkLinesAfterCameraIdle) {
                    pendingMarkLinesAfterCameraIdle = false;
                    flushMarkConnectionLinesNow();
                }
            };
            mapLibreMap.addOnCameraIdleListener(onCameraIdleListener);
            ensureLineOverlayAttached();
            mapLibreMap.addOnMapClickListener(latLng -> {
                dismissMapToolPanelsOnMapTap();
                if (moveEditingBattleMarkIfNeeded(latLng)) return true;
                BattleMark hitMark = findMapLibreBattleMarkAt(latLng);
                if (hitMark != null) {
                    onBattleMarkPinClicked(hitMark);
                    return true;
                }
                if (handleNavigationDestinationTap(latLng)) return true;
                if (!waitingForMarkPlacement) return false;
                if (selectedBattleMarkIndex < 0) return false;
                waitingForMarkPlacement = false;
                BattleMark mark = placeBattleMark(latLng, selectedBattleMarkIndex, true, true);
                if (mark != null) {
                    editingBattleMarkId = mark.id;
                    updateBattleMarkEditChrome(mark);
                    Toast.makeText(requireContext(),
                            "再次点击地图可调整位置；拖动绿色圆钮或图钉可旋转",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            });
            mapLibreMap.addOnMapLongClickListener(latLng -> {
                BattleMark hitMark = findMapLibreBattleMarkAt(latLng);
                if (hitMark == null) return false;
                ensureSelfUserId();
                if (hitMark.ownerId > 0 && hitMark.ownerId != selfUserId) {
                    Toast.makeText(requireContext(), "仅创建该作战标注的人员可删除", Toast.LENGTH_SHORT).show();
                    return true;
                }
                showDeleteMarkConfirmDialog(hitMark);
                return true;
            });
            chooseStyleAfterProbe();
            mapView.post(this::refreshPendingRemoteOverlays);
        });
        updateGroupStatsView(0, 0);
        updateActiveGroupMeetingEntry(false, 0);
        view.post(() -> {
            captureMapOverlayPortraitSnapshotIfNeeded(view);
            applyMapOverlayOrientationUi();
            refreshMapOverlayTextScale();
        });
        return view;
    }

    public static Fragment_map newEmbeddedChildForTest() {
        Fragment_map fg = new Fragment_map();
        Bundle b = new Bundle();
        b.putBoolean(ARG_EMBEDDED_CHILD, true);
        fg.setArguments(b);
        return fg;
    }

    private boolean isEmbeddedChildMap() {
        Bundle args = getArguments();
        return args != null && args.getBoolean(ARG_EMBEDDED_CHILD, false);
    }

    private boolean isMapEmbeddedInAvPage() {
        return isEmbeddedChildMap() || getActivity() instanceof Activity_dlgTalk_av;
    }

    private void hideActiveGroupMeetingEntryInAvPage() {
        if (viewActiveGrpMeetingLamp != null) {
            viewActiveGrpMeetingLamp.clearAnimation();
            viewActiveGrpMeetingLamp.setVisibility(View.GONE);
            viewActiveGrpMeetingLamp.setOnClickListener(null);
        }
        if (tvActiveGrpMeetingEntry != null) {
            tvActiveGrpMeetingEntry.setVisibility(View.GONE);
            tvActiveGrpMeetingEntry.setOnClickListener(null);
        }
    }

    /**
     * Tianditu imagery + annotation layers, cycling t0–t7 in the tiles array.
     */
    private static String buildTiandituStyleJson(String tkRaw) {
        String tk = urlEncodeTk(tkRaw);
        String tilesImg = buildSubdomainTiles(
                "img_w",
                "img",
                tk);
        String tilesCia = buildSubdomainTiles(
                "cia_w",
                "cia",
                tk);
        return "{"
                + "\"version\":8,"
                + "\"sources\":{"
                + "\"tdtImg\":{\"type\":\"raster\"," + tilesImg + ",\"tileSize\":256},"
                + "\"tdtCia\":{\"type\":\"raster\"," + tilesCia + ",\"tileSize\":256}"
                + "},"
                + "\"layers\":["
                + "{\"id\":\"tdt-img-layer\",\"type\":\"raster\",\"source\":\"tdtImg\"},"
                + "{\"id\":\"tdt-cia-layer\",\"type\":\"raster\",\"source\":\"tdtCia\"}"
                + "]"
                + "}";
    }

    private static String buildSubdomainTiles(String pathPart, String layer, String tkEncoded) {
        StringBuilder sb = new StringBuilder("\"tiles\":[");
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(',');
            sb.append('"');
            sb.append("http://t").append(i).append(".tianditu.gov.cn/").append(pathPart).append("/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=").append(layer).append("&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX={z}&TILEROW={y}&TILECOL={x}&tk=");
            sb.append(tkEncoded);
            sb.append('"');
        }
        sb.append(']');
        return sb.toString();
    }

    private static String urlEncodeTk(String tk) {
        try {
            return URLEncoder.encode(tk, StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (Exception e) {
            return tk;
        }
    }

    /**
     * MapLibre 本地 Raster：{@code file:///绝对路径/{z}/{x}/{y}.png}。
     */
    private static String buildLocalXyzStyleJson(@NonNull String absoluteDir) {
        String base = absoluteDir.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String tileUrl = "file://" + base + "/{z}/{x}/{y}.png";
        return "{"
                + "\"version\":8,"
                + "\"sources\":{"
                + "\"local-xyz\":{\"type\":\"raster\",\"tiles\":[\""
                + tileUrl
                + "\"],\"tileSize\":256,\"minzoom\":"
                + LOCAL_XYZ_MIN_ZOOM
                + ",\"maxzoom\":"
                + LOCAL_XYZ_MAX_ZOOM
                + "}},"
                + "\"layers\":[{\"id\":\"local-xyz-layer\",\"type\":\"raster\",\"source\":\"local-xyz\"}]"
                + "}";
    }

    /**
     * @return HTTP status, or -1 on I/O error.
     */
    private static int probeTiandituTile(String tkRaw) {
        if (tkRaw == null || tkRaw.length() < 8) {
            return HttpURLConnection.HTTP_FORBIDDEN;
        }
        String tk = urlEncodeTk(tkRaw);
        int sub = (int) (System.currentTimeMillis() % 8);
        String urlStr = "http://t" + sub + ".tianditu.gov.cn/vec_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILEMATRIX=10&TILEROW=387&TILECOL=843&tk=" + tk;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "QingyangComm/1.0");
            int code = conn.getResponseCode();
            InputStream in = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            if (in != null) {
                try {
                    byte[] buf = new byte[2048];
                    while (in.read(buf) != -1) { /* drain */ }
                } finally {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return code;
        } catch (IOException e) {
            return -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void chooseStyleAfterProbe() {
        if (mapLibreMap == null || !isAdded()) return;

        if (TIANDITU_TOKEN == null || TIANDITU_TOKEN.length() < 10) {
            tiandituOnlineOk = false;
            locationHandler.post(this::loadBasemapPrefsAndApplyStyle);
            return;
        }

        new Thread(() -> {
            int code = probeTiandituTile(TIANDITU_TOKEN);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (!isAdded() || mapLibreMap == null) return;
                boolean keyBad =
                        code == HttpURLConnection.HTTP_FORBIDDEN
                                || code == HttpURLConnection.HTTP_UNAUTHORIZED;
                if (keyBad) {
                    Toast.makeText(requireContext(), MSG_TDT_KEY_INVALID, Toast.LENGTH_LONG).show();
                }
                tiandituOnlineOk = !keyBad;
                updateOnlineLayerAvailability();
                loadBasemapPrefsAndApplyStyle();
            });
        }, "tianditu-probe").start();
    }

    private void setupMapLayerBar(@NonNull View root) {
        ivLayerBtn = root.findViewById(R.id.ivLayerBtn);
        tvLayerOfflineDataAdd = root.findViewById(R.id.tvLayerOfflineDataAdd);
        layoutLayerBarRoot = root.findViewById(R.id.layoutLayerBar);
        layoutLayerPanel = root.findViewById(R.id.layoutLayerPanel);
        cbLayerVector = root.findViewById(R.id.cbLayerVector);
        cbLayerImagery = root.findViewById(R.id.cbLayerImagery);
        cbLayerAnnotation = root.findViewById(R.id.cbLayerAnnotation);
        cbLayerOffline = root.findViewById(R.id.cbLayerOffline);
        cbLayerOfflineImagery = root.findViewById(R.id.cbLayerOfflineImagery);
        cbLayerOfflineAnnotation = root.findViewById(R.id.cbLayerOfflineAnnotation);
        if (ivLayerBtn == null || layoutLayerPanel == null) {
            return;
        }
        ivLayerBtn.setOnClickListener(v -> {
            hideLocateMenuPanel();
            mapLayerPanelVisible = !mapLayerPanelVisible;
            layoutLayerPanel.setVisibility(mapLayerPanelVisible ? View.VISIBLE : View.GONE);
            if (mapLayerPanelVisible) {
                if (isMapLandscape()) {
                    updateLandscapeLayerPanelLayout(true);
                } else if (isMapSplitOverlayHost()) {
                    updateSplitLayerBarHostVisibility(true);
                }
                bringLayerBarToFront();
                layoutLayerPanel.post(() -> {
                    if (isMapLandscape() && getView() != null) {
                        applyLandscapeLayerPanelMaxHeight(getView());
                    }
                    bringLayerBarToFront();
                });
            } else {
                if (isMapLandscape()) {
                    updateLandscapeLayerPanelLayout(false);
                } else if (isMapSplitOverlayHost()) {
                    updateSplitLayerBarHostVisibility(false);
                }
                resetLayerBarStackOrder();
            }
        });
        if (tvLayerOfflineDataAdd != null) {
            tvLayerOfflineDataAdd.setOnClickListener(v -> handleOfflineTileDataAddClick());
        }
        CompoundButton.OnCheckedChangeListener listener =
                (buttonView, isChecked) -> {
                    if (suppressBasemapLayerCallback) {
                        return;
                    }
                    applyVectorImageryMutualExclusive(buttonView, isChecked);
                    applyAnnotationMutualExclusive(buttonView, isChecked);
                    onBasemapLayerCheckboxChanged();
                };
        if (cbLayerVector != null) {
            cbLayerVector.setOnCheckedChangeListener(listener);
        }
        if (cbLayerImagery != null) {
            cbLayerImagery.setOnCheckedChangeListener(listener);
        }
        if (cbLayerAnnotation != null) {
            cbLayerAnnotation.setOnCheckedChangeListener(listener);
        }
        if (cbLayerOffline != null) {
            cbLayerOffline.setOnCheckedChangeListener(listener);
        }
        if (cbLayerOfflineImagery != null) {
            cbLayerOfflineImagery.setOnCheckedChangeListener(listener);
        }
        if (cbLayerOfflineAnnotation != null) {
            cbLayerOfflineAnnotation.setOnCheckedChangeListener(listener);
        }
        refreshOfflineCheckboxAvailability();
    }

    private void handleOfflineTileDataAddClick() {
        if (!isAdded() || getContext() == null) {
            return;
        }
        File tileRoot = getLocalOfflineTileRootDir();
        File tileZip = getLocalOfflineTileZipFile();
        boolean hasTileRoot = tileRoot.isDirectory();
        boolean hasTileZip = tileZip.isFile();
        if (!hasTileRoot) {
            if (!hasTileZip) {
                Toast.makeText(requireContext(), "请上传离线数据", Toast.LENGTH_SHORT).show();
                return;
            }
            showOfflineDataConfirmDialog("存在离线数据是否解压？", () -> startOfflineTileDataImport(false));
            return;
        }
        if (!hasTileZip) {
            Toast.makeText(requireContext(), "请上传离线数据", Toast.LENGTH_SHORT).show();
            return;
        }
        showOfflineDataConfirmDialog("是否更新离线数据？", () -> startOfflineTileDataImport(true));
    }

    private void showOfflineDataConfirmDialog(@NonNull String message, @NonNull Runnable onConfirm) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        showMapConfirmDialog("离线数据", message, "是", "否", onConfirm, null);
    }

    private void startOfflineTileDataImport(boolean deleteExistingFirst) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        File tileRoot = getLocalOfflineTileRootDir();
        File tileZip = getLocalOfflineTileZipFile();
        if (!tileZip.isFile()) {
            Toast.makeText(requireContext(), "请上传离线数据", Toast.LENGTH_SHORT).show();
            return;
        }
        String initialProgressMessage = deleteExistingFirst ? "正在删除旧离线数据..." : "正在解压离线数据...";
        showOfflineDataProgressDialog(initialProgressMessage);
        postOfflineDataProgress(initialProgressMessage, 0);
        Runnable importTask = () -> new Thread(() -> {
            try {
                if (deleteExistingFirst && tileRoot.exists()) {
                    postOfflineDataProgress("正在统计旧离线数据...", 0);
                    int total = Math.max(1, countFileTreeUnits(tileRoot));
                    postOfflineDataProgress("正在删除旧离线数据...", 0);
                    final int[] deleted = {0};
                    deleteFileTree(tileRoot, () -> {
                        deleted[0]++;
                        int progress = Math.min(100, deleted[0] * 100 / total);
                        postOfflineDataProgress("正在删除旧离线数据...", progress);
                    });
                }
                setOfflineDataProgressNow("正在解压离线数据...", 0);
                stopAllMbtilesServers();
                unzipOfflineTileData(tileZip);
                locationHandler.post(() -> {
                    dismissOfflineDataProgressDialog();
                    if (!isAdded()) {
                        return;
                    }
                    refreshOfflineCheckboxAvailability();
                    loadBasemapPrefsAndApplyStyle(false);
                    Toast.makeText(requireContext(), "离线数据已就绪", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e(TAG_OFFLINE, "offline tile data import failed", e);
                locationHandler.post(() -> {
                    dismissOfflineDataProgressDialog();
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "离线数据处理失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        refreshOfflineCheckboxAvailability();
                    }
                });
            }
        }, "offline-tile-import").start();
        locationHandler.postDelayed(importTask, 200);
    }

    private void showOfflineDataProgressDialog(@NonNull String message) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        dismissOfflineDataProgressDialog();
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, "离线数据");
        hideMapDialogFooter(content);
        LinearLayout box = new LinearLayout(requireContext());
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(4), dp(16), dp(12));
        int contentWidth = Math.min(
                getResources().getDisplayMetrics().widthPixels - dp(72),
                dp(360));
        if (contentWidth < dp(220)) {
            contentWidth = Math.max(dp(160), getResources().getDisplayMetrics().widthPixels - dp(96));
        }
        offlineDataProgressText = new TextView(requireContext());
        offlineDataProgressText.setText(message + " 0%");
        offlineDataProgressText.setTextColor(Color.parseColor("#111111"));
        offlineDataProgressText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        offlineDataProgressText.setSingleLine(false);
        offlineDataProgressText.setMaxLines(2);
        offlineDataProgressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
        offlineDataProgressBar.setMax(100);
        offlineDataProgressBar.setProgress(0);
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                contentWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        pbLp.topMargin = dp(10);
        box.addView(offlineDataProgressText, new LinearLayout.LayoutParams(
                contentWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        box.addView(offlineDataProgressBar, pbLp);
        setMapDialogCustomContent(content, box);
        offlineDataProgressDialog = createMapStyledDialog(content);
        offlineDataProgressDialog.setCancelable(false);
        offlineDataProgressDialog.show();
    }

    private void postOfflineDataProgress(@NonNull String message, int progress) {
        synchronized (offlineDataProgressLock) {
            offlineDataProgressPendingMessage = message;
            offlineDataProgressPendingValue = Math.max(0, Math.min(100, progress));
            if (offlineDataProgressPostPending) {
                return;
            }
            offlineDataProgressPostPending = true;
        }
        locationHandler.post(() -> {
            String latestMessage;
            int latestProgress;
            synchronized (offlineDataProgressLock) {
                latestMessage = offlineDataProgressPendingMessage;
                latestProgress = offlineDataProgressPendingValue;
                offlineDataProgressPostPending = false;
            }
            if (offlineDataProgressDialog == null || !offlineDataProgressDialog.isShowing()) {
                return;
            }
            if (offlineDataProgressText != null) {
                offlineDataProgressText.setText(latestMessage + " " + latestProgress + "%");
            }
            if (offlineDataProgressBar != null) {
                offlineDataProgressBar.setProgress(latestProgress);
            }
        });
    }

    private void setOfflineDataProgressNow(@NonNull String message, int progress) {
        int safeProgress = Math.max(0, Math.min(100, progress));
        locationHandler.post(() -> {
            if (offlineDataProgressDialog == null || !offlineDataProgressDialog.isShowing()) {
                return;
            }
            if (offlineDataProgressText != null) {
                offlineDataProgressText.setText(message + " " + safeProgress + "%");
            }
            if (offlineDataProgressBar != null) {
                offlineDataProgressBar.setProgress(safeProgress);
            }
        });
    }

    private void dismissOfflineDataProgressDialog() {
        if (offlineDataProgressDialog != null && offlineDataProgressDialog.isShowing()) {
            try {
                offlineDataProgressDialog.dismiss();
            } catch (Exception e) {
                Log.w(TAG_OFFLINE, "dismiss offline data progress dialog", e);
            }
        }
        offlineDataProgressDialog = null;
        offlineDataProgressBar = null;
        offlineDataProgressText = null;
        synchronized (offlineDataProgressLock) {
            offlineDataProgressPostPending = false;
            offlineDataProgressPendingMessage = null;
            offlineDataProgressPendingValue = 0;
        }
    }

    private int countFileTreeUnits(@NonNull File file) {
        if (!file.exists()) {
            return 0;
        }
        int count = 1;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                count += countFileTreeUnits(child);
            }
        }
        return count;
    }

    private void deleteFileTree(@NonNull File file, @NonNull Runnable onUnitDeleted) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteFileTree(child, onUnitDeleted);
            }
        }
        if (!file.delete() && file.exists()) {
            throw new IOException("删除失败: " + file.getAbsolutePath());
        }
        onUnitDeleted.run();
    }

    private void unzipOfflineTileData(@NonNull File zipFile) throws IOException {
        File storageRoot = new File("/storage/emulated/0");
        File tileRoot = getLocalOfflineTileRootDir();
        long totalBytes = Math.max(1L, sumZipFileEntryBytes(zipFile));
        final long[] doneBytes = {0L};
        final int[] lastProgress = {-1};
        byte[] buffer = new byte[128 * 1024];
        String storageCanonical = storageRoot.getCanonicalPath();
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new java.io.FileInputStream(zipFile)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (TextUtils.isEmpty(entryName)) {
                    zis.closeEntry();
                    continue;
                }
                File out = resolveOfflineTileZipEntry(storageRoot, tileRoot, entryName);
                String outCanonical = out.getCanonicalPath();
                if (!outCanonical.equals(storageCanonical)
                        && !outCanonical.startsWith(storageCanonical + File.separator)) {
                    throw new IOException("非法压缩路径: " + entryName);
                }
                if (entry.isDirectory()) {
                    if (!out.isDirectory() && !out.mkdirs()) {
                        throw new IOException("创建目录失败: " + out.getAbsolutePath());
                    }
                } else {
                    File parent = out.getParentFile();
                    if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("创建目录失败: " + parent.getAbsolutePath());
                    }
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out))) {
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                            doneBytes[0] += len;
                            int progress = (int) Math.min(99L, doneBytes[0] * 100L / totalBytes);
                            if (progress != lastProgress[0]) {
                                lastProgress[0] = progress;
                                postOfflineDataProgress("正在解压离线数据...", progress);
                            }
                        }
                    }
                }
                if (entry.isDirectory()) {
                    long size = entry.getSize();
                    if (size > 0) {
                        doneBytes[0] += size;
                    }
                }
                zis.closeEntry();
            }
        }
        postOfflineDataProgress("正在解压离线数据...", 100);
    }

    private long sumZipFileEntryBytes(@NonNull File zipFile) throws IOException {
        long total = 0L;
        try (ZipFile zf = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                long size = entry.getSize();
                if (size > 0) {
                    total += size;
                }
            }
        } catch (ZipException e) {
            Log.w(TAG_OFFLINE, "read zip central directory failed, fallback to file length: " + zipFile.getAbsolutePath(), e);
            return zipFile.length();
        }
        return total > 0 ? total : zipFile.length();
    }

    @NonNull
    private File resolveOfflineTileZipEntry(
            @NonNull File storageRoot,
            @NonNull File tileRoot,
            @NonNull String entryName) {
        String normalized = entryName.replace('\\', '/');
        if (normalized.equals("mapTitles") || normalized.startsWith("mapTitles/")) {
            return new File(storageRoot, normalized);
        }
        return new File(tileRoot, normalized);
    }

    /**
     * 矢量底图与影像底图二选一：同时勾选时影像画在矢量下方会被完全挡住，表现为「选了影像仍是矢量」。
     */
    private void applyVectorImageryMutualExclusive(@NonNull CompoundButton changed, boolean isChecked) {
        if (!isChecked || !tiandituOnlineOk) {
            return;
        }
        int id = changed.getId();
        suppressBasemapLayerCallback = true;
        try {
            if (id == R.id.cbLayerImagery) {
                if (cbLayerVector != null && cbLayerVector.isChecked()) {
                    cbLayerVector.setChecked(false);
                }
            } else if (id == R.id.cbLayerVector) {
                if (cbLayerImagery != null && cbLayerImagery.isChecked()) {
                    cbLayerImagery.setChecked(false);
                }
            }
        } finally {
            suppressBasemapLayerCallback = false;
        }
    }

    /** 在线注记与离线注记二选一。 */
    private void applyAnnotationMutualExclusive(@NonNull CompoundButton changed, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        int id = changed.getId();
        suppressBasemapLayerCallback = true;
        try {
            if (id == R.id.cbLayerAnnotation) {
                if (cbLayerOfflineAnnotation != null && cbLayerOfflineAnnotation.isChecked()) {
                    cbLayerOfflineAnnotation.setChecked(false);
                }
            } else if (id == R.id.cbLayerOfflineAnnotation) {
                if (cbLayerAnnotation != null && cbLayerAnnotation.isChecked()) {
                    cbLayerAnnotation.setChecked(false);
                }
            }
        } finally {
            suppressBasemapLayerCallback = false;
        }
    }

    /** 读偏好后若两路底图同时为 true，保留矢量（与历史默认一致） */
    private void normalizeVectorImageryBothChecked() {
        if (cbLayerVector == null || cbLayerImagery == null || !tiandituOnlineOk) {
            return;
        }
        if (cbLayerVector.isChecked() && cbLayerImagery.isChecked()) {
            suppressBasemapLayerCallback = true;
            try {
                cbLayerImagery.setChecked(false);
            } finally {
                suppressBasemapLayerCallback = false;
            }
        }
    }

    /** 读偏好后若在线注记与离线注记都为 true，优先保留在线注记。 */
    private void normalizeAnnotationBothChecked() {
        if (cbLayerAnnotation == null || cbLayerOfflineAnnotation == null) {
            return;
        }
        if (cbLayerAnnotation.isChecked() && cbLayerOfflineAnnotation.isChecked()) {
            suppressBasemapLayerCallback = true;
            try {
                cbLayerOfflineAnnotation.setChecked(false);
            } finally {
                suppressBasemapLayerCallback = false;
            }
        }
    }

    private void setupLocatePanel(@NonNull View root) {
        layoutLocatePanel = root.findViewById(R.id.layoutLocatePanel);
        tvLocateMenuFollow = root.findViewById(R.id.tvLocateMenuFollow);
        TextView tvSelf = root.findViewById(R.id.tvLocateMenuSelf);
        TextView tvAll = root.findViewById(R.id.tvLocateMenuAll);
        if (ivLocateBtn != null) {
            ivLocateBtn.setOnClickListener(v -> locateToCurrentPosition());
            ivLocateBtn.setOnLongClickListener(v -> {
                toggleLocateMenuPanel();
                return true;
            });
        }
        if (tvSelf != null) {
            tvSelf.setOnClickListener(v -> {
                locateToCurrentPosition();
                hideLocateMenuPanel();
            });
        }
        if (tvAll != null) {
            tvAll.setOnClickListener(v -> {
                locateToAllMembers();
                hideLocateMenuPanel();
            });
        }
        if (tvLocateMenuFollow != null) {
            tvLocateMenuFollow.setOnClickListener(v -> {
                followSelfCamera = !followSelfCamera;
                navFollowSuspendedByGesture = false;
                locationHandler.removeCallbacks(resumeNavFollowRunnable);
                updateLocateFollowMenuLabel();
                Toast.makeText(requireContext(), followSelfCamera ? "已开启跟随" : "已关闭跟随", Toast.LENGTH_SHORT).show();
                if (followSelfCamera) {
                    locateToCurrentPosition();
                }
                hideLocateMenuPanel();
            });
        }
        updateLocateFollowMenuLabel();
    }

    private void toggleHostOrientation() {
        if (!isAdded()) return;
        android.app.Activity host = getActivity();
        if (host == null) return;
        int current = getResources().getConfiguration().orientation;
        if (current == Configuration.ORIENTATION_LANDSCAPE) {
            host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void toggleLocateMenuPanel() {
        if (layoutLocatePanel == null || !isAdded()) {
            return;
        }
        boolean show = layoutLocatePanel.getVisibility() != View.VISIBLE;
        if (show) {
            hideLayerPanel();
            updateLocateFollowMenuLabel();
            layoutLocatePanel.setVisibility(View.VISIBLE);
            bringLocatePanelToFront();
            layoutLocatePanel.post(this::bringLocatePanelToFront);
        } else {
            layoutLocatePanel.setVisibility(View.GONE);
            layoutLocatePanel.setTranslationZ(0f);
            resetRightToolsStackOrder();
        }
    }

    /** 定位弹层必须在根布局最前，否则会被全屏连线层、后加的远端箭头等挡住导致点击无响应。 */
    private void bringLocatePanelToFront() {
        if (layoutLocatePanel == null || layoutLocatePanel.getVisibility() != View.VISIBLE) {
            return;
        }
        if (layoutRightToolsRoot != null) {
            ViewGroup p = (ViewGroup) layoutRightToolsRoot.getParent();
            if (p != null) {
                p.bringChildToFront(layoutRightToolsRoot);
            }
            layoutRightToolsRoot.setTranslationZ(dp(16));
        }
        layoutLocatePanel.bringToFront();
        layoutLocatePanel.setTranslationZ(dp(24));
    }

    /**
     * 关闭定位菜单后恢复右侧工具条在根布局中的顺序，避免 {@link #bringLocatePanelToFront()} 长期留在最顶层
     * 盖住右下角 {@link #layoutLayerBarRoot}（图层按钮与弹层）。
     */
    private void resetRightToolsStackOrder() {
        if (layoutRightToolsRoot != null) {
            layoutRightToolsRoot.setTranslationZ(0f);
        }
        if (layoutLocatePanel != null) {
            layoutLocatePanel.setTranslationZ(0f);
        }
        if (layoutRightToolsRoot != null && ivMarkBtn != null) {
            reorderChildImmediatelyBefore(layoutRightToolsRoot, ivMarkBtn);
        }
    }

    private void hideLocateMenuPanel() {
        if (layoutLocatePanel != null) {
            layoutLocatePanel.setVisibility(View.GONE);
        }
        resetRightToolsStackOrder();
    }

    /** 图层面板与定位菜单同理：需压过全屏连线层与动态箭头，关闭后恢复顺序以免长期挡其它控件。 */
    private void bringLayerBarToFront() {
        if (layoutLayerPanel == null || layoutLayerPanel.getVisibility() != View.VISIBLE) {
            return;
        }
        if (layoutLayerBarRoot != null) {
            ViewGroup p = (ViewGroup) layoutLayerBarRoot.getParent();
            if (p != null) {
                p.bringChildToFront(layoutLayerBarRoot);
            }
            layoutLayerBarRoot.setTranslationZ(dp(16));
        }
        if (layoutLayerPanel != null) {
            layoutLayerPanel.setTranslationZ(dp(24));
        }
    }

    private void resetLayerBarStackOrder() {
        if (layoutLayerPanel != null) {
            layoutLayerPanel.setTranslationZ(0f);
        }
        if (layoutLayerBarRoot != null) {
            layoutLayerBarRoot.setTranslationZ(0f);
            if (tvCurrentCoord != null) {
                reorderChildImmediatelyBefore(layoutLayerBarRoot, tvCurrentCoord);
            }
        }
    }

    /** 将 child 插到 anchor 之前，与 layout_fg_map 中 inflate 顺序一致。 */
    private static void reorderChildImmediatelyBefore(@NonNull View child, @NonNull View anchor) {
        ViewGroup p = (ViewGroup) child.getParent();
        if (p == null || anchor.getParent() != p || child == anchor) {
            return;
        }
        p.removeView(child);
        int insertAt = p.indexOfChild(anchor);
        if (insertAt < 0) {
            p.addView(child);
        } else {
            p.addView(child, insertAt);
        }
    }

    /**
     * 根 FrameLayout 上插入「地图浮层」子 View 的 index：紧挨 {@link #layoutRightToolsRoot} 之前，
     * 避免默认 {@link ViewGroup#addView(View)} 追加到最顶层盖住定位菜单（含「定位自己」）。
     */
    private int indexToInsertMapOverlayBelowRightTools(@Nullable ViewGroup parent) {
        if (parent == null) {
            return 0;
        }
        if (layoutRightToolsRoot != null && layoutRightToolsRoot.getParent() == parent) {
            int i = parent.indexOfChild(layoutRightToolsRoot);
            return Math.max(0, i);
        }
        return parent.getChildCount();
    }

    /**
     * 鑷繁鐨勭孩鑹插畾浣嶅浘鏍囧簲濮嬬粓鍘嬭繃鍏朵粬鍦板浘鍔ㄦ€佽鐩栫墿锛屼絾涓嶅簲鐩栦綇鍙充晶宸ュ叿鏍忋€?
     */
    private void bringSelfLocationMarkerAboveMapOverlays() {
        if (ivHeading == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) ivHeading.getParent();
        if (parent == null) {
            return;
        }
        if (layoutRightToolsRoot != null && layoutRightToolsRoot.getParent() == parent) {
            reorderChildImmediatelyBefore(ivHeading, layoutRightToolsRoot);
            return;
        }
        if (layoutLayerBarRoot != null && layoutLayerBarRoot.getParent() == parent) {
            reorderChildImmediatelyBefore(ivHeading, layoutLayerBarRoot);
            return;
        }
        ivHeading.bringToFront();
    }

    private void hideLayerPanel() {
        if (layoutLayerPanel != null && layoutLayerPanel.getVisibility() == View.VISIBLE) {
            layoutLayerPanel.setVisibility(View.GONE);
            mapLayerPanelVisible = false;
            if (isMapLandscape()) {
                updateLandscapeLayerPanelLayout(false);
            }
            resetLayerBarStackOrder();
        }
    }

    /** 点击地图空白处时收起定位/图层面板 */
    private void dismissMapToolPanelsOnMapTap() {
        hideLocateMenuPanel();
        hideLayerPanel();
    }

    private void updateLocateFollowMenuLabel() {
        if (tvLocateMenuFollow != null) {
            tvLocateMenuFollow.setText(followSelfCamera ? "关闭跟随" : "开启跟随");
        }
    }

    private void updateOnlineLayerAvailability() {
        if (cbLayerImagery != null) {
            cbLayerImagery.setEnabled(tiandituOnlineOk);
        }
        if (cbLayerAnnotation != null) {
            cbLayerAnnotation.setEnabled(tiandituOnlineOk);
        }
        if (!tiandituOnlineOk) {
            suppressBasemapLayerCallback = true;
            try {
                if (cbLayerImagery != null) {
                    cbLayerImagery.setChecked(false);
                }
                if (cbLayerAnnotation != null) {
                    cbLayerAnnotation.setChecked(false);
                }
            } finally {
                suppressBasemapLayerCallback = false;
            }
        }
        ensureBasemapSelectionValid();
    }

    private void refreshOfflineCheckboxAvailability() {
        boolean hasOfflineVectorDir = hasOfflineVectorTiles();
        boolean hasOfflineImageryDir = hasOfflineImageryTiles();
        boolean hasAnnotationDir = hasOfflineAnnotationTiles();
        if (cbLayerOffline != null) {
            cbLayerOffline.setEnabled(hasOfflineVectorDir);
            if (!hasOfflineVectorDir) {
                suppressBasemapLayerCallback = true;
                try {
                    cbLayerOffline.setChecked(false);
                } finally {
                    suppressBasemapLayerCallback = false;
                }
            }
        }
        if (cbLayerOfflineImagery != null) {
            cbLayerOfflineImagery.setEnabled(hasOfflineImageryDir);
            if (!hasOfflineImageryDir) {
                suppressBasemapLayerCallback = true;
                try {
                    cbLayerOfflineImagery.setChecked(false);
                } finally {
                    suppressBasemapLayerCallback = false;
                }
            }
        }
        if (cbLayerOfflineAnnotation != null) {
            cbLayerOfflineAnnotation.setEnabled(hasAnnotationDir);
            if (!hasAnnotationDir) {
                suppressBasemapLayerCallback = true;
                try {
                    cbLayerOfflineAnnotation.setChecked(false);
                } finally {
                    suppressBasemapLayerCallback = false;
                }
            }
        }
    }

    private void onBasemapLayerCheckboxChanged() {
        refreshOfflineCheckboxAvailability();
        ensureBasemapSelectionValid();
        saveBasemapLayerPrefs();
        if (mapLibreMap != null) {
            applyCompositeBasemapStyle(false);
        }
    }

    /** 至少保留一种底图；必要时自动勾选矢量 */
    private void ensureBasemapSelectionValid() {
        if (!hasAnyBasemapLayerSelected()) {
            suppressBasemapLayerCallback = true;
            try {
                if (cbLayerVector != null) {
                    cbLayerVector.setChecked(true);
                }
            } finally {
                suppressBasemapLayerCallback = false;
            }
        }
    }

    private boolean hasAnyBasemapLayerSelected() {
        if (cbLayerVector != null && cbLayerVector.isChecked()) {
            return true;
        }
        if (tiandituOnlineOk && cbLayerImagery != null && cbLayerImagery.isChecked()) {
            return true;
        }
        if (cbLayerOfflineImagery != null && cbLayerOfflineImagery.isChecked()
                && hasOfflineImageryTiles()) {
            return true;
        }
        if (tiandituOnlineOk && cbLayerAnnotation != null && cbLayerAnnotation.isChecked()) {
            return true;
        }
        if (cbLayerOfflineAnnotation != null && cbLayerOfflineAnnotation.isChecked()
                && hasOfflineAnnotationTiles()) {
            return true;
        }
        return cbLayerOffline != null && cbLayerOffline.isChecked()
                && hasOfflineVectorTiles();
    }

    /**
     * 当前启用的底图栈（底→顶）：离线矢量 → 离线影像 → 影像 → 矢量 → 离线注记 → 在线注记；仅包含已勾选且可用的类型。
     */
    private ArrayList<MapLayerType> buildActiveBasemapLayerStack() {
        ArrayList<MapLayerType> out = new ArrayList<>();
        MapLayerType[] bottomToTop = {
                MapLayerType.OFFLINE_VECTOR,
                MapLayerType.OFFLINE_IMAGERY,
                MapLayerType.IMAGERY,
                MapLayerType.VECTOR,
                MapLayerType.OFFLINE_ANNOTATION,
                MapLayerType.ANNOTATION,
        };
        for (MapLayerType t : bottomToTop) {
            if (isBasemapLayerWanted(t)) {
                out.add(t);
            }
        }
        return out;
    }

    private boolean isBasemapLayerWanted(MapLayerType t) {
        switch (t) {
            case VECTOR:
                return cbLayerVector != null && cbLayerVector.isChecked();
            case IMAGERY:
                return tiandituOnlineOk && cbLayerImagery != null && cbLayerImagery.isChecked();
            case ANNOTATION:
                return tiandituOnlineOk && cbLayerAnnotation != null && cbLayerAnnotation.isChecked();
            case OFFLINE_VECTOR:
                return hasOfflineVectorTiles()
                        && cbLayerOffline != null && cbLayerOffline.isChecked();
            case OFFLINE_IMAGERY:
                return hasOfflineImageryTiles()
                        && cbLayerOfflineImagery != null && cbLayerOfflineImagery.isChecked();
            case OFFLINE_ANNOTATION:
                return hasOfflineAnnotationTiles()
                        && cbLayerOfflineAnnotation != null && cbLayerOfflineAnnotation.isChecked();
            default:
                return false;
        }
    }

    private void loadBasemapPrefsAndApplyStyle() {
        loadBasemapPrefsAndApplyStyle(true);
    }

    private void loadBasemapPrefsAndApplyStyle(boolean locateIfNeeded) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        refreshOfflineCheckboxAvailability();
        suppressBasemapLayerCallback = true;
        try {
            SharedPreferences p = requireContext().getSharedPreferences(PREF_BASEMAP_LAYERS, Context.MODE_PRIVATE);
            if (!p.contains(PREF_KEY_CB_VECTOR)) {
                applyDefaultBasemapPrefs();
            } else {
                if (cbLayerVector != null) {
                    cbLayerVector.setChecked(p.getBoolean(PREF_KEY_CB_VECTOR, true));
                }
                if (cbLayerImagery != null) {
                    cbLayerImagery.setChecked(p.getBoolean(PREF_KEY_CB_IMAGERY, false));
                }
                if (cbLayerAnnotation != null) {
                    cbLayerAnnotation.setChecked(p.getBoolean(PREF_KEY_CB_ANNOTATION, true));
                }
                if (cbLayerOffline != null) {
                    cbLayerOffline.setChecked(p.getBoolean(PREF_KEY_CB_OFFLINE, hasOfflineVectorTiles()));
                }
                if (cbLayerOfflineImagery != null) {
                    cbLayerOfflineImagery.setChecked(p.getBoolean(PREF_KEY_CB_OFFLINE_IMAGERY, hasOfflineImageryTiles()));
                }
                if (cbLayerOfflineAnnotation != null) {
                    cbLayerOfflineAnnotation.setChecked(p.getBoolean(PREF_KEY_CB_OFFLINE_ANNOTATION, hasOfflineAnnotationTiles()));
                }
            }
            normalizeVectorImageryBothChecked();
            normalizeAnnotationBothChecked();
        } finally {
            suppressBasemapLayerCallback = false;
        }
        refreshOfflineCheckboxAvailability();
        updateOnlineLayerAvailability();
        ensureBasemapSelectionValid();
        applyCompositeBasemapStyle(locateIfNeeded);
    }

    private void applyDefaultBasemapPrefs() {
        boolean hasOffVector = hasOfflineVectorTiles();
        boolean hasOffImagery = hasOfflineImageryTiles();
        boolean hasOffAnnotation = hasOfflineAnnotationTiles();
        if (cbLayerVector != null) {
            cbLayerVector.setChecked(true);
        }
        if (cbLayerImagery != null) {
            cbLayerImagery.setChecked(false);
        }
        if (cbLayerAnnotation != null) {
            cbLayerAnnotation.setChecked(true);
        }
        if (cbLayerOffline != null) {
            cbLayerOffline.setChecked(hasOffVector);
        }
        if (cbLayerOfflineImagery != null) {
            cbLayerOfflineImagery.setChecked(hasOffImagery);
        }
        if (cbLayerOfflineAnnotation != null) {
            cbLayerOfflineAnnotation.setChecked(!tiandituOnlineOk && hasOffAnnotation);
        }
    }

    private void saveBasemapLayerPrefs() {
        if (!isAdded() || getContext() == null) {
            return;
        }
        SharedPreferences p = requireContext().getSharedPreferences(PREF_BASEMAP_LAYERS, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putBoolean(PREF_KEY_CB_VECTOR, cbLayerVector != null && cbLayerVector.isChecked());
        e.putBoolean(PREF_KEY_CB_IMAGERY, cbLayerImagery != null && cbLayerImagery.isChecked());
        e.putBoolean(PREF_KEY_CB_ANNOTATION, cbLayerAnnotation != null && cbLayerAnnotation.isChecked());
        e.putBoolean(PREF_KEY_CB_OFFLINE, cbLayerOffline != null && cbLayerOffline.isChecked());
        e.putBoolean(PREF_KEY_CB_OFFLINE_IMAGERY, cbLayerOfflineImagery != null && cbLayerOfflineImagery.isChecked());
        e.putBoolean(PREF_KEY_CB_OFFLINE_ANNOTATION, cbLayerOfflineAnnotation != null && cbLayerOfflineAnnotation.isChecked());
        e.apply();
    }

    private void applyCompositeBasemapStyle() {
        applyCompositeBasemapStyle(true);
    }

    private void applyCompositeBasemapStyle(boolean locateIfNeeded) {
        String json = buildCompositeBasemapStyleJson();
        applyMapStyle(json, true, locateIfNeeded);
    }

    private String buildCompositeBasemapStyleJson() {
        ArrayList<MapLayerType> stack = buildActiveBasemapLayerStack();
        if (stack.isEmpty()) {
            Log.d(TAG_OFFLINE, "basemap-style: stack empty, fallback STYLE_OSM");
            return STYLE_OSM;
        }
        String tk = urlEncodeTk(TIANDITU_TOKEN);
        LinkedHashSet<String> sourceKeys = new LinkedHashSet<>();
        ArrayList<String> layerSourceIds = new ArrayList<>();
        for (MapLayerType t : stack) {
            String sid = resolveBasemapSourceId(t);
            if (sid == null) {
                continue;
            }
            sourceKeys.add(sid);
            layerSourceIds.add(sid);
        }
        if (layerSourceIds.isEmpty()) {
            Log.d(TAG_OFFLINE, "basemap-style: no usable source, fallback STYLE_OSM");
            return STYLE_OSM;
        }
        logBasemapStyleDecision(stack, sourceKeys, layerSourceIds);
        StringBuilder sources = new StringBuilder();
        sources.append('{');
        boolean first = true;
        for (String sid : sourceKeys) {
            if (!first) {
                sources.append(',');
            }
            first = false;
            sources.append('"').append(sid).append("\":");
            sources.append(buildRasterSourceBody(sid, tk));
        }
        sources.append('}');
        StringBuilder layers = new StringBuilder();
        layers.append('[');
        int layerIndex = 0;
        for (String sid : layerSourceIds) {
            if (layerIndex > 0) {
                layers.append(',');
            }
            layers.append("{\"id\":\"basemap-")
                    .append(layerIndex)
                    .append("\",\"type\":\"raster\",\"source\":\"")
                    .append(sid)
                    .append("\"}");
            layerIndex++;
        }
        // 部分 DEM 切片在 hillshade 叠加下会把底图整体压暗到近似黑屏，先禁用 hillshade。
        layers.append(']');
        return "{"
                + "\"version\":8,"
                + "\"sources\":"
                + sources
                + ",\"layers\":"
                + layers
                + "}";
    }

    private void logBasemapStyleDecision(
            @NonNull ArrayList<MapLayerType> stack,
            @NonNull LinkedHashSet<String> sourceKeys,
            @NonNull ArrayList<String> layerSourceIds) {
        boolean vecDir = hasOfflineVectorTiles();
        boolean vecChecked = cbLayerOffline != null && cbLayerOffline.isChecked();
        Log.d(TAG_OFFLINE,
                "basemap-style: offlineVectorChecked=" + vecChecked
                        + ", vecDir=" + vecDir
                        + ", stack=" + stack
                        + ", sources=" + sourceKeys
                        + ", layers=" + layerSourceIds);
    }

    @Nullable
    private String resolveBasemapSourceId(MapLayerType t) {
        switch (t) {
            case VECTOR:
                return tiandituOnlineOk ? "tdtVec" : "osm";
            case IMAGERY:
                return tiandituOnlineOk ? "tdtImg" : null;
            case ANNOTATION:
                return tiandituOnlineOk ? "tdtCia" : null;
            case OFFLINE_VECTOR:
                return hasOfflineVectorTiles() ? "localVec" : null;
            case OFFLINE_IMAGERY:
                return hasOfflineImageryTiles() ? "localXyz" : null;
            case OFFLINE_ANNOTATION:
                return hasOfflineAnnotationTiles() ? "localZj" : null;
            default:
                return null;
        }
    }

    private String buildRasterSourceBody(String sourceId, String tkEncoded) {
        switch (sourceId) {
            case "tdtVec":
                return "{\"type\":\"raster\","
                        + buildSubdomainTiles("vec_w", "vec", tkEncoded)
                        + ",\"tileSize\":256}";
            case "tdtImg":
                return "{\"type\":\"raster\","
                        + buildSubdomainTiles("img_w", "img", tkEncoded)
                        + ",\"tileSize\":256}";
            case "tdtCia":
                return "{\"type\":\"raster\","
                        + buildSubdomainTiles("cia_w", "cia", tkEncoded)
                        + ",\"tileSize\":256}";
            case "osm":
                return "{\"type\":\"raster\",\"tiles\":[\"https://tile.openstreetmap.org/{z}/{x}/{y}.png\"],\"tileSize\":256}";
            case "localVec": {
                String mbtilesTileUrl = ensureVectorMbtilesTileUrl();
                if (!TextUtils.isEmpty(mbtilesTileUrl)) {
                    return "{\"type\":\"raster\",\"tiles\":[\""
                            + mbtilesTileUrl
                            + "\"],\"tileSize\":256,\"minzoom\":"
                            + LOCAL_XYZ_MIN_ZOOM
                            + ",\"maxzoom\":"
                            + LOCAL_XYZ_MAX_ZOOM
                            + "}";
                }
                String base = getLocalOfflineVectorTileRootDir().getAbsolutePath().trim();
                if (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                String tileUrl = "file://" + base + "/{z}/{x}/{y}.png";
                return "{\"type\":\"raster\",\"tiles\":[\""
                        + tileUrl
                        + "\"],\"tileSize\":256,\"minzoom\":"
                        + LOCAL_XYZ_MIN_ZOOM
                        + ",\"maxzoom\":"
                        + LOCAL_XYZ_MAX_ZOOM
                        + "}";
            }
            case "localXyz": {
                String mbtilesTileUrl = ensureImageryMbtilesTileUrl();
                if (!TextUtils.isEmpty(mbtilesTileUrl)) {
                    return "{\"type\":\"raster\",\"tiles\":[\""
                            + mbtilesTileUrl
                            + "\"],\"tileSize\":256,\"minzoom\":"
                            + LOCAL_XYZ_MIN_ZOOM
                            + ",\"maxzoom\":"
                            + LOCAL_XYZ_MAX_ZOOM
                            + "}";
                }
                String base = getLocalOfflineImageryTileRootDir().getAbsolutePath().trim();
                if (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                String tileUrl = "file://" + base + "/{z}/{x}/{y}.png";
                return "{\"type\":\"raster\",\"tiles\":[\""
                        + tileUrl
                        + "\"],\"tileSize\":256,\"minzoom\":"
                        + LOCAL_XYZ_MIN_ZOOM
                        + ",\"maxzoom\":"
                        + LOCAL_XYZ_MAX_ZOOM
                        + "}";
            }
            case "localZj": {
                String mbtilesTileUrl = ensureAnnotationMbtilesTileUrl();
                if (!TextUtils.isEmpty(mbtilesTileUrl)) {
                    return "{\"type\":\"raster\",\"tiles\":[\""
                            + mbtilesTileUrl
                            + "\"],\"tileSize\":256,\"minzoom\":"
                            + LOCAL_XYZ_MIN_ZOOM
                            + ",\"maxzoom\":"
                            + LOCAL_XYZ_MAX_ZOOM
                            + "}";
                }
                String base = getLocalOfflineAnnotationTileRootDir().getAbsolutePath().trim();
                if (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                String tileUrl = "file://" + base + "/{z}/{x}/{y}.png";
                return "{\"type\":\"raster\",\"tiles\":[\""
                        + tileUrl
                        + "\"],\"tileSize\":256,\"minzoom\":"
                        + LOCAL_XYZ_MIN_ZOOM
                        + ",\"maxzoom\":"
                        + LOCAL_XYZ_MAX_ZOOM
                        + "}";
            }
            default:
                return "{\"type\":\"raster\",\"tiles\":[],\"tileSize\":256}";
        }
    }

    @Nullable
    private String ensureVectorMbtilesTileUrl() {
        return ensureMbtilesTileUrl(getLocalOfflineVectorMbtilesFile(), true);
    }

    @Nullable
    private String ensureImageryMbtilesTileUrl() {
        return ensureMbtilesTileUrl(getLocalOfflineImageryMbtilesFile(), false);
    }

    @Nullable
    private String ensureAnnotationMbtilesTileUrl() {
        return ensureMbtilesTileUrl(getLocalOfflineAnnotationMbtilesFile(), null);
    }

    @Nullable
    private String ensureMbtilesTileUrl(@NonNull File mbtiles, @Nullable Boolean vectorLayer) {
        if (!mbtiles.isFile()) {
            return null;
        }
        try {
            MbtilesTileServer server = getMbtilesServer(vectorLayer);
            if (server == null || !server.matches(mbtiles)) {
                stopMbtilesServer(vectorLayer);
                server = new MbtilesTileServer(mbtiles);
                server.start();
                setMbtilesServer(vectorLayer, server);
            }
            return server.getTileUrlTemplate();
        } catch (Exception e) {
            Log.e(TAG_OFFLINE, "start mbtiles server failed: " + mbtiles.getAbsolutePath(), e);
            stopMbtilesServer(vectorLayer);
            return null;
        }
    }

    @Nullable
    private MbtilesTileServer getMbtilesServer(@Nullable Boolean vectorLayer) {
        if (vectorLayer == null) {
            return annotationMbtilesServer;
        }
        return vectorLayer ? vectorMbtilesServer : imageryMbtilesServer;
    }

    private void setMbtilesServer(@Nullable Boolean vectorLayer, @Nullable MbtilesTileServer server) {
        if (vectorLayer == null) {
            annotationMbtilesServer = server;
        } else if (vectorLayer) {
            vectorMbtilesServer = server;
        } else {
            imageryMbtilesServer = server;
        }
    }

    private void stopMbtilesServer(@Nullable Boolean vectorLayer) {
        MbtilesTileServer server = getMbtilesServer(vectorLayer);
        if (server != null) {
            server.close();
            setMbtilesServer(vectorLayer, null);
        }
    }

    private void stopAllMbtilesServers() {
        stopMbtilesServer(true);
        stopMbtilesServer(false);
        stopMbtilesServer(null);
    }

    private void stopImageryMbtilesServer() {
        stopAllMbtilesServers();
    }

    private static final class MbtilesTileServer {
        private final File mbtilesFile;
        private final SQLiteDatabase db;
        private final ServerSocket serverSocket;
        private final Thread acceptThread;
        private volatile boolean running = true;

        MbtilesTileServer(@NonNull File mbtilesFile) throws IOException {
            this.mbtilesFile = mbtilesFile;
            this.db = SQLiteDatabase.openDatabase(
                    mbtilesFile.getAbsolutePath(),
                    null,
                    SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            this.serverSocket = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
            this.acceptThread = new Thread(this::acceptLoop, "mbtiles-imagery-server");
        }

        void start() {
            acceptThread.start();
        }

        boolean matches(@NonNull File file) {
            return mbtilesFile.getAbsolutePath().equals(file.getAbsolutePath()) && running && !serverSocket.isClosed();
        }

        @NonNull
        String getTileUrlTemplate() {
            return "http://127.0.0.1:" + serverSocket.getLocalPort() + "/tiles/{z}/{x}/{y}.png";
        }

        void close() {
            running = false;
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
            try {
                db.close();
            } catch (Exception ignored) {
            }
        }

        private void acceptLoop() {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleClient(socket), "mbtiles-imagery-client").start();
                } catch (IOException e) {
                    if (running) {
                        Log.w(TAG_OFFLINE, "mbtiles accept failed", e);
                    }
                }
            }
        }

        private void handleClient(@NonNull Socket socket) {
            try (Socket s = socket;
                 InputStream in = s.getInputStream();
                 java.io.OutputStream out = s.getOutputStream()) {
                String requestLine = readHttpRequestLine(in);
                drainHttpHeaders(in);
                TileCoord coord = parseTileRequest(requestLine);
                if (coord == null) {
                    writeHttpResponse(out, 404, "text/plain", new byte[0]);
                    return;
                }
                byte[] tile = readTile(coord.z, coord.x, coord.y);
                if (tile == null || tile.length == 0) {
                    writeHttpResponse(out, 404, "text/plain", new byte[0]);
                    return;
                }
                writeHttpResponse(out, 200, detectTileMime(tile), tile);
            } catch (Exception e) {
                Log.w(TAG_OFFLINE, "mbtiles client failed", e);
            }
        }

        @Nullable
        private byte[] readTile(int z, int x, int y) {
            byte[] tile = readTileRow(z, x, y);
            if (tile != null) {
                return tile;
            }
            int tmsY = (1 << z) - 1 - y;
            return tmsY == y ? null : readTileRow(z, x, tmsY);
        }

        @Nullable
        private byte[] readTileRow(int z, int x, int y) {
            synchronized (db) {
                try (Cursor c = db.rawQuery(
                        "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
                        new String[]{String.valueOf(z), String.valueOf(x), String.valueOf(y)})) {
                    if (c.moveToFirst()) {
                        return c.getBlob(0);
                    }
                } catch (Exception e) {
                    Log.w(TAG_OFFLINE, "read mbtiles tile failed z=" + z + " x=" + x + " y=" + y, e);
                }
            }
            return null;
        }

        @Nullable
        private static String readHttpRequestLine(@NonNull InputStream in) throws IOException {
            StringBuilder sb = new StringBuilder(96);
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\n') {
                    break;
                }
                if (b != '\r') {
                    sb.append((char) b);
                }
                if (sb.length() > 512) {
                    break;
                }
            }
            return sb.length() == 0 ? null : sb.toString();
        }

        private static void drainHttpHeaders(@NonNull InputStream in) throws IOException {
            int matched = 0;
            int[] end = {'\r', '\n', '\r', '\n'};
            int cur;
            while ((cur = in.read()) != -1) {
                if (cur == end[matched]) {
                    matched++;
                    if (matched == end.length) {
                        return;
                    }
                } else {
                    matched = cur == '\r' ? 1 : 0;
                }
            }
        }

        @Nullable
        private static TileCoord parseTileRequest(@Nullable String requestLine) {
            if (TextUtils.isEmpty(requestLine)) {
                return null;
            }
            String[] parts = requestLine.split(" ");
            if (parts.length < 2 || !"GET".equals(parts[0])) {
                return null;
            }
            String path = parts[1];
            int query = path.indexOf('?');
            if (query >= 0) {
                path = path.substring(0, query);
            }
            String[] seg = path.split("/");
            if (seg.length < 5 || !"tiles".equals(seg[1])) {
                return null;
            }
            try {
                int z = Integer.parseInt(seg[2]);
                int x = Integer.parseInt(seg[3]);
                String yPart = seg[4];
                int dot = yPart.indexOf('.');
                if (dot > 0) {
                    yPart = yPart.substring(0, dot);
                }
                int y = Integer.parseInt(yPart);
                if (z < 0 || z > 30 || x < 0 || y < 0) {
                    return null;
                }
                return new TileCoord(z, x, y);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @NonNull
        private static String detectTileMime(@NonNull byte[] tile) {
            if (tile.length >= 8
                    && (tile[0] & 0xFF) == 0x89
                    && tile[1] == 0x50
                    && tile[2] == 0x4E
                    && tile[3] == 0x47) {
                return "image/png";
            }
            if (tile.length >= 3
                    && (tile[0] & 0xFF) == 0xFF
                    && (tile[1] & 0xFF) == 0xD8
                    && (tile[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }
            if (tile.length >= 4
                    && tile[0] == 'R'
                    && tile[1] == 'I'
                    && tile[2] == 'F'
                    && tile[3] == 'F') {
                return "image/webp";
            }
            return "application/octet-stream";
        }

        private static void writeHttpResponse(
                @NonNull java.io.OutputStream out,
                int code,
                @NonNull String contentType,
                @NonNull byte[] body) throws IOException {
            String reason = code == 200 ? "OK" : "Not Found";
            String header = "HTTP/1.1 " + code + " " + reason + "\r\n"
                    + "Content-Type: " + contentType + "\r\n"
                    + "Content-Length: " + body.length + "\r\n"
                    + "Connection: close\r\n"
                    + "Cache-Control: max-age=86400\r\n"
                    + "\r\n";
            out.write(header.getBytes(StandardCharsets.US_ASCII));
            out.write(body);
            out.flush();
        }

        private static final class TileCoord {
            final int z;
            final int x;
            final int y;

            TileCoord(int z, int x, int y) {
                this.z = z;
                this.x = x;
                this.y = y;
            }
        }
    }

    private void applyMapStyle(String styleJson, boolean removeOverlayBeforeStyleSwitch, boolean locateIfNeeded) {
        if (mapLibreMap == null) return;
        mapLibreMap.setStyle(new Style.Builder().fromJson(styleJson), style -> {
            if (mapLibreMap == null || !isAdded()) return;
            boolean hasOfflineVector = style.getSource("localVec") != null;
            boolean hasOfflineAnno = style.getSource("localZj") != null;
            Log.d(TAG_OFFLINE, "basemap-apply: styleLoaded localVecSource=" + hasOfflineVector);
            Log.d(TAG_OFFLINE, "annotation-apply: styleLoaded localZjSource=" + hasOfflineAnno);
            logOfflineAnnotationTileSample();
            mapLibreMap.getUiSettings().setLogoEnabled(false);
            mapLibreMap.getUiSettings().setAttributionEnabled(false);
            // 使用自定义罗盘，关闭地图默认指北针
            mapLibreMap.getUiSettings().setCompassEnabled(false);
            mapLibreMap.getUiSettings().setRotateGesturesEnabled(false);
            ensureMapLibreMarkConnectionLayer(style);
            ensureMapLibreMarkLinkLabelsLayer(style);
            ensureMapLibreBattleMarkLayers(style);
            // 切换地图图层时不再重置定位/相机；仅在首次进入且尚无定位时再触发定位。
            if (currentLatLng == null && locateIfNeeded) {
                hasCenteredOnFirstFix = false;
                mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.9042, 116.4074), 10.0));
                tryLocateAndMark();
            } else {
                updateHeadingViewPosition();
                flushMarkConnectionLinesNow();
                updateMapLibreBattleMarks();
            }
            refreshPendingRemoteOverlays();
            trySyncBattleMarksAndCompassFromHub();
            updateMapLibreBattleMarks();
        });
    }

    /** 打印离线注记在当前相机缩放级别下的示例瓦片路径，便于排查目录/层级不匹配。 */
    private void logOfflineAnnotationTileSample() {
        if (mapLibreMap == null) return;
        CameraPosition cp = mapLibreMap.getCameraPosition();
        if (cp == null || cp.target == null) return;
        int z = (int) Math.floor(cp.zoom);
        if (z < LOCAL_XYZ_MIN_ZOOM) z = LOCAL_XYZ_MIN_ZOOM;
        if (z > LOCAL_XYZ_MAX_ZOOM) z = LOCAL_XYZ_MAX_ZOOM;
        double lat = Math.max(-85.05112878, Math.min(85.05112878, cp.target.getLatitude()));
        double lon = cp.target.getLongitude();
        while (lon < -180d) lon += 360d;
        while (lon >= 180d) lon -= 360d;
        double n = Math.pow(2d, z);
        int x = (int) Math.floor((lon + 180d) / 360d * n);
        int y = (int) Math.floor((1d - Math.log(Math.tan(Math.toRadians(lat)) + 1d / Math.cos(Math.toRadians(lat))) / Math.PI) / 2d * n);
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        int max = (int) n - 1;
        if (x > max) x = max;
        if (y > max) y = max;
        File sample = new File(getLocalOfflineAnnotationTileRootDir(), z + "/" + x + "/" + y + ".png");
        boolean checked = cbLayerOfflineAnnotation != null && cbLayerOfflineAnnotation.isChecked();
        Log.d(TAG_OFFLINE, "annotation-sample: checked=" + checked
                + ", zoom=" + cp.zoom + " -> z=" + z
                + ", center=(" + lat + "," + lon + ")"
                + ", sample=" + sample.getAbsolutePath()
                + ", exists=" + sample.isFile());
    }

    private void trySyncBattleMarksAndCompassFromHub() {
    }



    private void syncCompassTrackingFromHub(boolean enabled) {
        compassTrackingEnabled = enabled;
        if (compassTopRotatingLayer != null) {
            compassTopRotatingLayer.setRotation(enabled ? -selfHeadingDeg : 0f);
        }
        if (layoutCompassRotatingLayer != null) {
            layoutCompassRotatingLayer.setRotation(enabled ? -selfHeadingDeg : 0f);
        }
        if (ivCompassBtn != null) {
            ivCompassBtn.setAlpha(enabled ? 1f : 0.8f);
        }
    }

    private void tryLocateAndMark() {
        if (getContext() == null || mapLibreMap == null) return;
        if (!hasAnyLocationPermission()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(android.content.Context.LOCATION_SERVICE);
        if (locationManager == null) return;

        Location cached = getBestLastKnownLocation(locationManager);
        if (cached != null) {
            showLocationMarker(cached);
        }

        requestFreshLocation(locationManager);
    }

    private boolean hasAnyLocationPermission() {
        if (getContext() == null) return false;
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasFineLocationPermission() {
        if (getContext() == null) return false;
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Nullable
    private static Location getBestLastKnownLocation(LocationManager locationManager) {
        Location gps = null;
        Location network = null;
        try {
            gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException e) {
            return null;
        }
        // 坐标系一致性优先：GPS_PROVIDER 更接近 WGS84，优先于 NETWORK_PROVIDER（后者常为 GCJ 系）。
        if (gps != null) return gps;
        return network;
    }

    /**
     * GPS 仍在“新鲜”窗口内时丢弃纯网络回调，{@link #showLocationMarker} 前的 lastKnown 路径不受影响。
     */
    private boolean shouldDiscardNetworkLocationPreferGps(@NonNull Location location) {
        if (!LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
            return false;
        }
        if (lastLiveGpsElapsedRealtime < 0L) {
            return false;
        }
        return (SystemClock.elapsedRealtime() - lastLiveGpsElapsedRealtime) < PREFER_GPS_STALE_AFTER_MS;
    }

    /** 是否应丢弃当前定位点：处理乱序旧点、低质量 network 点与高速回拽场景。 */
    private boolean shouldDiscardIncomingLocation(@NonNull Location location) {
        if (shouldDiscardNetworkLocationPreferGps(location)) {
            return true;
        }
        if (isOlderThanLastAccepted(location)) {
            return true;
        }
        if (LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
            if (location.hasAccuracy() && location.getAccuracy() > NETWORK_ACCURACY_REJECT_M) {
                return true;
            }
            if (lastAcceptedSpeedMps >= NETWORK_DROP_SPEED_THRESHOLD_MPS) {
                return true;
            }
        }
        return false;
    }

    private boolean isOlderThanLastAccepted(@NonNull Location location) {
        long incomingNs = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                ? location.getElapsedRealtimeNanos() : 0L;
        if (incomingNs > 0L && lastAcceptedElapsedRealtimeNanos > 0L) {
            return incomingNs + LOCATION_OUT_OF_ORDER_TOLERANCE_MS * 1_000_000L < lastAcceptedElapsedRealtimeNanos;
        }
        long t = location.getTime();
        return lastAcceptedLocationTimeMs > 0L
                && t > 0L
                && t + LOCATION_OUT_OF_ORDER_TOLERANCE_MS < lastAcceptedLocationTimeMs;
    }

    private void recordAcceptedLocation(@NonNull Location location) {
        long nowMs = location.getTime();
        long nowNs = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                ? location.getElapsedRealtimeNanos() : 0L;
        if (lastAcceptedLocationTimeMs > 0L) {
            float[] d = new float[1];
            Location.distanceBetween(
                    location.getLatitude(), location.getLongitude(),
                    currentLatLng != null ? currentLatLng.getLatitude() : location.getLatitude(),
                    currentLatLng != null ? currentLatLng.getLongitude() : location.getLongitude(),
                    d);
            double dtSec = 0d;
            if (nowNs > 0L && lastAcceptedElapsedRealtimeNanos > 0L) {
                dtSec = (nowNs - lastAcceptedElapsedRealtimeNanos) / 1_000_000_000d;
            } else if (nowMs > 0L && lastAcceptedLocationTimeMs > 0L) {
                dtSec = (nowMs - lastAcceptedLocationTimeMs) / 1000d;
            }
            if (dtSec > 0.15d) {
                lastAcceptedSpeedMps = (float) (d[0] / dtSec);
            }
        }
        lastAcceptedLocationTimeMs = nowMs;
        if (nowNs > 0L) {
            lastAcceptedElapsedRealtimeNanos = nowNs;
        }
    }

    private void requestFreshLocation(LocationManager locationManager) {
        if (!isAdded()) return;
        // 每次重新注册前先停旧监听；不能用 requestingFreshLocation 短路，否则持续定位时无法再次进入。
        cancelLocationTimeout();
        stopPendingLocationUpdates(locationManager);

        boolean networkOk;
        boolean gpsOk;
        try {
            networkOk = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            gpsOk = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Toast.makeText(requireContext(), MSG_LOCATION_ENABLE, Toast.LENGTH_LONG).show();
            return;
        }
        if (!networkOk && !gpsOk) {
            Toast.makeText(requireContext(), MSG_LOCATION_ENABLE, Toast.LENGTH_LONG).show();
            return;
        }

        requestingFreshLocation = true;
        hasFreshLocationFix = false;
        pendingLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (!isAdded() || mapLibreMap == null) return;
                if (shouldDiscardIncomingLocation(location)) {
                    return;
                }
                if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
                    lastLiveGpsElapsedRealtime = SystemClock.elapsedRealtime();
                }
                recordAcceptedLocation(location);
                // 首包后不要 removeUpdates，否则走动不再回调，坐标会“冻住”。
                cancelLocationTimeout();
                hasFreshLocationFix = true;
                requestingFreshLocation = false;
                cancelLocationTimeout();
                showLocationMarker(location);
            }
        };

        boolean hasRegisteredProvider = false;
        if (networkOk) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME_MS, LOCATION_UPDATE_MIN_DISTANCE_M,
                        pendingLocationListener, Looper.getMainLooper());
                hasRegisteredProvider = true;
            } catch (SecurityException ignored) {
            }
        }
        // GPS 仅在具备 FINE 权限时请求，避免部分机型因精确权限缺失导致整体失败。
        if (gpsOk && hasFineLocationPermission()) {
            try {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME_MS, LOCATION_UPDATE_MIN_DISTANCE_M,
                        pendingLocationListener, Looper.getMainLooper());
                hasRegisteredProvider = true;
            } catch (SecurityException ignored) {
            }
        }
        if (!hasRegisteredProvider) {
            requestingFreshLocation = false;
            hasFreshLocationFix = false;
            pendingLocationListener = null;
            return;
        }

        locationTimeoutRunnable = () -> {
            if (!isAdded() || !isResumed() || mapLibreMap == null || pendingLocationListener == null) return;
            if (!hasFreshLocationFix) {
                Toast.makeText(requireContext(), MSG_LOCATION_TIMEOUT, Toast.LENGTH_LONG).show();
            }
        };
        locationHandler.postDelayed(locationTimeoutRunnable, LOCATION_TIMEOUT_MS);
    }

    private void stopPendingLocationUpdates(@Nullable LocationManager locationManager) {
        cancelLocationTimeout();
        if (locationManager == null || pendingLocationListener == null) return;
        try {
            locationManager.removeUpdates(pendingLocationListener);
        } catch (Exception ignored) {
        }
        pendingLocationListener = null;
        requestingFreshLocation = false;
        hasFreshLocationFix = false;
    }

    private void cancelLocationTimeout() {
        if (locationTimeoutRunnable != null) {
            locationHandler.removeCallbacks(locationTimeoutRunnable);
            locationTimeoutRunnable = null;
        }
    }

    private void showLocationMarker(@NonNull Location location) {
        if (mapLibreMap == null || !isAdded()) return;
        currentLocationProviderLabel = normalizeLocationProviderLabel(location.getProvider());
        hasCurrentAltitude = false;
        currentAltitudeMeters = 0d;
        tryAssignAltitudeFields(location);
        // 网络定位点不再用 GPS lastKnown 补高程，否则类型已是 network 却仍显示 GPS 椭球高，易误解。
        if (!hasCurrentAltitude
                && getContext() != null
                && hasFineLocationPermission()
                && !LocationManager.NETWORK_PROVIDER.equals(location.getProvider())) {
            try {
                LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                enrichAltitudeFromGpsLastKnown(location, lm);
            } catch (Exception ignored) {
            }
        }
        LatLng latLng = toMapLibreLatLng(location);
        currentLatLng = latLng;
        updateDisplayedLocationTarget(latLng);
        sendSelfLocationToGroupMaybe();
        maybeAutoRerouteOnDeviation(latLng);
    }

    /**
     * 从单次 {@link Location} 尽量取出椭球高。网络定位常无高程；部分机型 GPS 有高程但未置 {@link Location#hasAltitude()}。
     */
    private void tryAssignAltitudeFields(@NonNull Location loc) {
        if (loc.hasAltitude()) {
            hasCurrentAltitude = true;
            currentAltitudeMeters = loc.getAltitude();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && loc.hasVerticalAccuracy()) {
            float v = loc.getVerticalAccuracyMeters();
            if (v > 0f && v < 500f) {
                double a = loc.getAltitude();
                if (!Double.isNaN(a)) {
                    hasCurrentAltitude = true;
                    currentAltitudeMeters = a;
                    return;
                }
            }
        }
        String provider = loc.getProvider();
        if (provider != null
                && (LocationManager.GPS_PROVIDER.equals(provider)
                || provider.toLowerCase(Locale.ROOT).contains("gps"))) {
            if (loc.hasAccuracy() && loc.getAccuracy() > 0f && loc.getAccuracy() < 200f) {
                double a = loc.getAltitude();
                if (!Double.isNaN(a)) {
                    hasCurrentAltitude = true;
                    currentAltitudeMeters = a;
                }
            }
        }
    }

    /**
     * 当前 fix 无高程且非 {@link LocationManager#NETWORK_PROVIDER} 时，用时间上较近、位置接近的 GPS lastKnown 补高程。
     * 网络点不调用，避免「类型 network」仍显示 GPS 高程。
     */
    private void enrichAltitudeFromGpsLastKnown(@NonNull Location primary, @Nullable LocationManager lm) {
        if (lm == null) {
            return;
        }
        Location gps;
        try {
            gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            return;
        }
        if (gps == null) {
            return;
        }
        if (System.currentTimeMillis() - gps.getTime() > 300_000L) {
            return;
        }
        float[] dist = new float[1];
        Location.distanceBetween(
                primary.getLatitude(), primary.getLongitude(),
                gps.getLatitude(), gps.getLongitude(), dist);
        if (dist[0] > 400f) {
            return;
        }
        tryAssignAltitudeFields(gps);
    }

    private void updateDisplayedLocationTarget(@NonNull LatLng target) {
        updateNavigationRouteProgress(target);
        if (displayedLatLng == null || distanceMeters(displayedLatLng, target) > LOCATION_SMOOTH_SNAP_DISTANCE_M) {
            locationHandler.removeCallbacks(locationSmoothRunnable);
            displayedLatLng = target;
            locationSmoothFrom = null;
            locationSmoothTarget = null;
            renderDisplayedLocation();
            return;
        }
        locationHandler.removeCallbacks(locationSmoothRunnable);
        locationSmoothFrom = displayedLatLng;
        locationSmoothTarget = target;
        locationSmoothStartMs = System.currentTimeMillis();
        locationHandler.post(locationSmoothRunnable);
    }

    private void renderDisplayedLocation() {
        if (displayedLatLng == null || mapLibreMap == null || !isAdded()) return;
        if ((followSelfCamera && !navFollowSuspendedByGesture) || !hasCenteredOnFirstFix) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(displayedLatLng, 16.0));
            hasCenteredOnFirstFix = true;
        }
        if (ivHeading != null) {
            ivHeading.setVisibility(View.VISIBLE);
            bringSelfLocationMarkerAboveMapOverlays();
        }
        updateCurrentCoordView(displayedLatLng);
        updateHeadingViewPosition();
        updateHeadingRotationForNavigationRoute();
        scheduleDebouncedMarkConnectionLines();
    }

    @NonNull
    private static LatLng interpolateLatLng(@NonNull LatLng from, @NonNull LatLng to, float t) {
        double lat = from.getLatitude() + (to.getLatitude() - from.getLatitude()) * t;
        double lng = from.getLongitude() + (to.getLongitude() - from.getLongitude()) * t;
        return new LatLng(lat, lng);
    }

    private static float distanceMeters(@NonNull LatLng a, @NonNull LatLng b) {
        float[] result = new float[1];
        Location.distanceBetween(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude(), result);
        return result[0];
    }

    /**
     * 统一系统定位坐标到 MapLibre(WGS84)：
     * - GPS_PROVIDER 直接按 WGS84 使用；
     * - NETWORK/PASSIVE 常见为 GCJ，先转 WGS84，避免在非高德底图上出现百米级偏移。
     */
    @NonNull
    private static LatLng toMapLibreLatLng(@NonNull Location location) {
        String provider = location.getProvider();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        /*if (LocationManager.NETWORK_PROVIDER.equals(provider)
                || LocationManager.PASSIVE_PROVIDER.equals(provider)) {
            double[] w = com.qycx.mapnav.GcjWgs84Transform.gcj02ToWgs84(lat, lng);
            return new LatLng(w[0], w[1]);
        }*/
        return new LatLng(lat, lng);
    }

    @SuppressLint("DefaultLocale")
    private void updateCurrentCoordView(@Nullable LatLng latLng) {
        if (tvCurrentCoord == null) return;
        if (latLng == null) {
            tvCurrentCoord.setText("类型: --  经度: --  纬度: --  高程: --");
            return;
        }
        if (hasCurrentAltitude) {
            tvCurrentCoord.setText(String.format(Locale.getDefault(),
                    "类型: %s  经度: %.6f  纬度: %.6f  高程: %.1fm",
                    currentLocationProviderLabel,
                    latLng.getLongitude(), latLng.getLatitude(), currentAltitudeMeters));
        } else {
            tvCurrentCoord.setText(String.format(Locale.getDefault(),
                    "类型: %s  经度: %.6f  纬度: %.6f  高程: --",
                    currentLocationProviderLabel,
                    latLng.getLongitude(), latLng.getLatitude()));
        }
        applyMapOverlayTextSizes();
    }

    @NonNull
    private static String normalizeLocationProviderLabel(@Nullable String provider) {
        if (TextUtils.isEmpty(provider)) {
            return "--";
        }
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            return "gps";
        }
        if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
            return "network";
        }
        if (LocationManager.PASSIVE_PROVIDER.equals(provider)) {
            return "passive";
        }
        return provider.toLowerCase(Locale.ROOT);
    }

    private void updateHeadingViewPosition() {
        LatLng markerLatLng = displayedLatLng != null ? displayedLatLng : currentLatLng;
        if (mapLibreMap == null || ivHeading == null || markerLatLng == null || ivHeading.getVisibility() != View.VISIBLE) return;
        PointF p = mapLibreMap.getProjection().toScreenLocation(markerLatLng);
        if (p == null) return;
        int w = ivHeading.getWidth();
        int h = ivHeading.getHeight();
        if (w == 0 || h == 0) {
            if (headingLayoutRetrySeq < MAX_HEADING_LAYOUT_RETRIES && mapView != null) {
                mapView.removeCallbacks(headingLayoutRetryRunnable);
                mapView.postDelayed(headingLayoutRetryRunnable, 48);
            }
            return;
        }
        headingLayoutRetrySeq = 0;
        if (mapView != null) {
            mapView.removeCallbacks(headingLayoutRetryRunnable);
        }
        ivHeading.setX(p.x - w / 2f);
        ivHeading.setY(p.y - h / 2f);
        bringSelfLocationMarkerAboveMapOverlays();
        updateHeadingRotationForNavigationRoute();
    }

    private void updateHeadingRotationForNavigationRoute() {
        if (ivHeading == null || ivHeading.getVisibility() != View.VISIBLE) return;
        if (!isInAppNavigationActive()) return;
        LatLng markerLatLng = displayedLatLng != null ? displayedLatLng : currentLatLng;
        if (markerLatLng == null) return;
        Float bearing = estimateRouteBearingAt(markerLatLng);
        if (bearing != null) {
            ivHeading.setRotation(bearing);
        }
    }

    @Nullable
    private Float estimateRouteBearingAt(@NonNull LatLng cur) {
        if (navigationRouteWgs84.size() < 2) return null;
        int bestSegment = -1;
        double bestMeters = Double.MAX_VALUE;
        for (int i = 0; i < navigationRouteWgs84.size() - 1; i++) {
            LatLng a = navigationRouteWgs84.get(i);
            LatLng b = navigationRouteWgs84.get(i + 1);
            double meters = distanceToRouteSegmentMeters(cur, a, b);
            if (meters < bestMeters) {
                bestMeters = meters;
                bestSegment = i;
            }
        }
        if (bestSegment < 0) return null;
        LatLng a = navigationRouteWgs84.get(bestSegment);
        LatLng b = navigationRouteWgs84.get(bestSegment + 1);
        return bearingBetween(a, b);
    }

    private static float bearingBetween(@NonNull LatLng from, @NonNull LatLng to) {
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        double dLng = Math.toRadians(to.getLongitude() - from.getLongitude());
        double y = Math.sin(dLng) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2)
                - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLng);
        float bearing = (float) Math.toDegrees(Math.atan2(y, x));
        if (bearing < 0f) bearing += 360f;
        return bearing;
    }

    private void handleNavigationMapTouch(int action) {
        if (!followSelfCamera || !isInAppNavigationActive()) return;
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            navFollowSuspendedByGesture = true;
            locationHandler.removeCallbacks(resumeNavFollowRunnable);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            locationHandler.removeCallbacks(resumeNavFollowRunnable);
            locationHandler.postDelayed(resumeNavFollowRunnable, NAV_FOLLOW_RESUME_DELAY_MS);
        }
    }

    private void startHeadingUpdates() {
        if (sensorManager == null || rotationVectorSensor == null || headingListener != null) return;
        headingListener = new SensorEventListener() {
            private final float[] rotationMatrix = new float[9];
            private final float[] orientation = new float[3];

            @Override
            public void onSensorChanged(SensorEvent event) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0) azimuth += 360f;
                selfHeadingDeg = azimuth;
                if (ivHeading != null && ivHeading.getVisibility() == View.VISIBLE && !isInAppNavigationActive()) {
                    ivHeading.setRotation(azimuth);
                }
                if (compassTrackingEnabled && compassTopRotatingLayer != null) {
                    // 指针固定指北，罗盘盘面随方向转动
                    compassTopRotatingLayer.setRotation(-azimuth);
                }
                sendSelfLocationToGroupMaybe();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(headingListener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void stopHeadingUpdates() {
        if (sensorManager != null && headingListener != null) {
            sensorManager.unregisterListener(headingListener);
        }
        headingListener = null;
    }

    private void toggleCompassPanel() {
        if (compassTrackingEnabled) {
            compassTrackingEnabled = false;
            if (compassTopRotatingLayer != null) {
                compassTopRotatingLayer.setRotation(0f);
            }
            if (layoutCompassRotatingLayer != null) {
                layoutCompassRotatingLayer.setRotation(0f);
            }
            if (ivCompassBtn != null) ivCompassBtn.setAlpha(0.8f);
        } else {
            compassTrackingEnabled = true;
            if (compassTopRotatingLayer != null) {
                compassTopRotatingLayer.setRotation(-selfHeadingDeg);
            }
            if (layoutCompassRotatingLayer != null) {
                layoutCompassRotatingLayer.setRotation(-selfHeadingDeg);
            }
            if (ivCompassBtn != null) ivCompassBtn.setAlpha(1f);
        }
    }

    private void locateToCurrentPosition() {
        if (!isAdded() || mapLibreMap == null) return;
        if (mapView != null) {
            mapView.removeCallbacks(scheduledLocateAllMembersRunnable);
        }
        // 尚未收到 onLocationChanged 时，用 lastKnown 立刻给出可飞过去的坐标。
        if (currentLatLng == null && getContext() != null && hasAnyLocationPermission()) {
            LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            Location last = getBestLastKnownLocation(lm);
            if (last != null) {
                showLocationMarker(last);
            }
        }
        if (currentLatLng != null) {
            // 关闭跟随时 displayedLatLng 可能仍在平滑中；只把相机对准 currentLatLng 会导致“定位自己”看起来无效或朝向标错位。
            locationHandler.removeCallbacks(locationSmoothRunnable);
            displayedLatLng = currentLatLng;
            locationSmoothFrom = null;
            locationSmoothTarget = null;
            // moveCamera 立即生效；animate 在部分机型/与全员 bounds 衔接时易被感知为“没反应”。
            mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f));
            hasCenteredOnFirstFix = true;
            updateCurrentCoordView(displayedLatLng);
            if (mapView != null) {
                mapView.post(() -> {
                    if (!isAdded() || mapLibreMap == null) return;
                    updateHeadingViewPosition();
                    flushMarkConnectionLinesNow();
                    triggerMapViewportRefreshIfNeeded();
                });
                mapView.postDelayed(() -> {
                    if (!isAdded() || mapLibreMap == null) return;
                    updateHeadingViewPosition();
                    flushMarkConnectionLinesNow();
                    triggerMapViewportRefreshIfNeeded();
                }, 220);
            }
            return;
        }
        tryLocateAndMark();
    }

    private void zoomMapBy(double delta) {
        if (!isAdded() || mapLibreMap == null) return;
        CameraPosition cp = mapLibreMap.getCameraPosition();
        double cur = cp != null ? cp.zoom : 10.0;
        double target = cur + delta;
        if (target < LOCAL_XYZ_MIN_ZOOM) target = LOCAL_XYZ_MIN_ZOOM;
        if (target > MAP_MAX_ZOOM) target = MAP_MAX_ZOOM;
        mapLibreMap.animateCamera(CameraUpdateFactory.zoomTo(target), 180);
    }

    private void locateToAllMembers() {
        if (!isAdded() || mapLibreMap == null) return;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int cnt = 0;

        if (currentLatLng != null) {
            boundsBuilder.include(currentLatLng);
            cnt++;
        }
        for (RemoteLoc r : remoteLocs.values()) {
            if (r == null || r.latLng == null) continue;
            boundsBuilder.include(r.latLng);
            cnt++;
        }
        if (cnt == 0) {
            Toast.makeText(requireContext(), "暂无可定位成员", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cnt == 1 && currentLatLng != null) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0));
            return;
        }
        try {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), dp(72)));
        } catch (Exception e) {
            // 极少数情况下 bounds 计算失败时，回退到第一个可用点。
            if (currentLatLng != null) {
                mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0));
            }
        }
    }

    /**
     * 将地图中心移到指定成员最近一次同步的位置；本人走定位自己；无位置时提示。
     */
    private void locateToPeerOnMap(long userId) {
        if (!isAdded() || mapLibreMap == null) return;
        ensureSelfUserId();
        if (userId > 0 && userId == selfUserId) {
            locateToCurrentPosition();
            return;
        }
        RemoteLoc r = remoteLocs.get(userId);
        if (r != null && r.latLng != null) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(r.latLng, 16.0));
            return;
        }
        Toast.makeText(requireContext(), "暂无该成员位置，请稍候地图同步", Toast.LENGTH_SHORT).show();
    }

    private void showBattleMarkPicker() {
        if (!isAdded()) return;
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        Context ctx = requireContext();
        final int[] tempSelected = {selectedBattleMarkIndex};
        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_battle_mark_pick, null, false);
        RecyclerView rv = content.findViewById(R.id.rvBattleMarkTypes);
        ViewGroup.LayoutParams rvLp = rv.getLayoutParams();
        rvLp.height = computeActiveMembersScrollMaxHeightPx();
        rv.setLayoutParams(rvLp);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        rv.setHasFixedSize(true);
        final AlertDialog[] dlgRef = new AlertDialog[1];
        BattleMarkTypePickAdapter adapter = new BattleMarkTypePickAdapter(tempSelected[0], which -> {
            tempSelected[0] = which;
            updateBattleMarkConfirmEnabled(content, tempSelected[0]);
        });
        rv.setAdapter(adapter);
        DividerItemDecoration div = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
        Drawable divDrawable = ContextCompat.getDrawable(ctx, R.drawable.bg_map_dialog_list_divider);
        if (divDrawable != null) {
            div.setDrawable(divDrawable);
        }
        rv.addItemDecoration(div);

        TextView btnCancel = content.findViewById(R.id.btnBattleMarkCancel);
        TextView btnConfirm = content.findViewById(R.id.btnBattleMarkConfirm);
        btnCancel.setOnClickListener(v -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            if (tempSelected[0] < 0) return;
            selectedBattleMarkIndex = tempSelected[0];
            waitingForMarkPlacement = true;
            editingBattleMarkId = null;
            Toast.makeText(ctx, "已选中，请点击地图放置标注", Toast.LENGTH_SHORT).show();
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });

        dlgRef[0] = createMapStyledDialog(content, () -> {
            updateBattleMarkConfirmEnabled(content, tempSelected[0]);
            rv.post(() -> {
                rv.requestLayout();
                RecyclerView.Adapter<?> a = rv.getAdapter();
                if (a != null) {
                    a.notifyDataSetChanged();
                }
            });
        });
        dlgRef[0].show();
    }

    private void updateBattleMarkConfirmEnabled(View battleDialogContent, int selectedIndex) {
        TextView btnConfirm = battleDialogContent.findViewById(R.id.btnBattleMarkConfirm);
        if (btnConfirm == null) return;
        boolean ok = selectedIndex >= 0;
        btnConfirm.setEnabled(ok);
        btnConfirm.setAlpha(ok ? 1f : 0.45f);
    }

    private final class BattleMarkTypePickAdapter extends RecyclerView.Adapter<BattleMarkTypePickAdapter.VH> {
        private int selectedIndex;
        private final IntConsumer onSelectionChanged;

        BattleMarkTypePickAdapter(int initialSelected, IntConsumer onSelectionChanged) {
            this.selectedIndex = initialSelected >= 0 && initialSelected < BATTLE_MARK_TYPE_COUNT ? initialSelected : -1;
            this.onSelectionChanged = onSelectionChanged;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_battle_mark_type, parent, false);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp != null) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            h.iv.setImageResource(getBattleMarkIconRes(position));
            h.iv.setContentDescription("作战标注第" + (position + 1) + "项");
            boolean sel = position == selectedIndex;
            h.itemView.setBackgroundColor(sel ? Color.parseColor("#E8F2EC") : Color.WHITE);
            h.itemView.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                selectedIndex = pos;
                notifyDataSetChanged();
                onSelectionChanged.accept(selectedIndex);
            });
        }

        @Override
        public int getItemCount() {
            return BATTLE_MARK_TYPE_COUNT;
        }

        static final class VH extends RecyclerView.ViewHolder {
            final ImageView iv;

            VH(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.ivBattleMarkTypeIcon);
            }
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 活动组成员列表区域最大高度（px）。成员多时在此区域内纵向滚动；略压高度避免弹窗过长占屏。
     * 约屏高 40%，上限约 360dp、下限约 180dp。
     */
    private int computeActiveMembersScrollMaxHeightPx() {
        int screenH = requireContext().getResources().getDisplayMetrics().heightPixels;
        int byRatio = (int) (screenH * 0.40f);
        return Math.max(dp(180), Math.min(byRatio, dp(360)));
    }

    private AlertDialog createMapStyledDialog(@NonNull View content) {
        return createMapStyledDialog(content, null);
    }

    private AlertDialog createMapStyledDialog(@NonNull View content, @Nullable Runnable onShowExtra) {
        prepareMapDialogContentRoot(content);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.MapStyledDialogTheme)
                .setView(content)
                .create();
        dialog.setOnShowListener(d -> {
            applyMapStyledDialogWindow((AlertDialog) d, content);
            if (onShowExtra != null) {
                onShowExtra.run();
            }
        });
        return dialog;
    }

    private void prepareMapDialogContentRoot(@NonNull View content) {
        content.setClipToOutline(true);
        content.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    }

    /** 与作战标注弹窗一致：透明窗口 + 圆角内容 + 宽度 84%。 */
    private void applyMapStyledDialogWindow(@Nullable AlertDialog ad, @NonNull View content) {
        if (ad == null || !isAdded()) return;
        Window w = ad.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View decor = w.getDecorView();
            if (decor != null) {
                decor.setBackgroundResource(android.R.color.transparent);
            }
            View panel = w.findViewById(android.R.id.content);
            if (panel != null) {
                panel.setBackgroundResource(android.R.color.transparent);
            }
            clearDialogWrapperBackgrounds(content);
            WindowManager.LayoutParams lp = w.getAttributes();
            lp.width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.84f);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            w.setAttributes(lp);
        }
    }

    /** 去掉 AlertDialog 内层默认白底方角容器，避免圆角外漏白。 */
    private void clearDialogWrapperBackgrounds(@NonNull View content) {
        ViewParent parent = content.getParent();
        while (parent instanceof View) {
            View wrapper = (View) parent;
            wrapper.setBackgroundResource(android.R.color.transparent);
            wrapper.setPadding(0, 0, 0, 0);
            if (wrapper.getId() == android.R.id.content) {
                break;
            }
            parent = wrapper.getParent();
        }
    }

    @NonNull
    private View inflateMapDialogShell() {
        return LayoutInflater.from(requireContext()).inflate(R.layout.dialog_map_shell, null, false);
    }

    private void bindMapDialogTitle(@NonNull View content, @NonNull CharSequence title) {
        TextView tv = content.findViewById(R.id.tvMapDialogTitle);
        if (tv != null) tv.setText(title);
    }

    private void bindMapDialogMessage(@NonNull View content, @Nullable CharSequence message) {
        TextView tv = content.findViewById(R.id.tvMapDialogMessage);
        if (tv == null) return;
        if (TextUtils.isEmpty(message)) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(message);
        }
    }

    private void setMapDialogCustomContent(@NonNull View shell, @NonNull View custom) {
        FrameLayout slot = shell.findViewById(R.id.layoutMapDialogCustom);
        if (slot == null) return;
        slot.setVisibility(View.VISIBLE);
        slot.removeAllViews();
        slot.addView(custom);
    }

    private void hideMapDialogFooter(@NonNull View shell) {
        View footer = shell.findViewById(R.id.layoutMapDialogFooter);
        View divider = shell.findViewById(R.id.viewMapDialogFooterDivider);
        if (footer != null) footer.setVisibility(View.GONE);
        if (divider != null) divider.setVisibility(View.GONE);
    }

    private void showMapDialogSingleFooter(@NonNull View shell, @NonNull CharSequence label) {
        View footer = shell.findViewById(R.id.layoutMapDialogFooter);
        View divider = shell.findViewById(R.id.viewMapDialogFooterDivider);
        TextView btnSingle = shell.findViewById(R.id.btnMapDialogSingle);
        if (footer != null) footer.setVisibility(View.GONE);
        if (divider != null) divider.setVisibility(View.VISIBLE);
        if (btnSingle != null) {
            btnSingle.setVisibility(View.VISIBLE);
            btnSingle.setText(label);
        }
    }

    private AlertDialog showMapConfirmDialog(
            @NonNull CharSequence title,
            @Nullable CharSequence message,
            @NonNull CharSequence confirmText,
            @NonNull CharSequence cancelText,
            @Nullable Runnable onConfirm,
            @Nullable Runnable onCancel) {
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, title);
        bindMapDialogMessage(content, message);
        TextView btnCancel = content.findViewById(R.id.btnMapDialogCancel);
        TextView btnConfirm = content.findViewById(R.id.btnMapDialogConfirm);
        btnCancel.setText(cancelText);
        btnConfirm.setText(confirmText);
        AlertDialog dialog = createMapStyledDialog(content);
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (onCancel != null) onCancel.run();
        });
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });
        dialog.show();
        return dialog;
    }

    private AlertDialog showMapAckDialog(
            @NonNull CharSequence title,
            @NonNull CharSequence message,
            @NonNull CharSequence buttonText) {
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, title);
        bindMapDialogMessage(content, message);
        showMapDialogSingleFooter(content, buttonText);
        AlertDialog dialog = createMapStyledDialog(content);
        TextView btnSingle = content.findViewById(R.id.btnMapDialogSingle);
        if (btnSingle != null) {
            btnSingle.setOnClickListener(v -> dialog.dismiss());
        }
        dialog.show();
        return dialog;
    }

    private void addMapDialogActionRow(@NonNull View shell, @NonNull CharSequence label, @Nullable Runnable onClick) {
        LinearLayout actions = shell.findViewById(R.id.layoutMapDialogExtraActions);
        if (actions == null) return;
        actions.setVisibility(View.VISIBLE);
        if (actions.getChildCount() > 0) {
            View divider = new View(requireContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));
            divider.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.map_dialog_divider));
            actions.addView(divider);
        }
        TextView row = (TextView) LayoutInflater.from(requireContext())
                .inflate(R.layout.item_map_dialog_action, actions, false);
        row.setText(label);
        actions.addView(row);
        row.setOnClickListener(v -> {
            if (onClick != null) onClick.run();
        });
    }

    /** 弹窗内容区主操作按钮文字样式（绿字、16sp），用于成员列表等行内操作。 */
    private void styleMapDialogContentActionButton(Button b) {
        b.setAllCaps(false);
        b.setTextColor(Color.parseColor("#00B578"));
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        b.setMinHeight(dp(44));
        b.setBackgroundResource(android.R.color.transparent);
    }

    @Override
    public void onStart() {
        super.onStart();
        syncCompassTrackingFromHub(compassTrackingEnabled);
        if (mapView != null) mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
        addMapLayoutChangeListener();
        triggerMapViewportRefreshIfNeeded();
        // Hub 同步（作战标注/指北）较重，放到下一帧避免与 mapView.onResume、首帧布局抢同一帧主线程。
        if (mapLibreMap != null && mapView != null) {
            mapView.post(() -> {
                if (!isAdded() || mapLibreMap == null) {
                    return;
                }
                trySyncBattleMarksAndCompassFromHub();
            });
        }
        if (shouldRunVideoMirror()) {
            startVideoMirrorLoop();
        }
        startHeadingUpdates();
        locationHandler.removeCallbacks(onlineCleanupRunnable);
        locationHandler.postDelayed(onlineCleanupRunnable, 2000L);
        resolveActiveGroupSelectionMaybe(true);
        startActiveGroupMeetingStatusMonitor();
        tryRequestMarkSyncSnapshot();
        locationHandler.postDelayed(this::tryRequestMarkSyncSnapshot, 2000L);
        locationHandler.postDelayed(this::tryRequestMarkSyncSnapshot, 6000L);
        locationHandler.postDelayed(this::tryRestoreLocalMarkLinksIfNeeded, 6500L);
        if (mapLibreMap != null && getContext() != null && (currentLatLng == null || pendingLocationListener == null)) {
            boolean ok = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (ok) {
                tryLocateAndMark();
            }
        }
        // 仅在本轮 mapView 首次 onResume 时自动定位全员；Tab 切回地图不再拉镜头。
        if (mapView != null) {
            mapViewResumeCount++;
            if (mapViewResumeCount == 1) {
                mapView.removeCallbacks(scheduledLocateAllMembersRunnable);
                mapView.postDelayed(scheduledLocateAllMembersRunnable, 3000);
            }
        }
        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            if (mapView != null) {
                mapView.post(main::requestMapLocKeywordIfNeeded);
            } else {
                main.requestMapLocKeywordIfNeeded();
            }
        }
    }

    @Override
    public void onPause() {
        removeMapLayoutChangeListener();
        stopVideoMirrorLoop();
        stopHeadingUpdates();
        cancelLocationTimeout();
        locationHandler.removeCallbacks(onlineCleanupRunnable);
        stopActiveGroupMeetingStatusMonitor();
        stopSyntheticRemoteMovingPointsTest();
        if (mapView != null) {
            mapView.removeCallbacks(scheduledLocateAllMembersRunnable);
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapView != null) mapView.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        unregisterMarkLineFilterInstance();
        if (mapView != null) {
            mapView.removeCallbacks(scheduledLocateAllMembersRunnable);
        }
        removeMapLayoutChangeListener();
        stopInAppNavigation();        dismissOfflineDownloadDialog();
        dismissOfflineDataProgressDialog();
        stopImageryMbtilesServer();
        waitingForNavDestPick = false;
        amapOfflineMapMgrKeepalive = null;
        stopVideoMirrorLoop();
        locationHandler.removeCallbacks(locationSmoothRunnable);
        displayedLatLng = null;
        locationSmoothFrom = null;
        locationSmoothTarget = null;
        requestingFreshLocation = false;
        LocationManager lm = null;
        Context ctx = getContext();
        if (ctx != null) {
            lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
        stopPendingLocationUpdates(lm);
        stopHeadingUpdates();
        sensorManager = null;
        rotationVectorSensor = null;
        ivCompass = null;
        ivLocateBtn = null;
        layoutRightToolsRoot = null;
        layoutLocatePanel = null;
        tvLocateMenuFollow = null;
        ivNavBtn = null;
        ivLayerBtn = null;
        tvLayerOfflineDataAdd = null;
        layoutLayerBarRoot = null;
        layoutLayerPanel = null;
        pendingNavCityName = null;
        cbLayerVector = null;
        cbLayerImagery = null;
        cbLayerAnnotation = null;
        cbLayerOffline = null;
        cbLayerOfflineImagery = null;
        cbLayerOfflineAnnotation = null;
        layoutNavBar = null;
        tvNavInstruction = null;
        btnNavStop = null;
        ivCompassBtn = null;
        ivZoomInBtn = null;
        ivZoomOutBtn = null;
        ivMarkBtn = null;
        ivMarkLineFilterBtn = null;
        ivMarkNoteListBtn = null;
        ivHeading = null;
        ivCompassNeedleLarge = null;
        layoutCompassPanel = null;
        layoutCompassRotatingLayer = null;
        compassTopRotatingLayer = null;
        layoutFloatingVideoLegacy = null;
        tvGroupStats = null;
        viewActiveGrpMeetingLamp = null;
        tvActiveGrpMeetingEntry = null;
        activeGroupMeetingId = 0;
        activeGroupMeetingRunning = false;
        removeRemoteMarkNoticeView();
        tvCurrentCoord = null;
        if (floatingVideoPanel != null) {
            floatingVideoPanel.release();
            floatingVideoPanel = null;
        }
        if (systemOverlayTestWindow != null) {
            systemOverlayTestWindow.dismiss();
            systemOverlayTestWindow = null;
        }
        floatingVideoTargetId = 0;
        remotePreviewByUser.clear();
        expandedPreviewUserId = -1;
        if (remoteCameraStrip != null) remoteCameraStrip.removeAllViews();
        remoteCameraStripScroll = null;
        remoteCameraStrip = null;
        markLinks.clear();
        clearMarkSyncCache();
        hasCurrentAltitude = false;
        currentLocationProviderLabel = "--";
        lastLiveGpsElapsedRealtime = -1L;
        lastAcceptedLocationTimeMs = -1L;
        lastAcceptedElapsedRealtimeNanos = -1L;
        lastAcceptedSpeedMps = 0f;
        currentLatLng = null;
        locationHandler.removeCallbacks(remoteLocationSmoothRunnable);
        locationHandler.removeCallbacks(syntheticRemoteStepRunnable);
        if (lineOverlayView != null) {
            ViewGroup parent = (ViewGroup) lineOverlayView.getParent();
            if (parent != null) parent.removeView(lineOverlayView);
            lineOverlayView = null;
        }
        locationHandler.removeCallbacks(onlineCleanupRunnable);
        stopActiveGroupMeetingStatusMonitor();
        recentActiveOnlineTs.clear();
        for (RemoteLoc r : remoteLocs.values()) {
            if (r.arrow != null) r.arrow.setVisibility(View.GONE);
            if (r.name != null) r.name.setVisibility(View.GONE);
            r.displayLatLng = null;
            r.smoothFrom = null;
            r.smoothTarget = null;
        }
        remoteLocs.clear();
        remoteUserDisplayNameCache.clear();
        clearBattleMarks();
        battleOverlayLayoutRetrySeq = 0;
        remoteOverlayLayoutRetrySeq = 0;
        headingLayoutRetrySeq = 0;
        isCameraMovingForMarkLines = false;
        pendingMarkLinesAfterCameraIdle = false;
        navFollowSuspendedByGesture = false;
        if (mapView != null) {
            mapView.removeCallbacks(battleOverlayLayoutRetryRunnable);
            mapView.removeCallbacks(remoteOverlayRetryRunnable);
            mapView.removeCallbacks(headingLayoutRetryRunnable);
        }
        locationHandler.removeCallbacks(debouncedMarkLinesRunnable);
        locationHandler.removeCallbacks(resumeNavFollowRunnable);
        if (mapLibreMap != null && onCameraMoveListener != null) {
            mapLibreMap.removeOnCameraMoveListener(onCameraMoveListener);
        }
        onCameraMoveListener = null;
        if (mapLibreMap != null && onCameraIdleListener != null) {
            mapLibreMap.removeOnCameraIdleListener(onCameraIdleListener);
        }
        onCameraIdleListener = null;
        locationHandler.removeCallbacks(mapLibreMarkLinkLabelsUpdateRunnable);
        pendingMapLibreMarkLinkLabelsUpdate = false;
        removeMapLibreMarkLinkLabelsLayer();
        removeMapLibreBattleMarkLayers();
        removeMapLibreMarkConnectionLayer();
        mapLibreMap = null;
        if (mapView != null) {
            mapView.onDestroy();
            mapView = null;
        }
        super.onDestroyView();
    }

    private void sendSelfLocationMaybe() {
        if (!GROUP_LOCATION_PUSH_ENABLED) return;
        if (!isAdded() || currentLatLng == null) return;
        long now = System.currentTimeMillis();
        if (now - lastLocSendMs < LOCATION_SEND_INTERVAL_MS) return;
        lastLocSendMs = now;
        ensureSelfUserId();
        Set<Long> peers = loadPeersForMapPush();
        if (peers.isEmpty()) return;
        String payload = String.format(Locale.US, "%s,%f,%f,%f,%d",
                LOC_PREFIX, currentLatLng.getLatitude(), currentLatLng.getLongitude(), selfHeadingDeg, now);
        for (Long peer : peers) {
            if (peer == null || peer == 0 || peer == selfUserId) continue;
            sendHtmlToPeer(peer, payload);
        }
    }

    /** 经定位服务器向活动组广播本机坐标（含朝向），locStr 在 myMapExt 内按开关加密。 */
    private void sendSelfLocationToGroupMaybe() {
        if (!MapLocSettings.canShareLocation()) return;
        if (!isAdded() || currentLatLng == null) return;
        long now = System.currentTimeMillis();
        if (now - lastLocSendMs < LOCATION_SEND_INTERVAL_MS) return;
        lastLocSendMs = now;
        sendSelfLocationToGroupPayload(now);
    }

    /** 口令就绪后立即上报，不受 2 秒节流限制。 */
    public void flushSelfLocationToGroupNow() {
        if (!MapLocSettings.canShareLocation()) return;
        if (!isAdded() || currentLatLng == null) return;
        long now = System.currentTimeMillis();
        lastLocSendMs = now;
        sendSelfLocationToGroupPayload(now);
    }

    private void sendSelfLocationToGroupPayload(long now) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.m_pMapExt == null) return;
        String payload = String.format(Locale.US, "%s,%f,%f,%f,%d",
                LOC_PREFIX, currentLatLng.getLatitude(), currentLatLng.getLongitude(), selfHeadingDeg, now);
        long[] grpIds = getTaskGrp();
        if (grpIds == null) return;
        for (long grpId : grpIds) {
            if (grpId <= 0 || !isSelfMemberOfGroup(grpId)) continue;
               pProcInfo.m_pMapExt.sendLocation(null, payload, grpId);
        }
    }

    /** 经融合中心向活动组同步作战标注/连线（add/del 载荷格式不变）。 */
    private void sendMarkPayloadToActiveGroups(@NonNull String payload) {
        if (TextUtils.isEmpty(payload)) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.m_pMapExt == null) return;
        long[] grpIds = getTaskGrp();
        if (grpIds == null) return;
        for (long grpId : grpIds) {
            if (grpId <= 0 || !isSelfMemberOfGroup(grpId)) continue;
            pProcInfo.m_pMapExt.sendMark(payload, grpId);
        }
    }

    /** 接收活动组他人定位（已由 myMapExt 解密），更新地图标记。 */
    public void applyRemoteLocation(long userId, @NonNull String plainLocStr) {
        if (!isAdded() || userId <= 0) return;
        ensureSelfUserId();
        if (selfUserId > 0 && userId == selfUserId) return;
        applyRemoteLocationPlain(userId, plainLocStr);
    }

    /** 接收活动组他人同步注记/连线/晚登录快照（融合中心 hg 下发），更新地图。 */
    public void applyRemoteMark(long userId, @NonNull String markStr) {
        if (!isAdded() || userId <= 0 || markStr.isEmpty()) return;
        ensureSelfUserId();
        String trimmed = markStr.trim();
        if (trimmed.startsWith(MARK_SYNC_PREFIX)) {
            handleRemoteMarkSyncMsg(userId, trimmed);
            return;
        }
        if (selfUserId > 0 && userId == selfUserId) return;
        if (trimmed.startsWith(MARK_LINK_PREFIX)) {
            handleRemoteMarkLinkMsg(userId, trimmed);
            return;
        }
        handleRemoteMarkMsg(userId, trimmed);
    }

    /** 口令清空后，移除他人定位标记。 */
    public void clearRemoteLocations() {
        if (!isAdded()) return;
        stopSyntheticRemoteMovingPointsTest();
        for (Long id : new ArrayList<>(remoteLocs.keySet())) {
            if (id == null) continue;
            RemoteLoc r = remoteLocs.remove(id);
            if (r == null) continue;
            if (r.arrow != null) {
                ViewGroup p = (ViewGroup) r.arrow.getParent();
                if (p != null) p.removeView(r.arrow);
            }
            if (r.name != null) {
                ViewGroup p = (ViewGroup) r.name.getParent();
                if (p != null) p.removeView(r.name);
            }
        }
        updateRemoteOverlays(false);
        flushMarkConnectionLinesNow();
    }

    private void applyRemoteLocationPlain(long userId, @NonNull String text) {
        try {
            String[] arr = text.split(",");
            if (arr.length < 5) return;
            if (!LOC_PREFIX.equals(arr[0])) return;
            double lat = Double.parseDouble(arr[1]);
            double lng = Double.parseDouble(arr[2]);
            float heading = Float.parseFloat(arr[3]);
            long ts = Long.parseLong(arr[4]);

            RemoteLoc r = ensureRemoteLocEntry(userId, null);
            r.latLng = new LatLng(lat, lng);
            updateRemoteDisplayTarget(r, r.latLng);
            updateRemoteHeadingTarget(r, heading);
            r.ts = ts;
            markUserActiveOnline(userId);
            attachRemoteLocViewsIfNeeded(r);
            updateRemoteOverlays(true);
            maybeRefreshGroupStatsByActiveGroups();
            if (mapLibreMap == null && mapView != null) {
                mapView.post(this::refreshPendingRemoteOverlays);
            }
        } catch (Exception ignored) {
        }
    }

    private void refreshPendingRemoteOverlays() {
        if (!isAdded()) return;
        for (RemoteLoc r : remoteLocs.values()) {
            if (r == null) continue;
            attachRemoteLocViewsIfNeeded(r);
        }
        if (mapLibreMap != null) {
            updateRemoteOverlays(true);
        }
    }

    private void ensureSelfUserId() {
        if (selfUserId != 0) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.pMisCnt == null) return;
        selfUserId = pProcInfo.pMisCnt.idInfo.ui64Id;
    }

    private Set<Long> loadPeersForMapPush() {
        long now = System.currentTimeMillis();
        if (now - lastPeerReloadMs < 5000 && !cachedPeers.isEmpty()) return cachedPeers;
        lastPeerReloadMs = now;
        ensureSelfUserId();
        if (selfUserId == 0 || getContext() == null) return cachedPeers;
        /*String fileName = "recentContacts.txt" + selfUserId;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                requireContext().openFileInput(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                int p0 = line.indexOf("peer={");
                if (p0 < 0) continue;
                int p1 = line.indexOf('}', p0);
                if (p1 <= p0 + 6) continue;
                String s = line.substring(p0 + 6, p1).trim();
                if (TextUtils.isEmpty(s)) continue;
                try {
                    long id = Long.parseLong(s);
                    if (id > 0 && id != selfUserId) cachedPeers.add(id);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }*/
        if (cachedPeers.isEmpty()) {
            // appendPeersFromMsgList(cachedPeers);
        }
        Set<Long> newPeers = new HashSet<>();
        appendPeersFromImObjList(newPeers);
        // 联系人页可能短暂为空，保留上一次可用结果避免分发瞬断。
        if (!newPeers.isEmpty()) {
            cachedPeers.clear();
            cachedPeers.addAll(newPeers);
        }
        refreshGroupStatsByActiveGroups();
        return cachedPeers;
    }

    private void appendPeersFromMsgList(Set<Long> out) {
        if (!isAdded()) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if (mainActivity == null) return;
        FragmentPagerAdapter_main adapter = mainActivity.getMainPagerAdapter();
        if (adapter == null || adapter.mFragment_msg1 == null) return;
        Fragment_msgList msgList = adapter.mFragment_msg1;
        if (msgList.mTableItems == null) return;
        for (TableItem.TableItem_msg item : msgList.mTableItems) {
            if (item == null) continue;
            long id = item.ui64Id;

            if (id > 0 && id != selfUserId) out.add(id);
        }
    }

    private void appendPeersFromImObjList(Set<Long> out) {
        if (!isAdded()) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ensureSelfUserId();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if (mainActivity == null) return;
        FragmentPagerAdapter_main adapter = mainActivity.getMainPagerAdapter();
        if (adapter == null || adapter.mFragment_imObj1 == null) return;
        Fragment_imObjList imObjList = adapter.mFragment_imObj1;
        if (imObjList.mTableItems_imObj == null) return;

        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        if (activeGrpIds.isEmpty()) return;

        for (TableItem.TableItem_imObj item : imObjList.mTableItems_imObj) {
            if (item == null) continue;
            if (item.uiObjType != qyDefs.Consts.CONST_objType_imGrp) continue;
            long grpId = item.ui64Id;
            if (grpId <= 0) continue;
            if (!activeGrpIds.contains(grpId)) continue;
            if (!isSelfMemberOfGroup(grpId)) continue;
            out.add(grpId);
        }
    }
    /**
     * 活动组 IM 群 id 列表。业务上统一走 {@link TaskActiveGrp#getTaskGrp()}，后续动态活动组只改该类即可。
     */
    public long[] getTaskGrp() {
        return TaskActiveGrp.getTaskGrp();
    }

    public void onActiveGroupCandidatesChanged() {
        if (!isAdded()) return;
        if (mapView != null) {
            mapView.post(() -> {
                if (isAdded()) {
                    resolveActiveGroupSelectionMaybe(true);
                }
            });
        } else {
            resolveActiveGroupSelectionMaybe(true);
        }
    }

    private boolean hasSelectedActiveGroup() {
        return TaskActiveGrp.hasSelectedGroup();
    }

    private long getSelectedActiveGroupId() {
        return TaskActiveGrp.getSelectedGroupId();
    }

    private String getSelectedActiveGroupDisplayName() {
        return TaskActiveGrp.getSelectedGroupDisplayName();
    }

    private Set<Long> loadActiveGroupIdsFromTaskGrp() {
        Set<Long> out = new HashSet<>();
        long[] grpIds = getTaskGrp();
        if (grpIds == null) return out;
        for (long grpId : grpIds) {
            if (grpId > 0) out.add(grpId);
        }
        return out;
    }

    private boolean isSelfMemberOfGroup(long grpId) {
        return TaskActiveGrp.isSelfMemberOfGroup(grpId);
    }

    private void resolveActiveGroupSelectionMaybe(boolean showPrompt) {
        long oldGroupId = getSelectedActiveGroupId();
        boolean changed = TaskActiveGrp.resolveSelectionAfterLogin();
        long newGroupId = getSelectedActiveGroupId();
        if (changed || oldGroupId != newGroupId) {
            onActiveGroupChanged(false);
        } else {
            refreshGroupStatsByActiveGroups();
            refreshActiveGroupMeetingStatus();
        }
        if (showPrompt
                && !activeGroupPickPromptShown
                && !hasSelectedActiveGroup()
                && TaskActiveGrp.getMyGroups().size() > 1) {
            activeGroupPickPromptShown = true;
            showActiveGroupPickDialog(true);
        }
    }

    private void onActiveGroupChanged(boolean manualSwitch) {
        clearRemoteLocations();
        activeGroupMemberIdsCache.clear();
        cachedPeers.clear();
        recentActiveOnlineTs.clear();
        clearBattleMarks();
        clearMarkSyncCache();
        lastLocSendMs = 0L;
        refreshGroupStatsByActiveGroups();
        refreshActiveGroupMeetingStatus();
        if (hasSelectedActiveGroup()) {
            if (getActivity() instanceof MainActivity && MapLocSettings.isEncryptionEnabled()) {
                MainActivity main = (MainActivity) getActivity();
                main.clearMapLocKeywordAndRemoteLocations();
                if (mapView != null) {
                    mapView.post(main::requestMapLocKeywordIfNeeded);
                } else {
                    main.requestMapLocKeywordIfNeeded();
                }
            }
            tryRequestMarkSyncSnapshot(true);
            if (locationHandler != null) {
                locationHandler.postDelayed(() -> tryRequestMarkSyncSnapshot(true), 2000L);
            }
            flushSelfLocationToGroupNow();
            if (manualSwitch && isAdded()) {
                Toast.makeText(requireContext(), "已切换活动组：" + getSelectedActiveGroupDisplayName(), Toast.LENGTH_SHORT).show();
            }
        }
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).notifyMeActiveGroupChanged();
        }
    }

    private void refreshGroupStatsByActiveGroups() {
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        if (activeGrpIds.isEmpty()) {
            updateGroupStatsView(0, 0);
            return;
        }
        Set<Long> allMembers = new HashSet<>();
        for (Long grpId : activeGrpIds) {
            if (grpId == null || grpId <= 0) continue;
            allMembers.addAll(loadGroupMemberIds(grpId));
        }
        int onlineCnt = 0;
        for (Long userId : allMembers) {
            if (userId == null || userId <= 0) continue;
            if (isMemberOnlineForActiveGroups(userId, activeGrpIds)) onlineCnt++;
        }
        updateGroupStatsView(allMembers.size(), onlineCnt);
    }

    /**
     * 在线状态优先用会议页已刷新的组成员列表；否则退回本地 imObj。
     */
    private boolean isMemberOnlineForActiveGroups(long userId, Set<Long> activeGrpIds) {
        if (userId <= 0 || activeGrpIds == null) return false;
        long now = System.currentTimeMillis();
        if (isUserRecentlyActiveOnline(userId, now)) return true;
        for (Long grpId : activeGrpIds) {
            if (grpId == null || grpId <= 0) continue;
            java.util.Map<Long, Short> map = Fragment_dlgTalk_av2.tryGetGrpMemberRunningStatusMap(grpId);
            if (map == null) continue;
            Short st = map.get(userId);
            if (st != null) {
                return st == Consts.CONST_usRunningStatus_online;
            }
        }
        return isUserOnline(userId);
    }

    private boolean isKnownActiveGroupMember(long userId) {
        if (userId <= 0) return false;
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        for (Long grpId : activeGrpIds) {
            if (grpId == null || grpId <= 0) continue;
            if (loadGroupMemberIds(grpId).contains(userId)) {
                return true;
            }
        }
        return false;
    }

    private Set<Long> loadGroupMemberIds(long grpId) {
        Set<Long> members = new HashSet<>();
        if (grpId <= 0) return members;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return members;
        try {
            myDb.CMyDb db = (myDb.CMyDb) pQyMc.gui.pDb;
            if (db == null || db.m_var_pQ_qyImGrpMemTab == null) return members;
            genericQueue.qyFuncs.qTraverse(db.m_var_pQ_qyImGrpMemTab, tmpHandler_collectGrpMemberIds, grpId, members);
        } catch (Exception ignored) {
        }
        return members;
    }

    private boolean isUserOnline(long userId) {
        if (userId <= 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return false;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
        if (pMisCnt != null && userId == pMisCnt.idInfo.ui64Id) {
            return pMisCnt.status.usRunningStatus == com.qycx.base.qyCusResPublic.Consts.CONST_usRunningStatus_online;
        }
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        if (pDbFuncs == null || pQyMc.gui.pDb == null) return false;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = userId;
        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(
                pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType,
                qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0,
                msgrInfo, regInfo, null, null)) {
            return false;
        }
        return msgrInfo.usRunningStatus == com.qycx.base.qyCusResPublic.Consts.CONST_usRunningStatus_online;
    }

    private boolean isUserRecentlyActiveOnline(long userId, long now) {
        Long ts = recentActiveOnlineTs.get(userId);
        return ts != null && now - ts <= REMOTE_STALE_MS;
    }

    private void markUserActiveOnline(long userId) {
        if (userId <= 0) return;
        recentActiveOnlineTs.put(userId, System.currentTimeMillis());
    }

    private void pruneExpiredOnlineMembersAndRefreshStats() {
        long now = System.currentTimeMillis();
        recentActiveOnlineTs.entrySet().removeIf(e -> e == null || e.getValue() == null || now - e.getValue() > REMOTE_STALE_MS);
        refreshGroupStatsByActiveGroups();
    }

    private void updateGroupStatsView(int memberCount, int onlineCount) {
        if (!isAdded() || tvGroupStats == null) return;
        if (!hasSelectedActiveGroup()) {
            tvGroupStats.setText("未选择活动组");
        } else {
            tvGroupStats.setText(String.format(Locale.CHINA, "活动组[%s]：成员 %d 在线 %d",
                    getSelectedActiveGroupDisplayName(), memberCount, onlineCount));
        }
        applyMapOverlayTextSizes();
    }

    private void startActiveGroupMeetingStatusMonitor() {
        locationHandler.removeCallbacks(activeGroupMeetingStatusRunnable);
        refreshActiveGroupMeetingStatus();
        locationHandler.postDelayed(activeGroupMeetingStatusRunnable, ACTIVE_GROUP_MEETING_REFRESH_MS);
    }

    private void stopActiveGroupMeetingStatusMonitor() {
        locationHandler.removeCallbacks(activeGroupMeetingStatusRunnable);
        if (viewActiveGrpMeetingLamp != null) {
            viewActiveGrpMeetingLamp.clearAnimation();
        }
    }

    private void refreshActiveGroupMeetingStatus() {
        if (!hasSelectedActiveGroup()) {
            hideActiveGroupMeetingEntryForNoSelection();
            return;
        }
        ActiveGroupMeetingState state = findActiveGroupMeetingStateForDisplay();
        updateActiveGroupMeetingEntry(state != null, state != null ? state.groupId : 0);
    }

    private void hideActiveGroupMeetingEntryForNoSelection() {
        activeGroupMeetingRunning = false;
        activeGroupMeetingId = 0;
        if (viewActiveGrpMeetingLamp != null) {
            viewActiveGrpMeetingLamp.clearAnimation();
            viewActiveGrpMeetingLamp.setVisibility(View.GONE);
        }
        if (tvActiveGrpMeetingEntry != null) {
            tvActiveGrpMeetingEntry.setVisibility(View.GONE);
            tvActiveGrpMeetingEntry.setEnabled(false);
        }
    }

    private void updateActiveGroupMeetingEntry(boolean running, long groupId) {
        if (isMapEmbeddedInAvPage()) {
            hideActiveGroupMeetingEntryInAvPage();
            return;
        }
        activeGroupMeetingRunning = running;
        activeGroupMeetingId = running ? groupId : 0;
        if (viewActiveGrpMeetingLamp != null) {
            viewActiveGrpMeetingLamp.setVisibility(View.VISIBLE);
            viewActiveGrpMeetingLamp.setBackgroundResource(running
                    ? R.drawable.bg_meeting_lamp_green
                    : R.drawable.bg_meeting_lamp_red);
            viewActiveGrpMeetingLamp.clearAnimation();
            if (running) {
                AlphaAnimation blink = new AlphaAnimation(1.0f, 0.25f);
                blink.setDuration(650L);
                blink.setRepeatMode(Animation.REVERSE);
                blink.setRepeatCount(Animation.INFINITE);
                viewActiveGrpMeetingLamp.startAnimation(blink);
            }
        }
        if (tvActiveGrpMeetingEntry != null) {
            tvActiveGrpMeetingEntry.setVisibility(View.VISIBLE);
            tvActiveGrpMeetingEntry.setText(running ? "点击进入会议" : "暂无会议");
            tvActiveGrpMeetingEntry.setTextColor(Color.parseColor(running ? "#00D26A" : "#FFFFFF"));
            tvActiveGrpMeetingEntry.setEnabled(running);
        }
        applyMapOverlayTextSizes();
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingState() {
        return findActiveGroupMeetingStateForEnter();
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingStateForDisplay() {
        return findActiveGroupMeetingStateInternal(false);
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingStateForEnter() {
        return findActiveGroupMeetingStateInternal(true);
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingStateInternal(boolean forEnter) {
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return null;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
        if (pMisCnt == null) return null;
        ActiveGroupMeetingState notifyState = findActiveGroupMeetingStateFromNotify(pQyMc, pProcInfo, activeGrpIds);
        if (notifyState != null) return notifyState;
        ActiveGroupMeetingState taskState = findActiveGroupMeetingStateFromTasks(pQyMc, pProcInfo, pMisCnt, activeGrpIds, forEnter);
        if (taskState != null) return taskState;
        if (activeGrpIds.isEmpty()) return null;
        for (Long grpIdObj : activeGrpIds) {
            if (grpIdObj == null || grpIdObj <= 0) continue;
            long grpId = grpIdObj;
            if (!isSelfMemberOfGroup(grpId)) continue;
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = grpId;
            Activity_dlgTalk talk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo);
            dlgTalkProc.DLG_TALK_var talkVar = talk != null ? talk.get_pm_var() : null;
            if (talk == null || talkVar == null || talkVar.av == null || talkVar.av.taskInfo == null) {
                continue;
            }
            if (!isGroupTalk(talk)) continue;
            if (talkVar.av.taskInfo.bTaskExists && talkVar.av.taskInfo.ucbVideoConference) {
                int idx = talkVar.av.taskInfo.iIndex_taskInfo;
                if (idx < 0) continue;
                if (forEnter && !isLiveGroupAvTaskIndex(idx)) continue;
                return new ActiveGroupMeetingState(grpId, talk, idx,
                        talkVar.av.taskInfo.iTaskId, forEnter, false);
            }
        }
        return null;
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingStateFromNotify(ctxQyMc.CCtxQyMc pQyMc,
                                                                          ctxQmc.CCtxQmc pProcInfo,
                                                                          Set<Long> activeGrpIds) {
        if (pQyMc == null || pQyMc.gui == null || !(pQyMc.gui.hMainWnd instanceof MainActivity)) return null;
        MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
        if (main.var_common == null
                || main.var_common.notifyTaskStatus == null
                || !main.var_common.notifyTaskStatus.bExists_task
                || !main.var_common.notifyTaskStatus.bAvCall) {
            return null;
        }
        int idx = main.var_common.notifyTaskStatus.index_taskInfo;
        if (idx < 0 || !qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, idx)) {
            return null;
        }
        qmcTaskInfo.QMC_TASK_INFO taskInfo = pProcInfo.getQmcTaskInfoByIndex(main.var_common.ctxCaller, idx, "map.activeGroupMeeting.notify");
        qnmCommProc_mis.MIS_MSG_TASK task = getRecentActiveAvTask(taskInfo, false);
        if (task == null) return null;
        long grpId = extractGroupIdFromAvTask(task);
        if (!isEligibleMeetingGroup(pQyMc, grpId, activeGrpIds)) return null;
        return new ActiveGroupMeetingState(grpId, findGroupTalker(pQyMc, grpId), idx,
                getTaskIdFromTaskInfo(taskInfo), true, true);
    }

    @Nullable
    private ActiveGroupMeetingState findActiveGroupMeetingStateFromTasks(ctxQyMc.CCtxQyMc pQyMc,
                                                                         ctxQmc.CCtxQmc pProcInfo,
                                                                         qmCommFunc_mis.MIS_CNT pMisCnt,
                                                                         Set<Long> activeGrpIds,
                                                                         boolean forEnter) {
        if (pProcInfo == null || pProcInfo.cfg == null) return null;
        for (int i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
            if (forEnter && !qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, i)) {
                continue;
            }
            qmcTaskInfo.QMC_TASK_INFO taskInfo = pProcInfo.getQmcTaskInfoByIndex(null, i, "map.activeGroupMeeting");

            qnmCommProc_mis.MIS_MSG_TASK task = getRecentActiveAvTask(taskInfo, forEnter);
            if (task == null) continue;
            long grpId = extractGroupIdFromAvTask(task);
            if (!isEligibleMeetingGroup(pQyMc, grpId, activeGrpIds)) continue;
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = grpId;
            Activity_dlgTalk talk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo);
            return new ActiveGroupMeetingState(grpId, talk, i, getTaskIdFromTaskInfo(taskInfo), forEnter, false);
        }
        return null;
    }

    private int getTaskIdFromTaskInfo(@Nullable qmcTaskInfo.QMC_TASK_INFO taskInfo) {
        return taskInfo != null && taskInfo.var != null ? taskInfo.var.iTaskId : 0;
    }

    private static boolean isGroupTalk(@Nullable Activity_dlgTalk talk) {
        if (talk == null) return false;
        dlgTalkProc.DLG_TALK_var var = talk.get_pm_var();
        return var != null && isGroup(var);
    }

    private void sendGroupConfReqFromMap(long groupId, short avLevel) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) return;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = groupId;
        pProcInfo.av.talk_doAv_info.usAvLevel = avLevel;
        sendConfReq(null, idInfo, (short) CONST_imOp_applyForConf, avLevel, (short) 0, null, null, null);
    }

    /** M0：确保地图组会议走 MainActivity 内嵌 av2 悬浮窗。 */
    private void ensureMapFloatingAvEnabled() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo != null && pProcInfo.cfg != null) {
            pProcInfo.cfg.bTest_floatingView = true;
        }
    }

    /** 注册 CIsTalker，不打开 Activity_dlgTalk（bStay_toAvCall）。 */
    private void registerTalkerSilent(long talkerId) {
        if (talkerId <= 0) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();
        dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, talkerId, true, false, true, param);
    }

    private void registerGroupTalkerSilent(long groupId) {
        registerTalkerSilent(groupId);
    }

    /** 通过 CIsTalker 标记待入会，不依赖 Activity_dlgTalk。 */
    private void markGroupTalkAvReady(long groupId) {
        if (groupId <= 0) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.pMisCnt == null) return;
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt, groupId);
        if (talker != null && talker.m_var != null && talker.m_var.av != null) {
            talker.m_var.av.bNeedDoTaskAv = true;
        }
    }

    /** 扫描 task 表，返回指定活动组当前可入会的 conf task index，无则 -1。 */
    private int findLiveGroupAvTaskIndexForGroup(long groupId) {
        if (groupId <= 0) return -1;
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.cfg == null) return -1;
        for (int i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
            if (!isLiveGroupAvTaskIndex(i)) continue;
            qmcTaskInfo.QMC_TASK_INFO taskInfo = pProcInfo.getQmcTaskInfoByIndex(
                    null, i, "map.findLiveGroupAvTaskIndex");
            qnmCommProc_mis.MIS_MSG_TASK task = getRecentActiveAvTask(taskInfo, false);
            if (task == null || extractGroupIdFromAvTask(task) != groupId) continue;
            if (!isEligibleMeetingGroup(pQyMc, groupId, activeGrpIds)) continue;
            return i;
        }
        return -1;
    }

    private int resolveLiveGroupAvTaskIndex(@NonNull ActiveGroupMeetingState state) {
        if (state.indexTaskInfo >= 0 && isLiveGroupAvTaskIndex(state.indexTaskInfo)) {
            return state.indexTaskInfo;
        }
        return findLiveGroupAvTaskIndexForGroup(state.groupId);
    }

    /**
     * 地图组会议悬浮窗一律用 av_accept + 有效 task index 入会（sendConfReq 后 task 已存在，不可再用 av/-1 重发）。
     */
    private void resolveMapGroupAvEntry(@NonNull ActiveGroupMeetingState state, @NonNull int[] outCmd, @NonNull int[] outIndex) {
        int idx = resolveLiveGroupAvTaskIndex(state);
        if (idx >= 0) {
            outCmd[0] = tmpResource.IDC_BUTTON_av_accept;
            outIndex[0] = idx;
            return;
        }
        outCmd[0] = tmpResource.IDC_BUTTON_av;
        outIndex[0] = -1;
    }

    private boolean isLiveGroupAvTaskIndex(int indexTaskInfo) {
        if (indexTaskInfo < 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return false;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) return false;
        if (!qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, indexTaskInfo)) {
            return false;
        }
        qmcTaskInfo.QMC_TASK_INFO taskInfo = pProcInfo.getQmcTaskInfoByIndex(
                null, indexTaskInfo, "map.isLiveGroupAvTaskIndex");
        return taskInfo != null && taskInfo.bUsed && getRecentActiveAvTask(taskInfo, false) != null;
    }

    /** 仅关闭旧悬浮窗并静默注册 talker；不清理 AV task（task 清理只在重新 sendConfReq 前做）。 */
    private void prepareMapFloatingAvShell(@NonNull MainActivity main, long groupId) {
        main.closeFloatingView();
        registerGroupTalkerSilent(groupId);
    }

    /** 打开悬浮窗前同步 talker AV 入口参数；已有 task 由 index_taskInfo 承接，不能清空 taskInfo。 */
    private void syncMapTalkerAvBeforeFloating(long groupId, int cmd, int indexTask) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || pQyMc.get_pProcInfo() == null || pQyMc.get_pProcInfo().pMisCnt == null) {
            return;
        }
        CIsTalker talker = findIsTalker(pQyMc.get_pProcInfo().pMisCnt, groupId);
        if (talker == null || talker.m_var == null || talker.m_var.av == null) {
            return;
        }
        dlgTalkProc.DLG_TALK_var v = talker.m_var;
        v.m_bNeedResumeTaskAv = false;
        v.m_talkAvVar.m_iCmd = cmd;
        v.m_talkAvVar.m_index_taskInfo1 = indexTask;
        v.av.bNeedDoTaskAv = true;
    }

    /** EGL / TextureView 就绪后再次触发 doTask_av，避免二次入会卡在 pause 分支。 */
    private void kickMapFloatingAvStart(@NonNull MainActivity main, long groupId) {
        kickMapFloatingAvStart(main, groupId, 0);
    }

    private void kickMapFloatingAvStart(@NonNull MainActivity main, long groupId, int attempt) {
        if (!isAdded()) return;
        if (!(main.m_floatingView_fg instanceof Fragment_dlgTalk_av2)) {
            return;
        }
        Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) main.m_floatingView_fg;
        if (!fg.isAdded() || fg.mTextureView == null || !fg.mTextureView.isAvailable()) {
            if (attempt < 30 && locationHandler != null) {
                locationHandler.postDelayed(() -> kickMapFloatingAvStart(main, groupId, attempt + 1), 120L);
            }
            return;
        }
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || pQyMc.get_pProcInfo() == null || pQyMc.get_pProcInfo().pMisCnt == null) {
            return;
        }
        CIsTalker talker = findIsTalker(pQyMc.get_pProcInfo().pMisCnt, groupId);
        if (talker == null || talker.m_var == null || talker.m_var.av == null) {
            return;
        }
        dlgTalkProc.DLG_TALK_var v = talker.m_var;
        if (v.m_share_eglContext == null) {
            if (attempt < 30 && locationHandler != null) {
                locationHandler.postDelayed(() -> kickMapFloatingAvStart(main, groupId, attempt + 1), 120L);
            }
            return;
        }
        if (!v.av.bNeedDoTaskAv) {
            v.av.bNeedDoTaskAv = true;
        }
        v.m_bNeedResumeTaskAv = false;
        doChkTalkDraw(v);
        fg.waitUntilTextureViewAvailable(null, null, null);
    }

    /**
     * M0：活动组会议在地图页通过 {@link MainActivity#makeFloatingView} 打开 av2 悬浮 Fragment。
     */
    private boolean enterMapGroupMeetingFloating(@NonNull ActiveGroupMeetingState state) {
        if (avCallProc.blockDuplicateVideoMeetingStart()) {
            return false;
        }
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || !(pQyMc.gui.hMainWnd instanceof MainActivity)) {
            return false;
        }
        int liveIdx = resolveLiveGroupAvTaskIndex(state);
        if (liveIdx < 0) {
            Log.d("MAP_AV_DBG", "enterMapGroupMeetingFloating skip: no live task index, group="
                    + state.groupId + ", stateIdx=" + state.indexTaskInfo);
            return false;
        }
        if (state.indexTaskInfo != liveIdx) {
            state = new ActiveGroupMeetingState(state.groupId, state.talk, liveIdx,
                    state.taskId, state.enterable, false);
        }
        final long groupId = state.groupId;
        mapSilentAcceptNotifyMeetingIfNeeded(groupId);
        MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
        ensureMapFloatingAvEnabled();
        prepareMapFloatingAvShell(main, groupId);
        int[] cmd = new int[1];
        int[] indexTask = new int[1];
        resolveMapGroupAvEntry(state, cmd, indexTask);
        if (cmd[0] != tmpResource.IDC_BUTTON_av_accept || indexTask[0] < 0) {
            Log.d("MAP_AV_DBG", "enterMapGroupMeetingFloating skip: bad entry cmd=" + cmd[0]
                    + " idx=" + indexTask[0] + ", group=" + groupId);
            return false;
        }
        syncMapTalkerAvBeforeFloating(groupId, cmd[0], indexTask[0]);
        markGroupTalkAvReady(groupId);
        if (!avCallProc.anc_hasAllPermission(main)) {
            avCallProc.anc_isPermissionOk(main);
            if (isAdded()) {
                Toast.makeText(requireContext(), "需要相机和麦克风权限", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        int level = qyWmComm.Consts.CONST_do_1080P_AV_Call;
        int rc = main.makeFloatingView(groupId, cmd[0], indexTask[0], level);
        Log.d("MAP_AV_DBG", "makeFloatingView group=" + groupId + " cmd=" + cmd[0]
                + " idx=" + indexTask[0] + " rc=" + rc);
        if (rc == 0 && locationHandler != null) {
            locationHandler.postDelayed(() -> kickMapFloatingAvStart(main, groupId), 350L);
        }
        if (rc != 0 && isAdded()) {
            Toast.makeText(requireContext(), "无法打开地图视频窗", Toast.LENGTH_SHORT).show();
        }
        return rc == 0;
    }

    private boolean isEligibleMeetingGroup(@NonNull ctxQyMc.CCtxQyMc pQyMc,
                                           long groupId,
                                           @Nullable Set<Long> activeGrpIds) {
        if (groupId <= 0) return false;
        if (activeGrpIds != null && activeGrpIds.contains(groupId)) return true;
        if (isSelfMemberOfGroup(groupId)) return true;
        Activity_dlgTalk talk = findGroupTalker(pQyMc, groupId);
        return isGroupTalk(talk);
    }

    @Nullable
    private qnmCommProc_mis.MIS_MSG_TASK getRecentActiveAvTask(@Nullable qmcTaskInfo.QMC_TASK_INFO taskInfo,
                                                               boolean requireRecent) {
        if (taskInfo == null || !taskInfo.bUsed || taskInfo.var == null) return null;
        qmcTaskInfo.QMC_taskData_common taskData = taskInfo.getTaskData();
        if (taskData == null) return null;
        if (taskData.uiType != qmcTaskInfo.Consts.CONST_taskDataType_conf) return null;
        if (!(taskData.msgU instanceof qnmCommProc_mis.MIS_MSG_TASK)) return null;
        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) taskData.msgU;
        if (task.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return null;
        if (task.data == null || task.data.route == null || task.data.buf == null) return null;
        qnmCommProc_mis.IM_CONTENTU content = task.data.buf;
        if (content.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo) return null;
        if (requireRecent) {
            long elapsedMs = tmpFunc.qyFuncs.myGetTickCount(null) - taskInfo.var.dwTickCnt_recv_lastRefreshed1;
            if (Math.abs(elapsedMs) >= 20_000L) return null;
        }
        return task;
    }

    private long extractGroupIdFromAvTask(@NonNull qnmCommProc_mis.MIS_MSG_TASK task) {
        if (task.data != null && task.data.buf instanceof qnmCommProc_mis.TRANSFER_AV_INFO) {
            qnmCommProc_mis.TRANSFER_AV_INFO avInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) task.data.buf;
            if (avInfo.idInfo_logicalPeer != null && avInfo.idInfo_logicalPeer.ui64Id > 0) {
                return avInfo.idInfo_logicalPeer.ui64Id;
            }
        }
        return task.addr_logicalPeer != null && task.addr_logicalPeer.idInfo != null
                ? task.addr_logicalPeer.idInfo.ui64Id
                : 0;
    }

    private void enterActiveGroupMeetingIfAny() {
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        if (avCallProc.blockDuplicateVideoMeetingStart()) {
            return;
        }
        ActiveGroupMeetingState state = findActiveGroupMeetingStateForEnter();
        if (state != null) {
            enterResolvedActiveGroupMeeting(state);
            return;
        }
        state = findActiveGroupMeetingStateForDisplay();
        if (state == null || !isAdded()) {
            Toast.makeText(requireContext(), "当前活动组暂无进行中的会议", Toast.LENGTH_SHORT).show();
            updateActiveGroupMeetingEntry(false, 0);
            return;
        }
        recoverActiveGroupMeetingFromDisplay(state);
    }

    private void enterResolvedActiveGroupMeeting(@NonNull ActiveGroupMeetingState state) {
        if (resolveLiveGroupAvTaskIndex(state) >= 0) {
            enterMapGroupMeetingFloating(state);
            return;
        }
        if (locationHandler != null) {
            locationHandler.postDelayed(() -> waitGroupConferenceTaskAndEnter(state.groupId, 0), 250L);
        }
    }

    private void recoverActiveGroupMeetingFromDisplay(@NonNull ActiveGroupMeetingState displayState) {
        long now = SystemClock.elapsedRealtime();
        if (activeGroupMeetingRecoveryGroupId == displayState.groupId
                && now - activeGroupMeetingRecoveryStartedMs < ACTIVE_GROUP_MEETING_RECOVERY_THROTTLE_MS) {
            Log.d("MAP_AV_DBG", "recoverActiveGroupMeeting skip duplicate, group=" + displayState.groupId);
            return;
        }
        activeGroupMeetingRecoveryGroupId = displayState.groupId;
        activeGroupMeetingRecoveryStartedMs = now;
        Toast.makeText(requireContext(), "正在恢复会议", Toast.LENGTH_SHORT).show();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        registerGroupTalkerSilent(displayState.groupId);
        if (locationHandler != null) {
            locationHandler.postDelayed(() -> tryRecoverActiveGroupMeetingAfterRegister(displayState.groupId, 0), 260L);
        }
    }

    private void tryRecoverActiveGroupMeetingAfterRegister(long groupId, int attempt) {
        if (!isAdded()) return;
        ActiveGroupMeetingState enterState = findActiveGroupMeetingStateForEnter();
        if (enterState != null && enterState.groupId == groupId) {
            clearActiveGroupMeetingRecovery(groupId);
            enterResolvedActiveGroupMeeting(enterState);
            return;
        }
        ActiveGroupMeetingState displayState = findActiveGroupMeetingStateForDisplay();
        if (displayState != null
                && displayState.groupId == groupId
                && tryRecoverActiveGroupMeetingByTaskId(displayState)) {
            clearActiveGroupMeetingRecovery(groupId);
            return;
        }
        if (attempt < 5 && locationHandler != null) {
            locationHandler.postDelayed(() -> tryRecoverActiveGroupMeetingAfterRegister(groupId, attempt + 1), 220L);
            return;
        }
        sendGroupConfReqFromMap(groupId, (short) qyWmComm.Consts.CONST_do_1080P_AV_Call);
        Log.d("MAP_AV_DBG", "recoverActiveGroupMeeting sendConfReq, group=" + groupId);
        if (locationHandler != null) {
            locationHandler.postDelayed(() -> waitGroupConferenceTaskAndEnter(groupId, 0), 300);
        }
    }

    private boolean tryRecoverActiveGroupMeetingByTaskId(@NonNull ActiveGroupMeetingState displayState) {
        if (displayState.taskId <= 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return false;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) return false;
        int idx = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, displayState.taskId);
        qmcTaskInfo.QMC_TASK_INFO taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, idx, "map.recoverActiveGroupMeetingByTaskId");
        qnmCommProc_mis.MIS_MSG_TASK task = getRecentActiveAvTask(taskInfo, false);
        if (task == null || extractGroupIdFromAvTask(task) != displayState.groupId) return false;
        taskInfo.var.bClosed = false;
        task.iStatus = qyDefs_open.Consts.CONST_imTaskStatus_resp;
        registerGroupTalkerSilent(displayState.groupId);
        markGroupTalkAvReady(displayState.groupId);
        ActiveGroupMeetingState enterState = new ActiveGroupMeetingState(
                displayState.groupId, null, idx, displayState.taskId, true, false);
        enterMapGroupMeetingFloating(enterState);
        Log.d("MAP_AV_DBG", "recoverActiveGroupMeeting floating by taskId=" + displayState.taskId + ", idx=" + idx);
        return true;
    }

    @Nullable
    private Activity_dlgTalk findGroupTalker(@NonNull ctxQyMc.CCtxQyMc pQyMc, long groupId) {
        try {
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
            if (pMisCnt == null) return null;
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = groupId;
            return guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo);
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 地图组会议：静默 accept 通知，不弹 CookieBar、不打开联系人会话页。 */
    private void mapSilentAcceptNotifyMeetingIfNeeded(long groupId) {
        if (groupId <= 0 || mapGroupNotifyAcceptedGroupId == groupId) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || !(pQyMc.gui.hMainWnd instanceof MainActivity)) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.pMisCnt == null) return;
        Set<Long> singleGroup = new HashSet<>();
        singleGroup.add(groupId);
        ActiveGroupMeetingState notifyState = findActiveGroupMeetingStateFromNotify(pQyMc, pProcInfo, singleGroup);
        if (notifyState == null || notifyState.groupId != groupId) return;
        try {
            MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
            MainActivity.tmp_notify_cancel_ifForegrounded(main);
            registerGroupTalkerSilent(groupId);
            CIsTalker talker = findIsTalker(pProcInfo.pMisCnt, groupId);
            if (talker == null || talker.m_var == null) return;
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(
                    null, notifyState.indexTaskInfo, "map.silentAcceptNotify");
            if (pTaskInfo == null || !pTaskInfo.bUsed || pTaskInfo.var == null) return;
            audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);
            dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
            param.bManualCmd = true;
            Fragment avFg = main.m_floatingView_fg instanceof Fragment
                    ? (Fragment) main.m_floatingView_fg : null;
            dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(
                    avFg,
                    talker.m_var,
                    pTaskInfo.var.iTaskId,
                    (short) qnmCommProc_mis.Consts.CONST_imOp_recv_accept,
                    null,
                    param);
            mapGroupNotifyAcceptedGroupId = groupId;
            Log.d("MAP_AV_DBG", "map silent accept notify, group=" + groupId);
        } catch (Exception e) {
            Log.d("MAP_AV_DBG", "map silent accept notify failed: " + e.getMessage());
        }
    }

    private void clearActiveGroupMeetingRecovery(long groupId) {
        if (activeGroupMeetingRecoveryGroupId == groupId) {
            activeGroupMeetingRecoveryGroupId = 0;
            activeGroupMeetingRecoveryStartedMs = 0;
        }
    }

    private static final class ActiveGroupMeetingState {
        final long groupId;
        @Nullable
        final Activity_dlgTalk talk;
        final int indexTaskInfo;
        final int taskId;
        final boolean enterable;
        final boolean fromNotify;

        ActiveGroupMeetingState(long groupId,
                                @Nullable Activity_dlgTalk talk,
                                int indexTaskInfo,
                                int taskId,
                                boolean enterable,
                                boolean fromNotify) {
            this.groupId = groupId;
            this.talk = talk;
            this.indexTaskInfo = indexTaskInfo;
            this.taskId = taskId;
            this.enterable = enterable;
            this.fromNotify = fromNotify;
        }
    }

    private static final class ActiveGroupMemberViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvStatus;
        final TextView btnPeerLocate;
        final Button btnPeerVideo;

        ActiveGroupMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvStatus = itemView.findViewById(R.id.tvMemberStatus);
            btnPeerLocate = itemView.findViewById(R.id.btnPeerLocate);
            btnPeerVideo = itemView.findViewById(R.id.btnPeerVideo);
        }
    }

    private final class ActiveGroupMembersAdapter extends RecyclerView.Adapter<ActiveGroupMemberViewHolder> {
        private final List<Long> items;
        private final Set<Long> activeGrpIds;
        private final Consumer<Long> onPeerVideo;
        private final Consumer<Long> onLocatePeer;
        private final Consumer<Long> onPeerDebugLongPress;

        ActiveGroupMembersAdapter(List<Long> items, Set<Long> activeGrpIds,
                                  Consumer<Long> onPeerVideo, Consumer<Long> onLocatePeer,
                                  Consumer<Long> onPeerDebugLongPress) {
            this.items = items;
            this.activeGrpIds = activeGrpIds;
            this.onPeerVideo = onPeerVideo;
            this.onLocatePeer = onLocatePeer;
            this.onPeerDebugLongPress = onPeerDebugLongPress;
        }

        @NonNull
        @Override
        public ActiveGroupMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_active_group_member, parent, false);
            return new ActiveGroupMemberViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ActiveGroupMemberViewHolder h, int position) {
            long uid = items.get(position);
            h.tvName.setText(getUserDisplayName(uid));
            boolean online = isMemberOnlineForActiveGroups(uid, activeGrpIds);
            h.tvStatus.setText(online ? "在线" : "离线");
            h.tvStatus.setTextColor(Color.parseColor(online ? "#00B578" : "#8A8F99"));
            styleMapDialogRowActionText(h.btnPeerLocate);
            styleMapDialogContentActionButton(h.btnPeerVideo);
            h.btnPeerLocate.setOnClickListener(v -> onLocatePeer.accept(uid));
            if (isMapEmbeddedInAvPage()) {
                h.btnPeerVideo.setVisibility(View.GONE);
                h.btnPeerVideo.setOnClickListener(null);
            } else {
                h.btnPeerVideo.setVisibility(View.VISIBLE);
                h.btnPeerVideo.setOnClickListener(v -> onPeerVideo.accept(uid));
            }
            h.itemView.setOnLongClickListener(v -> {
                onPeerDebugLongPress.accept(uid);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static final class ActiveGroupPickViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvId;

        ActiveGroupPickViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvActiveGroupPickName);
            tvId = itemView.findViewById(R.id.tvActiveGroupPickId);
        }
    }

    private final class ActiveGroupPickAdapter extends RecyclerView.Adapter<ActiveGroupPickViewHolder> {
        private final List<TaskActiveGrp.GroupItem> items;
        private long selectedGroupId;
        private final Consumer<Long> onSelectionChanged;

        ActiveGroupPickAdapter(List<TaskActiveGrp.GroupItem> items, long selectedGroupId,
                               Consumer<Long> onSelectionChanged) {
            this.items = items;
            this.selectedGroupId = selectedGroupId;
            this.onSelectionChanged = onSelectionChanged;
        }

        @NonNull
        @Override
        public ActiveGroupPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_active_group_pick, parent, false);
            return new ActiveGroupPickViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ActiveGroupPickViewHolder h, int position) {
            TaskActiveGrp.GroupItem item = items.get(position);
            boolean selected = item != null && item.groupId == selectedGroupId;
            h.tvName.setText(item != null ? item.groupName : "");
            h.tvId.setText(item != null ? "ID: " + item.groupId : "");
            h.itemView.setBackgroundColor(Color.parseColor(selected ? "#E8F2EC" : "#FFFFFF"));
            h.itemView.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                TaskActiveGrp.GroupItem picked = items.get(pos);
                if (picked == null) return;
                selectedGroupId = picked.groupId;
                notifyDataSetChanged();
                onSelectionChanged.accept(selectedGroupId);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    /** 列表行内「定位」等文字链与主色一致（非 Button）。 */
    private void styleMapDialogRowActionText(TextView tv) {
        tv.setAllCaps(false);
        tv.setTextColor(Color.parseColor("#00B578"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tv.setBackgroundResource(android.R.color.transparent);
    }

    private void showActiveGroupMembersDialog() {
        if (!isAdded()) return;
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        ensureSelfUserId();
        List<Long> members = loadActiveGroupMembersSorted();
        if (members.isEmpty()) {
            Toast.makeText(requireContext(), "暂无活动组成员", Toast.LENGTH_SHORT).show();
            return;
        }
        Context ctx = requireContext();
        final Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();

        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_active_group_members, null, false);
        TextView title = content.findViewById(R.id.tvActiveGrpDialogTitle);
        if (title != null) {
            title.setText("活动组成员 - " + getSelectedActiveGroupDisplayName());
        }
        RecyclerView rv = content.findViewById(R.id.rvActiveGroupMembers);
        ViewGroup.LayoutParams rvLp = rv.getLayoutParams();
        rvLp.height = computeActiveMembersScrollMaxHeightPx();
        rv.setLayoutParams(rvLp);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        final AlertDialog[] dlgRef = new AlertDialog[1];
        rv.setAdapter(new ActiveGroupMembersAdapter(members, activeGrpIds,
                peerId -> {
                    if (dlgRef[0] != null) dlgRef[0].dismiss();
                    onActiveMemberVideoClick(peerId);
                },
                peerId -> {
                    if (dlgRef[0] != null) dlgRef[0].dismiss();
                    locateToPeerOnMap(peerId);
                },
                peerId -> {
                    if (dlgRef[0] != null) dlgRef[0].dismiss();
                    startSyntheticRemoteMovingPointsTest();
                    Toast.makeText(requireContext(),
                            "已启动50人动点压测（长按成员：" + getUserDisplayName(peerId) + "）",
                            Toast.LENGTH_SHORT).show();
                }));
        DividerItemDecoration div = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
        Drawable divDrawable = ContextCompat.getDrawable(ctx, R.drawable.bg_map_dialog_list_divider);
        if (divDrawable != null) {
            div.setDrawable(divDrawable);
        }
        rv.addItemDecoration(div);

        TextView btnGroupVideo = content.findViewById(R.id.btnActiveGrpGroupVideo);
        TextView btnCancel = content.findViewById(R.id.btnActiveGrpCancel);
        TextView btnConfirm = content.findViewById(R.id.btnActiveGrpConfirm);
        View videoActionsRow = content.findViewById(R.id.layoutActiveGrpVideoActions);
        if (isMapEmbeddedInAvPage()) {
            btnGroupVideo.setVisibility(View.GONE);
            if (videoActionsRow != null) {
                videoActionsRow.setVisibility(View.GONE);
            }
        } else {
            btnGroupVideo.setOnClickListener(v -> {
                if (dlgRef[0] != null) dlgRef[0].dismiss();
                startGroupVideoCall();
            });
        }
        btnCancel.setOnClickListener(v -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });

        dlgRef[0] = createMapStyledDialog(content);
        dlgRef[0].show();
    }

    private void showActiveGroupPickDialog(boolean fromAutoPrompt) {
        if (!isAdded()) return;
        List<TaskActiveGrp.GroupItem> groups = TaskActiveGrp.getMyGroups();
        if (groups.isEmpty()) {
            Toast.makeText(requireContext(), "暂无可选择活动组", Toast.LENGTH_SHORT).show();
            return;
        }
        Context ctx = requireContext();
        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_active_group_pick, null, false);
        RecyclerView rv = content.findViewById(R.id.rvActiveGroupPick);
        ViewGroup.LayoutParams rvLp = rv.getLayoutParams();
        rvLp.height = Math.min(computeActiveMembersScrollMaxHeightPx(), dp(280));
        rv.setLayoutParams(rvLp);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        final long[] tempSelected = new long[]{getSelectedActiveGroupId()};
        TextView btnCancel = content.findViewById(R.id.btnActiveGroupPickCancel);
        TextView btnConfirm = content.findViewById(R.id.btnActiveGroupPickConfirm);
        final AlertDialog[] dlgRef = new AlertDialog[1];
        ActiveGroupPickAdapter adapter = new ActiveGroupPickAdapter(groups, tempSelected[0], groupId -> {
            tempSelected[0] = groupId;
            updateActiveGroupPickConfirmEnabled(btnConfirm, tempSelected[0]);
        });
        rv.setAdapter(adapter);
        DividerItemDecoration div = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
        Drawable divDrawable = ContextCompat.getDrawable(ctx, R.drawable.bg_map_dialog_list_divider);
        if (divDrawable != null) {
            div.setDrawable(divDrawable);
        }
        rv.addItemDecoration(div);
        updateActiveGroupPickConfirmEnabled(btnConfirm, tempSelected[0]);
        btnCancel.setOnClickListener(v -> {
            if (fromAutoPrompt) {
                TaskActiveGrp.clearSelectedGroup();
                refreshGroupStatsByActiveGroups();
                refreshActiveGroupMeetingStatus();
            }
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            if (tempSelected[0] <= 0) {
                Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
                return;
            }
            long oldGroupId = getSelectedActiveGroupId();
            TaskActiveGrp.setSelectedGroupId(tempSelected[0]);
            activeGroupPickPromptShown = true;
            if (dlgRef[0] != null) dlgRef[0].dismiss();
            if (oldGroupId != tempSelected[0]) {
                onActiveGroupChanged(true);
            } else {
                refreshGroupStatsByActiveGroups();
                refreshActiveGroupMeetingStatus();
            }
        });

        dlgRef[0] = createMapStyledDialog(content);
        dlgRef[0].show();
    }

    private void updateActiveGroupPickConfirmEnabled(@Nullable TextView btnConfirm, long selectedGroupId) {
        if (btnConfirm == null) return;
        boolean ok = selectedGroupId > 0;
        btnConfirm.setEnabled(ok);
        btnConfirm.setAlpha(ok ? 1f : 0.45f);
    }

    /** 成员行内单聊视频：静默注册 talker 后走地图悬浮窗（不打开联系人会话页）。 */
    private void onActiveMemberVideoClick(long peerId) {
        startPeerVideoCall(peerId);
    }

    private void startPeerVideoCall(long peerId) {
        if (peerId <= 0 || !isAdded()) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || !(pQyMc.gui.hMainWnd instanceof MainActivity)) {
            Toast.makeText(requireContext(), "无法发起视频通话", Toast.LENGTH_SHORT).show();
            return;
        }
        floatingVideoTargetId = peerId;
        ensureMapFloatingAvEnabled();
        registerTalkerSilent(peerId);
        int level = qyWmComm.Consts.CONST_do_1080P_AV_Call;
        if (pQyMc.appParams != null && pQyMc.appParams.bDbg) {
            level = qyWmComm.Consts.CONST_do_480P_AV_Call;
        }
        MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
        if (locationHandler != null) {
            int finalLevel = level;
            locationHandler.postDelayed(() -> tryStartPeerVideoFloating(main, peerId, finalLevel, 0), 150L);
        } else {
            tryStartPeerVideoFloating(main, peerId, level, 0);
        }
    }

    private void tryStartPeerVideoFloating(@NonNull MainActivity main, long peerId, int level, int attempt) {
        if (!isAdded()) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc != null ? pQyMc.get_pProcInfo() : null;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
        if (pMisCnt == null) return;
        if (findIsTalker(pMisCnt, peerId) == null) {
            if (attempt < 6 && locationHandler != null) {
                registerTalkerSilent(peerId);
                locationHandler.postDelayed(() -> tryStartPeerVideoFloating(main, peerId, level, attempt + 1), 200L);
                return;
            }
            Toast.makeText(requireContext(), "正在准备通话，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!anc_hasAllPermission(main)) {
            anc_isPermissionOk(main);
            Toast.makeText(requireContext(), "需要相机和麦克风权限", Toast.LENGTH_SHORT).show();
            return;
        }
        markGroupTalkAvReady(peerId);
        int rc = talk_doAvCall(main, peerId, level, false);
        Log.d("MAP_AV_DBG", "startPeerVideoFloating peer=" + peerId + " rc=" + rc);
        if (rc != 0 && isAdded()) {
            Toast.makeText(requireContext(), "无法打开视频通话", Toast.LENGTH_SHORT).show();
        }
    }

    /** 对活动组发起组会议（与具体成员无关）。 */
    private void startGroupVideoCall() {
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        if (avCallProc.blockDuplicateVideoMeetingStart()) {
            return;
        }
        long gid = getSelectedActiveGroupId();
        if (gid <= 0) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            return;
        }
        floatingVideoTargetId = gid;
        startMapGroupConferenceAndEnterFloating(gid);
    }

    private void setMapGroupConfSuppressAvNotifyUi(@NonNull MainActivity main, long groupId) {
        main.suppressAvNotifyUiForGroupId = groupId;
        MainActivity.tmp_notify_cancel_ifForegrounded(main);
    }

    private void clearMapGroupConfSuppressAvNotifyUi(long groupId) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || !(pQyMc.gui.hMainWnd instanceof MainActivity)) return;
        MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
        if (main.suppressAvNotifyUiForGroupId == groupId) {
            main.suppressAvNotifyUiForGroupId = 0;
        }
    }

    /** M0：静默注册 talker → 发起组会议 → 等待任务 → 地图悬浮窗入会。 */
    private void startMapGroupConferenceAndEnterFloating(long groupId) {
        mapGroupNotifyAcceptedGroupId = 0;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc != null && pQyMc.gui.hMainWnd instanceof MainActivity) {
            MainActivity main = (MainActivity) pQyMc.gui.hMainWnd;
            setMapGroupConfSuppressAvNotifyUi(main, groupId);
            main.closeFloatingView();
            TalkAvStarter.removeTaskAvForPeer(groupId);
            registerGroupTalkerSilent(groupId);
        }
        sendGroupConfReqFromMap(groupId, (short) qyWmComm.Consts.CONST_do_1080P_AV_Call);
        Log.d("MAP_AV_DBG", "startMapGroupConference sendConfReq, group=" + groupId);
        if (locationHandler != null) {
            locationHandler.postDelayed(() -> waitGroupConferenceTaskAndEnter(groupId, 0), 300);
        }
    }

    private void waitGroupConferenceTaskAndEnter(long groupId, int attempt) {
        if (!isAdded()) return;

        ActiveGroupMeetingState state = findActiveGroupMeetingStateForEnter();

        int liveIdx = findLiveGroupAvTaskIndexForGroup(groupId);
        if (liveIdx >= 0) {
            clearActiveGroupMeetingRecovery(groupId);
            ActiveGroupMeetingState enterState = (state != null && state.groupId == groupId)
                    ? state
                    : new ActiveGroupMeetingState(groupId, null, liveIdx, 0, true, false);
            if (enterState.indexTaskInfo != liveIdx) {
                enterState = new ActiveGroupMeetingState(enterState.groupId, enterState.talk,
                        liveIdx, enterState.taskId, true, false);
            }
            if (enterMapGroupMeetingFloating(enterState)) {
                clearMapGroupConfSuppressAvNotifyUi(groupId);
                Log.d("MAP_AV_DBG", "groupVideoAutoEnter floating, group=" + groupId
                        + ", attempt=" + attempt + ", idx=" + liveIdx);
            }
            return;
        }

        if (attempt < 30 && locationHandler != null) {
            locationHandler.postDelayed(() -> waitGroupConferenceTaskAndEnter(groupId, attempt + 1), 400);
            return;
        }
        clearMapGroupConfSuppressAvNotifyUi(groupId);
        Toast.makeText(requireContext(), "组会议任务尚未建立，请稍后重试", Toast.LENGTH_SHORT).show();
        clearActiveGroupMeetingRecovery(groupId);
        Log.d("MAP_AV_DBG", "groupVideoAutoEnter timeout waiting task, group=" + groupId);
    }

    private List<Long> loadActiveGroupMembersSorted() {
        ensureSelfUserId();
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        Set<Long> all = new HashSet<>();
        for (Long grpId : activeGrpIds) {
            if (grpId == null || grpId <= 0) continue;
            all.addAll(loadGroupMemberIds(grpId));
        }
        if (selfUserId > 0) all.remove(selfUserId);
        ArrayList<Long> list = new ArrayList<>(all);
        Collections.sort(list, Long::compareTo);
        return list;
    }

    private long pickPrimaryGroupId() {
        return hasSelectedActiveGroup() ? getSelectedActiveGroupId() : 0;
    }

    /**
     * 发起视频：
     * <ul>
     *   <li>单选：与「联系人 → 打开该用户会话 → 点视频通话」一致，走单聊 {@code doTask_startAvCall} / P2P。</li>
     *   <li>多选：与「群会话里发起视频会议」一致，对活动组 id 走 {@code sendConfReq}（非单聊 doCmd 分支）。</li>
     * </ul>
     */
    private void startVideoCallForSelection(Set<Long> selected) {
        if (selected == null || selected.isEmpty()) return;
        if (selected.size() == 1) {
            Long only = null;
            for (Long id : selected) {
                only = id;
                break;
            }
            if (only == null || only <= 0) return;
            startPeerVideoCall(only);
            return;
        }
        long gid = pickPrimaryGroupId();
        if (gid <= 0) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        floatingVideoTargetId = gid;
        startGroupVideoCall();
    }

    /**
     * 「查看摄像头」：不跳转会话，仅在地图下方为所选成员各开一小窗（可关闭、点击放大/再点还原）；
     * 若已有会议界面，小窗会镜像会议 TextureView 画面。
     */
    private void openTalkForCameraView(Set<Long> selected) {
        if (selected == null || selected.isEmpty()) return;
        showRemotePreviewStrip(selected);
        Toast.makeText(requireContext(), "已在下方打开查看窗口；有会议时可显示画面，点击窗口可放大/缩小", Toast.LENGTH_SHORT).show();
    }

    private void showRemotePreviewStrip(Set<Long> userIds) {
        if (remoteCameraStrip == null || !isAdded()) return;
        remoteCameraStrip.removeAllViews();
        remotePreviewByUser.clear();
        expandedPreviewUserId = -1;
        for (Long uid : userIds) {
            if (uid == null || uid <= 0) continue;
            FrameLayout card = buildRemotePreviewCard(uid);
            remotePreviewByUser.put(uid, card);
            remoteCameraStrip.addView(card);
        }
        if (remoteCameraStrip.getChildCount() == 0) {
            if (remoteCameraStripScroll != null) remoteCameraStripScroll.setVisibility(View.GONE);
            stopVideoMirrorLoop();
            return;
        }
        if (remoteCameraStripScroll != null) remoteCameraStripScroll.setVisibility(View.VISIBLE);
        startVideoMirrorLoop();
    }

    private void startVideoMirrorLoop() {
        locationHandler.removeCallbacks(videoMirrorRunnable);
        locationHandler.post(videoMirrorRunnable);
    }

    private void stopVideoMirrorLoop() {
        locationHandler.removeCallbacks(videoMirrorRunnable);
        clearMirrorBitmaps();
    }

    private void clearMirrorBitmaps() {
        if (remoteCameraStrip != null) {
            for (int i = 0; i < remoteCameraStrip.getChildCount(); i++) {
                View card = remoteCameraStrip.getChildAt(i);
                if (!(card instanceof ViewGroup)) continue;
                ImageView iv = card.findViewWithTag("mirror");
                if (iv != null) {
                    iv.setImageBitmap(null);
                }
            }
        }
        if (floatingVideoPanel != null) {
            floatingVideoPanel.clearMirrorBitmap();
        }
        if (lastMirrorBitmap != null && !lastMirrorBitmap.isRecycled()) {
            lastMirrorBitmap.recycle();
            lastMirrorBitmap = null;
        }
    }

    private void showFloatingVideoWindow(@NonNull String title) {
        if (!isAdded() || floatingVideoPanel == null) return;
        floatingVideoPanel.show(title);
        loadFloatingFragmentForTest();
        startVideoMirrorLoop();
    }

    private void hideFloatingVideoWindow() {
        if (floatingVideoPanel == null) return;
        floatingVideoPanel.hide();
        if (layoutFloatingFragmentHost != null) {
            layoutFloatingFragmentHost.setVisibility(View.GONE);
        }
        if (remoteCameraStrip == null || remoteCameraStrip.getChildCount() == 0) {
            stopVideoMirrorLoop();
        }
    }

    private void loadFloatingFragmentForTest() {
        if (!isAdded() || layoutFloatingFragmentHost == null) return;
        if (isEmbeddedChildMap()) return;
        layoutFloatingFragmentHost.setVisibility(View.VISIBLE);
        layoutFloatingFragmentHost.bringToFront();
        try {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layoutFloatingFragmentHost, Fragment_map.newEmbeddedChildForTest(), "floating-fragment-map-test")
                    .commitNowAllowingStateLoss();
            Toast.makeText(requireContext(), "已在悬浮窗加载 Fragment_map（测试）", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.w(TAG_OFFLINE, "load floating fragment failed", e);
            Toast.makeText(requireContext(), "悬浮窗加载 Fragment 失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean tryShowSystemOverlayTestWindow(boolean toggleWhenShowing) {
        if (!isAdded() || getContext() == null) return false;
        if (!Settings.canDrawOverlays(requireContext())) {
            Toast.makeText(requireContext(), "请先授予悬浮窗权限", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(i);
            return false;
        }
        if (systemOverlayTestWindow == null) {
            systemOverlayTestWindow = new SystemOverlayTestWindow(requireContext());
        }
        if (systemOverlayTestWindow.isShowing()) {
            if (toggleWhenShowing) {
                systemOverlayTestWindow.dismiss();
                Toast.makeText(requireContext(), "已关闭系统级悬浮窗", Toast.LENGTH_SHORT).show();
                return true;
            }
            return true;
        } else {
            systemOverlayTestWindow.show();
            Toast.makeText(requireContext(), "已显示系统级悬浮窗", Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    private FrameLayout buildRemotePreviewCard(long userId) {
        Context ctx = requireContext();
        int wSmall = dp(118);
        int h = dp(152);
        FrameLayout card = new FrameLayout(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wSmall, h);
        lp.setMargins(dp(4), 0, dp(4), 0);
        card.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xCC1A1A1A);
        bg.setCornerRadius(dp(8));
        card.setBackground(bg);
        card.setTag(userId);

        ImageView mirror = new ImageView(ctx);
        mirror.setTag("mirror");
        mirror.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mirror.setBackgroundColor(0xFF202020);
        FrameLayout.LayoutParams mlp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mlp.topMargin = dp(34);
        card.addView(mirror, mlp);

        TextView title = new TextView(ctx);
        title.setText(getUserDisplayName(userId));
        title.setTextColor(Color.WHITE);
        title.setTextSize(13f);
        title.setPadding(dp(8), dp(8), dp(36), dp(4));
        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.gravity = Gravity.TOP;
        card.addView(title, tlp);

        TextView close = new TextView(ctx);
        close.setText("✕");
        close.setTextColor(Color.WHITE);
        close.setTextSize(16f);
        close.setPadding(dp(8), dp(4), dp(8), dp(4));
        FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.gravity = Gravity.END | Gravity.TOP;
        close.setOnClickListener(v -> removeRemotePreview(userId));
        card.addView(close, clp);

        card.setOnClickListener(v -> toggleRemotePreviewExpand(userId));
        return card;
    }

    private void toggleRemotePreviewExpand(long userId) {
        if (expandedPreviewUserId == userId) {
            expandedPreviewUserId = -1;
        } else {
            expandedPreviewUserId = userId;
        }
        applyRemotePreviewSizes();
    }

    private void applyRemotePreviewSizes() {
        if (remoteCameraStrip == null) return;
        int wSmall = dp(118);
        int maxW = getResources().getDisplayMetrics().widthPixels - dp(24);
        int wLarge = Math.min(dp(300), maxW);
        for (int i = 0; i < remoteCameraStrip.getChildCount(); i++) {
            View v = remoteCameraStrip.getChildAt(i);
            Object tag = v.getTag();
            if (!(tag instanceof Long)) continue;
            long uid = (Long) tag;
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            lp.width = (uid == expandedPreviewUserId) ? wLarge : wSmall;
            v.setLayoutParams(lp);
        }
    }

    private void removeRemotePreview(long userId) {
        FrameLayout card = remotePreviewByUser.remove(userId);
        if (card != null && remoteCameraStrip != null) {
            remoteCameraStrip.removeView(card);
        }
        if (expandedPreviewUserId == userId) {
            expandedPreviewUserId = -1;
        }
        if (remoteCameraStrip != null && remoteCameraStrip.getChildCount() == 0 && remoteCameraStripScroll != null) {
            remoteCameraStripScroll.setVisibility(View.GONE);
            stopVideoMirrorLoop();
        }
    }

    private int sendHtmlToPeer(long peerId, String text) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.pMisCnt == null) return -1;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if (mainActivity == null) return -1;

        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = (int) tStartTran;

        qnmCommProc_mis.IM_HTML_CONTENT content = new qnmCommProc_mis.IM_HTML_CONTENT();
        content.uiType = imCommType_defs.Consts.CONST_imCommType_htmlContent;
        content.wBuf = text;
        int lenInBytes = 20 + text.length() * Character.BYTES;

        qnmCommProc_mis.MSG_ROUTE route = new qnmCommProc_mis.MSG_ROUTE();
        route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
        route.idInfo_to.ui64Id = peerId;

        int ret = postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(
                mainActivity.var_common.ctxCaller,
                pMisCnt,
                route,
                qnmCommProc_mis.Consts.CONST_misMsgType_talk,
                (byte) 0,
                (short) qnmCommProc.Consts.CONST_qyCmd_talkTo,
                tStartTran,
                uiTranNo,
                (short) 0,
                content,
                lenInBytes,
                peerId,
                peerId,
                0,
                null,
                false
        );
        return ret;
    }

    public int gui_procMsg(qnmCommProc_mis.MIS_MSGU pMsgU) {
        return 0;
    }



    private void handleRemoteLocMsg(long fromId, String text) {
        applyRemoteLocationPlain(fromId, text);
    }

    @NonNull
    private RemoteLoc ensureRemoteLocEntry(long userId, @Nullable String fixedLabel) {
        RemoteLoc r = remoteLocs.get(userId);
        if (r != null) {
            return r;
        }
        r = new RemoteLoc();
        r.userId = userId;
        r.arrow = new ImageView(requireContext());
        r.arrow.setImageResource(R.drawable.ic_location_heading);
        r.arrow.setLongClickable(true);
        r.arrow.setOnLongClickListener(v -> {
            showRemoteLocationVideoConfirmDialog(userId);
            return true;
        });
        r.name = new TextView(requireContext());
        r.name.setTextColor(Color.parseColor("#111111"));
        r.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        r.name.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
        r.name.setPadding(dp(4), dp(1), dp(4), dp(1));
        r.name.setSingleLine(true);
        r.name.setGravity(Gravity.CENTER);
        r.name.setMaxWidth(dp(120));
        if (!TextUtils.isEmpty(fixedLabel)) {
            r.cachedNameLabel = fixedLabel;
            r.name.setText(fixedLabel);
        }
        attachRemoteLocViewsIfNeeded(r);
        remoteLocs.put(userId, r);
        return r;
    }

    private void attachRemoteLocViewsIfNeeded(@NonNull RemoteLoc r) {
        if (r.arrow == null || r.name == null || mapView == null) return;
        if (r.arrow.getParent() != null && r.name.getParent() != null) return;
        ViewGroup parent = (ViewGroup) mapView.getParent();
        if (parent == null) return;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        FrameLayout.LayoutParams nameLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int insert = indexToInsertMapOverlayBelowRightTools(parent);
        if (r.arrow.getParent() == null) {
            parent.addView(r.arrow, insert, lp);
        }
        if (r.name.getParent() == null) {
            parent.addView(r.name, insert + 1, nameLp);
        }
        bringSelfLocationMarkerAboveMapOverlays();
    }

    private void showRemoteLocationVideoConfirmDialog(long userId) {
        if (!isAdded() || userId <= 0) return;
        ensureSelfUserId();
        if (selfUserId > 0 && userId == selfUserId) {
            return;
        }
        if (isMapEmbeddedInAvPage()) {
            Toast.makeText(requireContext(), "当前已在视频通话页面", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = getUserDisplayName(userId);
        showMapConfirmDialog("视频通话", "是否与 " + name + " 进行视频通话？", "确定", "取消",
                () -> onActiveMemberVideoClick(userId), null);
    }

    @NonNull
    private static LatLng offsetLatLngMeters(@NonNull LatLng center, float eastMeters, float northMeters) {
        final double latDegPerMeter = 1d / 111_320d;
        double cos = Math.cos(Math.toRadians(center.getLatitude()));
        double lngDegPerMeter = 1d / Math.max(1e-6, 111_320d * cos);
        return new LatLng(
                center.getLatitude() + northMeters * latDegPerMeter,
                center.getLongitude() + eastMeters * lngDegPerMeter
        );
    }

    /**
     * 压测入口：随机创建 50 个动点，每个点在自身中心 100m 范围内移动，用于模拟多人在线地图压力。
     * 可在调试时手动调用；若已在运行会先重置再启动。
     */
    public void startSyntheticRemoteMovingPointsTest() {
        if (!isAdded() || mapLibreMap == null) {
            return;
        }
        stopSyntheticRemoteMovingPointsTest();
        LatLng center = currentLatLng;
        if (center == null) {
            CameraPosition cp = mapLibreMap.getCameraPosition();
            center = cp != null && cp.target != null ? cp.target : new LatLng(39.9042, 116.4074);
        }
        long now = System.currentTimeMillis();
        for (int i = 0; i < SYNTHETIC_REMOTE_COUNT; i++) {
            long id = SYNTHETIC_REMOTE_ID_BASE + i;
            float angle = syntheticRemoteRandom.nextFloat() * 360f;
            float radius = syntheticRemoteRandom.nextFloat() * SYNTHETIC_REMOTE_RADIUS_M;
            double rad = Math.toRadians(angle);
            LatLng c = offsetLatLngMeters(
                    center,
                    (float) (Math.cos(rad) * radius),
                    (float) (Math.sin(rad) * radius));
            syntheticRemoteIds.add(id);
            syntheticRemoteCenters.put(id, c);
            syntheticRemoteAnglesDeg.put(id, angle);
            syntheticRemoteRadiusM.put(id, syntheticRemoteRandom.nextFloat() * SYNTHETIC_REMOTE_RADIUS_M);
            RemoteLoc r = ensureRemoteLocEntry(id, String.format(Locale.getDefault(), "压测%02d", i + 1));
            r.latLng = c;
            r.displayLatLng = c;
            r.heading = angle;
            r.displayHeading = normalizeHeadingDeg(angle);
            r.headingInitialized = true;
            r.smoothHeadingFrom = null;
            r.smoothHeadingTarget = null;
            r.ts = now;
        }
        syntheticRemoteTestRunning = true;
        updateRemoteOverlays(true);
        flushMarkConnectionLinesNow();
        locationHandler.removeCallbacks(syntheticRemoteStepRunnable);
        locationHandler.postDelayed(syntheticRemoteStepRunnable, SYNTHETIC_REMOTE_UPDATE_MS);
    }

    /** 停止并清理压测生成的模拟动点。 */
    public void stopSyntheticRemoteMovingPointsTest() {
        syntheticRemoteTestRunning = false;
        locationHandler.removeCallbacks(syntheticRemoteStepRunnable);
        for (Long id : new ArrayList<>(syntheticRemoteIds)) {
            if (id == null) continue;
            RemoteLoc r = remoteLocs.remove(id);
            if (r == null) continue;
            if (r.arrow != null) {
                ViewGroup p = (ViewGroup) r.arrow.getParent();
                if (p != null) p.removeView(r.arrow);
            }
            if (r.name != null) {
                ViewGroup p = (ViewGroup) r.name.getParent();
                if (p != null) p.removeView(r.name);
            }
        }
        syntheticRemoteIds.clear();
        syntheticRemoteCenters.clear();
        syntheticRemoteAnglesDeg.clear();
        syntheticRemoteRadiusM.clear();
        updateRemoteOverlays(false);
        flushMarkConnectionLinesNow();
    }

    private void maybeRefreshGroupStatsByActiveGroups() {
        long now = System.currentTimeMillis();
        if (now - lastGroupStatsRefreshTs < GROUP_STATS_REFRESH_MIN_INTERVAL_MS) {
            return;
        }
        lastGroupStatsRefreshTs = now;
        refreshGroupStatsByActiveGroups();
    }

    private boolean isUserInActiveGroupsFast(long userId) {
        if (userId <= 0) return false;
        long now = System.currentTimeMillis();
        if (now - activeGroupMemberCacheTs > ACTIVE_GROUP_MEMBER_CACHE_TTL_MS || activeGroupMemberIdsCache.isEmpty()) {
            rebuildActiveGroupMemberCache();
        }
        return activeGroupMemberIdsCache.contains(userId);
    }

    private void rebuildActiveGroupMemberCache() {
        activeGroupMemberIdsCache.clear();
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        for (Long grpId : activeGrpIds) {
            if (grpId == null || grpId <= 0) continue;
            activeGroupMemberIdsCache.addAll(loadGroupMemberIds(grpId));
        }
        activeGroupMemberCacheTs = System.currentTimeMillis();
    }

    private void updateRemoteDisplayTarget(@NonNull RemoteLoc r, @NonNull LatLng target) {
        if (r.displayLatLng == null || distanceMeters(r.displayLatLng, target) > LOCATION_SMOOTH_SNAP_DISTANCE_M) {
            r.displayLatLng = target;
            r.smoothFrom = null;
            r.smoothTarget = null;
            return;
        }
        r.smoothFrom = r.displayLatLng;
        r.smoothTarget = target;
        r.smoothStartMs = System.currentTimeMillis();
        scheduleRemoteLocationSmoothFrame();
    }

    private void updateRemoteHeadingTarget(@NonNull RemoteLoc r, float targetHeading) {
        float target = normalizeHeadingDeg(targetHeading);
        r.heading = target;
        if (!r.headingInitialized) {
            r.displayHeading = target;
            r.headingInitialized = true;
            r.smoothHeadingFrom = null;
            r.smoothHeadingTarget = null;
            return;
        }
        float delta = shortestAngleDeltaDeg(r.displayHeading, target);
        if (Math.abs(delta) < 0.5f) {
            r.displayHeading = target;
            r.smoothHeadingFrom = null;
            r.smoothHeadingTarget = null;
            return;
        }
        r.smoothHeadingFrom = r.displayHeading;
        r.smoothHeadingTarget = target;
        r.smoothHeadingStartMs = System.currentTimeMillis();
        scheduleRemoteLocationSmoothFrame();
    }

    private void scheduleRemoteLocationSmoothFrame() {
        locationHandler.removeCallbacks(remoteLocationSmoothRunnable);
        locationHandler.post(remoteLocationSmoothRunnable);
    }

    private static float normalizeHeadingDeg(float deg) {
        float v = deg % 360f;
        if (v < 0f) {
            v += 360f;
        }
        return v;
    }

    /** 沿最短弧插值朝向（0~360）。 */
    private static float lerpAngleDeg(float fromDeg, float toDeg, float t) {
        float delta = shortestAngleDeltaDeg(fromDeg, toDeg);
        return normalizeHeadingDeg(fromDeg + delta * t);
    }

    private static float shortestAngleDeltaDeg(float fromDeg, float toDeg) {
        float delta = (toDeg - fromDeg + 540f) % 360f - 180f;
        return delta;
    }

    private void updateRemoteOverlays() {
        updateRemoteOverlays(true);
    }

    /**
     * @param refreshLabels 为 false 时仅更新箭头/姓名位置与透明度，不因姓名重复 {@link TextView#setText}（相机拖动主路径）。
     */
    private void updateRemoteOverlays(boolean refreshLabels) {
        if (mapLibreMap == null) return;
        long now = System.currentTimeMillis();
        boolean anyNeedLayout = false;
        for (RemoteLoc r : remoteLocs.values()) {
            attachRemoteLocViewsIfNeeded(r);
            LatLng markerLatLng = r.displayLatLng != null ? r.displayLatLng : r.latLng;
            if (r.arrow == null || markerLatLng == null) continue;
            PointF p = mapLibreMap.getProjection().toScreenLocation(markerLatLng);
            if (p == null) continue;
            int w = r.arrow.getWidth();
            int h = r.arrow.getHeight();
            if (w == 0 || h == 0) {
                anyNeedLayout = true;
                continue;
            }
            r.arrow.setX(p.x - w / 2f);
            r.arrow.setY(p.y - h / 2f);
            float rotation = r.headingInitialized ? r.displayHeading : r.heading;
            r.arrow.setRotation(rotation);
            r.arrow.setVisibility(View.VISIBLE);
            boolean stale = now - r.ts > REMOTE_STALE_MS;
            r.arrow.setAlpha(stale ? 0.35f : 1f);
            if (r.name != null) {
                if (refreshLabels) {
                    String display = getUserDisplayNameCached(r.userId);
                    if (!TextUtils.equals(display, r.cachedNameLabel)) {
                        r.cachedNameLabel = display;
                        r.name.setText(display);
                    }
                }
                int nw = r.name.getWidth();
                int nh = r.name.getHeight();
                if (nw == 0 || nh == 0) {
                    r.name.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    nw = r.name.getMeasuredWidth();
                    nh = r.name.getMeasuredHeight();
                }
                r.name.setX(p.x - nw / 2f);
                r.name.setY(p.y + h / 2f + dp(2));
                r.name.setVisibility(View.VISIBLE);
                r.name.setAlpha(stale ? 0.35f : 1f);
            }
        }
        if (anyNeedLayout) {
            if (remoteOverlayLayoutRetrySeq < MAX_REMOTE_OVERLAY_LAYOUT_RETRIES && mapView != null) {
                mapView.removeCallbacks(remoteOverlayRetryRunnable);
                mapView.postDelayed(remoteOverlayRetryRunnable, 48);
            }
        } else {
            remoteOverlayLayoutRetrySeq = 0;
            if (mapView != null) {
                mapView.removeCallbacks(remoteOverlayRetryRunnable);
            }
        }
        bringSelfLocationMarkerAboveMapOverlays();
    }

    private long debounceDelayMsForMapLines() {
        if (mapLibreMap == null) {
            return MAP_LINES_DEBOUNCE_MS;
        }
        CameraPosition cp = mapLibreMap.getCameraPosition();
        float z = cp != null ? (float) cp.zoom : 0f;
        return z >= MAP_LINES_HIGH_ZOOM_THRESHOLD ? MAP_LINES_DEBOUNCE_HIGH_ZOOM_MS : MAP_LINES_DEBOUNCE_MS;
    }

    private void scheduleDebouncedMarkConnectionLines() {
        if (lineOverlayView == null || mapLibreMap == null) {
            return;
        }
        ensureCameraMoveStateNotStale();
        if (isCameraMovingForMarkLines) {
            pendingMarkLinesAfterCameraIdle = true;
            return;
        }
        locationHandler.removeCallbacks(debouncedMarkLinesRunnable);
        locationHandler.postDelayed(debouncedMarkLinesRunnable, debounceDelayMsForMapLines());
    }

    /** 取消防抖并立即重算作战连线（完整标签版本，用于停拖后或数据变更）。 */
    private void flushMarkConnectionLinesNow() {
        ensureCameraMoveStateNotStale();
        if (isCameraMovingForMarkLines) {
            pendingMarkLinesAfterCameraIdle = true;
            return;
        }
        locationHandler.removeCallbacks(debouncedMarkLinesRunnable);
        updateMarkConnectionLines(true);
    }

    private void ensureCameraMoveStateNotStale() {
        if (!isCameraMovingForMarkLines) {
            return;
        }
        long dt = SystemClock.elapsedRealtime() - lastCameraMoveRealtimeMs;
        if (dt <= CAMERA_MOVE_STALE_TIMEOUT_MS) {
            return;
        }
        isCameraMovingForMarkLines = false;
    }

    private void clearMarkConnectionLines() {
        if (lineOverlayView == null) {
            clearMapLibreMarkConnectionLines();
            return;
        }
        lineOverlayView.setLines(Collections.emptyList());
        clearMapLibreMarkConnectionLines();
    }

    public void setUseMapLibreMarkConnectionLines(boolean enabled) {
        if (useMapLibreMarkConnectionLines == enabled) {
            return;
        }
        useMapLibreMarkConnectionLines = enabled;
        if (enabled) {
            if (lineOverlayView != null) {
                lineOverlayView.setLines(Collections.emptyList());
            }
            updateMapLibreMarkConnectionLines();
            scheduleMapLibreSelfMarkLinkLabelsUpdate();
        } else {
            clearMapLibreMarkConnectionLines();
            clearMapLibreSelfMarkLinkLabels();
            flushMarkConnectionLinesNow();
        }
    }

    public boolean isUseMapLibreMarkConnectionLines() {
        return useMapLibreMarkConnectionLines;
    }

    public boolean isUseMapLibreSelfMarkLinkLabels() {
        return useMapLibreSelfMarkLinkLabels;
    }

    public void setUseMapLibreSelfMarkLinkLabels(boolean enabled) {
        if (useMapLibreSelfMarkLinkLabels == enabled) {
            return;
        }
        useMapLibreSelfMarkLinkLabels = enabled;
        if (enabled) {
            updateMapLibreSelfMarkLinkLabelsNow();
        } else {
            clearMapLibreSelfMarkLinkLabels();
        }
    }

    public void setUseMapLibreBattleMarks(boolean enabled) {
        if (useMapLibreBattleMarks == enabled) {
            return;
        }
        useMapLibreBattleMarks = enabled;
        if (enabled) {
            for (BattleMark mark : battleMarksOnMap.values()) {
                if (shouldRenderBattleMarkWithMapLibre(mark)) {
                    removeBattleMarkView(mark);
                }
            }
            updateMapLibreBattleMarks();
        } else {
            removeMapLibreBattleMarkLayers();
            for (BattleMark mark : battleMarksOnMap.values()) {
                ensureBattleMarkView(mark);
            }
            repositionBattleMarkRootsOnly();
        }
        flushMarkConnectionLinesNow();
    }

    public boolean isUseMapLibreBattleMarks() {
        return useMapLibreBattleMarks;
    }

    private String battleMarkIconId(int typeIndex) {
        return BATTLE_MARK_ICON_PREFIX + typeIndex;
    }

    private boolean shouldRenderBattleMarkWithMapLibre(@Nullable BattleMark mark) {
        return useMapLibreBattleMarks
                && mark != null
                && !mark.pending
                && !TextUtils.equals(editingBattleMarkId, mark.id)
                && mark.latLng != null;
    }

    private boolean shouldUseBattleMarkView(@Nullable BattleMark mark) {
        return mark != null && !shouldRenderBattleMarkWithMapLibre(mark);
    }

    private boolean ensureMapLibreBattleMarkLayers(@Nullable Style style) {
        if (style == null) return false;
        try {
            if (style.getSource(BATTLE_MARKS_SOURCE_ID) == null) {
                style.addSource(new GeoJsonSource(BATTLE_MARKS_SOURCE_ID, EMPTY_BATTLE_MARKS));
            }
            Context ctx = getContext();
            if (ctx != null) {
                for (int i = 0; i < BATTLE_MARK_TYPE_COUNT; i++) {
                    if (style.getImage(battleMarkIconId(i)) == null) {
                        Drawable icon = ContextCompat.getDrawable(ctx, getBattleMarkIconRes(i));
                        if (icon == null) continue;
                        style.addImage(battleMarkIconId(i), icon);
                    }
                }
            }
            if (style.getLayer(BATTLE_MARKS_LAYER_ID) != null) {
                style.removeLayer(BATTLE_MARKS_LAYER_ID);
            }
            SymbolLayer layer = new SymbolLayer(BATTLE_MARKS_LAYER_ID, BATTLE_MARKS_SOURCE_ID)
                    .withProperties(
                            iconImage(get("icon")),
                            iconSize(BATTLE_MARK_MAPLIBRE_ICON_SIZE),
                            iconAnchor(Property.ICON_ANCHOR_CENTER),
                            iconRotate(get("rotation")),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true));
            style.addLayer(layer);
            return true;
        } catch (Throwable e) {
            Log.w(TAG_MAP_AV_PERF, "ensure MapLibre battle marks layer failed: " + e.getMessage());
            return false;
        }
        finally {
            // 名称层是增强信息，创建失败时不应影响图钉本体展示。
            try {
                if (style.getSource(BATTLE_MARKS_OWNER_SOURCE_ID) == null) {
                    style.addSource(new GeoJsonSource(BATTLE_MARKS_OWNER_SOURCE_ID, EMPTY_BATTLE_MARKS));
                }
                if (style.getLayer(BATTLE_MARKS_OWNER_LAYER_ID) == null) {
                    SymbolLayer ownerLayer = new SymbolLayer(BATTLE_MARKS_OWNER_LAYER_ID, BATTLE_MARKS_OWNER_SOURCE_ID)
                            .withProperties(
                                    textField(get("ownerName")),
                                    textSize(11f),
                                    textColor(Color.parseColor("#1F2126")),
                                    textHaloColor(Color.parseColor("#CCFFFFFF")),
                                    textHaloWidth(1.1f),
                                    textAnchor(Property.TEXT_ANCHOR_TOP),
                                    textOffset(new Float[]{0f, 1.35f}),
                                    textAllowOverlap(true),
                                    textIgnorePlacement(true));
                    style.addLayer(ownerLayer);
                }
            } catch (Throwable e) {
                Log.w(TAG_MAP_AV_PERF, "ensure MapLibre battle mark owner layer failed: " + e.getMessage());
            }
        }
    }

    private void removeMapLibreBattleMarkLayers() {
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (style == null) return;
        try {
            if (style.getLayer(BATTLE_MARKS_OWNER_LAYER_ID) != null) {
                style.removeLayer(BATTLE_MARKS_OWNER_LAYER_ID);
            }
            if (style.getLayer(BATTLE_MARKS_LAYER_ID) != null) {
                style.removeLayer(BATTLE_MARKS_LAYER_ID);
            }
            if (style.getSource(BATTLE_MARKS_OWNER_SOURCE_ID) != null) {
                style.removeSource(BATTLE_MARKS_OWNER_SOURCE_ID);
            }
            if (style.getSource(BATTLE_MARKS_SOURCE_ID) != null) {
                style.removeSource(BATTLE_MARKS_SOURCE_ID);
            }
            for (int i = 0; i < BATTLE_MARK_TYPE_COUNT; i++) {
                if (style.getImage(battleMarkIconId(i)) != null) {
                    style.removeImage(battleMarkIconId(i));
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private void clearMapLibreBattleMarks() {
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (style == null) return;
        GeoJsonSource source = style.getSourceAs(BATTLE_MARKS_SOURCE_ID);
        if (source != null) {
            source.setGeoJson(EMPTY_BATTLE_MARKS);
        }
        GeoJsonSource ownerSource = style.getSourceAs(BATTLE_MARKS_OWNER_SOURCE_ID);
        if (ownerSource != null) {
            ownerSource.setGeoJson(EMPTY_BATTLE_MARKS);
        }
    }

    private void updateMapLibreBattleMarks() {
        if (!useMapLibreBattleMarks) {
            clearMapLibreBattleMarks();
            return;
        }
        ensureSelfUserId();
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (!ensureMapLibreBattleMarkLayers(style)) return;
        GeoJsonSource source = style.getSourceAs(BATTLE_MARKS_SOURCE_ID);
        if (source == null) return;
        GeoJsonSource ownerSource = style.getSourceAs(BATTLE_MARKS_OWNER_SOURCE_ID);

        List<Feature> features = new ArrayList<>();
        for (BattleMark mark : battleMarksOnMap.values()) {
            if (!shouldRenderBattleMarkWithMapLibre(mark)) continue;
            Feature feature = Feature.fromGeometry(Point.fromLngLat(
                    mark.latLng.getLongitude(), mark.latLng.getLatitude()));
            feature.addStringProperty("markId", mark.id);
            feature.addStringProperty("icon", battleMarkIconId(mark.typeIndex));
            feature.addNumberProperty("typeIndex", mark.typeIndex);
            feature.addNumberProperty("ownerId", mark.ownerId);
            feature.addNumberProperty("ts", mark.ts);
            feature.addNumberProperty("rotation", mark.rotationDeg);
            features.add(feature);
        }
        source.setGeoJson(features.isEmpty() ? EMPTY_BATTLE_MARKS : FeatureCollection.fromFeatures(features));
        if (ownerSource != null) {
            ownerSource.setGeoJson(EMPTY_BATTLE_MARKS);
        }
    }

    @Nullable
    private BattleMark findMapLibreBattleMarkAt(@NonNull LatLng latLng) {
        if (!useMapLibreBattleMarks || mapLibreMap == null || waitingForMarkPlacement) return null;
        try {
            PointF screenPoint = mapLibreMap.getProjection().toScreenLocation(latLng);
            if (screenPoint == null) return null;
            List<Feature> features = mapLibreMap.queryRenderedFeatures(screenPoint, BATTLE_MARKS_LAYER_ID);
            if (features == null || features.isEmpty()) return null;
            for (Feature feature : features) {
                if (feature == null) continue;
                String markId = feature.getStringProperty("markId");
                if (!TextUtils.isEmpty(markId)) {
                    BattleMark mark = battleMarksOnMap.get(markId);
                    if (mark != null) return mark;
                }
            }
        } catch (Throwable e) {
            Log.w(TAG_MAP_AV_PERF, "query MapLibre battle mark failed: " + e.getMessage());
        }
        return null;
    }

    private boolean ensureMapLibreMarkLinkLabelsLayer(@Nullable Style style) {
        if (style == null) return false;
        try {
            if (style.getSource(MARK_LINK_LABELS_SOURCE_ID) == null) {
                style.addSource(new GeoJsonSource(MARK_LINK_LABELS_SOURCE_ID, EMPTY_MARK_LINK_LABELS));
            }
            if (style.getLayer(MARK_LINK_LABELS_LAYER_ID) == null) {
                SymbolLayer layer = new SymbolLayer(MARK_LINK_LABELS_LAYER_ID, MARK_LINK_LABELS_SOURCE_ID)
                        .withProperties(
                                iconImage(get("icon")),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true));
                style.addLayer(layer);
            }
            return true;
        } catch (Throwable e) {
            Log.w(TAG_MAP_AV_PERF, "ensure MapLibre mark link labels layer failed: " + e.getMessage());
            return false;
        }
    }

    private void removeMapLibreMarkLinkLabelsLayer() {
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (style == null) return;
        try {
            if (style.getLayer(MARK_LINK_LABELS_LAYER_ID) != null) {
                style.removeLayer(MARK_LINK_LABELS_LAYER_ID);
            }
            if (style.getSource(MARK_LINK_LABELS_SOURCE_ID) != null) {
                style.removeSource(MARK_LINK_LABELS_SOURCE_ID);
            }
        } catch (Throwable ignored) {
        }
    }

    private void clearMapLibreSelfMarkLinkLabels() {
        locationHandler.removeCallbacks(mapLibreMarkLinkLabelsUpdateRunnable);
        pendingMapLibreMarkLinkLabelsUpdate = false;
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (style == null) return;
        GeoJsonSource source = style.getSourceAs(MARK_LINK_LABELS_SOURCE_ID);
        if (source != null) {
            source.setGeoJson(EMPTY_MARK_LINK_LABELS);
        }
    }

    private void scheduleMapLibreSelfMarkLinkLabelsUpdate() {
        if (!useMapLibreMarkConnectionLines || !useMapLibreSelfMarkLinkLabels) {
            clearMapLibreSelfMarkLinkLabels();
            return;
        }
        long now = SystemClock.elapsedRealtime();
        long elapsed = now - lastMapLibreMarkLinkLabelsUpdateMs;
        if (elapsed >= MARK_LINK_LABELS_UPDATE_INTERVAL_MS) {
            locationHandler.removeCallbacks(mapLibreMarkLinkLabelsUpdateRunnable);
            pendingMapLibreMarkLinkLabelsUpdate = false;
            updateMapLibreSelfMarkLinkLabelsNow();
            return;
        }
        if (!pendingMapLibreMarkLinkLabelsUpdate) {
            pendingMapLibreMarkLinkLabelsUpdate = true;
            locationHandler.postDelayed(mapLibreMarkLinkLabelsUpdateRunnable,
                    MARK_LINK_LABELS_UPDATE_INTERVAL_MS - elapsed);
        }
    }

    private void updateMapLibreSelfMarkLinkLabelsNow() {
        lastMapLibreMarkLinkLabelsUpdateMs = SystemClock.elapsedRealtime();
        if (!useMapLibreMarkConnectionLines || !useMapLibreSelfMarkLinkLabels || mapLibreMap == null) {
            clearMapLibreSelfMarkLinkLabels();
            return;
        }
        Style style = mapLibreMap.getStyle();
        if (!ensureMapLibreMarkLinkLabelsLayer(style)) return;
        GeoJsonSource source = style.getSourceAs(MARK_LINK_LABELS_SOURCE_ID);
        if (source == null) return;
        ensureSelfUserId();
        LatLng from = displayedLatLng != null ? displayedLatLng : currentLatLng;
        if (selfUserId <= 0 || from == null) {
            source.setGeoJson(EMPTY_MARK_LINK_LABELS);
            return;
        }

        List<Feature> features = new ArrayList<>();
        for (MarkLink link : markLinks.values()) {
            if (link == null || link.userId != selfUserId || TextUtils.isEmpty(link.markId)) continue;
            BattleMark mark = battleMarksOnMap.get(link.markId);
            if (mark == null || mark.latLng == null) continue;
            float[] out = new float[2];
            Location.distanceBetween(
                    from.getLatitude(), from.getLongitude(),
                    mark.latLng.getLatitude(), mark.latLng.getLongitude(), out);
            LatLng labelPoint = interpolateLatLng(from, mark.latLng, 0.55f);
            String label = String.format(Locale.getDefault(),
                    "\u6211 %s %.0f\u00b0", formatDistance(out[0]), normalizeDeg(out[1]));
            String iconId = "qy-mark-link-label-" + mark.id;
            Bitmap labelBitmap = buildMarkLinkLabelBitmap(label);
            if (labelBitmap == null) continue;
            if (style.getImage(iconId) != null) {
                style.removeImage(iconId);
            }
            style.addImage(iconId, labelBitmap);
            Feature feature = Feature.fromGeometry(Point.fromLngLat(
                    labelPoint.getLongitude(), labelPoint.getLatitude()));
            feature.addStringProperty("icon", iconId);
            feature.addStringProperty("label", label);
            feature.addStringProperty("markId", mark.id);
            feature.addNumberProperty("distance", out[0]);
            feature.addNumberProperty("bearing", normalizeDeg(out[1]));
            features.add(feature);
        }
        source.setGeoJson(features.isEmpty() ? EMPTY_MARK_LINK_LABELS : FeatureCollection.fromFeatures(features));
    }

    @Nullable
    private Bitmap buildMarkLinkLabelBitmap(@NonNull String label) {
        try {
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(Math.max(12f, dp(13)));
            textPaint.setFakeBoldText(true);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            int padH = dp(8);
            int padV = dp(4);
            int width = Math.max(1, (int) Math.ceil(textPaint.measureText(label)) + padH * 2);
            int height = Math.max(1, (int) Math.ceil(fm.descent - fm.ascent) + padV * 2);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(Color.parseColor("#99000000"));
            canvas.drawRoundRect(0f, 0f, width, height, dp(6), dp(6), bgPaint);
            float baseline = padV - fm.ascent;
            canvas.drawText(label, padH, baseline, textPaint);
            return bitmap;
        } catch (Throwable e) {
            Log.w(TAG_MAP_AV_PERF, "build mark link label bitmap failed: " + e.getMessage());
            return null;
        }
    }

    private boolean ensureMapLibreMarkConnectionLayer(@Nullable Style style) {
        if (style == null) return false;
        try {
            if (style.getSource(MARK_LINKS_SOURCE_ID) == null) {
                style.addSource(new GeoJsonSource(MARK_LINKS_SOURCE_ID, EMPTY_MARK_LINKS));
            }
            if (style.getLayer(MARK_LINKS_LAYER_ID) == null) {
                LineLayer layer = new LineLayer(MARK_LINKS_LAYER_ID, MARK_LINKS_SOURCE_ID)
                        .withProperties(
                                lineColor(get("color")),
                                lineWidth(3f),
                                lineOpacity(0.92f),
                                lineDasharray(new Float[]{4f, 3f}),
                                lineCap(Property.LINE_CAP_ROUND),
                                lineJoin(Property.LINE_JOIN_ROUND));
                style.addLayer(layer);
            }
            return true;
        } catch (Throwable e) {
            Log.w(TAG_MAP_AV_PERF, "ensure MapLibre mark links layer failed: " + e.getMessage());
            return false;
        }
    }

    private void removeMapLibreMarkConnectionLayer() {
        if (mapLibreMap == null) return;
        removeMapLibreMarkLinkLabelsLayer();
        Style style = mapLibreMap.getStyle();
        if (style == null) return;
        try {
            if (style.getLayer(MARK_LINKS_LAYER_ID) != null) {
                style.removeLayer(MARK_LINKS_LAYER_ID);
            }
            if (style.getSource(MARK_LINKS_SOURCE_ID) != null) {
                style.removeSource(MARK_LINKS_SOURCE_ID);
            }
        } catch (Throwable ignored) {
        }
    }

    private void clearMapLibreMarkConnectionLines() {
        if (mapLibreMap == null) return;
        clearMapLibreSelfMarkLinkLabels();
        Style style = mapLibreMap.getStyle();
        if (!ensureMapLibreMarkConnectionLayer(style)) return;
        GeoJsonSource source = style.getSourceAs(MARK_LINKS_SOURCE_ID);
        if (source != null) {
            source.setGeoJson(EMPTY_MARK_LINKS);
        }
    }

    private void updateMapLibreMarkConnectionLines() {
        if (mapLibreMap == null) return;
        Style style = mapLibreMap.getStyle();
        if (!ensureMapLibreMarkConnectionLayer(style)) return;
        GeoJsonSource source = style.getSourceAs(MARK_LINKS_SOURCE_ID);
        if (source == null) return;

        List<Feature> features = new ArrayList<>();
        ensureSelfUserId();
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        for (MarkLink link : markLinks.values()) {
            if (link == null || TextUtils.isEmpty(link.markId) || !shouldDrawMarkLink(link, activeGrpIds)) continue;
            BattleMark mark = battleMarksOnMap.get(link.markId);
            if (mark == null || mark.latLng == null) continue;
            LatLng from = null;
            if (link.userId == selfUserId) {
                from = displayedLatLng != null ? displayedLatLng : currentLatLng;
            } else {
                RemoteLoc r = remoteLocs.get(link.userId);
                from = r != null && r.displayLatLng != null ? r.displayLatLng : (r != null ? r.latLng : null);
            }
            if (from == null) continue;
            Feature f = Feature.fromGeometry(LineString.fromLngLats(buildMarkLinkPoints(from, mark.latLng)));
            f.addStringProperty("color", resolveMarkLinkColorHex(link));
            features.add(f);
        }
        source.setGeoJson(FeatureCollection.fromFeatures(features));
        scheduleMapLibreSelfMarkLinkLabelsUpdate();
    }

    @NonNull
    private static List<Point> buildMarkLinkPoints(@NonNull LatLng from, @NonNull LatLng to) {
        List<Point> points = new ArrayList<>(2);
        points.add(Point.fromLngLat(from.getLongitude(), from.getLatitude()));
        points.add(Point.fromLngLat(to.getLongitude(), to.getLatitude()));
        return points;
    }

    private void registerMarkLineFilterInstance() {
        synchronized (MARK_LINE_FILTER_LOCK) {
            for (Iterator<WeakReference<Fragment_map>> it = MARK_LINE_FILTER_INSTANCES.iterator(); it.hasNext(); ) {
                Fragment_map fg = it.next().get();
                if (fg == null || fg == this) {
                    it.remove();
                }
            }
            MARK_LINE_FILTER_INSTANCES.add(new WeakReference<>(this));
        }
    }

    private void unregisterMarkLineFilterInstance() {
        synchronized (MARK_LINE_FILTER_LOCK) {
            for (Iterator<WeakReference<Fragment_map>> it = MARK_LINE_FILTER_INSTANCES.iterator(); it.hasNext(); ) {
                Fragment_map fg = it.next().get();
                if (fg == null || fg == this) {
                    it.remove();
                }
            }
        }
    }

    private static boolean getSharedShowOnlySelfMarkLines() {
        synchronized (MARK_LINE_FILTER_LOCK) {
            return sShowOnlySelfMarkLines;
        }
    }

    private void setSharedShowOnlySelfMarkLines(boolean onlySelf) {
        List<Fragment_map> targets = new ArrayList<>();
        synchronized (MARK_LINE_FILTER_LOCK) {
            sShowOnlySelfMarkLines = onlySelf;
            for (Iterator<WeakReference<Fragment_map>> it = MARK_LINE_FILTER_INSTANCES.iterator(); it.hasNext(); ) {
                Fragment_map fg = it.next().get();
                if (fg == null) {
                    it.remove();
                } else {
                    targets.add(fg);
                }
            }
        }
        for (Fragment_map fg : targets) {
            fg.applySharedMarkLineFilter(onlySelf);
        }
    }

    private void applySharedMarkLineFilter(boolean onlySelf) {
        if (!isAdded()) return;
        showOnlySelfMarkLines = onlySelf;
        updateMarkLineFilterButtonUi();
        flushMarkConnectionLinesNow();
    }

    private void updateMarkLineFilterButtonUi() {
        if (ivMarkLineFilterBtn == null) return;
        if (showOnlySelfMarkLines) {
            ivMarkLineFilterBtn.setImageResource(R.drawable.ic_mark_link_me_blocked);
            ivMarkLineFilterBtn.setAlpha(1f);
            ivMarkLineFilterBtn.setContentDescription("仅显示自己的连线");
        } else {
            ivMarkLineFilterBtn.setImageResource(R.drawable.ic_mark_link_all);
            ivMarkLineFilterBtn.setAlpha(0.92f);
            ivMarkLineFilterBtn.setContentDescription("显示全员连线");
        }
    }

    private void showSyncedMarkNotesDialog() {
        if (!isAdded() || getContext() == null) return;
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        ensureSelfUserId();
        List<BattleMark> syncedMarks = new ArrayList<>();
        for (BattleMark mark : battleMarksOnMap.values()) {
            if (mark == null || mark.pending || TextUtils.isEmpty(mark.id) || mark.latLng == null) continue;
            syncedMarks.add(mark);
        }
        if (syncedMarks.isEmpty()) {
            Toast.makeText(requireContext(), "暂无同步注记", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.sort(syncedMarks, (a, b) -> Long.compare(b.ts, a.ts));

        Context ctx = requireContext();
        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_synced_mark_notes, null, false);
        RecyclerView rv = content.findViewById(R.id.rvSyncedMarkNotes);
        ViewGroup.LayoutParams rvLp = rv.getLayoutParams();
        rvLp.height = computeActiveMembersScrollMaxHeightPx();
        rv.setLayoutParams(rvLp);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        final AlertDialog[] dlgRef = new AlertDialog[1];
        rv.setAdapter(new SyncedMarkNotesAdapter(syncedMarks, mark -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
            locateToBattleMark(mark);
        }, mark -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
            showDeleteMarkConfirmDialog(mark);
        }));
        DividerItemDecoration div = new DividerItemDecoration(ctx, DividerItemDecoration.VERTICAL);
        Drawable divDrawable = ContextCompat.getDrawable(ctx, R.drawable.bg_map_dialog_list_divider);
        if (divDrawable != null) {
            div.setDrawable(divDrawable);
        }
        rv.addItemDecoration(div);

        TextView btnClose = content.findViewById(R.id.btnSyncedMarkNotesClose);
        btnClose.setOnClickListener(v -> {
            if (dlgRef[0] != null) dlgRef[0].dismiss();
        });

        dlgRef[0] = createMapStyledDialog(content);
        dlgRef[0].show();
    }

    private static final class SyncedMarkNoteViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView tvOwner;
        final TextView tvTitle;
        final TextView tvDistance;
        final TextView tvLink;
        final TextView btnLocate;
        final TextView btnDelete;

        SyncedMarkNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivSyncedMarkIcon);
            tvOwner = itemView.findViewById(R.id.tvSyncedMarkOwner);
            tvTitle = itemView.findViewById(R.id.tvSyncedMarkTitle);
            tvDistance = itemView.findViewById(R.id.tvSyncedMarkDistance);
            tvLink = itemView.findViewById(R.id.tvSyncedMarkLink);
            btnLocate = itemView.findViewById(R.id.btnSyncedMarkLocate);
            btnDelete = itemView.findViewById(R.id.btnSyncedMarkDelete);
        }
    }

    private final class SyncedMarkNotesAdapter extends RecyclerView.Adapter<SyncedMarkNoteViewHolder> {
        private final List<BattleMark> items;
        private final Consumer<BattleMark> onLocate;
        private final Consumer<BattleMark> onDelete;

        SyncedMarkNotesAdapter(@NonNull List<BattleMark> items,
                               @NonNull Consumer<BattleMark> onLocate,
                               @NonNull Consumer<BattleMark> onDelete) {
            this.items = items;
            this.onLocate = onLocate;
            this.onDelete = onDelete;
        }

        @NonNull
        @Override
        public SyncedMarkNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_synced_mark_note, parent, false);
            return new SyncedMarkNoteViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SyncedMarkNoteViewHolder h, int position) {
            BattleMark mark = items.get(position);
            h.ivIcon.setImageResource(getBattleMarkIconRes(mark.typeIndex));
            h.tvOwner.setText(getBattleMarkOwnerDisplayName(mark));
            h.tvTitle.setText(battleMarks[Math.max(0, Math.min(mark.typeIndex, battleMarks.length - 1))]);
            h.tvDistance.setText("距离: " + formatDistanceToMark(mark));
            h.tvLink.setText("连线: " + formatSelfMarkLinkState(mark.id));
            h.btnLocate.setOnClickListener(v -> onLocate.accept(mark));
            ensureSelfUserId();
            if (selfUserId > 0 && mark.ownerId == selfUserId) {
                h.btnDelete.setVisibility(View.VISIBLE);
                h.btnDelete.setOnClickListener(v -> onDelete.accept(mark));
            } else {
                h.btnDelete.setVisibility(View.GONE);
                h.btnDelete.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    @NonNull
    private String formatDistanceToMark(@NonNull BattleMark mark) {
        LatLng base = displayedLatLng != null ? displayedLatLng : currentLatLng;
        if (base == null || mark.latLng == null) return "--";
        return formatDistance(distanceMeters(base, mark.latLng));
    }

    @NonNull
    private String formatSelfMarkLinkState(@NonNull String markId) {
        MarkLink link = getSelfMarkLink(markId);
        if (link == null) return "未连线";
        return link.synced ? "已连线" : "已连线(未同步)";
    }

    private void locateToBattleMark(@NonNull BattleMark mark) {
        if (mapLibreMap == null || mark.latLng == null) return;
        mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mark.latLng, 16.0));
    }

    @NonNull
    private static String markLinkKey(long userId, @NonNull String markId) {
        return userId + "|" + markId;
    }

    @Nullable
    private MarkLink getSelfMarkLink(@NonNull String markId) {
        ensureSelfUserId();
        if (selfUserId <= 0 || TextUtils.isEmpty(markId)) return null;
        return markLinks.get(markLinkKey(selfUserId, markId));
    }

    private MarkLink upsertMarkLink(@NonNull String markId, long userId, boolean synced, long ts) {
        if (TextUtils.isEmpty(markId) || userId <= 0) return null;
        String key = markLinkKey(userId, markId);
        MarkLink link = markLinks.get(key);
        if (link == null) {
            link = new MarkLink();
            link.markId = markId;
            link.userId = userId;
            markLinks.put(key, link);
        }
        link.synced = synced || link.synced;
        link.ts = ts > 0 ? ts : System.currentTimeMillis();
        persistLocalMarkLinksToSp();
        return link;
    }

    private void removeMarkLink(@NonNull String markId, long userId) {
        if (TextUtils.isEmpty(markId) || userId <= 0) return;
        markLinks.remove(markLinkKey(userId, markId));
        persistLocalMarkLinksToSp();
    }

    @NonNull
    private String localMarkLinksPrefKey() {
        return PREF_KEY_LOCAL_LINKS_PREFIX + selfUserId
                + PREF_KEY_LOCAL_LINKS_GROUP_PART + getSelectedActiveGroupId();
    }

    private void persistLocalMarkLinksToSp() {
        if (suppressLocalMarkLinkPersistence) return;
        ensureSelfUserId();
        if (selfUserId <= 0 || !hasSelectedActiveGroup() || getContext() == null) return;
        try {
            JSONArray arr = new JSONArray();
            for (MarkLink link : markLinks.values()) {
                if (link == null || link.synced || TextUtils.isEmpty(link.markId) || link.userId <= 0) {
                    continue;
                }
                JSONObject item = new JSONObject();
                item.put("markId", link.markId);
                item.put("userId", link.userId);
                item.put("ts", link.ts);
                arr.put(item);
            }
            requireContext().getSharedPreferences(PREF_MAP_MARK_LOCAL, Context.MODE_PRIVATE)
                    .edit()
                    .putString(localMarkLinksPrefKey(), arr.toString())
                    .apply();
        } catch (Exception ignored) {
        }
    }

    private void restoreLocalMarkLinksFromSp() {
        ensureSelfUserId();
        if (selfUserId <= 0 || !hasSelectedActiveGroup() || !isAdded() || getContext() == null) return;
        try {
            String json = requireContext().getSharedPreferences(PREF_MAP_MARK_LOCAL, Context.MODE_PRIVATE)
                    .getString(localMarkLinksPrefKey(), "[]");
            if (TextUtils.isEmpty(json)) return;
            JSONArray arr = new JSONArray(json);
            suppressLocalMarkLinkPersistence = true;
            try {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = arr.optJSONObject(i);
                    if (item == null) continue;
                    String markId = item.optString("markId", null);
                    long userId = item.optLong("userId", 0L);
                    long ts = item.optLong("ts", 0L);
                    if (TextUtils.isEmpty(markId) || userId <= 0) continue;
                    if (!battleMarksOnMap.containsKey(markId)) continue;
                    MarkLink existing = markLinks.get(markLinkKey(userId, markId));
                    if (existing != null && existing.synced) continue;
                    upsertMarkLink(markId, userId, false, ts > 0 ? ts : System.currentTimeMillis());
                }
            } finally {
                suppressLocalMarkLinkPersistence = false;
            }
            persistLocalMarkLinksToSp();
        } catch (Exception ignored) {
            suppressLocalMarkLinkPersistence = false;
        }
    }

    private void tryRestoreLocalMarkLinksIfNeeded() {
        if (!isAdded() || battleMarksOnMap.isEmpty()) return;
        restoreLocalMarkLinksFromSp();
        flushMarkConnectionLinesNow();
    }

    private void removeLinksForMark(@NonNull String markId) {
        if (TextUtils.isEmpty(markId) || markLinks.isEmpty()) return;
        for (String key : new ArrayList<>(markLinks.keySet())) {
            MarkLink link = markLinks.get(key);
            if (link != null && TextUtils.equals(markId, link.markId)) {
                removeMarkLink(link.markId, link.userId);
            }
        }
    }

    private boolean shouldDrawMarkLink(@NonNull MarkLink link, @Nullable Set<Long> activeGrpIds) {
        ensureSelfUserId();
        if (link.userId == selfUserId) {
            return true;
        }
        if (showOnlySelfMarkLines || !link.synced) {
            return false;
        }
        return isMarkLinkUserOnline(link.userId, activeGrpIds);
    }

    private boolean isMarkLinkUserOnline(long userId, @Nullable Set<Long> activeGrpIds) {
        if (userId <= 0) return false;
        long now = System.currentTimeMillis();
        if (isUserRecentlyActiveOnline(userId, now)) return true;
        RemoteLoc remote = remoteLocs.get(userId);
        if (remote != null && now - remote.ts <= REMOTE_STALE_MS) return true;
        Set<Long> ids = activeGrpIds != null ? activeGrpIds : loadActiveGroupIdsFromTaskGrp();
        return isMemberOnlineForActiveGroups(userId, ids);
    }

    private int resolveMarkLinkColor(@NonNull MarkLink link) {
        return Color.parseColor(resolveMarkLinkColorHex(link));
    }

    @NonNull
    private String resolveMarkLinkColorHex(@NonNull MarkLink link) {
        ensureSelfUserId();
        if (link.userId == selfUserId) {
            return link.synced ? MARK_LINK_COLOR_SELF_SYNCED : MARK_LINK_COLOR_LOCAL;
        }
        return MARK_LINK_COLOR_OTHER_SYNCED;
    }

    private boolean isBattleMarkLatLngLikelyOnScreen(@NonNull LatLng latLng) {
        if (mapLibreMap == null) {
            return true;
        }
        try {
            VisibleRegion vr = mapLibreMap.getProjection().getVisibleRegion();
            if (vr == null) {
                return true;
            }
            LatLngBounds b = vr.latLngBounds;
            if (b == null) {
                return true;
            }
            return b.contains(latLng);
        } catch (Throwable ignored) {
            return true;
        }
    }

    /** 仅重算作战标注悬浮窗屏幕坐标（不触发作战连线，供相机高频移动路径使用）。 */
    private void repositionBattleMarkRootsOnly() {
        if (mapLibreMap == null) return;
        boolean anyNeedLayout = false;
        for (BattleMark mark : battleMarksOnMap.values()) {
            if (mark == null || mark.root == null || mark.latLng == null) continue;
            if (mark.root.getWidth() == 0 || mark.root.getHeight() == 0) {
                anyNeedLayout = true;
                continue;
            }
            PointF overlayPt = latLngToMapOverlayPoint(mark.latLng);
            if (overlayPt == null) continue;
            positionBattleMarkRootOnMap(mark, overlayPt);
            mark.root.setVisibility(View.VISIBLE);
        }
        if (anyNeedLayout) {
            if (battleOverlayLayoutRetrySeq < MAX_BATTLE_OVERLAY_LAYOUT_RETRIES && mapView != null) {
                mapView.removeCallbacks(battleOverlayLayoutRetryRunnable);
                mapView.postDelayed(battleOverlayLayoutRetryRunnable, 48);
            }
        } else {
            battleOverlayLayoutRetrySeq = 0;
            if (mapView != null) {
                mapView.removeCallbacks(battleOverlayLayoutRetryRunnable);
            }
        }
    }

    private BattleMark placeBattleMark(@NonNull LatLng latLng, int typeIndex, boolean pending, boolean isLocal) {
        String id = (isLocal ? "L" : "R") + System.currentTimeMillis() + "_" + typeIndex + "_" + battleMarksOnMap.size();
        ensureSelfUserId();
        long ownerId = isLocal ? selfUserId : 0L;
        return placeBattleMarkWithId(id, latLng, typeIndex, pending, ownerId, false);
    }

    private BattleMark placeBattleMarkWithId(
            @NonNull String id,
            @NonNull LatLng latLng,
            int typeIndex,
            boolean pending,
            long ownerId,
            boolean skipHubWrite) {
        if (mapView == null || !isAdded()) return null;
        BattleMark dup = battleMarksOnMap.remove(id);
        if (dup != null && dup.root != null) {
            ViewGroup dupParent = (ViewGroup) dup.root.getParent();
            if (dupParent != null) {
                dupParent.removeView(dup.root);
            }
        }
        if (TextUtils.equals(editingBattleMarkId, id)) {
            editingBattleMarkId = null;
        }
        BattleMark mark = new BattleMark();
        mark.id = id;
        mark.ownerId = ownerId;
        mark.typeIndex = Math.max(0, Math.min(typeIndex, battleMarks.length - 1));
        mark.latLng = latLng;
        mark.ts = System.currentTimeMillis();
        mark.pending = pending;
        setupBattleMarkOverlayViews(mark);
        bindBattleMarkOverlayListeners(mark);
        applyBattleMarkRotation(mark);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup parent = (ViewGroup) mapView.getParent();
        parent.setClipChildren(false);
        parent.setClipToPadding(false);
        parent.addView(mark.root, indexToInsertMapOverlayBelowRightTools(parent), lp);
        battleMarksOnMap.put(mark.id, mark);
        updateBattleMarkOverlays();        return mark;
    }

    private TextView buildBattleMarkOwnerTextView(@NonNull BattleMark mark) {
        TextView tv = new TextView(requireContext());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tv.setTextColor(Color.parseColor("#1F2126"));
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxWidth(dp(96));
        tv.setPadding(dp(4), dp(1), dp(4), dp(1));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#CCFFFFFF"));
        bg.setCornerRadius(dp(4));
        tv.setBackground(bg);
        updateBattleMarkOwnerLabel(mark, tv);
        return tv;
    }

    private void updateBattleMarkOwnerLabel(@Nullable BattleMark mark) {
        if (mark == null || mark.ownerName == null) return;
        updateBattleMarkOwnerLabel(mark, mark.ownerName);
    }

    private void updateBattleMarkOwnerLabel(@NonNull BattleMark mark, @NonNull TextView tv) {
        tv.setVisibility(View.GONE);
    }

    private static float normalizeBattleMarkRotationDeg(float deg) {
        float r = deg % 360f;
        if (r < 0f) r += 360f;
        return r;
    }

    private void applyBattleMarkRotation(@NonNull BattleMark mark) {
        mark.rotationDeg = normalizeBattleMarkRotationDeg(mark.rotationDeg);
        if (mark.pin != null) {
            mark.pin.setRotation(mark.rotationDeg);
        }
    }

    private static final int BATTLE_MARK_ROTATE_HANDLE_DP = 36;
    private static final int BATTLE_MARK_BTN_DP = 36;
    private static final int BATTLE_MARK_PIN_DP = 40;
    private static final int BATTLE_MARK_HUB_MIN_W_DP = 112;
    private static final int BATTLE_MARK_HUB_MIN_H_DP = 96;

    private void configureBattleMarkRootLayout(@NonNull ViewGroup root) {
        root.setClipChildren(false);
        root.setClipToPadding(false);
        root.setPadding(0, 0, 0, 0);
    }

    /** MapView 投影坐标 → 与标注 root 相同的父布局坐标。 */
    @Nullable
    private PointF latLngToMapOverlayPoint(@NonNull LatLng latLng) {
        if (mapLibreMap == null || mapView == null) {
            return null;
        }
        PointF p = mapLibreMap.getProjection().toScreenLocation(latLng);
        if (p == null) {
            return null;
        }
        p.x += mapView.getLeft();
        p.y += mapView.getTop();
        return p;
    }

    /** 地理锚点：图钉底部中心（与点击落点一致）。 */
    @NonNull
    private float[] getBattleMarkGeoAnchorInRoot(@NonNull BattleMark mark) {
        if (mark.pin != null && mark.pin.getWidth() > 0 && mark.pin.getHeight() > 0) {
            return new float[]{
                    mark.pin.getLeft() + mark.pin.getWidth() * 0.5f,
                    mark.pin.getBottom()
            };
        }
        float fallbackW = mark.editHub != null && mark.editHub.getWidth() > 0
                ? mark.editHub.getWidth() : dp(BATTLE_MARK_HUB_MIN_W_DP);
        float fallbackH = mark.editHub != null && mark.editHub.getHeight() > 0
                ? mark.editHub.getHeight() : dp(BATTLE_MARK_HUB_MIN_H_DP);
        return new float[]{fallbackW * 0.5f, fallbackH * 0.5f};
    }

    private void positionBattleMarkRootOnMap(@NonNull BattleMark mark, @NonNull PointF overlayPoint) {
        if (mark.root == null) {
            return;
        }
        float[] anchor = getBattleMarkGeoAnchorInRoot(mark);
        mark.root.setX(overlayPoint.x - anchor[0]);
        mark.root.setY(overlayPoint.y - anchor[1]);
    }

    /**
     * 待确认标注：上确定、左旋转、中图钉、右删除（以图钉为中心固定布局）。
     */
    private void setupBattleMarkOverlayViews(@NonNull BattleMark mark) {
        Context ctx = requireContext();
        mark.rotationTouchActive = false;
        mark.root = new BattleMarkRootLayout(ctx, mark);
        configureBattleMarkRootLayout(mark.root);

        mark.editHub = new FrameLayout(ctx);
        mark.editHub.setClipChildren(false);
        mark.editHub.setClipToPadding(false);
        mark.editHub.setMinimumWidth(dp(BATTLE_MARK_HUB_MIN_W_DP));
        mark.editHub.setMinimumHeight(dp(BATTLE_MARK_HUB_MIN_H_DP));

        int pinSize = dp(BATTLE_MARK_PIN_DP);
        int btnSize = dp(BATTLE_MARK_BTN_DP);

        mark.pin = new ImageView(ctx);
        mark.pin.setImageResource(getBattleMarkIconRes(mark.typeIndex));
        mark.pin.setPadding(dp(2), dp(2), dp(2), dp(2));
        mark.editHub.addView(mark.pin, new FrameLayout.LayoutParams(
                pinSize, pinSize, Gravity.CENTER));

        mark.btnOk = new TextView(ctx);
        mark.btnOk.setText("\u2713");
        mark.btnOk.setTextSize(17f);
        mark.btnOk.setGravity(Gravity.CENTER);
        mark.btnOk.setTextColor(Color.WHITE);
        GradientDrawable okBg = new GradientDrawable();
        okBg.setColor(Color.parseColor("#00A65A"));
        okBg.setCornerRadius(dp(6));
        mark.btnOk.setBackground(okBg);
        FrameLayout.LayoutParams okLp = new FrameLayout.LayoutParams(btnSize, btnSize);
        okLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mark.editHub.addView(mark.btnOk, okLp);

        mark.btnDel = new TextView(ctx);
        mark.btnDel.setText("\u2715");
        mark.btnDel.setTextSize(20f);
        mark.btnDel.setGravity(Gravity.CENTER);
        mark.btnDel.setTextColor(Color.parseColor("#D32F2F"));
        FrameLayout.LayoutParams delLp = new FrameLayout.LayoutParams(btnSize, btnSize);
        delLp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        mark.editHub.addView(mark.btnDel, delLp);

        mark.ownerName = buildBattleMarkOwnerTextView(mark);
        mark.actions = null;

        mark.root.addView(mark.editHub, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        updateBattleMarkPendingChromeVisibility(mark);
        configureBattleMarkActionButtons(mark);
    }

    private void bindBattleMarkOverlayListeners(@NonNull BattleMark mark) {
        if (mark.btnDel != null) {
            mark.btnDel.setOnClickListener(v -> removeBattleMark(mark.id));
        }
        if (mark.btnOk != null) {
            mark.btnOk.setOnClickListener(v -> showSyncConfirmDialog(mark));
        }
        if (mark.pin != null) {
            mark.pin.setOnClickListener(v -> onBattleMarkPinClicked(mark));
        }
        final View.OnLongClickListener longDeleteListener = v -> {
            ensureSelfUserId();
            if (mark.ownerId > 0 && mark.ownerId != selfUserId) {
                Toast.makeText(requireContext(), "仅创建该作战标注的人员可删除", Toast.LENGTH_SHORT).show();
                return true;
            }
            showDeleteMarkConfirmDialog(mark);
            return true;
        };
        if (mark.root != null) {
            mark.root.setOnLongClickListener(longDeleteListener);
        }
        if (mark.pin != null) {
            mark.pin.setOnLongClickListener(longDeleteListener);
        }
    }

    private void updateBattleMarkPendingChromeVisibility(@NonNull BattleMark mark) {
        int vis = mark.pending ? View.VISIBLE : View.GONE;
        if (mark.btnOk != null) {
            mark.btnOk.setVisibility(vis);
        }
        if (mark.btnDel != null) {
            mark.btnDel.setVisibility(vis);
        }
    }

    private boolean isBattleMarkRotationEditing(@NonNull BattleMark mark) {
        return mark.pending
                && !TextUtils.isEmpty(editingBattleMarkId)
                && TextUtils.equals(editingBattleMarkId, mark.id);
    }

    private void updateBattleMarkEditChrome(@NonNull BattleMark mark) {
        boolean editing = isBattleMarkRotationEditing(mark) && mark.root != null;
        if (editing) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mark.root.setElevation(dp(12));
            }
            ensureBattleMarkRotationHandle(mark);
            layoutBattleMarkRotateHandle(mark);
            bringBattleMarkActionButtonsToFront(mark);
            bringBattleMarkRootToFront(mark);
        } else {
            if (mark.root != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mark.root.setElevation(0f);
                }
                mark.root.setTranslationZ(0f);
            }
            mark.rotationTouchActive = false;
            if (mark.rotateHandle != null) {
                mark.rotateHandle.setVisibility(View.GONE);
            }
        }
    }

    private void configureBattleMarkActionButtons(@NonNull BattleMark mark) {
        if (mark.btnOk != null) {
            mark.btnOk.setClickable(true);
            mark.btnOk.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mark.btnOk.setElevation(dp(8));
            }
        }
        if (mark.btnDel != null) {
            mark.btnDel.setClickable(true);
            mark.btnDel.setFocusable(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mark.btnDel.setElevation(dp(8));
            }
        }
    }

    private void bringBattleMarkActionButtonsToFront(@NonNull BattleMark mark) {
        if (mark.btnOk != null) {
            mark.btnOk.bringToFront();
        }
        if (mark.btnDel != null) {
            mark.btnDel.bringToFront();
        }
        if (mark.pin != null) {
            mark.pin.bringToFront();
        }
    }

    /** 编辑中把标注浮层提到地图其它 overlay 之上，避免被连线箭头等挡住点击。 */
    private void bringBattleMarkRootToFront(@NonNull BattleMark mark) {
        if (mark.root == null) return;
        ViewParent parentObj = mark.root.getParent();
        if (parentObj instanceof ViewGroup) {
            ((ViewGroup) parentObj).bringChildToFront(mark.root);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mark.root.setTranslationZ(dp(20));
        }
    }

    private void layoutBattleMarkRotateHandle(@NonNull BattleMark mark) {
        ensureBattleMarkRotationHandle(mark);
        bringBattleMarkActionButtonsToFront(mark);
    }

    private void ensureBattleMarkRotationHandle(@NonNull BattleMark mark) {
        if (mark.editHub == null) return;
        if (mark.rotateHandle == null) {
            TextView handle = new TextView(requireContext());
            handle.setText("\u27f3");
            handle.setTextSize(18f);
            handle.setTextColor(Color.parseColor("#00A65A"));
            handle.setGravity(Gravity.CENTER);
            handle.setClickable(false);
            handle.setFocusable(false);
            handle.setContentDescription("旋转注记");
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#F2FFFFFF"));
            bg.setCornerRadius(dp(18));
            bg.setStroke(Math.max(1, dp(2)), Color.parseColor("#CC00A65A"));
            handle.setBackground(bg);
            int size = dp(BATTLE_MARK_ROTATE_HANDLE_DP);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
            lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            mark.editHub.addView(handle, lp);
            mark.rotateHandle = handle;
        } else if (mark.rotateHandle.getParent() != mark.editHub) {
            ViewParent oldParent = mark.rotateHandle.getParent();
            if (oldParent instanceof ViewGroup) {
                ((ViewGroup) oldParent).removeView(mark.rotateHandle);
            }
            int size = dp(BATTLE_MARK_ROTATE_HANDLE_DP);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
            lp.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
            mark.editHub.addView(mark.rotateHandle, lp);
        }
        mark.rotateHandle.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mark.rotateHandle.setElevation(dp(4));
        }
    }

    private boolean isTouchOnView(@Nullable View view, @NonNull MotionEvent event, int slop) {
        if (view == null || view.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        int w = view.getWidth();
        int h = view.getHeight();
        if (w <= 0 || h <= 0) {
            return false;
        }
        float x = event.getRawX();
        float y = event.getRawY();
        return x >= loc[0] - slop && x <= loc[0] + w + slop
                && y >= loc[1] - slop && y <= loc[1] + h + slop;
    }

    private boolean isTouchOnBattleMarkActionButton(@NonNull BattleMark mark, @NonNull MotionEvent event) {
        if (!mark.pending) {
            return false;
        }
        return isTouchOnView(mark.btnOk, event, dp(4))
                || isTouchOnView(mark.btnDel, event, dp(4));
    }

    private boolean isTouchOnBattleMarkPin(@NonNull BattleMark mark, @NonNull MotionEvent event) {
        if (isTouchOnBattleMarkActionButton(mark, event)) {
            return false;
        }
        return isTouchOnView(mark.pin, event, dp(6));
    }

    private boolean isTouchInRotateHandle(@NonNull BattleMark mark, @NonNull MotionEvent event) {
        if (mark.rotateHandle == null || mark.rotateHandle.getVisibility() != View.VISIBLE) {
            return false;
        }
        int[] loc = new int[2];
        mark.rotateHandle.getLocationOnScreen(loc);
        int w = mark.rotateHandle.getWidth() > 0 ? mark.rotateHandle.getWidth() : dp(BATTLE_MARK_ROTATE_HANDLE_DP);
        int h = mark.rotateHandle.getHeight() > 0 ? mark.rotateHandle.getHeight() : dp(BATTLE_MARK_ROTATE_HANDLE_DP);
        int slop = dp(6);
        float x = event.getRawX();
        float y = event.getRawY();
        return x >= loc[0] - slop && x <= loc[0] + w + slop
                && y >= loc[1] - slop && y <= loc[1] + h + slop;
    }

    private void requestDisallowInterceptTouchEventForBattleMark(@NonNull BattleMark mark) {
        if (mark.root == null) return;
        for (ViewParent p = mark.root.getParent(); p != null; p = p.getParent()) {
            p.requestDisallowInterceptTouchEvent(true);
        }
    }

    private boolean onBattleMarkRotationTouch(@NonNull BattleMark mark, @NonNull MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            requestDisallowInterceptTouchEventForBattleMark(mark);
        }
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_UP) {
            updateBattleMarkRotationByTouch(mark, event);
        }        return true;
    }

    private void updateBattleMarkRotationByTouch(@NonNull BattleMark mark, @NonNull MotionEvent event) {
        if (mark.root == null) return;
        int[] loc = new int[2];
        mark.root.getLocationOnScreen(loc);
        float touchX = event.getRawX() - loc[0];
        float touchY = event.getRawY() - loc[1];
        float[] center = getBattleMarkPinCenterInRoot(mark);
        float angle = (float) Math.toDegrees(Math.atan2(touchY - center[1], touchX - center[0]));
        mark.rotationDeg = normalizeBattleMarkRotationDeg(angle);
        applyBattleMarkRotation(mark);
    }

    @NonNull
    private float[] getBattleMarkPinCenterInRoot(@NonNull BattleMark mark) {
        if (mark.pin != null && mark.root != null && mark.pin.getWidth() > 0 && mark.pin.getHeight() > 0) {
            int[] pinLoc = new int[2];
            int[] rootLoc = new int[2];
            mark.pin.getLocationOnScreen(pinLoc);
            mark.root.getLocationOnScreen(rootLoc);
            float cx = pinLoc[0] - rootLoc[0] + mark.pin.getWidth() * 0.5f;
            float cy = pinLoc[1] - rootLoc[1] + mark.pin.getHeight() * 0.5f;
            return new float[]{cx, cy};
        }
        float cx = mark.root != null && mark.root.getWidth() > 0
                ? mark.root.getWidth() * 0.5f : dp(38) * 0.5f;
        float cy = dp(16) + dp(22);
        return new float[]{cx, cy};
    }

    private String getBattleMarkOwnerDisplayName(@NonNull BattleMark mark) {
        if (mark.ownerId <= 0) return "";
        ensureSelfUserId();
        if (mark.ownerId == selfUserId) return "我";
        return getUserDisplayNameCached(mark.ownerId);
    }

    private int getBattleMarkIconRes(int typeIndex) {
        Context ctx = getContext();
        if (ctx == null) {
            return R.drawable.ic_mark_target;
        }
        int idx = Math.max(0, Math.min(typeIndex, BATTLE_MARK_TYPE_COUNT - 1));
        String name = BATTLE_MARK_DRAWABLE_NAMES[idx];
        int id = ctx.getResources().getIdentifier(name, "drawable", ctx.getPackageName());
        if (id != 0) {
            return id;
        }
        return R.drawable.ic_mark_target;
    }

    private void removeBattleMarkView(@Nullable BattleMark mark) {
        if (mark == null || mark.root == null) return;
        ViewParent parent = mark.root.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mark.root);
        }
        mark.root = null;
        mark.editHub = null;
        mark.pin = null;
        mark.ownerName = null;
        mark.rotateHandle = null;
        mark.actions = null;
        mark.btnOk = null;
        mark.btnDel = null;
    }

    private void ensureBattleMarkView(@Nullable BattleMark mark) {
        if (mark == null || mark.root != null || mapView == null || !isAdded()) return;
        ViewGroup parent = (ViewGroup) mapView.getParent();
        if (parent == null) return;
        setupBattleMarkOverlayViews(mark);
        bindBattleMarkOverlayListeners(mark);
        applyBattleMarkRotation(mark);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.setClipChildren(false);
        parent.setClipToPadding(false);
        parent.addView(mark.root, indexToInsertMapOverlayBelowRightTools(parent), lp);
    }

    private void showSyncConfirmDialog(@NonNull BattleMark mark) {
        if (!isAdded()) return;
        showMapConfirmDialog("作战标注", "是否同步到各组员？", "确定", "取消", () -> {
            sendBattleMarkToPeers(mark);
            mark.pending = false;
            if (TextUtils.equals(editingBattleMarkId, mark.id)) {
                editingBattleMarkId = null;
            }
            updateBattleMarkPendingChromeVisibility(mark);
            updateBattleMarkEditChrome(mark);
            updateBattleMarkOverlays();
        }, null);
    }

    private void sendBattleMarkToPeers(@NonNull BattleMark mark) {
        ensureSelfUserId();
        if (mark.ownerId <= 0) {
            mark.ownerId = selfUserId;
        }
        cacheSyncedMark(mark);
        String payload = String.format(Locale.US, "%s,%s,%s,%d,%.6f,%.6f,%d,%d,%.1f",
                MARK_PREFIX, MARK_OP_ADD, mark.id, mark.typeIndex,
                mark.latLng.getLatitude(), mark.latLng.getLongitude(), mark.ts, mark.ownerId,
                mark.rotationDeg);
        sendMarkPayloadToActiveGroups(payload);
    }

    private void sendDeleteMarkToPeers(@NonNull String markId) {
        cacheDeletedMark(markId, System.currentTimeMillis());
        String payload = String.format("%s,%s,%s", MARK_PREFIX, MARK_OP_DEL, markId);
        sendMarkPayloadToActiveGroups(payload);
    }

    private void sendMarkLinkToPeers(@NonNull String markId, boolean add) {
        if (TextUtils.isEmpty(markId)) return;
        ensureSelfUserId();
        if (selfUserId <= 0) return;
        long ts = System.currentTimeMillis();
        if (add) {
            upsertMarkLink(markId, selfUserId, true, ts);
            cacheSyncedLink(markId, selfUserId, ts);
        } else {
            cacheDeletedLink(markId, selfUserId, ts);
        }
        String payload = String.format(Locale.US, "%s,%s,%s,%d,%d",
                MARK_LINK_PREFIX, add ? MARK_OP_ADD : MARK_OP_DEL, markId, selfUserId, ts);
        sendMarkPayloadToActiveGroups(payload);
    }

    public void setMarkSyncCacheEnabled(boolean enabled) {
        markSyncCacheEnabled = enabled;
        if (!enabled) {
            clearMarkSyncCache();
        }
    }

    private void clearMarkSyncCache() {
        syncedMarkCache.clear();
        syncedLinkCache.clear();
        deletedMarkCache.clear();
        deletedLinkCache.clear();
        lastMarkSyncReqTsByGroup.clear();
        markSyncReqAttemptByGroup.clear();
        markSyncGroupsWithSnapshot.clear();
    }

    private void tryRequestMarkSyncSnapshot() {
        tryRequestMarkSyncSnapshot(false);
    }

    /** 与地图 onResume / 登录后相同的标注与连线拉取流程；{@code force} 为 true 时跳过节流。 */
    private void tryRequestMarkSyncSnapshot(boolean force) {
        if (!markSyncCacheEnabled || !isAdded()) return;
        ensureSelfUserId();
        if (selfUserId <= 0) return;
        long now = System.currentTimeMillis();
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        for (Long groupId : activeGrpIds) {
            if (groupId == null || groupId <= 0 || !isSelfMemberOfGroup(groupId)) continue;
            Long last = lastMarkSyncReqTsByGroup.get(groupId);
            if (!force && last != null && now - last < MARK_SYNC_REQ_MIN_INTERVAL_MS) continue;
            markSyncGroupsWithSnapshot.remove(groupId);
            markSyncReqAttemptByGroup.put(groupId, 0);
            if (sendMarkSyncRequest(groupId, now)) {
                scheduleMarkSyncRequestRetry(groupId);
            }
        }
    }

    private void showManualMarkSyncConfirmDialog() {
        if (!isAdded()) return;
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        showMapConfirmDialog("同步标注", "是否重新同步标注信息？", "确认", "取消",
                this::performManualMarkSync, null);
    }

    /** 手动重新同步：与 onResume 中登录后拉取标注/连线的时序一致。 */
    private void performManualMarkSync() {
        if (!isAdded()) return;
        ensureSelfUserId();
        if (selfUserId <= 0) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasSelectedActiveGroup()) {
            Toast.makeText(requireContext(), "请先选择活动组", Toast.LENGTH_SHORT).show();
            showActiveGroupPickDialog(false);
            return;
        }
        Toast.makeText(requireContext(), "正在重新同步标注信息…", Toast.LENGTH_SHORT).show();
        trySyncBattleMarksAndCompassFromHub();
        tryRequestMarkSyncSnapshot(true);
        locationHandler.postDelayed(() -> tryRequestMarkSyncSnapshot(true), 2000L);
        locationHandler.postDelayed(() -> tryRequestMarkSyncSnapshot(true), 6000L);
        locationHandler.postDelayed(this::tryRestoreLocalMarkLinksIfNeeded, 6500L);
    }

    private void scheduleMarkSyncRequestRetry(final long groupId) {
        if (!markSyncCacheEnabled || groupId <= 0) return;
        locationHandler.postDelayed(() -> {
            if (!markSyncCacheEnabled || !isAdded() || markSyncGroupsWithSnapshot.contains(groupId)) return;
            int attempt = markSyncReqAttemptByGroup.containsKey(groupId) ? markSyncReqAttemptByGroup.get(groupId) : 0;
            if (attempt >= MARK_SYNC_REQ_MAX_RETRIES) return;
            markSyncReqAttemptByGroup.put(groupId, attempt + 1);
            if (sendMarkSyncRequest(groupId, System.currentTimeMillis())) {
                scheduleMarkSyncRequestRetry(groupId);
            }
        }, MARK_SYNC_REQ_RETRY_DELAY_MS);
    }

    private boolean sendMarkSyncRequest(long groupId, long ts) {
        if (!markSyncCacheEnabled || groupId <= 0) return false;
        ensureSelfUserId();
        if (selfUserId <= 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return false;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.m_pMapExt == null) return false;
        lastMarkSyncReqTsByGroup.put(groupId, ts);
        String payload = String.format(Locale.US, "%s,%s,%d,%d,%d,%d",
                MARK_SYNC_PREFIX, MARK_SYNC_OP_REQ, selfUserId, groupId, ts, 0);
        int ret = pProcInfo.m_pMapExt.sendMark(payload, groupId);
        Log.d(TAG_MAP_SYNC_FLOW, "req send hg group=" + groupId + " ret=" + ret);
        return ret == 0;
    }

    @NonNull
    private List<Long> selectMarkSyncRequestPeers(long groupId) {
        List<Long> out = new ArrayList<>(MARK_SYNC_REQ_MAX_PEERS);
        if (groupId <= 0) return out;
        ensureSelfUserId();
        Set<Long> groupMembers = loadGroupMemberIds(groupId);
        boolean hasGroupMembers = !groupMembers.isEmpty();
        long now = System.currentTimeMillis();
        List<Map.Entry<Long, Long>> recent = new ArrayList<>(recentActiveOnlineTs.entrySet());
        Collections.sort(recent, (a, b) -> Long.compare(
                b != null && b.getValue() != null ? b.getValue() : 0L,
                a != null && a.getValue() != null ? a.getValue() : 0L));
        for (Map.Entry<Long, Long> entry : recent) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            long userId = entry.getKey();
            if (now - entry.getValue() > REMOTE_STALE_MS) continue;
            if (!hasGroupMembers || groupMembers.contains(userId)) {
                addMarkSyncPeerIfAllowed(out, userId);
            }
            if (out.size() >= MARK_SYNC_REQ_MAX_PEERS) return out;
        }

        Map<Long, Short> statusMap = Fragment_dlgTalk_av2.tryGetGrpMemberRunningStatusMap(groupId);
        if (statusMap != null && !statusMap.isEmpty()) {
            List<Long> onlineIds = new ArrayList<>();
            for (Map.Entry<Long, Short> entry : statusMap.entrySet()) {
                if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
                if (entry.getValue() == Consts.CONST_usRunningStatus_online
                        && (!hasGroupMembers || groupMembers.contains(entry.getKey()))) {
                    onlineIds.add(entry.getKey());
                }
            }
            Collections.sort(onlineIds);
            for (Long userId : onlineIds) {
                addMarkSyncPeerIfAllowed(out, userId);
                if (out.size() >= MARK_SYNC_REQ_MAX_PEERS) return out;
            }
        }

        List<Long> dbOnlineIds = new ArrayList<>();
        for (Long userId : groupMembers) {
            if (userId == null || userId <= 0 || userId == selfUserId) continue;
            if (isUserOnline(userId)) {
                dbOnlineIds.add(userId);
            }
        }
        Collections.sort(dbOnlineIds);
        for (Long userId : dbOnlineIds) {
            addMarkSyncPeerIfAllowed(out, userId);
            if (out.size() >= MARK_SYNC_REQ_MAX_PEERS) return out;
        }
        return out;
    }

    private void addMarkSyncPeerIfAllowed(@NonNull List<Long> out, long userId) {
        if (userId <= 0 || userId == selfUserId || out.contains(userId)) return;
        if (out.size() >= MARK_SYNC_REQ_MAX_PEERS) return;
        out.add(userId);
    }

    private void handleRemoteMarkSyncMsg(long fromId, String text) {
        if (!markSyncCacheEnabled) return;
        try {
            String[] head = text.split(",", 3);
            if (head.length < 2) return;
            if (MARK_SYNC_OP_REQ.equals(head[1])) {
                handleRemoteMarkSyncRequest(fromId, text);
            } else if (MARK_SYNC_OP_SNAPSHOT.equals(head[1])) {
                handleRemoteMarkSyncSnapshot(fromId, text);
            } else if (MARK_SYNC_OP_SNAPSHOT_PART.equals(head[1])) {
                handleRemoteMarkSyncSnapshotPart(fromId, text);
            }
        } catch (Exception ignored) {
        }
    }

    private void handleRemoteMarkSyncRequest(long fromId, String text) {
        try {
            String[] arr = text.split(",");
            if (arr.length < 6) {
                Log.d(TAG_MAP_SYNC_FLOW, "req ignore legacy from=" + fromId);
                return;
            }
            long requestUserId = Long.parseLong(arr[2]);
            long groupId = Long.parseLong(arr[3]);
            int slot = Math.max(0, Math.min(MARK_SYNC_REQ_MAX_PEERS - 1, Integer.parseInt(arr[5])));
            if (requestUserId <= 0 || groupId <= 0 || requestUserId == selfUserId) return;
            if (fromId != requestUserId) return;
            if (!isSelfMemberOfGroup(groupId) || !loadGroupMemberIds(groupId).contains(requestUserId)) return;
            long delay = slot * MARK_SYNC_RESP_SLOT_DELAY_MS;
            Log.d(TAG_MAP_SYNC_FLOW, "req recv from=" + fromId
                    + " group=" + groupId + " slot=" + slot + " delay=" + delay);
            locationHandler.postDelayed(() -> sendMarkSyncSnapshotTo(requestUserId, groupId), delay);
        } catch (Exception ignored) {
        }
    }

    private void sendMarkSyncSnapshotTo(long requestUserId, long groupId) {
        if (!markSyncCacheEnabled || requestUserId <= 0 || groupId <= 0) return;
        ensureSelfUserId();
        if (selfUserId <= 0 || requestUserId == selfUserId) return;
        try {
            seedMarkSyncCacheFromCurrentMap();
            if (syncedMarkCache.isEmpty() && syncedLinkCache.isEmpty()) {
                Log.d(TAG_MAP_SYNC_FLOW, "snapshot skip empty to=" + requestUserId + " group=" + groupId);
                return;
            }
            JSONArray marks = new JSONArray();
            for (CachedMark mark : syncedMarkCache.values()) {
                if (mark == null || TextUtils.isEmpty(mark.id)) continue;
                JSONObject item = new JSONObject();
                item.put("id", mark.id);
                item.put("ownerId", mark.ownerId);
                item.put("typeIndex", mark.typeIndex);
                item.put("lat", mark.lat);
                item.put("lng", mark.lng);
                item.put("ts", mark.ts);
                item.put("rotation", mark.rotationDeg);
                marks.put(item);
            }
            JSONArray links = new JSONArray();
            for (CachedLink link : syncedLinkCache.values()) {
                if (link == null || TextUtils.isEmpty(link.markId) || link.userId <= 0) continue;
                JSONObject item = new JSONObject();
                item.put("markId", link.markId);
                item.put("userId", link.userId);
                item.put("ts", link.ts);
                links.put(item);
            }
            JSONArray deletedMarks = new JSONArray();
            for (Map.Entry<String, DeleteTombstone> entry : deletedMarkCache.entrySet()) {
                if (TextUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
                JSONObject item = new JSONObject();
                item.put("markId", entry.getKey());
                item.put("ts", entry.getValue().ts);
                deletedMarks.put(item);
            }
            JSONArray deletedLinks = new JSONArray();
            for (Map.Entry<String, DeleteTombstone> entry : deletedLinkCache.entrySet()) {
                if (TextUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
                MarkLinkKeyParts parts = parseMarkLinkKey(entry.getKey());
                if (parts == null) continue;
                JSONObject item = new JSONObject();
                item.put("markId", parts.markId);
                item.put("userId", parts.userId);
                item.put("ts", entry.getValue().ts);
                deletedLinks.put(item);
            }
            sendMarkSyncSnapshotPartsTo(requestUserId, groupId, marks, links, deletedMarks, deletedLinks);
        } catch (Exception ignored) {
        }
    }

    private void sendMarkSyncSnapshotPartsTo(long requestUserId,
                                             long groupId,
                                             @NonNull JSONArray marks,
                                             @NonNull JSONArray links,
                                             @NonNull JSONArray deletedMarks,
                                             @NonNull JSONArray deletedLinks) throws Exception {
        List<JSONObject> parts = buildMarkSyncSnapshotParts(marks, links, deletedMarks, deletedLinks);
        if (parts.isEmpty()) {
            Log.d(TAG_MAP_SYNC_FLOW, "snapshot parts skip empty to=" + requestUserId + " group=" + groupId);
            return;
        }
        final long batchId = System.currentTimeMillis();
        final int totalParts = parts.size();
        for (int i = 0; i < totalParts; i++) {
            final int partIndex = i;
            final String json = parts.get(i).toString();
            final String payload = String.format(Locale.US, "%s,%s,%d,%d,%d,%d,%d,%d,%s",
                    MARK_SYNC_PREFIX, MARK_SYNC_OP_SNAPSHOT_PART, selfUserId, requestUserId,
                    groupId, batchId, partIndex, totalParts, json);
            final int chars = payload.length();
            final int bytes = 20 + chars * Character.BYTES;
            locationHandler.postDelayed(() -> {
                int ret = sendHtmlToPeer(requestUserId, payload);
                Log.d(TAG_MAP_SYNC_SIZE, "snapshot part send to=" + requestUserId
                        + " group=" + groupId
                        + " batch=" + batchId
                        + " part=" + (partIndex + 1) + "/" + totalParts
                        + " chars=" + chars
                        + " bytes=" + bytes
                        + " ret=" + ret);
            }, partIndex * MARK_SYNC_SNAPSHOT_PART_SEND_INTERVAL_MS);
        }
    }

    @NonNull
    private List<JSONObject> buildMarkSyncSnapshotParts(@NonNull JSONArray marks,
                                                        @NonNull JSONArray links,
                                                        @NonNull JSONArray deletedMarks,
                                                        @NonNull JSONArray deletedLinks) throws Exception {
        List<JSONObject> parts = new ArrayList<>();
        JSONObject current = newEmptyMarkSyncSnapshotRoot();
        current = appendSnapshotArrayToParts(parts, current, "marks", marks);
        current = appendSnapshotArrayToParts(parts, current, "links", links);
        current = appendSnapshotArrayToParts(parts, current, "deletedMarks", deletedMarks);
        current = appendSnapshotArrayToParts(parts, current, "deletedLinks", deletedLinks);
        if (countSnapshotItems(current) > 0) {
            parts.add(current);
        }
        return parts;
    }

    @NonNull
    private JSONObject appendSnapshotArrayToParts(@NonNull List<JSONObject> parts,
                                                  @NonNull JSONObject current,
                                                  @NonNull String arrayName,
                                                  @NonNull JSONArray source) throws Exception {
        JSONObject out = current;
        for (int i = 0; i < source.length(); i++) {
            Object item = source.opt(i);
            if (item == null) continue;
            JSONObject candidate = copySnapshotRoot(out);
            candidate.getJSONArray(arrayName).put(item);
            if (countSnapshotItems(out) > 0
                    && candidate.toString().length() > MARK_SYNC_SNAPSHOT_PART_TARGET_CHARS) {
                parts.add(out);
                out = newEmptyMarkSyncSnapshotRoot();
                out.getJSONArray(arrayName).put(item);
            } else {
                out = candidate;
            }
        }
        return out;
    }

    @NonNull
    private JSONObject newEmptyMarkSyncSnapshotRoot() throws Exception {
        JSONObject root = new JSONObject();
        root.put("marks", new JSONArray());
        root.put("links", new JSONArray());
        root.put("deletedMarks", new JSONArray());
        root.put("deletedLinks", new JSONArray());
        return root;
    }

    @NonNull
    private JSONObject copySnapshotRoot(@NonNull JSONObject src) throws Exception {
        JSONObject dst = newEmptyMarkSyncSnapshotRoot();
        copySnapshotArray(src, dst, "marks");
        copySnapshotArray(src, dst, "links");
        copySnapshotArray(src, dst, "deletedMarks");
        copySnapshotArray(src, dst, "deletedLinks");
        return dst;
    }

    private void copySnapshotArray(@NonNull JSONObject src,
                                   @NonNull JSONObject dst,
                                   @NonNull String arrayName) throws Exception {
        JSONArray in = src.optJSONArray(arrayName);
        JSONArray out = dst.getJSONArray(arrayName);
        if (in == null) return;
        for (int i = 0; i < in.length(); i++) {
            Object item = in.opt(i);
            if (item != null) out.put(item);
        }
    }

    private int countSnapshotItems(@NonNull JSONObject root) {
        int n = 0;
        JSONArray marks = root.optJSONArray("marks");
        JSONArray links = root.optJSONArray("links");
        JSONArray deletedMarks = root.optJSONArray("deletedMarks");
        JSONArray deletedLinks = root.optJSONArray("deletedLinks");
        if (marks != null) n += marks.length();
        if (links != null) n += links.length();
        if (deletedMarks != null) n += deletedMarks.length();
        if (deletedLinks != null) n += deletedLinks.length();
        return n;
    }

    private void seedMarkSyncCacheFromCurrentMap() {
        if (!markSyncCacheEnabled) return;
        for (BattleMark mark : battleMarksOnMap.values()) {
            if (mark == null || mark.pending || TextUtils.isEmpty(mark.id) || mark.latLng == null) continue;
            cacheSyncedMark(mark);
        }
        for (MarkLink link : markLinks.values()) {
            if (link == null || !link.synced || TextUtils.isEmpty(link.markId) || link.userId <= 0) continue;
            cacheSyncedLink(link.markId, link.userId, link.ts);
        }
    }

    private void handleRemoteMarkSyncSnapshot(long fromId, String text) {
        try {
            String[] arr = text.split(",", 8);
            if (arr.length < 8) return;
            long senderUserId = Long.parseLong(arr[2]);
            long requestUserId = Long.parseLong(arr[3]);
            long groupId = Long.parseLong(arr[4]);
            if (requestUserId != selfUserId || groupId <= 0) return;
            if (senderUserId != 0 && senderUserId != fromId) return;
            if (!isSelfMemberOfGroup(groupId)) return;
            if (senderUserId != 0 && !loadGroupMemberIds(groupId).contains(senderUserId)) return;
            mergeMarkSyncSnapshotPayload(arr[7]);
            markSyncGroupsWithSnapshot.add(groupId);
            Log.d(TAG_MAP_SYNC_SIZE, "snapshot recv from=" + (senderUserId != 0 ? senderUserId : fromId)
                    + " group=" + groupId
                    + " chars=" + arr[7].length()
                    + " syncedGroups=" + markSyncGroupsWithSnapshot.size());
            if (senderUserId > 0) {
                markUserActiveOnline(senderUserId);
            }
            flushMarkConnectionLinesNow();
            refreshGroupStatsByActiveGroups();
        } catch (Exception ignored) {
        }
    }

    private void handleRemoteMarkSyncSnapshotPart(long fromId, String text) {
        try {
            String[] arr = text.split(",", 9);
            if (arr.length < 9) return;
            long senderUserId = Long.parseLong(arr[2]);
            long requestUserId = Long.parseLong(arr[3]);
            long groupId = Long.parseLong(arr[4]);
            String batchId = arr[5];
            int partIndex = Integer.parseInt(arr[6]);
            int totalParts = Integer.parseInt(arr[7]);
            if (requestUserId != selfUserId || groupId <= 0) return;
            if (senderUserId != 0 && senderUserId != fromId) return;
            if (!isSelfMemberOfGroup(groupId)) return;
            if (senderUserId != 0 && !loadGroupMemberIds(groupId).contains(senderUserId)) return;
            mergeMarkSyncSnapshotPayload(arr[8]);
            markSyncGroupsWithSnapshot.add(groupId);
            Log.d(TAG_MAP_SYNC_SIZE, "snapshot part recv from=" + (senderUserId != 0 ? senderUserId : fromId)
                    + " group=" + groupId
                    + " batch=" + batchId
                    + " part=" + (partIndex + 1) + "/" + totalParts
                    + " chars=" + arr[8].length()
                    + " syncedGroups=" + markSyncGroupsWithSnapshot.size());
            if (senderUserId > 0) {
                markUserActiveOnline(senderUserId);
            }
            flushMarkConnectionLinesNow();
            refreshGroupStatsByActiveGroups();
        } catch (Exception ignored) {
        }
    }

    private void mergeMarkSyncSnapshotPayload(@NonNull String payload) {
        if (!markSyncCacheEnabled || TextUtils.isEmpty(payload)) return;
        try {
            JSONObject root = new JSONObject(payload);
            JSONArray deletedMarks = root.optJSONArray("deletedMarks");
            if (deletedMarks != null) {
                for (int i = 0; i < deletedMarks.length(); i++) {
                    JSONObject item = deletedMarks.optJSONObject(i);
                    if (item != null) cacheDeletedMark(item.optString("markId", null), item.optLong("ts", 0L));
                }
            }
            JSONArray deletedLinks = root.optJSONArray("deletedLinks");
            if (deletedLinks != null) {
                for (int i = 0; i < deletedLinks.length(); i++) {
                    JSONObject item = deletedLinks.optJSONObject(i);
                    if (item != null) cacheDeletedLink(item.optString("markId", null), item.optLong("userId", 0L), item.optLong("ts", 0L));
                }
            }
            JSONArray marks = root.optJSONArray("marks");
            if (marks != null) {
                for (int i = 0; i < marks.length(); i++) {
                    JSONObject item = marks.optJSONObject(i);
                    if (item == null) continue;
                    cacheSyncedMarkData(item.optString("id", null), item.optLong("ownerId", 0L),
                            item.optInt("typeIndex", 0), item.optDouble("lat", 0d),
                            item.optDouble("lng", 0d), item.optLong("ts", 0L),
                            (float) item.optDouble("rotation", 0d));
                }
            }
            JSONArray links = root.optJSONArray("links");
            if (links != null) {
                for (int i = 0; i < links.length(); i++) {
                    JSONObject item = links.optJSONObject(i);
                    if (item != null) cacheSyncedLink(item.optString("markId", null), item.optLong("userId", 0L), item.optLong("ts", 0L));
                }
            }
            applyMarkSyncCacheToMap();
        } catch (Exception ignored) {
        }
    }

    private void cacheSyncedMark(@Nullable BattleMark mark) {
        if (mark == null || mark.latLng == null) return;
        cacheSyncedMarkData(mark.id, mark.ownerId, mark.typeIndex,
                mark.latLng.getLatitude(), mark.latLng.getLongitude(), mark.ts, mark.rotationDeg);
    }

    private void cacheSyncedMarkData(@Nullable String id, long ownerId, int typeIndex,
                                     double lat, double lng, long ts) {
        cacheSyncedMarkData(id, ownerId, typeIndex, lat, lng, ts, 0f);
    }

    private void cacheSyncedMarkData(@Nullable String id, long ownerId, int typeIndex,
                                     double lat, double lng, long ts, float rotationDeg) {
        if (!markSyncCacheEnabled || TextUtils.isEmpty(id) || ts <= 0L) return;
        DeleteTombstone deleted = deletedMarkCache.get(id);
        if (deleted != null && deleted.ts >= ts) return;
        CachedMark old = syncedMarkCache.get(id);
        if (old != null && old.ts > ts) return;
        CachedMark mark = new CachedMark();
        mark.id = id;
        mark.ownerId = ownerId;
        mark.typeIndex = typeIndex;
        mark.lat = lat;
        mark.lng = lng;
        mark.ts = ts;
        mark.rotationDeg = rotationDeg;
        syncedMarkCache.put(id, mark);
        deletedMarkCache.remove(id);
    }

    private void cacheDeletedMark(@Nullable String markId, long ts) {
        if (!markSyncCacheEnabled || TextUtils.isEmpty(markId)) return;
        long safeTs = ts > 0L ? ts : System.currentTimeMillis();
        putTombstone(deletedMarkCache, markId, safeTs);
        syncedMarkCache.remove(markId);
        for (String key : new ArrayList<>(syncedLinkCache.keySet())) {
            CachedLink link = syncedLinkCache.get(key);
            if (link != null && TextUtils.equals(markId, link.markId)) {
                syncedLinkCache.remove(key);
            }
        }
    }

    private void cacheSyncedLink(@Nullable String markId, long userId, long ts) {
        if (!markSyncCacheEnabled || TextUtils.isEmpty(markId) || userId <= 0 || ts <= 0L) return;
        DeleteTombstone deletedMark = deletedMarkCache.get(markId);
        if (deletedMark != null && deletedMark.ts >= ts) return;
        String key = markLinkKey(userId, markId);
        DeleteTombstone deletedLink = deletedLinkCache.get(key);
        if (deletedLink != null && deletedLink.ts >= ts) return;
        CachedLink old = syncedLinkCache.get(key);
        if (old != null && old.ts > ts) return;
        CachedLink link = new CachedLink();
        link.markId = markId;
        link.userId = userId;
        link.ts = ts;
        syncedLinkCache.put(key, link);
        deletedLinkCache.remove(key);
    }

    private void cacheDeletedLink(@Nullable String markId, long userId, long ts) {
        if (!markSyncCacheEnabled || TextUtils.isEmpty(markId) || userId <= 0) return;
        long safeTs = ts > 0L ? ts : System.currentTimeMillis();
        String key = markLinkKey(userId, markId);
        putTombstone(deletedLinkCache, key, safeTs);
        syncedLinkCache.remove(key);
    }

    private void putTombstone(@NonNull Map<String, DeleteTombstone> map, @NonNull String key, long ts) {
        DeleteTombstone old = map.get(key);
        if (old != null && old.ts >= ts) return;
        DeleteTombstone tombstone = new DeleteTombstone();
        tombstone.ts = ts;
        map.put(key, tombstone);
    }

    private void applyMarkSyncCacheToMap() {
        if (!markSyncCacheEnabled || !isAdded() || mapView == null) return;
        for (Map.Entry<String, DeleteTombstone> entry : deletedMarkCache.entrySet()) {
            if (entry == null || TextUtils.isEmpty(entry.getKey()) || entry.getValue() == null) continue;
            BattleMark mark = battleMarksOnMap.get(entry.getKey());
            if (mark != null && mark.ts <= entry.getValue().ts) {
                removeBattleMarkQuiet(entry.getKey());
            }
        }
        for (CachedMark cached : syncedMarkCache.values()) {
            if (cached == null || TextUtils.isEmpty(cached.id)) continue;
            DeleteTombstone deleted = deletedMarkCache.get(cached.id);
            if (deleted != null && deleted.ts >= cached.ts) continue;
            BattleMark existing = battleMarksOnMap.get(cached.id);
            if (existing != null && existing.ts > cached.ts) continue;
            BattleMark mark = placeBattleMarkWithId(cached.id, new LatLng(cached.lat, cached.lng),
                    cached.typeIndex, false, cached.ownerId, false);
            if (mark != null) {
                mark.ts = cached.ts;
                mark.rotationDeg = cached.rotationDeg;
                applyBattleMarkRotation(mark);
            }
        }
        for (CachedLink cached : syncedLinkCache.values()) {
            if (cached == null || TextUtils.isEmpty(cached.markId) || cached.userId <= 0) continue;
            if (!battleMarksOnMap.containsKey(cached.markId)) continue;
            DeleteTombstone deletedMark = deletedMarkCache.get(cached.markId);
            if (deletedMark != null && deletedMark.ts >= cached.ts) continue;
            DeleteTombstone deletedLink = deletedLinkCache.get(markLinkKey(cached.userId, cached.markId));
            if (deletedLink != null && deletedLink.ts >= cached.ts) continue;
            upsertMarkLink(cached.markId, cached.userId, true, cached.ts);
        }
        for (Map.Entry<String, DeleteTombstone> entry : deletedLinkCache.entrySet()) {
            MarkLinkKeyParts parts = parseMarkLinkKey(entry.getKey());
            if (parts == null) continue;
            MarkLink link = markLinks.get(entry.getKey());
            if (link != null && entry.getValue() != null && link.ts <= entry.getValue().ts) {
                removeMarkLink(parts.markId, parts.userId);
            }
        }
        restoreLocalMarkLinksFromSp();
        flushMarkConnectionLinesNow();
    }

    @Nullable
    private MarkLinkKeyParts parseMarkLinkKey(@Nullable String key) {
        if (TextUtils.isEmpty(key)) return null;
        int p = key.indexOf('|');
        if (p <= 0 || p >= key.length() - 1) return null;
        try {
            MarkLinkKeyParts parts = new MarkLinkKeyParts();
            parts.userId = Long.parseLong(key.substring(0, p));
            parts.markId = key.substring(p + 1);
            return parts;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class MarkLinkKeyParts {
        long userId;
        String markId;
    }

    private void showDeleteMarkConfirmDialog(@NonNull BattleMark mark) {
        if (!isAdded()) return;
        ensureSelfUserId();
        if (mark.ownerId > 0 && mark.ownerId != selfUserId) {
            Toast.makeText(requireContext(), "仅创建该作战标注的人员可删除", Toast.LENGTH_SHORT).show();
            return;
        }
        showMapConfirmDialog("删除作战标注",
                "是否删除该作战标注？\n\n注意事项：\n1. 删除将同步到各组成员。\n2. 只有创建作战标注的人员才可删除。",
                "确定", "取消",
                () -> {
                    removeBattleMark(mark.id);
                    sendDeleteMarkToPeers(mark.id);
                }, null);
    }

    private void handleRemoteMarkLinkMsg(long fromId, String text) {
        try {
            String[] arr = text.split(",");
            if (arr.length < 5) return;
            String op = arr[1];
            String markId = arr[2];
            long userId = Long.parseLong(arr[3]);
            long ts = Long.parseLong(arr[4]);
            if (TextUtils.isEmpty(markId) || userId <= 0) return;
            if (userId != fromId && !isKnownActiveGroupMember(userId)) {
                return;
            }
            if (MARK_OP_ADD.equals(op)) {
                upsertMarkLink(markId, userId, true, ts);
                cacheSyncedLink(markId, userId, ts);
            } else if (MARK_OP_DEL.equals(op)) {
                removeMarkLink(markId, userId);
                cacheDeletedLink(markId, userId, ts);
            } else {
                return;
            }
            markUserActiveOnline(userId);
            flushMarkConnectionLinesNow();
            refreshGroupStatsByActiveGroups();
        } catch (Exception ignored) {
        }
    }

    private void handleRemoteMarkMsg(long fromId, String text) {
        try {
            String[] arr = text.split(",");
            BattleMark receivedMark = null;
            if (arr.length >= 3 && MARK_OP_DEL.equals(arr[1])) {
                String markId = arr[2];
                if (!TextUtils.isEmpty(markId)) {
                    cacheDeletedMark(markId, System.currentTimeMillis());
                    BattleMark mark = battleMarksOnMap.get(markId);
                    if (mark == null) {
                        removeLinksForMark(markId);
                        flushMarkConnectionLinesNow();
                    } else if (mark.ownerId == fromId) {
                        removeBattleMark(markId);
                    }
                }
            } else if (arr.length >= 9 && MARK_OP_ADD.equals(arr[1])) {
                String markId = arr[2];
                int typeIndex = Integer.parseInt(arr[3]);
                double lat = Double.parseDouble(arr[4]);
                double lng = Double.parseDouble(arr[5]);
                long ts = Long.parseLong(arr[6]);
                long ownerId = Long.parseLong(arr[7]);
                float rotationDeg = Float.parseFloat(arr[8]);
                receivedMark = placeBattleMarkWithId(markId, new LatLng(lat, lng), typeIndex, false, ownerId, false);
                if (receivedMark != null) {
                    receivedMark.ts = ts;
                    receivedMark.rotationDeg = rotationDeg;
                    applyBattleMarkRotation(receivedMark);
                    updateBattleMarkOverlays();
                    cacheSyncedMark(receivedMark);
                }
            } else if (arr.length >= 8 && MARK_OP_ADD.equals(arr[1])) {
                String markId = arr[2];
                int typeIndex = Integer.parseInt(arr[3]);
                double lat = Double.parseDouble(arr[4]);
                double lng = Double.parseDouble(arr[5]);
                long ts = Long.parseLong(arr[6]);
                long ownerId = Long.parseLong(arr[7]);
                receivedMark = placeBattleMarkWithId(markId, new LatLng(lat, lng), typeIndex, false, ownerId, false);
                if (receivedMark != null) {
                    receivedMark.ts = ts;
                    cacheSyncedMark(receivedMark);
                }
            } else if (arr.length >= 7 && MARK_OP_ADD.equals(arr[1])) {
                String markId = arr[2];
                int typeIndex = Integer.parseInt(arr[3]);
                double lat = Double.parseDouble(arr[4]);
                double lng = Double.parseDouble(arr[5]);
                long ts = Long.parseLong(arr[6]);
                // 兼容旧 add 消息（无 ownerId）：默认消息发送者为创建者。
                receivedMark = placeBattleMarkWithId(markId, new LatLng(lat, lng), typeIndex, false, fromId, false);
            } else if (arr.length >= 5) {
                // 兼容旧格式：__QY_MARK_V1__,type,lat,lng,ts
                int typeIndex = Integer.parseInt(arr[1]);
                double lat = Double.parseDouble(arr[2]);
                double lng = Double.parseDouble(arr[3]);
                BattleMark m = placeBattleMark(new LatLng(lat, lng), typeIndex, false, false);
                if (m != null) {
                    m.ownerId = fromId;
                }
                receivedMark = m;
            } else {
                return;
            }
            markUserActiveOnline(fromId);
            flushMarkConnectionLinesNow();
            refreshGroupStatsByActiveGroups();
            if (receivedMark != null) {
                cacheSyncedMark(receivedMark);
                showRemoteMarkWarningToast(fromId, receivedMark);
            }
        } catch (Exception ignored) {
        }
    }

    private void showRemoteMarkWarningToast(long fromId, @NonNull BattleMark mark) {
        if (!isAdded() || getContext() == null) return;
        String title = "收到同步注记";
        String who = getUserDisplayNameCached(fromId);
        String dist = formatDistanceToMark(mark);
        Toast toast = Toast.makeText(requireContext(),
                title + "：" + who + "，距离 " + dist + "，点击定位", Toast.LENGTH_LONG);
        View toastView = toast.getView();
        if (toastView != null) {
            toastView.setClickable(true);
            toastView.setOnClickListener(v -> {
                toast.cancel();
                locateToBattleMark(mark);
            });
        }
        toast.show();
        showRemoteMarkNoticeBanner(title + "：" + who + "，距离 " + dist + "，点击定位", mark);
    }

    private void showRemoteMarkNoticeBanner(@NonNull String text, @NonNull BattleMark mark) {
        if (!isAdded() || mapView == null || getContext() == null) return;
        ViewParent parentObj = mapView.getParent();
        if (!(parentObj instanceof ViewGroup)) return;
        ViewGroup parent = (ViewGroup) parentObj;
        removeRemoteMarkNoticeView();

        TextView notice = new TextView(requireContext());
        notice.setText(text);
        notice.setTextColor(Color.WHITE);
        notice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        notice.setGravity(Gravity.CENTER_VERTICAL);
        notice.setSingleLine(true);
        notice.setEllipsize(TextUtils.TruncateAt.END);
        notice.setPadding(dp(14), dp(10), dp(14), dp(10));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#E6B45309"));
        bg.setCornerRadius(dp(6));
        notice.setBackground(bg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notice.setElevation(dp(10));
        }
        notice.setClickable(true);
        notice.setFocusable(true);
        notice.setOnClickListener(v -> {
            removeRemoteMarkNoticeView();
            locateToBattleMark(mark);
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        lp.leftMargin = dp(12);
        lp.rightMargin = dp(12);
        lp.topMargin = dp(64);
        parent.addView(notice, lp);
        notice.bringToFront();
        remoteMarkNoticeView = notice;
        locationHandler.postDelayed(() -> {
            if (remoteMarkNoticeView == notice) {
                removeRemoteMarkNoticeView();
            }
        }, 8000L);
    }

    private void removeRemoteMarkNoticeView() {
        if (remoteMarkNoticeView == null) return;
        ViewParent parent = remoteMarkNoticeView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(remoteMarkNoticeView);
        }
        remoteMarkNoticeView = null;
    }

    private boolean moveEditingBattleMarkIfNeeded(@NonNull LatLng latLng) {
        if (TextUtils.isEmpty(editingBattleMarkId)) return false;
        BattleMark mark = battleMarksOnMap.get(editingBattleMarkId);
        if (mark == null || !mark.pending) {
            editingBattleMarkId = null;
            return false;
        }
        mark.latLng = latLng;
        mark.ts = System.currentTimeMillis();
        updateBattleMarkOverlays();        return true;
    }

    private void updateBattleMarkOverlays() {
        for (BattleMark mark : battleMarksOnMap.values()) {
            updateBattleMarkOwnerLabel(mark);
            updateBattleMarkEditChrome(mark);
            if (shouldRenderBattleMarkWithMapLibre(mark)) {
                removeBattleMarkView(mark);
            } else if (shouldUseBattleMarkView(mark)) {
                ensureBattleMarkView(mark);
            }
        }
        updateMapLibreBattleMarks();
        repositionBattleMarkRootsOnly();
        flushMarkConnectionLinesNow();
    }

    private void removeBattleMarkQuiet(@NonNull String id) {
        BattleMark mark = battleMarksOnMap.remove(id);
        removeLinksForMark(id);
        if (mark == null) {
            updateMapLibreBattleMarks();
            flushMarkConnectionLinesNow();
            return;
        }
        if (TextUtils.equals(editingBattleMarkId, id)) {
            editingBattleMarkId = null;
        }
        removeBattleMarkView(mark);
        updateMapLibreBattleMarks();
        flushMarkConnectionLinesNow();
    }

    private void removeBattleMark(@NonNull String id) {
        removeBattleMarkQuiet(id);    }

    private void clearBattleMarks() {
        for (BattleMark mark : battleMarksOnMap.values()) {
            if (mark == null || mark.root == null) continue;
            ViewGroup parent = (ViewGroup) mark.root.getParent();
            if (parent != null) parent.removeView(mark.root);
        }
        battleMarksOnMap.clear();
        markLinks.clear();
        editingBattleMarkId = null;
        clearMapLibreBattleMarks();
        flushMarkConnectionLinesNow();
    }

    private void ensureLineOverlayAttached() {
        if (mapView == null) return;
        ViewGroup parent = (ViewGroup) mapView.getParent();
        if (parent == null) return;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (lineOverlayView == null) {
            lineOverlayView = new LineOverlayView(requireContext());
            lineOverlayView.setClickable(false);
            lineOverlayView.setFocusable(false);
        }
        int mapIdx = parent.indexOfChild(mapView);
        if (mapIdx < 0) {
            mapIdx = 0;
        }
        int desiredIdx = mapIdx + 1;
        if (lineOverlayView.getParent() == null) {
            parent.addView(lineOverlayView, desiredIdx, lp);
        } else if (lineOverlayView.getParent() == parent) {
            int cur = parent.indexOfChild(lineOverlayView);
            if (cur != desiredIdx) {
                parent.removeView(lineOverlayView);
                parent.addView(lineOverlayView, desiredIdx, lp);
            }
        } else {
            ViewGroup old = (ViewGroup) lineOverlayView.getParent();
            old.removeView(lineOverlayView);
            parent.addView(lineOverlayView, desiredIdx, lp);
        }
        // 禁止 bringToFront：全屏 overlay 会盖住右侧定位菜单/按钮，导致 tvLocateMenuSelf 等点击永远进不了监听器。
        if (layoutLocatePanel != null && layoutLocatePanel.getVisibility() == View.VISIBLE) {
            bringLocatePanelToFront();
        }
        if (layoutLayerPanel != null && layoutLayerPanel.getVisibility() == View.VISIBLE) {
            bringLayerBarToFront();
        }
    }

    @SuppressLint("DefaultLocale")
    private void onNavigationButtonClicked() {
        if (!isAdded() || getContext() == null) return;
        if (isInAppNavigationActive()) {
            Toast.makeText(requireContext(), "请点击取消导航", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentLatLng == null) {
            Toast.makeText(requireContext(), "请先等待定位成功", Toast.LENGTH_SHORT).show();
            return;
        }
        // 按需求：除下载离线地图外，导航流程不再依赖任何在线服务。
        pendingNavCityName = null;
        beginDestinationPicking();
    }

    private boolean isInAppNavigationActive() {
        if (layoutNavBar == null || layoutNavBar.getVisibility() != View.VISIBLE) {
            return false;
        }
        return pendingNavDestination != null || !navigationRouteWgs84.isEmpty();
    }

    private void showOfflineMapPromptThenContinue(@NonNull String cityName) {
        showMapConfirmDialog("离线地图",
                "当前城市：" + cityName + "\n是否下载该城市离线地图数据？\n（下载完成后可选择导航终点）",
                "下载", "跳过",
                () -> startAmapOfflineDownload(cityName),
                () -> {
                    pendingNavCityName = cityName;
                    beginDestinationPicking();
                });
    }

    private void startAmapOfflineDownload(@NonNull String cityName) {
        Log.i(TAG_OFFLINE, "startAmapOfflineDownload city=" + cityName);
        dismissOfflineDownloadDialog();
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, "离线地图下载");
        hideMapDialogFooter(content);
        android.widget.ProgressBar pb = new android.widget.ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(100);
        pb.setProgress(0);
        TextView tv = new TextView(requireContext());
        tv.setPadding(dp(16), dp(4), dp(16), dp(12));
        tv.setText("准备下载…");
        tv.setTextColor(Color.parseColor("#111111"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout box = new LinearLayout(requireContext());
        box.setOrientation(LinearLayout.VERTICAL);
        box.addView(tv);
        box.addView(pb);
        setMapDialogCustomContent(content, box);
        offlineDownloadDialog = createMapStyledDialog(content);
        offlineDownloadDialog.setCancelable(true);
        offlineDownloadDialog.setOnCancelListener(d -> amapOfflineMapMgrKeepalive = null);
        offlineDownloadDialog.show();
        amapOfflineMapMgrKeepalive = AmapOfflineMapHelper.ensureCityOffline(requireContext(), cityName, new AmapOfflineMapHelper.DownloadUi() {
            @Override
            public void onProgress(int percent, String name) {
                locationHandler.post(() -> {
                    if (!isAdded() || offlineDownloadDialog == null || !offlineDownloadDialog.isShowing()) return;
                    pb.setProgress(percent);
                    tv.setText(name + " " + percent + "%");
                });
            }

            @Override
            public void onComplete(String name) {
                Log.i(TAG_OFFLINE, "offline download complete name=" + name);
                locationHandler.post(() -> {
                    dismissOfflineDownloadDialog();
                    amapOfflineMapMgrKeepalive = null;
                    Toast.makeText(requireContext(), "离线地图已就绪", Toast.LENGTH_SHORT).show();
                    pendingNavCityName = name;
                    beginDestinationPicking();
                });
            }

            @Override
            public void onError(String message) {
                Log.e(TAG_OFFLINE, "offline download error: " + message);
                locationHandler.post(() -> {
                    dismissOfflineDownloadDialog();
                    amapOfflineMapMgrKeepalive = null;
                    Toast.makeText(requireContext(), message != null ? message : "离线下载失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void dismissOfflineDownloadDialog() {
        if (offlineDownloadDialog != null && offlineDownloadDialog.isShowing()) {
            try {
                offlineDownloadDialog.dismiss();
            } catch (Exception e) {
                Log.w(TAG_OFFLINE, "dismiss offline dialog", e);
            }
        }
        offlineDownloadDialog = null;
    }

    private void beginDestinationPicking() {
        waitingForNavDestPick = true;
        Toast.makeText(requireContext(), "请点击地图选择终点，或点击作战标注图钉", Toast.LENGTH_LONG).show();
    }

    private boolean handleNavigationDestinationTap(@NonNull LatLng latLng) {
        if (!waitingForNavDestPick) return false;
        BattleMark near = findNearestBattleMark(latLng, 55f);
        if (near != null) {
            onNavigationDestinationSelected(near.latLng, battleMarks[near.typeIndex]);
        } else {
            onNavigationDestinationSelected(latLng, "地图选点");
        }
        return true;
    }

    private BattleMark findNearestBattleMark(@NonNull LatLng tap, float maxMeters) {
        BattleMark best = null;
        float bestD = Float.MAX_VALUE;
        for (BattleMark m : battleMarksOnMap.values()) {
            if (m == null || m.latLng == null) continue;
            float[] d = new float[1];
            Location.distanceBetween(
                    tap.getLatitude(), tap.getLongitude(),
                    m.latLng.getLatitude(), m.latLng.getLongitude(), d);
            if (d[0] <= maxMeters && d[0] < bestD) {
                bestD = d[0];
                best = m;
            }
        }
        return best;
    }

    private void onBattleMarkPinClicked(@NonNull BattleMark mark) {
        if (waitingForNavDestPick && mark.latLng != null) {
            onNavigationDestinationSelected(mark.latLng, battleMarks[mark.typeIndex]);
            return;
        }
        showToggleMarkLinkConfirmDialog(mark);
    }

    private void showToggleMarkLinkConfirmDialog(@NonNull BattleMark mark) {
        if (!isAdded() || getContext() == null || TextUtils.isEmpty(mark.id)) return;
        ensureSelfUserId();
        if (selfUserId <= 0) {
            Toast.makeText(requireContext(), "未获取到当前用户，无法连线", Toast.LENGTH_SHORT).show();
            return;
        }
        MarkLink link = getSelfMarkLink(mark.id);
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, "作战连线");
        AlertDialog dialog = createMapStyledDialog(content);
        TextView btnCancel = content.findViewById(R.id.btnMapDialogCancel);
        TextView btnConfirm = content.findViewById(R.id.btnMapDialogConfirm);
        View btnDivider = content.findViewById(R.id.viewMapDialogFooterBtnDivider);
        if (link == null) {
            bindMapDialogMessage(content, "是否与该目标点连线？");
            addMapDialogActionRow(content, "仅本地连线", () -> {
                dialog.dismiss();
                upsertMarkLink(mark.id, selfUserId, false, System.currentTimeMillis());
                flushMarkConnectionLinesNow();
            });
            addMapDialogActionRow(content, "连线并同步", () -> {
                dialog.dismiss();
                sendMarkLinkToPeers(mark.id, true);
                flushMarkConnectionLinesNow();
            });
            btnConfirm.setVisibility(View.GONE);
            if (btnDivider != null) btnDivider.setVisibility(View.GONE);
            btnCancel.setText("取消");
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        } else if (!link.synced) {
            bindMapDialogMessage(content, "是否同步或删除与该目标点的连线？");
            addMapDialogActionRow(content, "同步", () -> {
                dialog.dismiss();
                sendMarkLinkToPeers(mark.id, true);
                flushMarkConnectionLinesNow();
            });
            addMapDialogActionRow(content, "删除连线", () -> {
                dialog.dismiss();
                removeMarkLink(mark.id, selfUserId);
                flushMarkConnectionLinesNow();
            });
            btnConfirm.setVisibility(View.GONE);
            if (btnDivider != null) btnDivider.setVisibility(View.GONE);
            btnCancel.setText("取消");
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        } else {
            bindMapDialogMessage(content, "是否断开与该目标点的连线？");
            btnConfirm.setText("断开连线");
            btnCancel.setText("取消");
            btnConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                removeMarkLink(mark.id, selfUserId);
                sendMarkLinkToPeers(mark.id, false);
                flushMarkConnectionLinesNow();
            });
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        }
        dialog.show();
    }

    private void onNavigationDestinationSelected(@NonNull LatLng dest, @NonNull String label) {
        waitingForNavDestPick = false;
        pendingNavDestination = dest;
        pendingNavDestLabel = label;
        showTransportAndConfirmDialog();
    }

    private void showTransportAndConfirmDialog() {
        if (!isAdded() || getContext() == null || pendingNavDestination == null) return;
        View sheet = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nav_transport, null, false);
        TextView sum = sheet.findViewById(R.id.tvNavDestSummary);
        android.widget.RadioButton rbDrive = sheet.findViewById(R.id.rbNavDrive);
        android.widget.RadioButton rbWalk = sheet.findViewById(R.id.rbNavWalk);
        if (sum != null) {
            sum.setText(String.format(java.util.Locale.getDefault(),
                    "终点：%s\n%.6f, %.6f",
                    pendingNavDestLabel,
                    pendingNavDestination.getLatitude(),
                    pendingNavDestination.getLongitude()));
        }
        View content = inflateMapDialogShell();
        bindMapDialogTitle(content, "导航方式");
        bindMapDialogMessage(content, null);
        setMapDialogCustomContent(content, sheet);
        TextView btnCancel = content.findViewById(R.id.btnMapDialogCancel);
        TextView btnConfirm = content.findViewById(R.id.btnMapDialogConfirm);
        btnCancel.setText("取消");
        btnConfirm.setText("确定");
        AlertDialog dlg = createMapStyledDialog(content);
        if (rbDrive != null) rbDrive.setChecked(true);
        btnCancel.setOnClickListener(v -> {
            dlg.dismiss();
            pendingNavDestination = null;
        });
        btnConfirm.setOnClickListener(v -> {
            dlg.dismiss();
            boolean driving = rbDrive != null && rbDrive.isChecked();
            startAmapRouteSearch(driving);
        });
        dlg.show();
    }

    private void startAmapRouteSearch(boolean driving) {
        if (!isAdded() || getContext() == null || currentLatLng == null || pendingNavDestination == null) return;
        navModeDriving = driving;
        if (tryStartGraphHopperRoute(driving)) {
            return;
        }
        applyOfflineStraightLineNavigation();
        if (driving) {
            Toast.makeText(requireContext(), "GraphHopper 算路失败，已降级为离线直线示意", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "GraphHopper 算路失败，步行使用离线直线示意", Toast.LENGTH_LONG).show();
        }
    }

    private static final class OfflineRoadsCheckResult {
        final boolean ok;
        @NonNull
        final String message;

        OfflineRoadsCheckResult(boolean ok, @NonNull String message) {
            this.ok = ok;
            this.message = message;
        }
    }

    @NonNull
    private OfflineRoadsCheckResult checkOfflineRoadsIntegrity() {
        File dir = getLocalRoadsHbDir();
        if (!dir.isDirectory()) {
            return new OfflineRoadsCheckResult(false,
                    "未找到离线路网目录：\n" + dir.getAbsolutePath());
        }
        List<String> missing = new ArrayList<>();
        for (String name : OFFLINE_ROADS_CORE_FILES) {
            File f = new File(dir, name);
            if (!f.isFile() || f.length() <= 0L) {
                missing.add(name);
            }
        }
        if (!missing.isEmpty()) {
            return new OfflineRoadsCheckResult(false,
                    "离线路网目录不完整（" + dir.getAbsolutePath() + "）\n缺少或损坏："
                            + TextUtils.join(", ", missing));
        }
        return new OfflineRoadsCheckResult(true, "ok");
    }

    @NonNull
    private String buildAvailableOfflineProfilesDesc() {
        List<String> profiles = new ArrayList<>(3);
        if (isOfflineRoadProfileFilesReady("car")) profiles.add("car");
        if (isOfflineRoadProfileFilesReady("foot")) profiles.add("foot");
        if (isOfflineRoadProfileFilesReady("bike")) profiles.add("bike");
        return TextUtils.join("/", profiles);
    }

    @NonNull
    private OfflineRoadsCheckResult checkOfflineRoadProfile(@NonNull String vehicle) {
        OfflineRoadsCheckResult base = checkOfflineRoadsIntegrity();
        if (!base.ok) {
            return base;
        }
        if (isOfflineRoadProfileFilesReady(vehicle)) {
            return new OfflineRoadsCheckResult(true, "ok");
        }
        String profileLabel = "car".equals(vehicle) ? "驾车(car)"
                : ("foot".equals(vehicle) ? "步行(foot)" : vehicle);
        File dir = getLocalRoadsHbDir();
        String needNode = "nodes_ch_" + vehicle;
        String needShortcut = "shortcuts_" + vehicle;
        return new OfflineRoadsCheckResult(false,
                "离线路网缺少 " + profileLabel + " profile 索引文件。\n目录："
                        + dir.getAbsolutePath()
                        + "\n请补齐文件："
                        + needNode + "、" + needShortcut);
    }

    private boolean isOfflineRoadProfileFilesReady(@NonNull String vehicle) {
        File dir = getLocalRoadsHbDir();
        File nodes = new File(dir, "nodes_ch_" + vehicle);
        File shortcuts = new File(dir, "shortcuts_" + vehicle);
        return nodes.isFile() && nodes.length() > 0L
                && shortcuts.isFile() && shortcuts.length() > 0L;
    }

    private void showNavigationDataErrorDialog(@NonNull String message) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        showMapAckDialog("离线导航数据检查失败", message, "知道了");
    }

    /**
     * 优先调用在线导航服务（本地部署 API）算路。
     */
    private boolean tryStartGraphHopperRoute(boolean driving) {
        final LatLng from = currentLatLng;
        final LatLng to = pendingNavDestination;
        if (from == null || to == null) {
            return false;
        }
        final String api = getGraphHopperWebRouteApi();
        if (api == null) {
            Toast.makeText(requireContext(), "未配置登录服务器(str_server)，无法算路", Toast.LENGTH_LONG).show();
            navRerouteInProgress = false;
            return false;
        }
        final String vehicle = driving ? "car" : "foot";
        final int gen = ++graphHopperRouteGeneration;
        navRerouteInProgress = true;
        Toast.makeText(requireContext(), "在线算路中…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            LocalServiceRouteResult r = requestRouteFromLocalService(from, to, vehicle);
            locationHandler.post(() -> {
                //dismissGraphBuildProgressDialog();
                navRerouteInProgress = false;
                navLastRerouteTs = System.currentTimeMillis();
                navDeviationHitCount = 0;
                if (!isAdded() || gen != graphHopperRouteGeneration) {
                    return;
                }
                if (r.ok && r.pointsWgs84 != null && !r.pointsWgs84.isEmpty()) {
                    String banner = (r.firstInstruction != null && !r.firstInstruction.isEmpty())
                            ? r.firstInstruction
                            : "沿路线前进";
                    applyNavigationRouteUi(
                            r.pointsWgs84,
                            r.distanceMeters,
                            r.durationSeconds,
                            r.instructions,
                            banner);
                    return;
                }
                String err = r.errorMessage != null ? r.errorMessage : "算路失败";
                Toast.makeText(requireContext(), "在线算路失败: " + err, Toast.LENGTH_LONG).show();
                applyOfflineStraightLineNavigation();
            });
        }, "online-graphhopper-web-route").start();
        return true;
    }

    @Nullable
    private String getGraphHopperWebRouteApi() {
        String host = null;
        Context context = getContext();
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
            host = normalizeGraphHopperHost(sp.getString("str_server", null));
        }
        if (TextUtils.isEmpty(host)) {
            host = normalizeGraphHopperHost(getLoginServerHostFromGlobal());
        }
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        return "http://" + host + GRAPHHOPPER_WEB_ROUTE_API_SUFFIX;
    }





    @Nullable
    private String getLoginServerHostFromGlobal() {
        try {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc != null ? pQyMc.get_pProcInfo() : null;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
            if (pMisCnt != null && pMisCnt.server != null) {
                return pMisCnt.server.ip;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    private String normalizeGraphHopperHost(@Nullable String host) {
        if (host == null) return null;
        host = host.trim();
        if (host.isEmpty()) return null;
        int schemePos = host.indexOf("://");
        if (schemePos >= 0) {
            host = host.substring(schemePos + 3);
        }
        int slashPos = host.indexOf('/');
        if (slashPos >= 0) {
            host = host.substring(0, slashPos);
        }
        int colonPos = host.indexOf(':');
        if (colonPos > 0) {
            host = host.substring(0, colonPos);
        }
        return host.trim();
    }

    @NonNull
    private LocalServiceRouteResult requestRouteFromLocalService(
            @NonNull LatLng fromWgs84,
            @NonNull LatLng toWgs84,
            @NonNull String vehicleProfile) {
        HttpURLConnection conn = null;
        try {
            String baseApi = getGraphHopperWebRouteApi();
            if (baseApi == null) {
                return LocalServiceRouteResult.failure("未配置登录服务器(str_server)");
            }
            String qs = "point=" + URLEncoder.encode(fromWgs84.getLatitude() + "," + fromWgs84.getLongitude(), "UTF-8")
                    + "&point=" + URLEncoder.encode(toWgs84.getLatitude() + "," + toWgs84.getLongitude(), "UTF-8")
                    + "&profile=" + URLEncoder.encode(vehicleProfile, "UTF-8")
                    + "&locale=zh-CN"
                    + "&instructions=true"
                    + "&calc_points=true"
                    + "&points_encoded=false";
            String reqUrl = baseApi + "?" + qs;
            conn = (HttpURLConnection) new URL(reqUrl).openConnection();
            conn.setConnectTimeout(12_000);
            conn.setReadTimeout(20_000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            String payload = readFully(is);
            if (payload == null || payload.trim().isEmpty()) {
                return LocalServiceRouteResult.failure("服务返回为空");
            }
            JSONObject root = new JSONObject(payload);
            if (root.optJSONArray("hints") != null && root.optJSONArray("hints").length() > 0) {
                JSONObject hint0 = root.optJSONArray("hints").optJSONObject(0);
                String msg = hint0 != null ? hint0.optString("message", "GraphHopper 返回错误") : "GraphHopper 返回错误";
                return LocalServiceRouteResult.failure(msg);
            }
            JSONArray paths = root.optJSONArray("paths");
            if (paths == null || paths.length() == 0) {
                return LocalServiceRouteResult.failure("GraphHopper 返回无路径");
            }
            JSONObject best = paths.optJSONObject(0);
            if (best == null) {
                return LocalServiceRouteResult.failure("GraphHopper 返回无路径");
            }
            JSONObject pointsObj = best.optJSONObject("points");
            JSONArray pointsArr = pointsObj != null ? pointsObj.optJSONArray("coordinates") : null;
            List<LatLng> pts = new ArrayList<>();
            if (pointsArr != null) {
                for (int i = 0; i < pointsArr.length(); i++) {
                    JSONArray pair = pointsArr.optJSONArray(i);
                    if (pair == null || pair.length() < 2) continue;
                    double lon = pair.optDouble(0);
                    double lat = pair.optDouble(1);
                    pts.add(new LatLng(lat, lon));
                }
            }
            if (pts.size() < 2) {
                return LocalServiceRouteResult.failure("服务返回路径点不足");
            }
            int distM = (int) Math.max(0, Math.round(best.optDouble("distance", 0d)));
            int durS = (int) Math.max(0, Math.round(best.optDouble("time", 0d) / 1000.0));
            List<NavInstructionLite> instructions = parseServiceInstructions(best.optJSONArray("instructions"));
            String firstInstr = instructions.isEmpty() ? "沿路线前进" : instructions.get(0).text;
            return LocalServiceRouteResult.success(pts, distM, durS, instructions, firstInstr);
        } catch (Throwable e) {
            return LocalServiceRouteResult.failure("GraphHopper 服务异常: " + safeErr(e));
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    @NonNull
    private static List<NavInstructionLite> parseServiceInstructions(@Nullable JSONArray arr) {
        List<NavInstructionLite> out = new ArrayList<>();
        if (arr == null) return out;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject ins = arr.optJSONObject(i);
            if (ins == null) continue;
            int sign = ins.optInt("sign", 0);
            String text = ins.optString("text", "");
            int distM = (int) Math.max(0, Math.round(ins.optDouble("distance", 0d)));
            int durS = (int) Math.max(0, Math.round(ins.optDouble("time", 0d) / 1000.0));
            int from = -1;
            int to = -1;
            JSONArray interval = ins.optJSONArray("interval");
            if (interval != null && interval.length() >= 2) {
                from = interval.optInt(0, -1);
                to = interval.optInt(1, -1);
            }
            if (TextUtils.isEmpty(text)) {
                text = "沿路线前进";
            }
            out.add(new NavInstructionLite(from, to, sign, text, distM, durS));
        }
        return out;
    }

    private static final class LocalServiceRouteResult {
        final boolean ok;
        @Nullable final List<LatLng> pointsWgs84;
        final int distanceMeters;
        final int durationSeconds;
        @Nullable final List<NavInstructionLite> instructions;
        @Nullable final String firstInstruction;
        @Nullable final String errorMessage;

        private LocalServiceRouteResult(boolean ok,
                                        @Nullable List<LatLng> pointsWgs84,
                                        int distanceMeters,
                                        int durationSeconds,
                                        @Nullable List<NavInstructionLite> instructions,
                                        @Nullable String firstInstruction,
                                        @Nullable String errorMessage) {
            this.ok = ok;
            this.pointsWgs84 = pointsWgs84;
            this.distanceMeters = distanceMeters;
            this.durationSeconds = durationSeconds;
            this.instructions = instructions;
            this.firstInstruction = firstInstruction;
            this.errorMessage = errorMessage;
        }

        @NonNull
        static LocalServiceRouteResult success(@NonNull List<LatLng> pts, int distM, int durS,
                                               @Nullable List<NavInstructionLite> instructions,
                                               @Nullable String first) {
            return new LocalServiceRouteResult(true, pts, distM, durS, instructions, first, null);
        }

        @NonNull
        static LocalServiceRouteResult failure(@NonNull String err) {
            return new LocalServiceRouteResult(false, null, 0, 0, null, null, err);
        }
    }

    @Nullable
    private static String readFully(@Nullable InputStream is) throws IOException {
        if (is == null) return null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    @NonNull
    private static String safeErr(@Nullable Throwable e) {
        if (e == null) return "unknown";
        String m = e.getMessage();
        if (m == null || m.trim().isEmpty()) return e.getClass().getSimpleName();
        return m;
    }

    private void applyNavigationRouteUi(
            @Nullable List<LatLng> wgs84Points,
            int distanceMeters,
            int durationSeconds,
            @Nullable List<NavInstructionLite> instructions,
            @Nullable String firstInstruction) {
        if (!isAdded()) return;
        navigationRouteWgs84.clear();
        if (wgs84Points != null) navigationRouteWgs84.addAll(wgs84Points);
        navigationInstructions.clear();
        if (instructions != null) {
            navigationInstructions.addAll(instructions);
        }
        navigationRouteTotalDistanceMeters = Math.max(0, distanceMeters);
        navigationRouteTotalDurationSeconds = Math.max(0, durationSeconds);
        if (lineOverlayView != null && mapLibreMap != null) {
            lineOverlayView.setNavigationRoute(mapLibreMap, navigationRouteWgs84);
            updateNavigationRouteProgress(displayedLatLng != null ? displayedLatLng : currentLatLng);
        }
        navInstructionBanner = firstInstruction != null ? firstInstruction : "沿路线前进";
        String dist = formatDistance(distanceMeters);
        String dur = formatDurationSeconds(durationSeconds);
        navRouteSummaryLine = (navModeDriving ? "驾车 " : "步行 ") + dist + " · 约" + dur;
        if (tvNavInstruction != null) {
            tvNavInstruction.setText(navRouteSummaryLine + "\n" + navInstructionBanner);
        }
        if (layoutNavBar != null) layoutNavBar.setVisibility(View.VISIBLE);
        fitCameraToNavigationRoute();
        startNavigationHud();
    }

    /**
     * 离线路网算路失败时：在地图上画当前位置到终点的直线（WGS84），保证导航界面可继续工作。
     */
    private void applyOfflineStraightLineNavigation() {
        if (currentLatLng == null || pendingNavDestination == null) {
            return;
        }
        float[] d = new float[1];
        Location.distanceBetween(
                currentLatLng.getLatitude(),
                currentLatLng.getLongitude(),
                pendingNavDestination.getLatitude(),
                pendingNavDestination.getLongitude(),
                d);
        navigationRouteWgs84.clear();
        navigationRouteWgs84.add(currentLatLng);
        navigationRouteWgs84.add(pendingNavDestination);
        if (lineOverlayView != null && mapLibreMap != null) {
            lineOverlayView.setNavigationRoute(mapLibreMap, navigationRouteWgs84);
            updateNavigationRouteProgress(displayedLatLng != null ? displayedLatLng : currentLatLng);
        }
        navigationInstructions.clear();
        navigationRouteTotalDistanceMeters = Math.round(d[0]);
        navigationRouteTotalDurationSeconds = 0;
        navInstructionBanner = "起终点直线（离线，非路网）";
        navRouteSummaryLine =
                (navModeDriving ? "驾车 " : "步行 ")
                        + "离线直线 "
                        + formatDistance(d[0])
                        + " · 联网后可算实际路线";
        if (tvNavInstruction != null) {
            tvNavInstruction.setText(navRouteSummaryLine + "\n" + navInstructionBanner);
        }
        if (layoutNavBar != null) {
            layoutNavBar.setVisibility(View.VISIBLE);
        }
        fitCameraToNavigationRoute();
        startNavigationHud();
    }

    private static String formatDurationSeconds(int sec) {
        if (sec < 60) return sec + "秒";
        if (sec < 3600) return (sec / 60) + "分钟";
        return (sec / 3600) + "小时" + ((sec % 3600) / 60) + "分";
    }

    private void fitCameraToNavigationRoute() {
        if (mapLibreMap == null || navigationRouteWgs84.isEmpty()) return;
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (LatLng p : navigationRouteWgs84) {
            b.include(p);
        }
        if (currentLatLng != null) b.include(currentLatLng);
        try {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(), dp(64)));
        } catch (Exception e) {
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngZoom(navigationRouteWgs84.get(0), 14.0));
        }
    }

    private void startNavigationHud() {
        cancelNavigationHud();
        navHudRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || layoutNavBar == null || layoutNavBar.getVisibility() != View.VISIBLE) return;
                if (currentLatLng != null && tvNavInstruction != null) {
                    int remainDist = estimateRemainingDistanceMeters(currentLatLng);
                    int remainSec = estimateRemainingDurationSeconds(remainDist);
                    String turnHint = resolveCurrentTurnInstruction(currentLatLng);
                    tvNavInstruction.setText(String.format(java.util.Locale.getDefault(),
                            "%s\n%s\n剩余 %s · 约%s",
                            navRouteSummaryLine,
                            turnHint,
                            formatDistance(remainDist),
                            formatDurationSeconds(remainSec)));
                }
                locationHandler.postDelayed(this, 2500L);
            }
        };
        locationHandler.postDelayed(navHudRunnable, 2500L);
    }

    private void cancelNavigationHud() {
        if (navHudRunnable != null) {
            locationHandler.removeCallbacks(navHudRunnable);
            navHudRunnable = null;
        }
    }

    private void stopInAppNavigation() {
        graphHopperRouteGeneration++;
        cancelNavigationHud();
        navRerouteInProgress = false;
        navDeviationHitCount = 0;
        navLastRerouteTs = 0L;
        navFollowSuspendedByGesture = false;
        locationHandler.removeCallbacks(resumeNavFollowRunnable);
        navigationRouteWgs84.clear();
        navigationInstructions.clear();
        navigationRouteTotalDistanceMeters = 0;
        navigationRouteTotalDurationSeconds = 0;
        pendingNavDestination = null;
        pendingNavDestLabel = null;
        pendingNavCityName = null;
        navRouteSummaryLine = "";
        navInstructionBanner = null;
        if (lineOverlayView != null) {
            lineOverlayView.clearNavigationRoute();
        }
        if (ivHeading != null && ivHeading.getVisibility() == View.VISIBLE) {
            ivHeading.setRotation(selfHeadingDeg);
        }
        if (layoutNavBar != null) layoutNavBar.setVisibility(View.GONE);
    }

    private int estimateRemainingDistanceMeters(@NonNull LatLng cur) {
        if (navigationRouteWgs84.isEmpty()) {
            return 0;
        }
        if (navigationRouteWgs84.size() == 1) {
            float[] d = new float[1];
            Location.distanceBetween(
                    cur.getLatitude(),
                    cur.getLongitude(),
                    navigationRouteWgs84.get(0).getLatitude(),
                    navigationRouteWgs84.get(0).getLongitude(),
                    d);
            return Math.max(0, Math.round(d[0]));
        }
        int nearestIdx = findNearestRoutePointIndex(cur);
        float[] d = new float[1];
        LatLng nearest = navigationRouteWgs84.get(nearestIdx);
        Location.distanceBetween(
                cur.getLatitude(), cur.getLongitude(),
                nearest.getLatitude(), nearest.getLongitude(),
                d);
        int remain = Math.round(d[0]);
        for (int i = nearestIdx; i < navigationRouteWgs84.size() - 1; i++) {
            LatLng a = navigationRouteWgs84.get(i);
            LatLng b = navigationRouteWgs84.get(i + 1);
            Location.distanceBetween(
                    a.getLatitude(), a.getLongitude(),
                    b.getLatitude(), b.getLongitude(),
                    d);
            remain += Math.round(d[0]);
        }
        return Math.max(0, remain);
    }

    private int estimateRemainingDurationSeconds(int remainDistMeters) {
        if (navigationRouteTotalDistanceMeters <= 0 || navigationRouteTotalDurationSeconds <= 0) {
            return 0;
        }
        double ratio = Math.max(0d, Math.min(1d, remainDistMeters / (double) navigationRouteTotalDistanceMeters));
        return (int) Math.max(0, Math.round(navigationRouteTotalDurationSeconds * ratio));
    }

    private int findNearestRoutePointIndex(@NonNull LatLng cur) {
        int nearestIdx = 0;
        float nearestMeter = Float.MAX_VALUE;
        float[] d = new float[1];
        for (int i = 0; i < navigationRouteWgs84.size(); i++) {
            LatLng p = navigationRouteWgs84.get(i);
            Location.distanceBetween(
                    cur.getLatitude(), cur.getLongitude(),
                    p.getLatitude(), p.getLongitude(),
                    d);
            if (d[0] < nearestMeter) {
                nearestMeter = d[0];
                nearestIdx = i;
            }
        }
        return nearestIdx;
    }

    private int estimateDeviationMeters(@NonNull LatLng cur) {
        if (navigationRouteWgs84.isEmpty()) return 0;
        if (navigationRouteWgs84.size() == 1) {
            LatLng p = navigationRouteWgs84.get(0);
            float[] d = new float[1];
            Location.distanceBetween(
                    cur.getLatitude(), cur.getLongitude(),
                    p.getLatitude(), p.getLongitude(),
                    d);
            return Math.max(0, Math.round(d[0]));
        }

        double minMeters = Double.MAX_VALUE;
        for (int i = 0; i < navigationRouteWgs84.size() - 1; i++) {
            LatLng a = navigationRouteWgs84.get(i);
            LatLng b = navigationRouteWgs84.get(i + 1);
            minMeters = Math.min(minMeters, distanceToRouteSegmentMeters(cur, a, b));
        }
        return Math.max(0, (int) Math.round(minMeters));
    }

    private static double distanceToRouteSegmentMeters(
            @NonNull LatLng cur,
            @NonNull LatLng a,
            @NonNull LatLng b) {
        double originLat = cur.getLatitude();
        double originLng = cur.getLongitude();
        double metersPerDegLat = 111_320d;
        double metersPerDegLng = Math.max(1d, 111_320d * Math.cos(Math.toRadians(originLat)));
        double ax = (a.getLongitude() - originLng) * metersPerDegLng;
        double ay = (a.getLatitude() - originLat) * metersPerDegLat;
        double bx = (b.getLongitude() - originLng) * metersPerDegLng;
        double by = (b.getLatitude() - originLat) * metersPerDegLat;
        double vx = bx - ax;
        double vy = by - ay;
        double len2 = vx * vx + vy * vy;
        if (len2 <= 0d) {
            return Math.hypot(ax, ay);
        }
        double t = Math.max(0d, Math.min(1d, -((ax * vx + ay * vy) / len2)));
        double px = ax + t * vx;
        double py = ay + t * vy;
        return Math.hypot(px, py);
    }

    private void updateNavigationRouteProgress(@Nullable LatLng cur) {
        if (lineOverlayView == null) return;
        if (cur == null || !isInAppNavigationActive() || navigationRouteWgs84.size() < 2) {
            lineOverlayView.setNavigationProgress(-1, null);
            return;
        }
        NavRouteProgress progress = estimateNavigationRouteProgress(cur);
        if (progress == null) {
            lineOverlayView.setNavigationProgress(-1, null);
        } else {
            lineOverlayView.setNavigationProgress(progress.segmentIndex, progress.point);
        }
    }

    @Nullable
    private NavRouteProgress estimateNavigationRouteProgress(@NonNull LatLng cur) {
        if (navigationRouteWgs84.size() < 2) return null;
        int bestSegment = -1;
        double bestMeters = Double.MAX_VALUE;
        LatLng bestPoint = null;
        for (int i = 0; i < navigationRouteWgs84.size() - 1; i++) {
            LatLng a = navigationRouteWgs84.get(i);
            LatLng b = navigationRouteWgs84.get(i + 1);
            LatLng projected = projectPointToRouteSegment(cur, a, b);
            double meters = distanceMeters(cur, projected);
            if (meters < bestMeters) {
                bestMeters = meters;
                bestSegment = i;
                bestPoint = projected;
            }
        }
        if (bestSegment < 0 || bestPoint == null) return null;
        return new NavRouteProgress(bestSegment, bestPoint);
    }

    @NonNull
    private static LatLng projectPointToRouteSegment(@NonNull LatLng cur,
                                                     @NonNull LatLng a,
                                                     @NonNull LatLng b) {
        double originLat = cur.getLatitude();
        double originLng = cur.getLongitude();
        double metersPerDegLat = 111_320d;
        double metersPerDegLng = Math.max(1d, 111_320d * Math.cos(Math.toRadians(originLat)));
        double ax = (a.getLongitude() - originLng) * metersPerDegLng;
        double ay = (a.getLatitude() - originLat) * metersPerDegLat;
        double bx = (b.getLongitude() - originLng) * metersPerDegLng;
        double by = (b.getLatitude() - originLat) * metersPerDegLat;
        double vx = bx - ax;
        double vy = by - ay;
        double len2 = vx * vx + vy * vy;
        double t = len2 <= 0d ? 0d : Math.max(0d, Math.min(1d, -((ax * vx + ay * vy) / len2)));
        double px = ax + t * vx;
        double py = ay + t * vy;
        double lat = originLat + py / metersPerDegLat;
        double lng = originLng + px / metersPerDegLng;
        return new LatLng(lat, lng);
    }

    private void maybeAutoRerouteOnDeviation(@NonNull LatLng cur) {
        if (pendingNavDestination == null) return;
        if (layoutNavBar == null || layoutNavBar.getVisibility() != View.VISIBLE) return;
        if (navigationRouteWgs84.size() < 2) return;
        if (navRerouteInProgress) return;
        long now = System.currentTimeMillis();
        if (now - navLastRerouteTs < NAV_REROUTE_COOLDOWN_MS) return;

        int deviation = estimateDeviationMeters(cur);
        if (deviation < NAV_REROUTE_DEVIATION_METERS) {
            navDeviationHitCount = 0;
            return;
        }
        navDeviationHitCount++;
        if (navDeviationHitCount < NAV_REROUTE_DEVIATION_HITS) {
            return;
        }
        navDeviationHitCount = 0;
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "检测到偏航，正在自动重算路线…", Toast.LENGTH_LONG).show();
        tryStartGraphHopperRoute(navModeDriving);
    }

    @NonNull
    private String resolveCurrentTurnInstruction(@NonNull LatLng cur) {
        if (navigationInstructions.isEmpty()) {
            return navInstructionBanner != null ? navInstructionBanner : "沿路线前进";
        }
        int idx = findNearestRoutePointIndex(cur);
        NavInstructionLite hit = null;
        for (NavInstructionLite ins : navigationInstructions) {
            if (ins == null) continue;
            int from = Math.max(0, ins.fromIndex);
            int to = Math.max(from, ins.toIndex);
            if (idx >= from && idx <= to) {
                hit = ins;
                break;
            }
        }
        if (hit == null) {
            hit = navigationInstructions.get(Math.max(0, navigationInstructions.size() - 1));
        }
        String prefix = mapTurnSignToLabel(hit.sign);
        if (TextUtils.isEmpty(hit.text)) {
            return prefix;
        }
        return prefix + " · " + hit.text;
    }

    @NonNull
    private static String mapTurnSignToLabel(int sign) {
        switch (sign) {
            case -3:
                return "向左急转";
            case -2:
                return "左转";
            case -1:
                return "向左前方";
            case 0:
                return "直行";
            case 1:
                return "向右前方";
            case 2:
                return "右转";
            case 3:
                return "向右急转";
            case 4:
                return "到达目的地";
            case 5:
                return "经由点";
            case 6:
                return "环岛";
            default:
                return "沿路线前进";
        }
    }

    private void updateMarkConnectionLines() {
        updateMarkConnectionLines(true);
    }

    private void updateMarkConnectionLines(boolean withLabels) {
        if (useMapLibreMarkConnectionLines) {
            if (lineOverlayView != null) {
                lineOverlayView.setLines(Collections.emptyList());
            }
            updateMapLibreMarkConnectionLines();
            return;
        }
        if (lineOverlayView == null) {
            ensureLineOverlayAttached();
        }
        if (lineOverlayView == null || mapLibreMap == null) return;
        List<LineItem> items = new ArrayList<>();
        ensureSelfUserId();
        Set<Long> activeGrpIds = loadActiveGroupIdsFromTaskGrp();
        for (MarkLink link : markLinks.values()) {
            if (link == null || TextUtils.isEmpty(link.markId) || !shouldDrawMarkLink(link, activeGrpIds)) continue;
            BattleMark mark = battleMarksOnMap.get(link.markId);
            if (mark == null || mark.latLng == null) continue;
            PointF markPt = mapLibreMap.getProjection().toScreenLocation(mark.latLng);
            if (markPt == null) continue;
            if (link.userId == selfUserId) {
                LatLng selfLineLatLng = displayedLatLng != null ? displayedLatLng : currentLatLng;
                if (selfLineLatLng == null) continue;
                PointF selfPt = mapLibreMap.getProjection().toScreenLocation(selfLineLatLng);
                if (selfPt != null) {
                    items.add(buildLine(selfPt, markPt, "我", withLabels, resolveMarkLinkColor(link)));
                }
            } else {
                RemoteLoc r = remoteLocs.get(link.userId);
                LatLng remoteLineLatLng = r != null && r.displayLatLng != null ? r.displayLatLng : (r != null ? r.latLng : null);
                if (remoteLineLatLng == null) continue;
                PointF rp = mapLibreMap.getProjection().toScreenLocation(remoteLineLatLng);
                if (rp != null) {
                    items.add(buildLine(rp, markPt, getUserDisplayNameCached(link.userId), false,
                            resolveMarkLinkColor(link)));
                }
            }
        }
        lineOverlayView.setLines(items);
    }

    @SuppressLint("DefaultLocale")
    private LineItem buildLine(PointF from, PointF to, String who, boolean withLabel, int color) {
        LineItem li = new LineItem();
        li.from = from;
        li.to = to;
        li.color = color;
        if (!withLabel) {
            li.label = null;
            return li;
        }
        LatLng fromGeo = mapLibreMap.getProjection().fromScreenLocation(from);
        LatLng toGeo = mapLibreMap.getProjection().fromScreenLocation(to);
        float[] out = new float[2];
        Location.distanceBetween(fromGeo.getLatitude(), fromGeo.getLongitude(), toGeo.getLatitude(), toGeo.getLongitude(), out);
        li.label = String.format("%s %s %.0f°", who, formatDistance(out[0]), normalizeDeg(out[1]));
        return li;
    }

    private static float normalizeDeg(float deg) {
        float d = deg;
        while (d < 0) d += 360f;
        while (d >= 360f) d -= 360f;
        return d;
    }

    @SuppressLint("DefaultLocale")
    private static String formatDistance(float meter) {
        if (meter >= 1000f) {
            return String.format("%.2fkm", meter / 1000f);
        }
        return String.format("%.0fm", meter);
    }

    private String getUserDisplayNameCached(long userId) {
        String s = remoteUserDisplayNameCache.get(userId);
        if (s != null) {
            return s;
        }
        s = getUserDisplayName(userId);
        remoteUserDisplayNameCache.put(userId, s);
        return s;
    }

    private String getUserDisplayName(long userId) {
        if (userId <= 0) return "未知";
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return "用户" + userId;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        if (pDbFuncs == null || pQyMc.gui == null || pQyMc.gui.pDb == null) return "用户" + userId;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = userId;
        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(
                pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType,
                qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0,
                msgrInfo, regInfo, null, null)) {
            return "用户" + userId;
        }
        if (!TextUtils.isEmpty(regInfo.syr)) return regInfo.syr;
        if (!TextUtils.isEmpty(regInfo.bm)) return regInfo.bm;
        if (!TextUtils.isEmpty(regInfo.dw)) return regInfo.dw;
        if (!TextUtils.isEmpty(msgrInfo.messengerName)) return msgrInfo.messengerName;
        return "用户" + userId;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCATION_PERMISSION) return;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                tryLocateAndMark();
                return;
            }
        }
    }
}
