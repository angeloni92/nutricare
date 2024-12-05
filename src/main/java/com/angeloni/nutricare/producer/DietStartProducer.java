package com.angeloni.nutricare.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.angeloni.nutricare.exception.QueueException;
import com.angeloni.nutricare.message.DietStartMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableJms
public class DietStartProducer {
	
	private static String SENDING_MESSAGE_LOG_INFO = "Sending message to queue [%s]";
	private static final String ERROR_QUEUE_MSG = "message.error.queue-start";
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Value("${jms.queue.diet.start}")
	private String dietStartQueue;
	
	/**
	 * Sends a diet start message to the specified JMS queue.
	 * <p>
	 * This method converts the provided {@link DietStartMessage} object to a JSON string and sends it to the 
	 * {@code dietStartQueue} using the {@link JmsTemplate}. The message is serialized with a pretty print format 
	 * for readability. If an error occurs during message sending or JSON processing, a {@link QueueException} is thrown.
	 * 
	 * @param dietStartMessage the message containing the diet start details to be sent. Must not be {@code null}.
	 * 
	 * @throws QueueException if there is an error sending the message to the queue or during the JSON serialization.
	 * 
	 * @see DietStartMessage
	 * @see JmsTemplate
	 * @see QueueException
	 * @see JsonProcessingException
	 * @see JmsException
	 */
	public void sendStartDietMessage(DietStartMessage dietStartMessage) {
		try {
			log.info(String.format(SENDING_MESSAGE_LOG_INFO, dietStartQueue));
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			jmsTemplate.convertAndSend(dietStartQueue, ow.writeValueAsString(dietStartMessage));
		}catch(JmsException | JsonProcessingException e) {
			throw new QueueException(ERROR_QUEUE_MSG);
		}
	}

}
