package com.example.lifemaster.presentation

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lifemaster.R
import com.example.lifemaster.presentation.community.CommunityFragment
import com.example.lifemaster.databinding.ActivityMainBinding
import com.example.lifemaster.presentation.group.AlarmFragment
import com.example.lifemaster.presentation.home.ToDoFragment
import com.example.lifemaster.presentation.total.detox.fragment.DetoxFragment
import com.example.lifemaster.presentation.total.detox.model.DetoxTargetApp
import com.example.lifemaster.presentation.total.detox.viewmodel.DetoxCommonViewModel
import com.example.lifemaster.presentation.total.detox.viewmodel.DetoxRepeatLockViewModel
import com.example.lifemaster.presentation.total.detox.viewmodel.DetoxTimeLockViewModel
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    // View 관련 변수
    private lateinit var binding: ActivityMainBinding
    private lateinit var totalApps: MutableList<ApplicationInfo>
    private lateinit var requiredApps: List<ApplicationInfo>
    private var foregroundStartTime: Long = 0L

    // ViewModel 변수
    private val detoxCommonViewModel: DetoxCommonViewModel by viewModels()
    private val detoxRepeatLockViewModel: DetoxRepeatLockViewModel by viewModels()
    private val detoxTimeLockViewModel: DetoxTimeLockViewModel by viewModels()

    // 실시간 UI 변경을 위한 변수
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeCacheDir.setReadOnly()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateRunnable = object : Runnable {
            override fun run() {
                val elapsedForegroundTime = SystemClock.elapsedRealtime() - foregroundStartTime // 포그라운드로 전환 이후 누적된 시간
                detoxCommonViewModel.updateTempElapsedForegroundTime(elapsedForegroundTime)
                handler.postDelayed(this, 1000L)
            }
        }

        totalApps = packageManager.getInstalledApplications(0)
        requiredApps = totalApps.filter { app ->
            val isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystemApp = (app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            !isSystemApp && !isUpdatedSystemApp
        }

        if(savedInstanceState == null) {
            binding.navigation.selectedItemId = R.id.action_detox
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, DetoxFragment()).commit()
        }

        fetchApplications()

        setupListeners()

        requestUsageAccessPermission(this)

//         차단할 앱 서비스 기능을 위한 접근성 권한 관련 코드
//        val isAccessibilityPermitted = checkAccessibilityPermissions()
//        Toast.makeText(this, "접근성 권한 허용되었는가? $isAccessibilityPermitted", Toast.LENGTH_SHORT).show()
//        if(!isAccessibilityPermitted) {
//            AlertDialog.Builder(this).apply {
//                setTitle("접근성 권한 허용 필요")
//                setMessage("앱을 사용하기 위해 접근성 권한이 필요합니다.")
//                setPositiveButton("허용") { _, _ ->
//                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
//                }
//                setCancelable(false)
//                create().show()
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        foregroundStartTime = SystemClock.elapsedRealtime() // 앱이 포그라운드로 전환된 시간 기록
        updateUsageStats()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    // 실제 디바이스에 설치된 어플리케이션을 가져오는 함수
    private fun fetchApplications() {

        val applicationList = arrayListOf<DetoxTargetApp>()
        val usageStatsMap = getDailyUsageStats(this)

        for(app in requiredApps) {
            val appName = app.loadLabel(packageManager).toString()
            val appIcon = app.loadUnbadgedIcon(packageManager)
            val appPackageName = app.packageName
            val accumulatedTime = usageStatsMap[app.packageName] ?: 0L // 누적 사용 시간은 실시간으로 변동되지 않음 (리팩토링 필요)
            applicationList.add(DetoxTargetApp(appIcon, appName, appPackageName, accumulatedTime))
        }

        detoxRepeatLockViewModel.blockServiceApplications = ArrayList(applicationList)
        detoxRepeatLockViewModel.repeatLockTargetApplications = ArrayList(applicationList)
        detoxTimeLockViewModel.allowServiceApplications = ArrayList(applicationList)
    }

    // 앱 사용 시간을 실시간으로 업데이트하는 함수 (1초마다 실행됨)
    private fun updateUsageStats() {

        val usageStatsMap = getDailyUsageStats(this)

        var totalUsageTime = 0L

        for(app in requiredApps) {
            val accumulatedUsageTime = usageStatsMap[app.packageName] ?: 0L
            if(accumulatedUsageTime>0) {
                Log.d("debugging", "${app.loadLabel(packageManager)}: ${convertLongFormat(accumulatedUsageTime)}")
            }
            totalUsageTime += accumulatedUsageTime
        }

        detoxCommonViewModel.updateTotalAccumulatedAppUsageTimes(totalUsageTime)
    }

    private fun convertLongFormat(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val remainSeconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, remainSeconds)
    }

    // 클릭 이벤트 관련 함수
    private fun setupListeners() {
        // [!] setOnClickListener 가 아니라 setOnItemSelectedListener 이기 때문에, 하단 개별 뷰를 누르지 않더라도 selectedItemId 를 해당 뷰로 바꿔주면 동작한다.
        binding.navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, ToDoFragment()).commit()
                    true
                }
                R.id.action_alarm -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, AlarmFragment()).commit()
                    true
                }
                R.id.action_community -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, CommunityFragment()).commit()
                    true
                }
                R.id.action_detox -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, DetoxFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }

    // 자정을 기준으로 하루 앱 사용 시간을 측정하는 함수
    private fun getDailyUsageStats(context: Context): Map<String, Long> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val eventMap = mutableMapOf<String, Long>()

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var currentForegroundApp: String? = null
        var lastEventTime = 0L

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            when (event.eventType) {
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                    currentForegroundApp = event.packageName
                    lastEventTime = event.timeStamp
                }
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                    if (currentForegroundApp != null && lastEventTime != 0L) {
                        val usageTime = event.timeStamp - lastEventTime
                        eventMap[currentForegroundApp] = (eventMap[currentForegroundApp] ?: 0) + usageTime
                    }
                    currentForegroundApp = null
                    lastEventTime = 0L
                }
            }
        }

        return eventMap
    }


    // 접근성 권한 활성화 여부 확인
    private fun checkAccessibilityPermissions(): Boolean {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)

        for(serviceInfo in enabledServices) {
            if(serviceInfo.resolveInfo.serviceInfo.packageName == application.packageName) return true
        }

        return false
    }

    // 앱 사용 시간에 대한 설정 화면으로 이동하는 함수(권한 없을 시)
    private fun requestUsageAccessPermission(context: Context) {
        if(!isUsageAccessGranted(context)) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }
    }

    // 앱 사용 시간 권한 활성화 여부를 확인하는 함수
    private fun isUsageAccessGranted(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}