package db

typealias ElementChangedListener<X> = (ElementChangeType, X) -> Unit

enum class ElementChangeType{
    Add, Update, Remove
}

class ObservableArrayList<X : Observable> : List<X>{

    constructor()

    constructor(vararg arr: X){
        collection.addAll(arr)
        arr.forEach { addHook(it) }
    }

    val hooks = mutableListOf<ChangeObserver<X>>()

    val collection = mutableListOf<X>()

    private val listeners = mutableListOf<ElementChangedListener<X>>()

    private fun signalChanged(type: ElementChangeType, element: X){
        listeners.forEach { it(type, element) }
    }

    fun addListener(f: ElementChangedListener<X>){
        listeners.add(f)
    }

    fun add(element: X) : X {
        collection.add(element)
        addHook(element)
        signalChanged(ElementChangeType.Add, element)
        return element
    }

    fun addHook(element: X){

        hooks.add(GenericChangeObserver(element){
            signalChanged(ElementChangeType.Update, element)
        })

    }

    fun addAll(elements: Collection<X>): Boolean {
        val added = collection.addAll(elements)
        if (added){
            elements.forEach {
                addHook(it)
                signalChanged(ElementChangeType.Add, it)
            }
        }
        return added
    }

    fun remove(element: X) : Boolean{

        val b = collection.remove(element)
        signalChanged(ElementChangeType.Remove, element)
        return b
    }

    fun clone() = collection.toList()

    override operator fun get(i : Int) = collection.get(i)

    override val size: Int
        get() = collection.size

    override fun iterator() =
        collection.iterator()

    override fun contains(element: X) =
        collection.contains(element)

    override fun containsAll(elements: Collection<X>) =
        collection.containsAll(elements)

    override fun indexOf(element: X) =
        collection.indexOf(element)

    override fun isEmpty() =
        collection.isEmpty()

    override fun lastIndexOf(element: X)=
        collection.lastIndexOf(element)

    override fun listIterator() =
        collection.listIterator()

    override fun listIterator(index: Int) =
        collection.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int) =
        throw IllegalAccessException("Not available on ObservableList")

}

class GenericChangeObserver <X : Observable> (t : X, val f: () -> Unit) : ChangeObserver<X>(t){

    fun all(new: Any?){
        f()
    }
}

fun <X : Observable> observableListOf(vararg initial: X) = ObservableArrayList(*initial)

fun <X : Observable> observableListOf() = ObservableArrayList<X>()