package me.kuangneipro.manager;

import java.util.ArrayList;
import java.util.List;

import me.kuangneipro.R;
import me.kuangneipro.entity.KuangInfo;
import me.kuangneipro.util.HostUtil;
import me.kuangneipro.util.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by zhumeng on 2014/11/2.
 */
public class MapEntityManager {

    public static final int MAP_KEY_GET = 1;

    public static void fillKuangListFromJson(JSONObject jsonObj, List<KuangInfo> kuangList) {
        try {
            Log.i("fillKuangListFromJson", jsonObj.toString());
            JSONArray jsonarray = jsonObj.getJSONArray("list");
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject oneKuangJson = jsonarray.getJSONObject(i);
                int id = oneKuangJson.getInt("id");
                String name = oneKuangJson.getString("name");
                String area = oneKuangJson.getString("area");
                String position = oneKuangJson.getString("position");
                List<LatLng> pts = new ArrayList<LatLng>();
                for (String pstr : position.split(",")) {
                    String[] p = pstr.split("\\|");
                    pts.add(new LatLng(Double.parseDouble(p[1]), Double.parseDouble(p[0])));
                }
                kuangList.add(new KuangInfo(id, name, area, pts));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getKuangList(HttpHelper httpRequest) {
        httpRequest.setUrl(HostUtil.KUANG_LIST_URL).asyncGet();
    }
}
