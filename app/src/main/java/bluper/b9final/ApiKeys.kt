package bluper.b9final

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import java.io.File

var apiKey: String = "NO_API_KEY"
  private set

fun acquireApiKey(ctx: Context) {
  val file = File(ctx.noBackupFilesDir, "steam")
  // refresh key if it's been more than 24 hours
  if (file.exists() && System.currentTimeMillis() - file.lastModified() < 86400000) {
    apiKey = file.readText()
    return
  }

  val input = EditText(ctx)
  input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

  AlertDialog.Builder(ctx)
    .setView(input)
    .setTitle(R.string.ask_api_key)
    .setMessage(R.string.message_api_key)
    .setPositiveButton("OK") { _, _ ->
      apiKey = input.text.toString()
      file.createNewFile()
      file.writeText(apiKey)
    }
    .show()
}