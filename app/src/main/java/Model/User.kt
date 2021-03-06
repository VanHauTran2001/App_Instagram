package Model

class User {
    private var username : String =""
    private var fullname : String =""
    private var bio : String =""
    private var image : String =""
    private var uid : String =""

    constructor()

    constructor(username: String, fullname: String, bio: String, image: String, uid: String) {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid
    }
    fun getUserName() : String{
        return username
    }
    fun setUserName(userName: String){
        this.username = userName
    }
    fun getFullName() : String{
        return fullname
    }
    fun setFullName(fullName: String){
        this.fullname = fullName
    }
    fun getBio() : String{
        return bio
    }
    fun setBio(bio: String){
        this.bio = bio
    }
    fun getImage() : String{
        return image
    }
    fun setImage(image: String){
        this.image = image
    }
    fun getUid() : String{
        return uid
    }
    fun setUid(uid: String){
        this.uid = uid
    }

}