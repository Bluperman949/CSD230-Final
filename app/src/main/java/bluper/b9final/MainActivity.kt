package bluper.b9final

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import bluper.b9final.databinding.ActivityMainBinding
import bluper.b9final.databinding.RecyclerItemBinding
import java.util.Locale

class MainActivity : ComponentActivity() {
  val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
  private val adapter by lazy { RecyclerAdapter() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    // prevent toolbar from obscuring content
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
      v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
      insets
    }

    // give recycler view ability to fill itself with items
    binding.recycler.adapter = adapter

    // search functionality
    binding.buttonSearch.setOnClickListener { v ->
      // TODO: but actually search fr
      adapter.clear()
    }

    // TODO: temporary, remove later
    binding.editTextSearch.addTextChangedListener { v ->
      adapter.addItem(v.toString())
    }
  }
}

private class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {
  private val data = arrayListOf<String>();

  fun clear() {
    val len = data.size
    data.clear()
    notifyItemRangeRemoved(0, len)
  }

  fun addItem(item: String) {
    data += item
    notifyItemInserted(data.size - 1)
  }

  fun addItems(items: List<String>) {
    data += items
    notifyItemRangeInserted(data.size - items.size, items.size)
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerHolder {
    // use recycler_item.xml as template for displaying list entries
    val inflater = LayoutInflater.from(viewGroup.context)
    val view = inflater.inflate(R.layout.recycler_item, viewGroup, false)
    return RecyclerHolder(view)
  }

  override fun onBindViewHolder(holder: RecyclerHolder, pos: Int) {
    holder.binding.textView.text = String.format(Locale.getDefault(), "Item #%d: %s", pos, data[pos])
  }

  class RecyclerHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding by lazy { RecyclerItemBinding.bind(view) }
  }
}