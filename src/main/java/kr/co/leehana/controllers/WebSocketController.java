package kr.co.leehana.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

/**
 * @author Hana Lee
 * @since 2015-12-03 15:45
 */
@Controller
public class WebSocketController {

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greetings(String message) {
		return "[" + LocalDate.now() + "] : " + message;
	}
}
