package bluper.b9final

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// TODO: about button

val json = Json { ignoreUnknownKeys = true }

abstract class AbstractActivity : ComponentActivity(), SearchView.OnQueryTextListener {
  protected val httpClient = OkHttpClient()

  protected abstract fun getInflatedBinding(): ViewBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // prevent toolbar from obscuring content
    ViewCompat.setOnApplyWindowInsetsListener(getInflatedBinding().root) { v, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
      v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
      insets
    }
    setContentView(getInflatedBinding().root)
  }

  protected fun <E : Exception> errorDialog(e: E) {
    e.printStackTrace()
    AlertDialog.Builder(this)
      .setTitle(R.string.error)
      .setMessage(e.message)
      .setPositiveButton("OK", null)
      .show()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_search, menu)
    val searchItem = menu?.findItem(R.id.action_search) ?: return false
    val searchView = searchItem.actionView as SearchView
    searchView.setOnQueryTextListener(this)
    return true
  }

  override fun onQueryTextSubmit(text: String?): Boolean { return false }

  @Throws(IOException::class)
  protected suspend fun httpRequest(request: Request): JsonElement {
    return withContext(Dispatchers.IO) {
      val responseStr = httpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("HTTP ${response.code} from \"${request.url}\"")
        response.body?.string() ?: throw IOException("Empty HTTP response from \"${request.url}\"")
      }
      json.decodeFromString<JsonElement>(responseStr)
    }
  }
}