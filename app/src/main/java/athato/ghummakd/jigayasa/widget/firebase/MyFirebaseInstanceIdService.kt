package athato.ghummakd.jigayasa.widget.firebase

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import athato.ghummakd.jigayasa.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseInstanceIdService : FirebaseMessagingService() {
    val TAG = "MyFirebaseInstanceIdService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //if the message contains data payload
        //It is a map of custom keyvalues
        //we can read it easily
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "No message")
        }

        //getting the title and the body

        handleMessage(remoteMessage)
        //then here we can use the title and body to build a notification
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        //1
        val handler = Handler(Looper.getMainLooper())
        val title = remoteMessage.notification!!.title
        val body = remoteMessage.notification!!.body
        Log.d(TAG, "title $title body $body")
        //2
        handler.post {
            Toast.makeText(
                baseContext, getString(R.string.app_name),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}