package athato.ghummakd.jigayasa.widget.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import athato.ghummakd.jigayasa.R
import athato.ghummakd.jigayasa.databinding.ActivityMainBinding
import athato.ghummakd.jigayasa.widget.AgjApplication
import athato.ghummakd.jigayasa.widget.RTDUpdateListener

class TodoListActivity : AppCompatActivity(), RTDUpdateListener {
    lateinit var binding: ActivityMainBinding
    private val todoAdapter by lazy { ToDoAdapter((application as AgjApplication).todo) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        (application as AgjApplication).activityRtdUpdateListener = this
        binding.todoRv.apply {
            layoutManager =
                LinearLayoutManager(this@TodoListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = todoAdapter
        }
    }

    override fun notifyDataChange() {
        todoAdapter.apply {
            todoAdapter.updateList((application as AgjApplication).todo)
        }
    }
}