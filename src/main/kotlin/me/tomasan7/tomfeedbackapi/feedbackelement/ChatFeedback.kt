package me.tomasan7.tomfeedbackapi.feedbackelement

import me.tomasan7.tomfeedbackapi.Feedback
import me.tomasan7.tomfeedbackapi.miniParse
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

open class ChatFeedback(val message: Component) : Feedback
{
    override fun apply(audience: Audience) = audience.sendMessage(message)

    companion object
    {
        fun deserialize(obj: Any): ChatFeedback?
        {
            if (obj !is String)
                return null

            return ChatFeedback(obj.miniParse())
        }
    }
}