package example

import db.ChangeObserver
import db.DB
import db.Observable
import db.ObservableArrayList

class Person : Observable(){

    var name: String by observable("")

    var description: String? by observable(null)

    var tags: ObservableArrayList<Tag> by observableList()

}

class Tag() : Observable(){
    constructor(tag: String) : this(){
        this.tag = tag
    }
    var tag : String by observable("")
}

class PersonObserver(t: Person) : ChangeObserver<Person>(t){

    fun name(new: String){
        println("New name: $new!!!!")
    }

    fun all(new: Any?){
        println("Prop changed $new")
    }

}

fun main() {

    val obj = DB.getObject("person") {
        Person()
    }

    obj.name = "John Miller"

    obj.description = "This is some random stuff"

    obj.tags.add(Tag("Hallo"))
    obj.tags.add(Tag("Yay"))

    val list = DB.getList<Person>("persons")

    val p1 = Person()

    list.add(p1)

    p1.name = "John Miller 2"
    p1.description = "Something"

    val p2 = list.add(Person())

    p2.name = "John Miller 3"
    p2.description = "Something else"

    p2.tags.add(Tag("Hallo"))
    p2.tags.add(Tag("Yay"))

    // #####
    //look into data/tournaments.json
    // #####

    DB.commit {
        p2.name = "John Doe"
    }

}