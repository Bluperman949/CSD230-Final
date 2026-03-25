package bluper.b9final

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.widget.EditText
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import bluper.b9final.databinding.ActivityTagsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// TODO: about button

class TagsActivity : ComponentActivity(), SearchView.OnQueryTextListener {
  val binding by lazy { ActivityTagsBinding.inflate(layoutInflater) }
  private val adapter by lazy { TagAdapter() }
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
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_search, menu)
    val searchItem = menu?.findItem(R.id.action_search) ?: return false
    val searchView = searchItem.actionView as SearchView
    searchView.setOnQueryTextListener(this)
    return true
  }

  override fun onQueryTextChange(text: String?): Boolean {
    adapter.clear()
    if (text.isNullOrBlank())  {
      adapter.addItems(steamTags)
      return true
    }
    adapter.addItems(steamTags.filter { it.name.contains(text, true) })
    return true
  }

  override fun onQueryTextSubmit(text: String?): Boolean { return false }

  private fun acquireApiKey() {
    val input = EditText(this@TagsActivity)
    input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

    AlertDialog.Builder(this@TagsActivity)
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
    AlertDialog.Builder(this@TagsActivity)
      .setTitle(R.string.error)
      .setMessage(e.message)
      .setPositiveButton("OK", null)
      .show()
  }
}