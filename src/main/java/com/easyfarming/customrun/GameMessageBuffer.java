package com.easyfarming.customrun;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Stores the last game/spam chat message used for compost and protection detection.
 */
@Singleton
public class GameMessageBuffer
{
	private String lastMessage = "";

	@Inject
	public GameMessageBuffer()
	{
	}

	public void onChatMessage(ChatMessage event)
	{
		String message = event.getMessage();
		if (event.getType() == ChatMessageType.GAMEMESSAGE
				|| event.getType() == ChatMessageType.SPAM)
		{
			lastMessage = message;
		}
	}

	public String getLastMessage()
	{
		return lastMessage;
	}

	/**
	 * Clears the last game message. Call when advancing so a prior compost line
	 * is not applied to the next patch.
	 */
	public void clearLastMessage()
	{
		lastMessage = "";
	}

	public boolean checkMessage(String targetMessage, String lastMessage)
	{
		return lastMessage.trim().equalsIgnoreCase(targetMessage.trim());
	}
}
