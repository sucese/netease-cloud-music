package com.guoxiaoxing.cloud.music.ui.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast

import com.guoxiaoxing.cloud.music.R
import com.guoxiaoxing.cloud.music.adapter.MenuItemAdapter
import com.guoxiaoxing.cloud.music.handler.HandlerUtil
import com.guoxiaoxing.cloud.music.magicasakura.utils.ThemeUtils
import com.guoxiaoxing.cloud.music.ui.BaseActivity
import com.guoxiaoxing.cloud.music.ui.fragment.BitSetFragment
import com.guoxiaoxing.cloud.music.ui.fragment.MineFragment
import com.guoxiaoxing.cloud.music.ui.fragment.TimingFragment
import com.guoxiaoxing.cloud.music.ui.fragment.MusicLibraryFragment
import com.guoxiaoxing.cloud.music.service.MusicPlayer
import com.guoxiaoxing.cloud.music.uitl.ThemeHelper
import com.guoxiaoxing.cloud.music.widget.CustomViewPager
import com.guoxiaoxing.cloud.music.widget.SplashScreen
import com.guoxiaoxing.cloud.music.widget.dialog.CardPickerDialog
import kotlinx.android.synthetic.main.activity_main.*

import java.util.ArrayList

class MainActivity : BaseActivity(), CardPickerDialog.ClickListener {

    private val tabs = ArrayList<ImageView?>()
    private var time: Long = 0
    var splashScreen: SplashScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = SplashScreen(this)
        splashScreen?.show(R.drawable.art_login_bg,
                SplashScreen.SLIDE_LEFT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setBackgroundDrawableResource(R.color.background_material_light_1)

        setToolBar()
        setViewPager()
        setupDrawer()
        HandlerUtil.getInstance(this).postDelayed({ splashScreen?.removeSplashScreen() }, 3000)
    }

    private fun setToolBar() {
        val toolbar = findViewById(R.id.activity_main_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        actionBar?.title = ""
    }

    private fun setViewPager() {
        tabs.add(activity_main_bar_music_library)
        tabs.add(activity_main_bar_mine)
        val customViewPager = findViewById(R.id.activity_main_viewpager) as CustomViewPager
        val mineFragment = MineFragment()
        val musicLibraryFragment = MusicLibraryFragment()
        val customViewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager)
        customViewPagerAdapter.addFragment(musicLibraryFragment)
        customViewPagerAdapter.addFragment(mineFragment)
        customViewPager.adapter = customViewPagerAdapter
        customViewPager.currentItem = 1
        activity_main_bar_mine.isSelected = true
        customViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                switchTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        activity_main_bar_music_library.setOnClickListener { customViewPager.currentItem = 0 }

        activity_main_bar_mine.setOnClickListener { customViewPager.currentItem = 1 }

        activity_main_bar_search.setOnClickListener {
            val intent = Intent(this@MainActivity, NetSearchWordsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this@MainActivity.startActivity(intent)
        }
    }

    private fun setupDrawer() {
        val inflater = LayoutInflater.from(this)
        activity_main_id_lv_left_menu.addHeaderView(inflater.inflate(R.layout.nav_header_main, activity_main_id_lv_left_menu, false))
        activity_main_id_lv_left_menu.adapter = MenuItemAdapter(this)
        activity_main_id_lv_left_menu.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                1 -> activity_main_fd.closeDrawers()
                2 -> {
                    val dialog = CardPickerDialog()
                    dialog.setClickListener(this@MainActivity)
                    dialog.show(supportFragmentManager, "theme")
                    activity_main_fd.closeDrawers()
                }
                3 -> {
                    val fragment3 = TimingFragment()
                    fragment3.show(supportFragmentManager, "timing")
                    activity_main_fd.closeDrawers()
                }
                4 -> {
                    val bfragment = BitSetFragment()
                    bfragment.show(supportFragmentManager, "bitset")
                    activity_main_fd.closeDrawers()
                }
                5 -> {
                    if (MusicPlayer.isPlaying()) {
                        MusicPlayer.playOrPause()
                    }
                    unbindService()
                    finish()
                    activity_main_fd.closeDrawers()
                }
            }
        }
    }

    private fun switchTabs(position: Int) {
        for (i in tabs.indices) {
            tabs.get(i)?.isSelected = position == i
        }
    }

    override fun onConfirm(currentTheme: Int) {
        if (ThemeHelper.getTheme(this@MainActivity) != currentTheme) {
            ThemeHelper.setTheme(this@MainActivity, currentTheme)
            ThemeUtils.refreshUI(this@MainActivity, object : ThemeUtils.ExtraRefreshable {
                override fun refreshGlobal(activity: Activity) {
                    //for global setting, just do once
                    if (Build.VERSION.SDK_INT >= 21) {
                        val context = this@MainActivity
                        val taskDescription = ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary))
                        setTaskDescription(taskDescription)
                        window.statusBarColor = ThemeUtils.getColorById(context, R.color.theme_color_primary)
                    }
                }

                override fun refreshSpecificView(view: View) {}
            }
            )
        }
        changeTheme()
    }

    internal class CustomViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()

        fun addFragment(fragment: Fragment) {
            mFragments.add(fragment)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home //Menu icon
            -> {
                activity_main_fd.openDrawer(Gravity.LEFT)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashScreen?.removeSplashScreen()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time > 1000) {
                Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show()
                time = System.currentTimeMillis()
            } else {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
