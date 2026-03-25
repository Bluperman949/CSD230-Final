package bluper.b9final

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bluper.b9final.databinding.RecyclerItemGameBinding
import bluper.b9final.databinding.RecyclerItemTagBinding

// for the initial Tags view //
class TagAdapter : Adapter<SteamTag, TagAdapter.Holder>() {
  override fun getLayoutId(): Int = R.layout.recycler_item_tag

  override fun populateHolder(holder: Holder, data: SteamTag) {
    val button = holder.binding.button
    button.text = data.name
    button.setOnClickListener { v ->
      val intent = Intent(v.context, GamesActivity::class.java)
        .putExtra("tag", data.tagid)
      v.context.startActivity(intent)
    }
  }

  override fun createHolder(view: View) = Holder(view)

  class Holder(view: View) : RecyclerView.ViewHolder(view) {
    val binding by lazy { RecyclerItemTagBinding.bind(view) }
  }
}

// for the Games view //
class GameAdapter : Adapter<SteamGame, GameAdapter.Holder>() {
  override fun getLayoutId(): Int = R.layout.recycler_item_game

  override fun populateHolder(holder: Holder, data: SteamGame) {
    holder.binding.title.text = data.name
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
    populateHolder(holder, data[pos])
  }

  abstract fun getLayoutId(): Int
  abstract fun populateHolder(holder: H, data: T)
  abstract fun createHolder(view: View): H
}
