package me.tomasan7.tomfeedbackapi.feedbackelement

import me.tomasan7.tomfeedbackapi.Feedback
import me.tomasan7.tomfeedbackapi.Placeholders
import me.tomasan7.tomfeedbackapi.PlaceholderFeedback
import me.tomasan7.tomfeedbackapi.miniParse
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import java.time.Duration

class TitleFeedback(
    val title: Component? = null,
    val subtitle: Component? = null,
    val times: Times = Times.DEFAULT
) : Feedback
{
    init
    {
        require(title != null || subtitle != null) { "Title and subtitle cannot be null at the same time. At least one must be non null." }
    }

    override fun apply(audience: Audience)
    {
        /* Send whole Title object if both title and subtitle are present. */
        if (title != null && subtitle != null)
        {
            audience.showTitle(buildTitle())
            return
        }

        /* Now, we will only send one part (either title or subtitle) so we need to send times separately beforehand. */
        audience.sendTitlePart(TitlePart.TIMES, times.titleTimes)

        /* Send only title. */
        if (title != null)
            audience.sendTitlePart(TitlePart.TITLE, title)
        /* Send only subtitle. */
        else if (subtitle != null)
            audience.sendTitlePart(TitlePart.SUBTITLE, subtitle)
    }

    /**
     * Creates a [Title] instance from this [TitleFeedback].
     * @param placeholders Placeholders to replace.
     */
    private fun buildTitle(): Title
    {
        /* Neither should be null, since it is called from apply, when neither is null. */
        val titleComp = title ?: Component.empty()
        val subtitleComp = subtitle ?: Component.empty()

        return Title.title(titleComp, subtitleComp, times.titleTimes)
    }

    companion object
    {
        fun deserialize(obj: Any): TitleFeedback?
        {
            if (obj !is String
                && obj !is Map<*, *>)
                return null

            if (obj is String)
                return TitleFeedback(obj.miniParse())
            else
            {
                val map = obj as Map<*, *>

                val title = map["title"] as? String
                val subtitle = map["sub-title"] as? String

                if (title == null && subtitle == null)
                    return null

                val fadeIn = map["fade-in"] as? Int ?: Times.DEFAULT.fadeIn
                val stay = map["stay"] as? Int ?: Times.DEFAULT.stay
                val fadeOut = map["fade-out"] as? Int ?: Times.DEFAULT.fadeOut

                return TitleFeedback(title?.miniParse(),
                                     subtitle?.miniParse(),
                                     Times(fadeIn, stay, fadeOut))
            }
        }

        data class Times(val fadeIn: Int, val stay: Int, val fadeOut: Int)
        {
            val titleTimes: Title.Times
                get() = Title.Times.times(
                    Duration.ofMillis((fadeIn * 50).toLong()),
                    Duration.ofMillis((stay * 50).toLong()),
                    Duration.ofMillis((fadeOut * 50).toLong())
                )

            companion object
            {
                val DEFAULT = Times(20, 20, 20)
            }
        }
    }
}