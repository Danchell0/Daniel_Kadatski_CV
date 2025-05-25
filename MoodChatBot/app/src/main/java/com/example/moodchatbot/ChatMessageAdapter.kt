package com.example.moodchatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moodchatbot.model.ChatMessage // Import the moved ChatMessage

class ChatMessageAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_USER_MESSAGE = 1
        const val VIEW_TYPE_BOT_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].senderType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER_MESSAGE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message_user, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message_bot, parent, false)
            BotMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.itemViewType == VIEW_TYPE_USER_MESSAGE) {
            (holder as UserMessageViewHolder).bind(message)
        } else {
            (holder as BotMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.user_message_text)
        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.text
        }
    }

    inner class BotMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.bot_message_text)
        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.text
        }
    }
}
