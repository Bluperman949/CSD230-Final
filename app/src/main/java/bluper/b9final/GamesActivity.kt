package bluper.b9final

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import bluper.b9final.databinding.ActivityGamesBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder.encode as urlencode

class GamesActivity : AbstractActivity() {
  val binding by lazy { ActivityGamesBinding.inflate(layoutInflater) }
  private val adapter by lazy { GameAdapter() }

  private var steamGames: List<SteamGame> = emptyList()

  private val queryJson by lazy {
    val tag = intent.getIntExtra("tag", -24)
    if (tag == -24) throw IllegalStateException("Tag not provided")
    urlencode(
      """
      {
        "query": {
          "count": 20,
          "filters": {
            "released_only": true,
            "tagids_must_match": [
              { "tagids": [$tag] }
            ]
          },
          "type_filters": {
            "include_games": true
          },
          "content_descriptors_excluded": ["1","2","3","4","5"]
        },
        "context": {
          "language": "english",
          "country_code": "us"
        },
        "data_request": {
          "include_basic_info": true
        }
      }
      """.replace(Regex("\\s+"), "").replace("\n", ""), "UTF-8"
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    loadSteamGames()
    binding.recycler.adapter = adapter
  }

  override fun getInflatedBinding() = binding

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

  override fun onQueryTextChange(p0: String?): Boolean {
    return false
  }
}