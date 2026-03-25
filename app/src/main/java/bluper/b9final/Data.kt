package bluper.b9final

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(InternalSerializationApi::class)
@Serializable
data class SteamTag(
  val tagid: Int,
  val name: String,
) {
  override fun toString(): String = name
}

@OptIn(InternalSerializationApi::class)
@Serializable
data class SteamGame(
  val appid: Int,
  val name: String,
  val basic_info: BasicInfo,
  val best_purchase_option: BestPurchaseOption? = null,
) {
  override fun toString(): String = name

  @Serializable
  data class BasicInfo(
    val short_description: String = "",
  )

  @Serializable
  data class BestPurchaseOption(
    val formatted_final_price: String,
  )
}

