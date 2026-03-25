package bluper.b9final

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import bluper.b9final.databinding.ActivityTagsBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Request
import java.io.IOException

class TagsActivity : AbstractActivity() {
  val binding by lazy { ActivityTagsBinding.inflate(layoutInflater) }
  private val adapter by lazy { TagAdapter() }

  private var steamTags: List<SteamTag> = emptyList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    acquireApiKey(this)
    loadSteamTags()
    binding.recycler.adapter = adapter
  }

  override fun getInflatedBinding() = binding

  override fun onQueryTextChange(text: String?): Boolean {
    adapter.clear()
    if (text.isNullOrBlank()) {
      adapter.addItems(steamTags)
      return true
    }
    adapter.addItems(steamTags.filter { it.name.contains(text, true) })
    return true
  }

  private fun loadSteamTags() {
    val request = Request.Builder()
      .url("https://api.steampowered.com/IStoreService/GetTagList/v1/?language=english")
      .build()
    lifecycleScope.launch {
      try {
        val responseElem = httpRequest(request)
        val response = responseElem.jsonObject["response"]?.jsonObject["tags"]
          ?: throw IOException("Failed reading Tags")
        steamTags = json.decodeFromJsonElement<List<SteamTag>>(response)
        adapter.addItems(steamTags)
      } catch (e: Exception) {
        errorDialog(e)
      }
    }
  }
}