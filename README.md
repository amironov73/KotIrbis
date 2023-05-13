# KotIrbis

ManagedIrbis (fully managed client for IRBIS64 library system) ported to Java.

### Supported environments

JavaIrbis currently supports Java 11.

### Example code

```kotlin
import arsmagna.IrbisConnection
import java.util.Arrays

fun main() {
    val connection = IrbisConnection().apply {
        host = "192.168.1.1"
        username = "librarian"
        password = "secret"
        database = "IBIS"
        workstation = 'R'
    }

    if (!connection.connect()) {
        println("Can't connect")
    }

    var found = connection.search("\"A=KING, STEPHEN\"")
    if (found == null) {
        println("Search failed")
    } else {
        println("Found records: ${found.size}")
        if (found.size > 10) {
            // Take 10 first records
            found = Arrays.copyOf(found, 10)
        }

        for (i in 0 until found!!.size) {
            val mfn = found[i]
            val record = connection.readRecord(mfn)
            val title = record.fm(200, 'a')
            println("Title: $title")
            val description = connection.formatRecord("@brief", mfn)
            println("Description: $description")
            println()

        }
    }

    connection.disconnect()
}
```