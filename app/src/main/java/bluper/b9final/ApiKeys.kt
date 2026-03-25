package bluper.b9final

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText

// TODO: save to disk

var apiKey: String = "NO_API_KEY"
  private set

fun acquireApiKey(ctx: Context) {
  val input = EditText(ctx)
  input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

  AlertDialog.Builder(ctx)
    .setView(input)
    .setTitle(R.string.ask_api_key)
    .setMessage(R.string.message_api_key)
    .setPositiveButton("OK") { _, _ -> apiKey = input.text.toString() }
    .show()
}