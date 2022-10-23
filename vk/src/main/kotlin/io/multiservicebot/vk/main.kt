package io.multiservicebot.vk

import io.multiservicebot.core.init
import io.multiservicebot.vk.network.vkapi.VkApiFactory

suspend fun main() = init {
    val vk = VkApiFactory.buildService(env["VK_TOKEN"])

}