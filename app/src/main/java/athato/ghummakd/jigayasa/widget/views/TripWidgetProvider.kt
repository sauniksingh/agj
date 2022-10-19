package athato.ghummakd.jigayasa.widget.views

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.widget.AgjApplication
import athato.ghummakd.jigayasa.widget.RTDUpdateListener
import athato.ghummakd.jigayasa.widget.model.TripPojo
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TripWidgetProvider : AppWidgetProvider(), RTDUpdateListener {
    private var remoteViews: RemoteViews? = null

    @Suppress("DEPRECATION")
    private val handler: Handler = Handler()
    private lateinit var runnable: Runnable
    private val onClick = "Openapp"

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    private var mContext: Context? = null
    private var mAppWidgetManager: AppWidgetManager? = null
    private var mAppWidgetIds: IntArray? = null
    private var request: Int = 0
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        mContext = context
//        (context?.applicationContext as AgjApplication).rtdUpdateListener = this
        mAppWidgetManager = appWidgetManager!!
        mAppWidgetIds = appWidgetIds!!
        initUI(context)
        val tripPojo = getCurrentTripPojo()
        countDownStart(tripPojo)
    }

    @SuppressLint("RemoteViewLayout")
    private fun initUI(context: Context?) {
        remoteViews = RemoteViews(
            context?.packageName,
            R.layout.trip
        )
        remoteViews?.layoutId?.apply {
            remoteViews?.setOnClickPendingIntent(this, getPendingSelfIntent())
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun countDownStart(
        tripPojo: TripPojo?
    ) {
        runnable = object : Runnable {
            override fun run() {
                try {
                    handler.postDelayed(this, 1000)
                    if (TextUtils.isEmpty(tripPojo?.timeStamp)) {
                        remoteViews?.setViewVisibility(R.id.linear_layout_2, View.GONE)
                        updateTitleMessage(tripPojo)
                    } else {
                        updateTime(tripPojo!!)
                    }
                    mAppWidgetManager?.updateAppWidget(mAppWidgetIds, remoteViews)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (this::runnable.isInitialized) {
            handler.postDelayed(runnable, 1000)
        }
        Log.d("trippojo", tripPojo.toString())
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        if (this::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
        mContext?.apply {
            cancelAlarm(this)
        }
    }

    fun updateTitleMessage(tripPojo: TripPojo?) {
        if (TextUtils.isEmpty(tripPojo?.greetingMsg)) {
            remoteViews?.setViewVisibility(R.id.hjTextView, View.GONE)
        } else {
            remoteViews?.setViewVisibility(R.id.hjTextView, View.VISIBLE)
            remoteViews?.setTextViewText(R.id.hjTextView, tripPojo?.greetingMsg)
        }
        if (TextUtils.isEmpty(tripPojo?.title)) {
            remoteViews?.setViewVisibility(R.id.title, View.GONE)
        } else {
            remoteViews?.setViewVisibility(R.id.title, View.VISIBLE)
            remoteViews?.setTextViewText(R.id.title, tripPojo?.title)
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getCurrentTripPojo(): TripPojo {
        val todo = (mContext?.applicationContext as AgjApplication).todo
        for (tripPojo in todo) {
            if (!TextUtils.isEmpty(tripPojo.timeStamp)) {
                val eventDate = dateFormat.parse(tripPojo.timeStamp)
                val currentDate = Date()
                if (!currentDate.after(eventDate)) {
                    return tripPojo
                }
            } else
                break
        }
        return if (todo.isNotEmpty()) todo.last() else TripPojo()
    }

    private fun updateTime(
        tripPojo: TripPojo

    ) {
        val eventDate = dateFormat.parse(tripPojo.timeStamp)
        val currentDate = Date()
        if (!currentDate.after(eventDate)) {
            val diff = eventDate?.time?.minus(currentDate.time)
            val days = diff?.div(24 * 60 * 60 * 1000)
            val hours = diff?.div(60 * 60 * 1000)?.rem(24)
            val minutes = diff?.div(60 * 1000)?.rem(60)
            val seconds = diff?.div(1000)?.rem(60)
            if (days!! > 0) {
                remoteViews?.setTextViewText(R.id.tv_days, String.format("%02d", days))
                remoteViews?.setViewVisibility(R.id.ll_days, View.VISIBLE)
            } else {
                remoteViews?.setViewVisibility(R.id.ll_days, View.GONE)
            }
            if (hours!! > 0) {
                remoteViews?.setTextViewText(R.id.tv_hour, String.format("%02d", hours))
                remoteViews?.setViewVisibility(R.id.ll_hour, View.VISIBLE)
            } else {
                remoteViews?.setViewVisibility(R.id.ll_hour, View.GONE)
            }
            if (minutes!! > 0) {
                remoteViews?.setTextViewText(
                    R.id.tv_minute,
                    String.format("%02d", minutes)
                )
                remoteViews?.setViewVisibility(R.id.ll_min, View.VISIBLE)
            } else {
                remoteViews?.setViewVisibility(R.id.ll_min, View.GONE)
            }
            if (seconds!! > 0) {
                remoteViews?.setTextViewText(
                    R.id.tv_second,
                    String.format("%02d", seconds)
                )
                remoteViews?.setViewVisibility(R.id.ll_sec, View.VISIBLE)
            } else {
                remoteViews?.setViewVisibility(R.id.ll_sec, View.GONE)
            }
            remoteViews?.setViewVisibility(R.id.linear_layout_2, View.VISIBLE)
            updateTitleMessage(tripPojo)
            mAppWidgetManager?.notifyAppWidgetViewDataChanged(mAppWidgetIds, R.id.linear_layout_2)
        } else {
            remoteViews?.setViewVisibility(R.id.linear_layout_1, View.VISIBLE)
            remoteViews?.setViewVisibility(R.id.linear_layout_2, View.GONE)
            handler.removeCallbacks(runnable)
            countDownStart(
                getCurrentTripPojo()
            )
        }
    }

    override fun notifyDataChange() {
        handler.removeCallbacks(runnable)
        countDownStart(
            getCurrentTripPojo()
        )
        setAlarm(getCurrentTripPojo())
    }

    inner class Alarm : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.apply {
                val tripPojo = this.getParcelableExtra<TripPojo>("event")
                countDownStart(tripPojo)
            }
        }
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, Alarm::class.java)
        val sender = PendingIntent.getBroadcast(
            context,
            resultCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun setAlarm(pojo: TripPojo) {
        request = pojo.key
        mContext?.apply {
            val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(this, Alarm::class.java)
            i.putExtra("event", pojo)
            val pi = PendingIntent.getBroadcast(
                this,
                request,
                i,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            am.setRepeating(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(),
                (1000 * 60).toLong(),
                pi
            )
        }
    }

    private fun getPendingSelfIntent(): PendingIntent? {
        val intent = Intent(mContext, javaClass)
        intent.action = onClick
        return PendingIntent.getBroadcast(
            mContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (onClick.equals(intent?.action, true)) {
            mContext?.startActivity(Intent(mContext, TodoListActivity::class.java))
        }
    }
}