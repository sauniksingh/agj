package athato.ghummakd.jigayasa.widget.views

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.databinding.ItemTodoBinding
import athato.ghummakd.jigayasa.widget.model.TripPojo
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Saunik Singh on 28/10/21.
 */
class ToDoAdapter(var mList: List<TripPojo>) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val handler: Handler = Handler(Looper.getMainLooper())
        private lateinit var runnable: Runnable

        fun bind(item: TripPojo) {
            binding.title.text = item.title
            binding.timeStamp.text = item.timeStamp
            if (!TextUtils.isEmpty(item.greetingMsg)) {
                binding.msg.text = item.greetingMsg
                binding.msg.visibility = View.VISIBLE
            } else {
                binding.msg.visibility = View.GONE
            }
            countDownStart(item)
            handler.post(runnable)
        }

        private fun countDownStart(item: TripPojo) {
            runnable = object : Runnable {
                override fun run() {
                    try {
                        handler.postDelayed(this, 60000)
                        if (TextUtils.isEmpty(item.timeStamp)) {
                            binding.linearLayout2.visibility = View.GONE
                        } else {
                            updateTime(item)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        private fun updateTime(item: TripPojo) {
            @SuppressLint("SimpleDateFormat")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val eventDate = dateFormat.parse(item.timeStamp)
            val currentDate = Date()
            if (!currentDate.after(eventDate)) {
                val diff = eventDate?.time?.minus(currentDate.time)
                val days = diff?.div(24 * 60 * 60 * 1000)
                val hours = diff?.div(60 * 60 * 1000)?.rem(24)
                val minutes = diff?.div(60 * 1000)?.rem(60)
                val seconds = diff?.div(1000)?.rem(60)
                if (days!! > 0) {
                    binding.tvDays.text = String.format("%02d", days)
                    binding.llDays.visibility = View.VISIBLE
                } else {
                    binding.llDays.visibility = View.GONE
                }
                if (hours!! > 0) {
                    binding.tvHour.text = String.format("%02d", hours)
                    binding.llHour.visibility = View.VISIBLE
                } else {
                    binding.llHour.visibility = View.GONE
                }
                if (minutes!! > 0) {
                    binding.tvMinute.text = String.format("%02d", minutes)
                    binding.llMin.visibility = View.VISIBLE
                } else {
                    binding.llMin.visibility = View.GONE
                }
//                if (seconds!! > 0) {
//                    binding.tvSecond.text = String.format("%02d", seconds)
//                    binding.llSec.visibility = View.VISIBLE
//                } else {
                binding.llSec.visibility = View.GONE
//                }
                binding.linearLayout2.visibility = View.VISIBLE
            } else {
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_todo,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(items: List<TripPojo>) {
        mList = items
        notifyDataSetChanged()
    }
}