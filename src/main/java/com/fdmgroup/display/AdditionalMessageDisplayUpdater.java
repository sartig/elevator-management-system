package com.fdmgroup.display;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextArea;

public class AdditionalMessageDisplayUpdater implements Runnable {
	private static AdditionalMessageDisplayUpdater instance;
	private JTextArea textArea = null;
	private Map<String, Integer> additionalMessages = new HashMap<>();
	private final int messageDisplayDurationInMs = 4000;
	private final int tickMs = 500;

	private AdditionalMessageDisplayUpdater() {
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}
	
	public static AdditionalMessageDisplayUpdater getInstance() {
		if(instance==null) instance = new AdditionalMessageDisplayUpdater();
		return instance;
	}

	@Override
	public void run() {

		while (!Thread.interrupted()) {
			showAdditionalMessages();
			try {
				Thread.sleep(tickMs);
				passTimeForAdditionalMessages(tickMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addAdditionalMessage(String message) {
		additionalMessages.put(message, messageDisplayDurationInMs);
	}

	private void showAdditionalMessages() {
		StringBuilder messages = new StringBuilder();
		messages.append("=====================================================================\n");
		additionalMessages.forEach((message, duration) -> messages.append(message).append("\n"));
		textArea.setText(messages.toString());
	}

	private void passTimeForAdditionalMessages(int timeInMs) {
		Map<String, Integer> newMessages = new HashMap<>();
		additionalMessages.forEach((message, duration) -> {
			int newDuration = duration - timeInMs;
			if (newDuration > 0) {
				newMessages.put(message, newDuration);
			}
		});
		additionalMessages = newMessages;

	}

}
