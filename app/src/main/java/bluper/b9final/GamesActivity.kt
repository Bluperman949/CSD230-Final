package bluper.b9final

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import bluper.b9final.databinding.ActivityGamesBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Request
import java.io.IOException

class GamesActivity : AbstractActivity() {
  val binding by lazy { ActivityGamesBinding.inflate(layoutInflater) }
  private val adapter by lazy { GameAdapter() }

  private var steamGames: List<SteamGame> = emptyList()

  private val queryJson by lazy {
    val tag = intent.getIntExtra("tag", -24)
    if (tag == -24) throw IllegalStateException("Tag not provided")
    """
    {"query":{"count":"50","sort":"0","filters":{"released_only":true,"type_filters":{"include_apps"
    :"","include_packages":"","include_bundles":"","include_games":true,"include_demos":"","include_
    mods":"","include_dlc":"","include_software":"","include_video":"","include_hardware":"","includ
    e_music":""},"tagids_must_match":[{"tagids":["$tag"]}],"content_descriptors_excluded":["1","4","
    5"]}},"context":{"language":"english","country_code":"us"},"data_request":{"include_reviews":tru
    e,"include_basic_info":true}}
    """.trimIndent().replace("\n", "")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    loadSteamGames()
    binding.recycler.adapter = adapter
  }

  override fun getInflatedBinding() = binding

  override fun onQueryTextChange(text: String?): Boolean {
    adapter.clear()
    if (text.isNullOrBlank()) {
      adapter.addItems(steamGames)
      return true
    }
    adapter.addItems(steamGames.filter { it.name.contains(text, true) })
    return true
  }

  private fun loadSteamGames() {
    val request = Request.Builder()
      .url("https://api.steampowered.com/IStoreQueryService/Query/v1/?key=$apiKey&input_json=$queryJson")
      .build()
    lifecycleScope.launch {
      try {

        val responseElem = httpRequest(request)
        val response = responseElem.jsonObject["response"]?.jsonObject["store_items"]
          ?: throw IOException("Failed reading Games for Tag")
        steamGames = json.decodeFromJsonElement<List<SteamGame>>(response)

        adapter.addItems(steamGames)

      } catch (e: Exception) {
        errorDialog(e)
      }
    }
  }

}