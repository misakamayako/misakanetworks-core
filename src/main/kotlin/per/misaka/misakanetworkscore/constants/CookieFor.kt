package per.misaka.misakanetworkscore.constants

enum class CookieFor (val value:String){
    Token("token"){
        override fun toString():String{
            return "token"
        }
    }
}