package Model

class Post {
    private var postid : String = ""
    private var description : String = ""
    private var postimage : String =""
    private var publisher : String = ""

    constructor()

    constructor(postid: String, description: String, postimage: String, publisher: String) {
        this.postid = postid
        this.description = description
        this.postimage = postimage
        this.publisher = publisher
    }
    fun getPostid():String{
        return postid
    }
    fun setPostid(postid: String){
        this.postid = postid
    }
    fun getDesciption():String{
        return description
    }
    fun setDesciption(description: String){
        this.description = description
    }
    fun getPostimage():String{
        return postimage
    }
    fun setPostimage(postimage: String){
        this.postimage = postimage
    }
    fun getPublisher():String{
        return publisher
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }
}