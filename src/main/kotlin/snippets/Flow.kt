package snippets

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {
    //channel with multiple producers and multiple consumers
    //channels send suspend when no one is receiving (except when under capacity)
    //channels receive also suspends when channel is empty
    //rendezvous channel
//    val channel = Channel<String>()
//    val channel = Channel<String>(50) //buffered channel
//    val i = AtomicInteger()
//
//    for (p in 1..2) {
//        async { 
//            while(true) {
//                channel.send("producer $p adding ${i.incrementAndGet()} at ${LocalDateTime.now()}")
//            }
//        }
//    }
//
//    for (c in 1..3) {
//        async {
//            while(true) {
//                delay(100)
//                val data = channel.receive()
//                println("consumer $c processing $data")
//            }
//        }
//    }


        //queue with multiple producers and multiple consumers
//    val queue = ConcurrentLinkedQueue<String>()
//    
//    val i = AtomicInteger()
//    
//    for (p in 1..2) {
//        async { 
//            while(true) {
//                queue.add("producer $p adding ${i.incrementAndGet()}")
//                delay(50)
//            }
//        }
//    }
//
//    for (c in 1..3) {
//        async {
//            while(true) {
//                delay(75)
//                val data = queue.poll()
//                if (data == null) continue
//                println("consumer $c processing $data")
//            }
//        }
//    }
    
    //sharedflow, multiple consumers, with offset
    //hot means it justs starts, independent of collectors
//    val sharedFlow = MutableSharedFlow<String>(replay = 2)
//    launch { 
//        sharedFlow.collect { println("collecting $it") }
//    }
//
//    sharedFlow.emit("1")
//    sharedFlow.emit("2")
//    sharedFlow.emit("3")
//
//    launch {
//        sharedFlow.collect { println("collecting in 2: $it") }
//    }
////
    //flow emit and consume, with buffer (non-sequential)
//    val flow = flow {
//        for (i in 1..10) {
//            emit(i)
//            delay(100)
//        }
//    }
//
//    flow.buffer().map { "new value: $it at ${LocalDateTime.now()}" }
////    flow.map { "new value: $it at ${LocalDateTime.now()}" }
//        .collect {
//            delay(100)
//            println(it) 
//        }
//
//    println("done")
//    
    //flattening flows
//    val flow1 = flow {
//        for (i in 1..10) {
//            emit("producing $i from 1")
//            delay(100)
//        }
//    }
//    val flow2 = flow {
//        for (i in 1..10) {
//            emit("producing $i from 2")
//            delay(100)
//        }
//    }
//    val combinedFlow = flowOf(flow1, flow2).flatMapMerge { it }
//
//    combinedFlow.buffer().map { "new value: $it at ${LocalDateTime.now()}" }
////    flow.map { "new value: $it at ${LocalDateTime.now()}" }
//        .collect {
//            delay(100)
//            println(it)
//        }
//
//    println("done")

}