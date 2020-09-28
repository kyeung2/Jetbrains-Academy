package phonebook

import java.time.Duration

fun Duration.prettyPrint(): String = "${toMinutesPart()} min. ${toSecondsPart()} sec. ${toMillisPart()} ms."

//data class Record(val number: Int, val name: String)

typealias Record = String
fun timedSearch(records: Array<Record>, names: List<String>, searchAlgorithm: (records: Array<Record>, name: String) -> Boolean): Pair<Duration, Int> {
    val start = System.currentTimeMillis()
    val found = names.filter { searchAlgorithm(records, it) }.count()
    return Pair(duration(start), found)
}

fun duration(start: Long): Duration {
    return Duration.ofMillis(System.currentTimeMillis() - start)
}
