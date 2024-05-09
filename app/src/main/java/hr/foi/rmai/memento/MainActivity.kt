package hr.foi.rmai.memento

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rmai.memento.adapters.MainPagerAdapter
import hr.foi.rmai.memento.database.TasksDatabase
import hr.foi.rmai.memento.fragments.CompletedFragment
import hr.foi.rmai.memento.fragments.NewsFragment
import hr.foi.rmai.memento.fragments.PendingFragment
import hr.foi.rmai.memento.helpers.MockDataLoader
import hr.foi.rmai.memento.services.TaskDeletionService

class MainActivity : AppCompatActivity() {
    lateinit var tabLayout : TabLayout
    lateinit var viewPager : ViewPager2
    lateinit var mainPagerAdapter : MainPagerAdapter
    lateinit var navDrawerLayout : DrawerLayout
    lateinit var navView : NavigationView
    lateinit var btnStartGame: Button

    lateinit var onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener
    lateinit var tvHighScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeMainPagerAdapter()
        connectViewPagerWithLayout()

        connectNavDrawerWithViewPager()

        TasksDatabase.buildInstance(applicationContext)
        MockDataLoader.loadMockData()

        prepareServices()
        connectGameButton()
    }

    private fun connectGameButton() {
        btnStartGame = findViewById(R.id.btnStartGame)
        btnStartGame.setOnClickListener {
            val intent: Intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        tvHighScore = findViewById(R.id.tvHighScore)
        tvHighScore.text = this.getSharedPreferences("RMAI-High-Scores", Context.MODE_PRIVATE)
            .getLong("fastestTime", 0).toString()
    }

    private fun connectNavDrawerWithViewPager() {
        navDrawerLayout = findViewById(R.id.nav_drawer_layout)
        navView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.title) {
                "PENDING" -> viewPager.setCurrentItem(0, true)
                "COMPLETED" -> viewPager.setCurrentItem(1, true)
                else -> viewPager.setCurrentItem(2, true)
            }

            navDrawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }

        val taskCounterItem = navView.menu
                                .add(2, 0, 0, "")

        attachTasksCountToMenuItem(taskCounterItem)
    }

    private fun attachTasksCountToMenuItem(taskCounterItem: MenuItem) {
        val sharedPreferences = getSharedPreferences("tasks_preferences",
            Context.MODE_PRIVATE)

        onSharedPreferenceChangeListener = OnSharedPreferenceChangeListener { _, key ->
            if (key == "tasks_created_counter") {
                updateTaskCounterMenuItem(sharedPreferences, taskCounterItem)
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(
            onSharedPreferenceChangeListener)

        updateTaskCounterMenuItem(sharedPreferences, taskCounterItem)
    }

    private fun updateTaskCounterMenuItem(sharedPreferences: SharedPreferences,
                                          taskCounterItem: MenuItem) {
        val tasksCreatedCount = sharedPreferences.getInt("tasks_created_counter", 0)
        taskCounterItem.title = "Tasks created: $tasksCreatedCount"
    }

    private fun getTasksCreatedCount(): Int {
        val sharedPreferences = getSharedPreferences("tasks_preferences",
                                                    Context.MODE_PRIVATE)
        return sharedPreferences.getInt("tasks_created_counter", 0)
    }

    private fun prepareServices() {
        createNotificationChannel()
        activateTaskDeletionService()
    }

    private fun activateTaskDeletionService() {
        val intent = Intent(this, TaskDeletionService::class.java)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getService(this, 0,
                            intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 15 * 60 * 1000,
            15 * 60 * 1000,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "task-timer",
            "Task Timer Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun connectViewPagerWithLayout() {
        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.viewpager)

        viewPager.adapter = mainPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setText(mainPagerAdapter.fragmentItems[position].titleRes)
            tab.setIcon(mainPagerAdapter.fragmentItems[position].iconRes)
        }.attach()
    }

    fun initializeMainPagerAdapter() {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        fillAdapterWithFragments()
    }

    fun fillAdapterWithFragments() {
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_pending,
                R.drawable.baseline_assignment_late_24,
                PendingFragment::class
            )
        )

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_completed,
                R.drawable.baseline_assignment_turned_in_24,
                CompletedFragment::class
            )
        )

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.news,
                R.drawable.baseline_assignment_24,
                NewsFragment::class
            )
        )

    }
}