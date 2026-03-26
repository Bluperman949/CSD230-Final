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
  val content_descriptorids: List<Int> = emptyList(),
  val reviews: Reviews? = null,
) {
  @Serializable
  data class Reviews(
    val summary_filtered: SummaryFiltered,
  ) {
    @Serializable
    data class SummaryFiltered(
      val review_count: Int,
      val percent_positive: Int,
    ) {
      override fun toString(): String =
        if (review_count == 0) "No reviews"
        else "$review_count reviews, $percent_positive% positive"
    }
  }

  @Serializable
  data class BasicInfo(
    val short_description: String = "",
  )

  override fun toString(): String = name
}

