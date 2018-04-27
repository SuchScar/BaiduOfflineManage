package com.baiduofflinemanagedemo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ExpandableListView
import android.widget.ListView
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.map.offline.MKOLUpdateElement
import com.baidu.mapapi.map.offline.MKOfflineMap
import com.baidu.mapapi.map.offline.MKOfflineMapListener
import kotlinx.android.synthetic.main.activity_baiduoffline_map.*
import java.util.*


class BaiduOfflineMapActivity : Activity(), MKOfflineMapListener, View.OnClickListener {

    private lateinit var mOffline: MKOfflineMap
    private lateinit var viewpagerAdapter: PagerAdapter
    private val views = ArrayList<View>()
    private lateinit var expandableListView: ExpandableListView
    private lateinit var listView: ListView

    private lateinit var provinceList: ArrayList<String>//所有城市列表界面用
    private lateinit var allCityList: ArrayList<ArrayList<BaiduOfflineMapModule>>//所有城市列表界面用
    private lateinit var baiduOfflineAdapter: BaiduOfflineExpandableAdapter

    private lateinit var updateCityList: ArrayList<BaiduOfflineMapModule>//下载管理界面专用
    private lateinit var dowmloadedAdapter: BaiduOfflineListAdapter

    private var firstTag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_baiduoffline_map)
        SDKInitializer.initialize(applicationContext)
        // 初始化离线地图管理
        mOffline = MKOfflineMap()
        mOffline.init(this)

        provinceList = arrayListOf()
        allCityList = arrayListOf()
        updateCityList = arrayListOf()

        initView()
        setListener()
        initCitData()
        baiduOfflineAdapter = BaiduOfflineExpandableAdapter(this@BaiduOfflineMapActivity, mOffline, provinceList, allCityList)
        expandableListView.setAdapter(baiduOfflineAdapter)
        expandableListView.setGroupIndicator(null)
        dowmloadedAdapter = BaiduOfflineListAdapter(this@BaiduOfflineMapActivity, mOffline, updateCityList)
        listView.adapter = dowmloadedAdapter

    }

    private fun setListener() {
        back_image_view.setOnClickListener { finish() }
    }

    fun initCitData() {
        provinceList.clear()
        allCityList.clear()
        updateCityList.clear()

        ////下面是处理所有城市界面的数据
        val hotCity = mOffline.hotCityList
        val offlineCity = mOffline.offlineCityList
        val downloadCity = mOffline.allUpdateInfo
        //先添加当前城市
        provinceList.add("当前城市")
        var nowcity = arrayListOf<BaiduOfflineMapModule>()
        var it = BaiduOfflineMapModule()
        val city = mOffline.searchCity("宁波市")[0]//放个宁波玩玩
        val down = mOffline.getUpdateInfo(city.cityID)

        it.cityInfo = city
        it.downInfo = down
        nowcity.add(it)
        allCityList.add(nowcity)

        //添加热门城市
        provinceList.add("热门城市")
        var hotlist = arrayListOf<BaiduOfflineMapModule>()
        for (it in hotCity) {
            var city = BaiduOfflineMapModule()
            city.cityInfo = it
            hotlist.add(city)
        }
        allCityList.add(hotlist)

        //处理所有城市列表(包括港澳台直辖市等)
        provinceList.add("全国概略图、直辖市、港澳台等")
        var alist = arrayListOf<BaiduOfflineMapModule>()
        for (it in offlineCity) {
            if (it.cityType == 2 || it.cityType == 0) {//表示直辖市啥的
                var city = BaiduOfflineMapModule()
                city.cityInfo = it
                alist.add(city)
            } else {//普通省处理
                provinceList.add(it.cityName)
                var blist = arrayListOf<BaiduOfflineMapModule>()
                for (a in it.childCities) {
                    var city = BaiduOfflineMapModule()
                    city.cityInfo = a
                    blist.add(city)
                }
                allCityList.add(blist)
            }
        }
        allCityList.add(2, alist)

        //为每一个城市匹配相应下载信息
        if (downloadCity != null) {
            for (a in downloadCity) {
                for (b in allCityList) {
                    for (c in b) {
                        if (c.cityID == a.cityID) {
                            c.downInfo = a
                        }
                    }
                }
            }
        }
        ////下面是处理下载管理界面的数据
        if (downloadCity != null) {
            for (it in downloadCity) {
                var a = BaiduOfflineMapModule()
                for (b in offlineCity) {
                    if (b.cityType == 1) {
                        for (c in b.childCities) {
                            if (it.cityID == c.cityID) {
                                a.cityInfo = c
                                a.downInfo = it
                                break
                            }
                        }
                    } else {
                        if (it.cityID == b.cityID) {
                            a.cityInfo = b
                            a.downInfo = it
                        }
                    }
                }
                updateCityList.add(a)
            }
            var tit1 = BaiduOfflineMapModule()
            var tit2 = BaiduOfflineMapModule()
            tit1.status = 111
            tit2.status = 222
            updateCityList.add(tit1)
            updateCityList.add(tit2)
            Collections.sort(updateCityList)
        }

        if (!firstTag) {
            dowmloadedAdapter.notifyDataSetChanged()
            baiduOfflineAdapter.notifyDataSetChanged()
        } else {
            firstTag = false
        }
    }

    /**
     * 初始化
     */
    private fun initView() {
        download_list_text.setOnClickListener(this)
        downloaded_list_text.setOnClickListener(this)

        expandableListView = ExpandableListView(this)
        listView = ListView(this)
        views.add(expandableListView)
        views.add(listView)

        viewpagerAdapter = MyPagerAdapter()
        viewpager.adapter = viewpagerAdapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        download_list_text
                                .setBackgroundResource(R.drawable.city_list_pressed)
                        download_list_text.setTextColor(resources.getColor(R.color.textBlue))
                        downloaded_list_text
                                .setBackgroundResource(R.drawable.down_manager)
                        downloaded_list_text.setTextColor(Color.WHITE)
                    }
                    1 -> {
                        download_list_text
                                .setBackgroundResource(R.drawable.city_list)
                        download_list_text.setTextColor(Color.WHITE)
                        downloaded_list_text
                                .setBackgroundResource(R.drawable.down_manager_pressed)
                        downloaded_list_text.setTextColor(resources.getColor(R.color.textBlue))
                    }
                }
            }

        })


    }

    /**
     *百度地图离线回调
     */
    override fun onGetOfflineMapState(p0: Int, p1: Int) {
        when (p0) {
            MKOfflineMap.TYPE_DOWNLOAD_UPDATE -> {
                var update = mOffline.getUpdateInfo(p1)
                for (b in allCityList) {
                    for (c in b) {
                        if (c.cityID == update.cityID) {
                            //手动判断下载完成
                            if (update.ratio == 100 && update.status == MKOLUpdateElement.DOWNLOADING) {
                                update.status = MKOLUpdateElement.FINISHED
                            }
                            c.downInfo = update
                        }
                    }
                }
                for (it in updateCityList) {
                    if (it.cityID == update.cityID) {
                        //手动判断下载完成
                        if (update.ratio == 100 && update.status == MKOLUpdateElement.DOWNLOADING) {
                            update.status = MKOLUpdateElement.FINISHED
                        }
                        it.downInfo = update
                    }
                }
                if ((update.ratio == 100 && update.status == MKOLUpdateElement.DOWNLOADING) || update.status == MKOLUpdateElement.FINISHED || update.status == MKOLUpdateElement.eOLDSInstalling) {
                    Collections.sort(updateCityList)
                }
                dowmloadedAdapter.notifyDataSetChanged()
                baiduOfflineAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            download_list_text -> {
                viewpager.currentItem = 0

                download_list_text
                        .setBackgroundResource(R.drawable.city_list_pressed)
                download_list_text.setTextColor(resources.getColor(R.color.textBlue))

                downloaded_list_text
                        .setBackgroundResource(R.drawable.down_manager)
                downloaded_list_text.setTextColor(Color.WHITE)

            }
            downloaded_list_text -> {
                viewpager.currentItem = 1

                download_list_text
                        .setBackgroundResource(R.drawable.city_list)
                download_list_text.setTextColor(Color.WHITE)
                downloaded_list_text
                        .setBackgroundResource(R.drawable.down_manager_pressed)
                downloaded_list_text.setTextColor(resources.getColor(R.color.textBlue))

            }
            back_image_view -> // 返回
                finish()
        }
    }

    internal inner class MyPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = views[position]
            container.addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(views.get(position))
        }

        override fun getCount(): Int {
            return views.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

    }
}
