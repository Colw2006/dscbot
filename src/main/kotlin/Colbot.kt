import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.impl.message.EmbedImpl
import com.mewna.catnip.entity.user.Presence
import com.mewna.catnip.shard.DiscordEvent
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.random.Random

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("ColBot")
    val token = File("token.txt").readText()
    val colbot = Catnip.catnip(token)
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
            .filter { it.content() == "" }
            .subscribe({ it.channel().sendMessage("") })
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content() == "%flip" }
        .subscribe {
            val toss = Random.nextInt() % 2 == 0
            if (toss) {
                it.channel().sendMessage("The coin landed on: **HEADS**")
            } else {
                it.channel().sendMessage("The coin landed on: **TAILS**")
            }
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content() == ("<@!738930347407966308>") }
        .subscribe({ it.channel().sendMessage("Hello!") })

    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().startsWith("<play") }
        .subscribe { msg ->
            val params = msg.content().split(' ').drop(1)
            colbot.game(params.joinToString(" "), Presence.ActivityType.PLAYING, "")
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().startsWith("<watch") }
        .subscribe { msg ->
            val params = msg.content().split(' ').drop(1)
            colbot.game(params.joinToString(" "), Presence.ActivityType.WATCHING, "")
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().startsWith("<stream") }
        .subscribe { msg ->
            val params = msg.content().split(' ').drop(1)
            colbot.game(params.joinToString(" "), Presence.ActivityType.STREAMING, "")
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().toLowerCase() == "=ping" }
        .subscribe {
            val pongCommand = "pong!"
            val pingStart = System.nanoTime()
            it.channel().sendMessage(pongCommand)
            colbot.observable(DiscordEvent.MESSAGE_CREATE)
                .filter { it.content() == pongCommand && it.author().id() == colbot.selfUser()?.id() }
                .take(1)
                .subscribe { pongMsg ->
                    val pingEnd = System.nanoTime()
                    val pingDuration = (pingEnd - pingStart).toDouble() / 1000000.0
                    pongMsg.channel().sendMessage("Ping took ${pingDuration}ms")
                }

        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().startsWith("=embed") }
        .subscribe {
            val fieldList = it.content()
                .splitToSequence(' ')
                .drop(n = 1)
                .map { pc ->
                    EmbedImpl.FieldImpl.builder()
                        .name(pc)
                        .value("* *")
                        .inline(true)
                        .build()
                }
                .toList()
            val embedObject = EmbedImpl.builder()
                .title("")
                .description("")
                .fields(fieldList)
                .build()
            it.channel().sendMessage(embedObject).subscribe({}, { err ->
                logger.error(err.message)
            })
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content().startsWith("=invite") }
        .subscribe {
            val fieldList = it.content()
                .splitToSequence(' ')
                .drop(n = 1)
                .map { pc ->
                    EmbedImpl.FieldImpl.builder()
                        .name(pc)
                        .value("")
                        .inline(true)
                        .build()
                }
                .toList()
            val embedObject = EmbedImpl.builder()
                .title("Invite me to your server!!")
                .description("[Invite Link](https://discord.com/api/oauth2/authorize?client_id=738930347407966308&permissions=124992&scope=bot)")
                .fields(fieldList)
                .build()
            it.channel().sendMessage(embedObject).subscribe({}, { err ->
                logger.error(err.message)
            })
        }
    colbot.observable(DiscordEvent.MESSAGE_CREATE)
            .filter { it.content() == "Leeroy Jenkins" }
            .subscribe({ it.channel().sendMessage("LEEEEEERRROOOOYYY JEEENNNKKKIINNSS") })

    colbot.observable(DiscordEvent.MESSAGE_CREATE)
        .filter { it.content() == "=commands" }
        .subscribe({ it.channel().sendMessage("") })


    colbot.connect()
}

