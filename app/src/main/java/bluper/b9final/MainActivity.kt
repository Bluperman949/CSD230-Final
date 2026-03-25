package bluper.b9final

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import bluper.b9final.databinding.ActivityMainBinding
import bluper.b9final.databinding.RecyclerItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// TODO: about button

@OptIn(InternalSerializationApi::class)
@Serializable
data class SteamTag(val tagid: Int, val name: String)

class MainActivity : ComponentActivity() {
  val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
  private val adapter by lazy { TagRecyclerAdapter() }
  private val httpClient = OkHttpClient()
  private val json = Json { ignoreUnknownKeys = true }

  private var apiKey = "NO_API_KEY"
  private var steamTags: List<SteamTag> = emptyList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    // prevent toolbar from obscuring content
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
      v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
      insets
    }

    // prompt user for API key
//    acquireApiKey()

    // acquire Tags from Steam API and add them to the recycler
    loadSteamTags()

    // give recycler view ability to fill itself with items
    binding.recycler.adapter = adapter

    // search functionality
    binding.buttonSearch.setOnClickListener { searchSteam(binding.editTextSearch.text.toString()) }
  }

  fun searchSteam(text: String) {
    if (text.isEmpty()) return
    adapter.clear()
  }

  private fun acquireApiKey() {
    val input = EditText(this@MainActivity)
    input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

    AlertDialog.Builder(this@MainActivity)
      .setView(input)
      .setTitle(R.string.ask_api_key)
      .setMessage(R.string.message_api_key)
      .setPositiveButton("OK") { _, _ -> apiKey = input.text.toString() }
      .show()
  }

  @Throws(IOException::class)
  private suspend fun httpRequest(request: Request): JsonElement {
    return withContext(Dispatchers.IO) {
      val responseStr = httpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("HTTP ${response.code} from \"${request.url}\"")
        response.body?.string() ?: throw IOException("Empty HTTP response from \"${request.url}\"")
      }
      json.decodeFromString<JsonElement>(responseStr)
    }
  }

  private fun loadSteamTags() {
    val request = Request.Builder()
      .url("https://api.steampowered.com/IStoreService/GetTagList/v1/?language=english")
      .build()
    lifecycleScope.launch {
      try {
        val responseElem = httpRequest(request)
        val response = responseElem.jsonObject["response"]?.jsonObject["tags"]
          ?: throw IOException("Failed reading Tags: ${request.url}")
        steamTags = json.decodeFromJsonElement<List<SteamTag>>(response)
        adapter.addItems(steamTags)
      } catch (e: Exception) {
        errorDialog(e)
      }
    }
  }

  private fun <E : Exception> errorDialog(e: E) {
    e.printStackTrace()
    AlertDialog.Builder(this@MainActivity)
      .setTitle(R.string.error)
      .setMessage(e.message)
      .setPositiveButton("OK", null)
      .show()
  }
}

private abstract class RecyclerAdapter<T> : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {
  private val data = arrayListOf<T>();

  fun clear() {
    data.clear()
    notifyItemRangeRemoved(0, data.size)
  }

  fun addItem(item: T) {
    data += item
    notifyItemInserted(data.size - 1)
  }

  fun addItems(items: Collection<T>) {
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
    holder.binding.textView.text = formatDatum(data[pos])
  }

  abstract fun formatDatum(datum: T): String

  class RecyclerHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding by lazy { RecyclerItemBinding.bind(view) }
  }
}

private class TagRecyclerAdapter : RecyclerAdapter<SteamTag>() {
  override fun formatDatum(datum: SteamTag): String = datum.name
}