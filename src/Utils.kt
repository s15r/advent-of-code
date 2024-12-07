import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.streams.asSequence

fun openFile(name: String) = Path("src/$name")
fun readLines(name: String) = openFile(name).readLines()
fun streamLines(name: String) = Files.lines(openFile(name)).asSequence()