package per.misaka.misakanetworkscore.exception.unofficialError

abstract class UnofficialError:RuntimeException() {
    abstract override val message:String
    abstract val statusCode:Int
}
