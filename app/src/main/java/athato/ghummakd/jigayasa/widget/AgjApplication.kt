package athato.ghummakd.jigayasa.widget

import android.app.Application
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.widget.model.TripPojo
import com.google.firebase.database.ChildEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * Created by Saunik Singh on 3/14/2020.
 * Bada Business
 */
class AgjApplication : Application() {
    //    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var childEventListener: ChildEventListener
//    var rtdUpdateListener: RTDUpdateListener? = null
    lateinit var activityRtdUpdateListener: RTDUpdateListener
    var todo = ArrayList<TripPojo>()
    override fun onCreate() {
        super.onCreate()
//        FirebaseApp.initializeApp(this)
        todo = getTodos()
//        mDatabaseReference =
//            FirebaseDatabase.getInstance("https://agj-testing.firebaseio.com/").reference
//        mDatabaseReference.keepSynced(true)
//        childEventListener = object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//                val tripPojo = dataSnapshot.getValue(TripPojo::class.java)
//                updateChild(tripPojo)
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
//                val tripPojo = dataSnapshot.getValue(TripPojo::class.java)
//                updateChild(tripPojo)
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                val todoItem = dataSnapshot.getValue(TripPojo::class.java)
//                removeChild(todoItem)
//            }
//
//            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
//                updateChild(dataSnapshot.getValue(TripPojo::class.java))
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        }
//        mDatabaseReference.addChildEventListener(childEventListener)
    }

    private fun getTodos(): ArrayList<TripPojo> {
        var menuArray: String? = null
        try {
            val `is`: InputStream = resources?.openRawResource(R.raw.todo)!!
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            menuArray = String(buffer, StandardCharsets.UTF_8)
        } catch (io: IOException) {
            io.printStackTrace()
        }
        val arrayToDo = object : TypeToken<ArrayList<TripPojo>>() {}.type
        return Gson().fromJson(menuArray, arrayToDo)
    }

    fun updateChild(pTripPojo: TripPojo?) {
        for (i in todo.indices) {
            val todoItem1 = todo[i]
            if (pTripPojo != null && pTripPojo.key == todoItem1.key) {
                todo[i] = pTripPojo
                break
            }
        }
//        rtdUpdateListener?.notifyDataChange()
        activityRtdUpdateListener.notifyDataChange()
    }

    fun removeChild(pTripPojo: TripPojo?) {
        val itr = todo.iterator()
        while (itr.hasNext()) {
            val todoPojo = itr.next()
            if (pTripPojo != null && pTripPojo.key == todoPojo.key) {
                itr.remove()
            }
        }
//        rtdUpdateListener?.notifyDataChange()
        activityRtdUpdateListener.notifyDataChange()
    }
}