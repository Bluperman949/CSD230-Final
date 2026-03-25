package bluper.b9final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import bluper.b9final.databinding.RecyclerItemGameBinding
import bluper.b9final.databinding.RecyclerItemTagBinding
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// for the initial Tags view //
@OptIn(InternalSerializationApi::class)
@Serializable
data class SteamTag(val tagid: Int, val name: String) {
  override fun toString(): String = name
}

class TagAdapter : Adapter<SteamTag, TagAdapter.Holder>() {
  override fun getLayoutId(): Int = R.layout.recycler_item_tag

  override fun formatDatum(holder: Holder, datum: SteamTag) {
    holder.binding.textView.text = datum.name
  }

  override fun createHolder(view: View) = Holder(view)

  class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val binding by lazy { RecyclerItemTagBinding.bind(view) }
  }
}

// for the Games view //
@OptIn(InternalSerializationApi::class)
@Serializable
data class SteamGame(val appid: Int, val name: String) {
  override fun toString(): String = name
}

class GameAdapter : Adapter<SteamGame, GameAdapter.Holder>() {
  override fun getLayoutId(): Int = R.layout.recycler_item_tag

  override fun formatDatum(holder: Holder, datum: SteamGame) {
    holder.binding.textView.text = datum.name
  }

  override fun createHolder(view: View) = Holder(view)

  class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val binding by lazy { RecyclerItemGameBinding.bind(view) }
  }
}

// common logic //
abstract class Adapter<T, H : RecyclerView.ViewHolder> :
  RecyclerView.Adapter<H>() {

  private val data = arrayListOf<T>()
  override fun getItemCount() = data.size

  fun clear() {
    val len = data.size
    data.clear()
    notifyItemRangeRemoved(0, len)
  }

  fun addItem(item: T) {
    data += item
    notifyItemInserted(data.size - 1)
  }

  fun addItems(items: Collection<T>) {
    data += items
    data.sortWith { a, b -> a.toString().compareTo(b.toString()) }
    notifyItemRangeInserted(data.size - items.size, items.size)
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): H {
    // use recycler_item.xml as template for displaying list entries
    val inflater = LayoutInflater.from(viewGroup.context)
    val view = inflater.inflate(getLayoutId(), viewGroup, false)
    return createHolder(view)
  }

  override fun onBindViewHolder(holder: H, pos: Int) {
    formatDatum(holder, data[pos])
  }

  abstract fun getLayoutId(): Int
  abstract fun formatDatum(holder: H, datum: T)
  abstract fun createHolder(view: View): H
}
