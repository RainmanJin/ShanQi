package com.hztuen.shanqi.activity.trace;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.hztuen.contacts.AppUrl;
import com.hztuen.model.Trip;
import com.hztuen.shanqi.MyApplication;
import com.hztuen.shanqi.R;
import com.hztuen.shanqi.activity.BaseActivity;
import com.hztuen.shanqi.activity.myself.TripActivity;
import com.hztuen.util.MyLogUtil;
import com.hztuen.util.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import okhttp3.Call;

import static android.R.attr.data;


/**
 * 轨迹偏纠
 */
public class TraceActivity extends BaseActivity implements TraceListener, OnClickListener{
	String TAG = "TraceActivity";
	private Button mGraspButton;
	private Spinner mRecordChoose;

	private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
	private MapView mMapView;
	private AMap mAMap;
	private LBSTraceClient mTraceClient;

	private String[] mRecordChooseArray;
	private List<TraceLocation> mTraceList;

	private static String mDistanceString, mStopTimeString;
	private static final String DISTANCE_UNIT_DES = " KM";
	private static final String TIME_UNIT_DES = " 分钟";

	private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer, TraceOverlay>();
	private int mSequenceLineID = 1000;

	private Trip mTrip;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);

		initHeadUI();
		mTrip = (Trip) getIntent().getSerializableExtra("mTrip");

		mGraspButton= (Button) findViewById(R.id.grasp_button);
		mGraspButton.setOnClickListener(this);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		mRecordChoose = (Spinner) findViewById(R.id.record_choose);
		mDistanceString = getResources().getString(R.string.distance);
		mStopTimeString = getResources().getString(R.string.stop_time);

		init();
		initDialog();
	}

	//初始化Dialog
	private void initDialog() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("正在规划...");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	//初始化头布局
	private void initHeadUI() {
		LinearLayout layoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
		layoutLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		TextView tvTitle = (TextView) findViewById(R.id.tvHeadTitle);
		tvTitle.setText(R.string.my_trace);
	}

	public  void quit(View v){
		finish();
	}
	/**
	 * 初始化
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(false);
			mAMap.getUiSettings().setZoomControlsEnabled(false);
		}

		if (MyApplication.mLocation != null){
			//把地图视角移动到定位点
			mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
					new LatLng(MyApplication.mLocation.latitude, MyApplication.mLocation.longitude),//新的中心点坐标
					16, //新的缩放级别
					0, //俯仰角0°~45°（垂直与地图时为0）
					0  ////偏航角 0~360° (正北方为0)
			)));
		}

		mTraceList=new ArrayList<>();
		getTraceInfo();
	}

	/**
	 * 获取轨迹
	 */
	private  void getTraceInfo(){
		if (mTrip == null || mTrip.getSn() == null || "".equals(mTrip.getSn())){
			return;
		}
		String sign = "";
		List<String> attributes = new ArrayList<>();
		attributes.add("sn=" + mTrip.getSn() + "");
		attributes.add("token=" + MyApplication.myToken);
		try {
			sign = StringUtil.sign(attributes);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		OkHttpUtils
				.post()
				.url(AppUrl.GET_TRAJECTORYLIST)
//				.addParams("sn", "1701211538291913")
				.addParams("sn", mTrip.getSn()+ "")
				.addParams("token", MyApplication.myToken)
				.addParams("sign", sign)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						Toast.makeText(TraceActivity.this, R.string.http_error, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(String response, int id) {
						MyLogUtil.i("我的行程:", response + "");
						JSONObject result = null;
						try {
							result = new JSONObject(response);
							String resultCode = result.getString("resultCode");
							if ("200".equals(resultCode)){
								JSONArray mJSONArray = result.getJSONArray("resultContent");
								if (mJSONArray.length() > 0){
//									String data="30.319198,120.351241 30.319198,120.351241 30.319198,120.351241 30.320463,120.351707 30.320747,120.351809 30.319116,120.351214 30.319170,120.351234 30.319314,120.351286 30.319273,120.351222 30.319273,120.351224 30.319290,120.351235 30.319301,120.350774 30.319305,120.350752 30.319331,120.350594 30.320180,120.350542 30.320819,120.350793 30.321342,120.350997 30.321480,120.350968 30.321452,120.350943 30.321428,120.350918 30.322125,120.351118 30.322446,120.351283 30.322704,120.351414 30.322171,120.351230 30.321395,120.351074 30.320817,120.350958 30.319305,120.350752 30.319788,120.350732 30.320921,120.347261 30.321466,120.345553 30.321739,120.344698 30.319461,120.348293 30.319446,120.348291 30.319105,120.348356 30.318994,120.348210 30.318661,120.348112 30.318349,120.348004 30.318267,120.348030 30.318204,120.347684 30.318186,120.348006 30.318344,120.347973 30.318937,120.348273 30.318787,120.347938 30.319076,120.348196 30.318732,120.348094 30.318508,120.348546 30.318345,120.348887 30.318548,120.349382 30.318538,120.349709 30.318608,120.350741 30.318655,120.351238 30.318630,120.351588 30.318645,120.351760 30.318588,120.352046 30.318553,120.352367 30.318543,120.352432 30.318541,120.352427 30.318538,120.352427 30.318442,120.352192 30.318931,120.351573 30.319376,120.351352 30.319389,120.352162 30.319315,120.353034 30.319415,120.353034 30.318478,120.355542 30.318566,120.355516 30.318614,120.356022 30.318591,120.356731 30.318550,120.357033 30.318612,120.357406 30.318601,120.357700 30.318211,120.357967 30.317981,120.358001 30.317831,120.358053 30.317718,120.358038 30.317464,120.358013 30.317308,120.357995 30.316950,120.358002 30.316909,120.358077 30.316106,120.357983 30.315635,120.357521 30.315596,120.357391 30.315636,120.356799 30.315713,120.356205 30.315824,120.355622 30.315788,120.355717 30.315753,120.355321 30.315681,120.354872 30.315804,120.354416 30.315828,120.354124 30.315764,120.351817 30.315770,120.351803 30.315747,120.351888 30.315733,120.351856 30.315723,120.351775 30.315675,120.351663 30.315638,120.351652 30.315704,120.351256 30.315726,120.351185 30.315751,120.351139 30.315749,120.350747 30.315649,120.348665 30.315709,120.348443 30.315847,120.348416 30.316222,120.348094 30.316273,120.347964 30.316492,120.347988 30.316517,120.348047 30.316547,120.347987 30.316845,120.348036 30.316918,120.348041 30.317476,120.348333 30.318297,120.347744 30.318417,120.348053 30.318477,120.348228 30.318752,120.348428 30.318680,120.348756 30.318603,120.348415 30.319106,120.348234 30.318843,120.348220";
//									mTraceList.addAll(cutString(data));
									mTraceList.addAll(cutString(mJSONArray));
//									mTraceList = TraceAsset.parseLocationsData(TraceActivity.this.getAssets(), "traceRecord" + File.separator + "AMapTrace.txt");

//									new Handler().postDelayed(new Runnable(){
//										public void run() {
//											//execute the task
//											traceGrasp();
//										}
//									}, 1000);
									traceGrasp();
									Handler handler=new Handler();
									Runnable runnable=new Runnable(){
										@Override
										public void run() {
											// TODO Auto-generated method stub
											//要做的事情，这里再次调用此Runnable对象
											traceGrasp();
										}
									};
									handler.postDelayed(runnable, 2000);
								}
							}else{
								Toast.makeText(TraceActivity.this, result.getString("resultMsg"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}




	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMapView != null) {
			mMapView.onDestroy();
		}
	}

	/**
	 * 轨迹纠偏失败回调
	 */
	@Override
	public void onRequestFailed(int lineID, String errorInfo) {
		Log.d(TAG, "onRequestFailed");
		Toast.makeText(this.getApplicationContext(), errorInfo,
				Toast.LENGTH_SHORT).show();
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
			setDistanceWaitInfo(overlay);
		}
	}

	/**
	 * 轨迹纠偏过程回调
	 */
	@Override
	public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
		Log.d(TAG, "onTraceProcessing");

		MyLogUtil.i("轨迹偏纠回调点",segments.toString());
		if (segments == null) {
			return;
		}
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
			overlay.add(segments);
		}
	}

	/**
	 * 轨迹纠偏结束回调
	 */
	@Override
	public void onFinished(int lineID, List<LatLng> linepoints, int distance,
						   int watingtime) {
		MyLogUtil.i("轨迹偏纠回调点",linepoints.toString());
//		mProgressDialog.dismiss();

		Log.d(TAG, "onFinished");
//		Toast.makeText(this.getApplicationContext(), "onFinished",Toast.LENGTH_SHORT).show();
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
			overlay.setDistance(distance);
			overlay.setWaitTime(watingtime);
			setDistanceWaitInfo(overlay);
		}

	}

	/**
	 * 调起一次轨迹纠偏
	 */
	private void traceGrasp() {
		if (mOverlayList.containsKey(mSequenceLineID)) {
			TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
			overlay.zoopToSpan();
			int status = overlay.getTraceStatus();
			String tipString = "";
			if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
				tipString = "该线路轨迹纠偏进行中...";
				setDistanceWaitInfo(overlay);
			} else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
				setDistanceWaitInfo(overlay);
				tipString = "该线路轨迹已完成";
				if (null != mProgressDialog && mProgressDialog.isShowing() ) mProgressDialog.dismiss();
			} else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
				tipString = "该线路轨迹失败";
				if (null != mProgressDialog && mProgressDialog.isShowing() ) mProgressDialog.dismiss();
			} else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
				tipString = "该线路轨迹纠偏已经开始";
			}
			Toast.makeText(this.getApplicationContext(), tipString,
					Toast.LENGTH_SHORT).show();
			return;
		}
		TraceOverlay mTraceOverlay = new TraceOverlay(mAMap);
		mOverlayList.put(mSequenceLineID, mTraceOverlay);
		List<LatLng> mapList = traceLocationToMap(mTraceList);
		mTraceOverlay.setProperCamera(mapList);

		mTraceClient = new LBSTraceClient(this.getApplicationContext());
		//传入轨迹参数
		mTraceClient.queryProcessedTrace(mSequenceLineID, mTraceList, mCoordinateType, this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.grasp_button:
				traceGrasp();//调起来
				break;
		}
	}

	/**
	 * 清除地图已完成或出错的轨迹
	 */
	private void cleanFinishTrace() {
		Iterator iter = mOverlayList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			TraceOverlay overlay = (TraceOverlay) entry.getValue();
			if (overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FINISH
					|| overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FAILURE) {
				overlay.remove();
				mOverlayList.remove(key);
			}
		}
	}

	/**
	 * 设置显示总里程和等待时间
	 *
	 * @param overlay
	 */
	private void setDistanceWaitInfo(TraceOverlay overlay) {
		int distance = -1;
		int waittime = -1;
		if (overlay != null) {
			distance = overlay.getDistance();
			waittime = overlay.getWaitTime();
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.0");


	}

	/**
	 * 轨迹纠偏点转换为地图LatLng
	 *
	 * @param traceLocationList
	 * @return
	 */
	public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
		List<LatLng> mapList = new ArrayList<LatLng>();
		for (TraceLocation location : traceLocationList) {
			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mapList.add(latlng);
		}
		return mapList;
	}


	private List<TraceLocation> cutString(String data){
		List<TraceLocation> mMapList=new ArrayList<>();
		String[] sourceStrArray = data.split(" ");
		for (int i = 0; i < sourceStrArray.length; i++) {
			MyLogUtil.i("数组分割",""+sourceStrArray[i]);
			String[] xy = sourceStrArray[i].split(",");
			double x=Double.valueOf(xy[0]).doubleValue();
			double y=Double.valueOf(xy[1]).doubleValue();
			TraceLocation location=new TraceLocation();
			location.setLatitude(x);
			location.setLongitude(y);
			mMapList.add(location);
		}
		return mMapList;
	}
	private List<TraceLocation> cutString(JSONArray data){
		List<TraceLocation> mMapList=new ArrayList<>();
		for (int i = 0; i < data.length(); i++) {
			try {
				MyLogUtil.i("数组分割",""+ data.get(i).toString());
				String[] xy = data.get(i).toString().split(",");
				double x=Double.valueOf(xy[0]).doubleValue();
				double y=Double.valueOf(xy[1]).doubleValue();
				TraceLocation location=new TraceLocation();
				location.setLatitude(y);
				location.setLongitude(x);
				mMapList.add(location);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return mMapList;
	}
}
