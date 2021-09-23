package athato.ghummakd.jigayasa.widget.firebase

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import athato.ghummakd.jigayasa.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (am.isBackgroundRestricted) {
            Toast.makeText(
                baseContext, getString(R.string.app_name) + "Yes Restricted",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                baseContext, getString(R.string.app_name) + "No Restricted",
                Toast.LENGTH_LONG
            ).show()
        }
        Log.d("main", "am.isBackgroundRestricted ${am.isBackgroundRestricted}")

        Log.d(
            "main",
            "notification ${NotificationManagerCompat.from(this).areNotificationsEnabled()}"
        )


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "AGJ"
        val channelName: CharSequence = "Chat"
        val importance = NotificationManager.IMPORTANCE_LOW
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager.createNotificationChannel(notificationChannel)

        Log.d(
            "main",
            "notification ${isNotificationChannelEnabled("AGJ")}"
        )
    }

    fun isNotificationChannelEnabled(channelId: String?): Boolean {
        if (!TextUtils.isEmpty(channelId)) {
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel: NotificationChannel? = manager.getNotificationChannel(channelId)
            return channel?.importance != NotificationManager.IMPORTANCE_NONE
        }
        return false

    }
}