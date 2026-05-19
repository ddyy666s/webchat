package com.chat.chat_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 聊天后端应用程序主入口
 * @author chat-backend
 * @since 2026-05-12
 */
@SpringBootApplication
@MapperScan({"com.chat.chat_backend.modules.user.mapper",
             "com.chat.chat_backend.modules.friend.mapper",
             "com.chat.chat_backend.modules.group.mapper",
             "com.chat.chat_backend.modules.message.mapper",
             "com.chat.chat_backend.modules.emoji.mapper",
             "com.chat.chat_backend.modules.impression.mapper",
             "com.chat.chat_backend.modules.notification.mapper",
             "com.chat.chat_backend.modules.admin.mapper",
             "com.chat.chat_backend.modules.health.mapper"})
public class ChatBackendApplication {

	/**
	 * 应用程序启动入口
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChatBackendApplication.class, args);
	}
}
