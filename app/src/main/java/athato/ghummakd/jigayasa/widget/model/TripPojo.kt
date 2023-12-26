package athato.ghummakd.jigayasa.widget.model

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripPojo(
    var title: String? = null,
    var greetingMsg: String? = null,
    var timeStamp: String? = null,
    var key: Int = 0
) : Parcelable {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}